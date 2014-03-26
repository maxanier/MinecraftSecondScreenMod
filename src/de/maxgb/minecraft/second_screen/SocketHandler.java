package de.maxgb.minecraft.second_screen;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.ArrayList;

import org.json.JSONObject;

import de.maxgb.minecraft.second_screen.info_listener.ChatListener;
import de.maxgb.minecraft.second_screen.info_listener.PlayerInfoListener;
import de.maxgb.minecraft.second_screen.info_listener.PlayerInventoryListener;
import de.maxgb.minecraft.second_screen.info_listener.ServerInfoListener;
import de.maxgb.minecraft.second_screen.info_listener.WorldInfoListener;
import de.maxgb.minecraft.second_screen.util.Constants;
import de.maxgb.minecraft.second_screen.util.Logger;
import de.maxgb.minecraft.second_screen.util.PROTOKOLL;
import de.maxgb.minecraft.second_screen.util.User;
import de.maxgb.minecraft.second_screen.util.Version;

public class SocketHandler extends Thread {

	public Socket socket;
	private boolean running;
	private ArrayList<StandardListener> listeners;
	private String TAG = "SocketHandler";
	public boolean remove;
	private User user;

	public SocketHandler(Socket accepted) {
		remove = false;
		this.socket = accepted;
		int id = SecondScreenMod.id();
		setName("SecondScreenMod-SocketHandler #" + id);
		TAG = "SocketHandler-#" + id;

		Logger.i(TAG,
				"Second Screen connection: "
						+ socket.getInetAddress().getHostAddress() + ":"
						+ socket.getLocalPort());
		listeners = new ArrayList<StandardListener>();

		start();

	}

	/**
	 * Stops the run method and sets remove to true so that the listener thread
	 * removes it from the list
	 */
	private void close() {

		running = false;
		try {
			socket.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
		socket = null;
		remove = true;

	}

	@Override
	public void run() {

		running = true;
		while (running) {

			BufferedReader reader = null;
			try {
				reader = new BufferedReader(new InputStreamReader(
						socket.getInputStream()));
			} catch (IOException e) {
				this.close();
				return;
			}

			// Read if is available
			if (reader != null) {
				// Get the message
				String msg = null;
				try {
					msg = reader.readLine();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				// ProcessMessage
				if (msg != null) {
					Logger.i(TAG, "Received Message: " + msg);// TODO Remove
					if(msg.startsWith(PROTOKOLL.REGISTER_COMMAND_BEGIN)){
						if(user==null){
							JSONObject result=new JSONObject();
							result.put("success", 0);
							result.put("error", "Login request required");
							send(PROTOKOLL.ERROR+" "+result.toString());
							return;
						}
					}
					if (msg.startsWith(PROTOKOLL.REGISTER_S_PLAYERINFO_LISTENER)) {

						listeners.add(new PlayerInfoListener(user));

					} else if (msg
							.startsWith(PROTOKOLL.UNREGISTER_S_PLAYERINFO_LISTENER)) {


							for (int i = 0; i < listeners.size(); i++) {
								StandardListener l = listeners.get(i);
								if (l instanceof PlayerInfoListener) {
									listeners.remove(i);
								}
							}

					} else if (msg
							.startsWith(PROTOKOLL.REGISTER_PLAYER_INVENTORY_LISTENER)) {

						

							listeners.add(new PlayerInventoryListener(user));
						
					} else if (msg
							.startsWith(PROTOKOLL.UNREGISTER_PLAYER_INVENTORY_LISTENER)) {
						
							for (int i = 0; i < listeners.size(); i++) {
								StandardListener l = listeners.get(i);
								if (l instanceof PlayerInventoryListener) {
									listeners.remove(i);
								}
							}
						
					} else if (msg
							.startsWith(PROTOKOLL.REGISTER_SERVER_INFO_LISTENER)) {
						listeners.add(new ServerInfoListener(user));

					} else if (msg
							.startsWith(PROTOKOLL.UNREGISTER_SERVER_INFO_LISTENER)) {
						for (int i = 0; i < listeners.size(); i++) {
							if (listeners.get(i) instanceof ServerInfoListener) {
								listeners.remove(i);
							}
						}
					} else if (msg
							.startsWith(PROTOKOLL.REGISTER_WORLD_INFO_LISTENER)) {
						listeners.add(new WorldInfoListener(user));
					} else if (msg
							.startsWith(PROTOKOLL.UNREGISTER_WORLD_INFO_LISTENER)) {
						for (int i = 0; i < listeners.size(); i++) {
							if (listeners.get(i) instanceof WorldInfoListener) {
								listeners.remove(i);
							}
						}
						
					
					} 
					else if(msg.startsWith(PROTOKOLL.REGISTER_CHAT_LISTENER)){
						listeners.add(new ChatListener(user));
					}
					else if(msg.startsWith(PROTOKOLL.UNREGISTER_CHAT_LISTENER)){
						for (int i = 0; i < listeners.size(); i++) {
							if (listeners.get(i) instanceof ChatListener) {
								listeners.remove(i);
								break;
							}
						}
					}else if (msg
							.startsWith(PROTOKOLL.UNREGISTER_ALL_LISTENER)) {
						listeners = new ArrayList<StandardListener>();
						System.gc();
					} else if (msg.startsWith(PROTOKOLL.CONNECT)) {
						JSONObject result = new JSONObject();
						result.put("versionid", Constants.FEATURE_VERSION);
						result.put("minecraftversion", Constants.MINECRAFT_VERSION);
						result.put("login_required", Configs.login_required);
						send(PROTOKOLL.CONNECT_RESULT + " " + result.toString());
					}
					else if(msg.startsWith(PROTOKOLL.LOGIN)){
						String params=msg.substring(PROTOKOLL.LOGIN.length()+1);
						JSONObject data=new JSONObject(params);
						String username=data.getString("username");
						
						int appversion=data.getInt("appversion");
						
						if(Configs.login_required){
							if(!data.has("password")){
								JSONObject result=new JSONObject();
								result.put("success", 0);
								result.put("error", "Password required");
								send(PROTOKOLL.LOGIN_RESULT+" "+result.toString());
								return;
							}
							else if(!auth(username,data.getString("password"))){
								JSONObject result=new JSONObject();
								result.put("success", 0);
								result.put("error", "Username or password wrong");
								send(PROTOKOLL.LOGIN_RESULT+" "+result.toString());
								return;
							}
						}
						user=new User(username,appversion);
						JSONObject result=new JSONObject();
						result.put("success", 1);
						result.put("newer_app_version", Version.isNewestAppVersion(appversion));
						send(PROTOKOLL.LOGIN_RESULT+" "+result.toString());
						
					}
							
					else if (msg.startsWith(PROTOKOLL.DISCONNECT)) {
						close();
					} else {
						send(PROTOKOLL.UNKNOWN);
					}

				}

			}

		}

	}

	private void send(String s) {
		if (s == null) {
			return;
		}

		// Start sending
		// try to get a writer
		BufferedWriter writer = null;
		try {
			writer = new BufferedWriter(new OutputStreamWriter(
					socket.getOutputStream()));
		} catch (Exception e) {
			Logger.e(TAG, "Failed to get BufferedWriter", e);
			this.close();
		}

		// if it is available, write
		if (writer != null) {
			try {

				writer.append(s + "\n");
				writer.flush();
				Logger.i(TAG, "Send message: " + s);
			} catch (IOException e) {
				Logger.e(TAG, "Failed to send message", e);
				close();
			}
		}
	}

	/**
	 * Sends a stopping message to the client and closes the handler
	 */
	public void stopping() {
		send(PROTOKOLL.SERVER_STOPPING);
		close();
	}

	public void tick() {
		for (StandardListener l : listeners) {
			send(l.tick());
		}
	}
	
	private boolean auth(String name,String passhash){
		//TODO
		return true;
	}

}
