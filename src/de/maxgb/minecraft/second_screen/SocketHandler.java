package de.maxgb.minecraft.second_screen;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.ArrayList;

import org.json.JSONObject;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent.ServerTickEvent;
import de.maxgb.minecraft.second_screen.info_listener.PlayerInfoListener;
import de.maxgb.minecraft.second_screen.info_listener.PlayerInventoryListener;
import de.maxgb.minecraft.second_screen.info_listener.ServerInfoListener;
import de.maxgb.minecraft.second_screen.info_listener.WorldInfoListener;
import de.maxgb.minecraft.second_screen.util.Constants;
import de.maxgb.minecraft.second_screen.util.Logger;
import de.maxgb.minecraft.second_screen.util.PROTOKOLL;

public class SocketHandler extends Thread {

	private SocketListener socketListener;
	public Socket socket;
	private boolean running;
	private ArrayList<StandardListener> listeners;
	private String TAG = "SocketHandler";
	public boolean remove;

	public SocketHandler(Socket accepted, SocketListener socketListener) {
		remove=false;
		this.socketListener = socketListener;
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
	 * Stops the run method and sets remove to true so that the listener thread removes it from the list
	 */
	private void close() {

		running = false;
		try {
			socket.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		socket = null;
		remove=true;

	}
	
	/**
	 * Sends a stopping message to the client and closes the handler
	 */
	public void stopping(){
		send(PROTOKOLL.SERVER_STOPPING);
		close();
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
					
					if (msg.startsWith(PROTOKOLL.REGISTER_S_PLAYERINFO_LISTENER)) {

						String params = null;
						try {
							params = msg
									.substring(PROTOKOLL.REGISTER_S_PLAYERINFO_LISTENER
											.length() + 1);
						} catch (Exception e) {
							Logger.w(TAG,
									"Register_Simple_Playerinfo message did not include the right paramter");
						}

						if (params != null && !params.equals("")) {

							listeners.add(new PlayerInfoListener(params));
						}

					} else if (msg
							.startsWith(PROTOKOLL.UNREGISTER_S_PLAYERINFO_LISTENER)) {

						String params = null;
						try {
							params = msg
									.substring(PROTOKOLL.REGISTER_S_PLAYERINFO_LISTENER
											.length() + 1);
						} catch (Exception e) {
							Logger.w(TAG,
									"Unregister_Simple_Playerinfo message did not include the right paramter");
						}

						if (params != null && !params.equals("")) {
							for (int i = 0; i < listeners.size(); i++) {
								StandardListener l = listeners.get(i);
								if (l instanceof PlayerInfoListener
										&& l.matchesParams(params)) {
									listeners.remove(i);
									break;
								}
							}
						}

					} else if (msg
							.startsWith(PROTOKOLL.REGISTER_PLAYER_INVENTORY_LISTENER)) {

						String params = null;
						try {
							params = msg
									.substring(PROTOKOLL.REGISTER_PLAYER_INVENTORY_LISTENER
											.length() + 1);
						} catch (Exception e) {
							Logger.w(TAG,
									"Register_Inventory_Player message did not include the right paramter");
						}

						if (params != null && !params.equals("")) {

							listeners.add(new PlayerInventoryListener(params));
						}
					} else if (msg
							.startsWith(PROTOKOLL.UNREGISTER_PLAYER_INVENTORY_LISTENER)) {
						String params = null;
						try {
							params = msg
									.substring(PROTOKOLL.UNREGISTER_PLAYER_INVENTORY_LISTENER
											.length() + 1);
						} catch (Exception e) {
							Logger.w(TAG,
									"Unregister_Inventory_Player message did not include the right paramter");
						}

						if (params != null && !params.equals("")) {
							for (int i = 0; i < listeners.size(); i++) {
								StandardListener l = listeners.get(i);
								if (l instanceof PlayerInventoryListener
										&& l.matchesParams(params)) {
									listeners.remove(i);
									break;
								}
							}
						}
					} else if (msg
							.startsWith(PROTOKOLL.REGISTER_SERVER_INFO_LISTENER)) {
						listeners.add(new ServerInfoListener(null));

					} else if (msg
							.startsWith(PROTOKOLL.UNREGISTER_SERVER_INFO_LISTENER)) {
						for (int i = 0; i < listeners.size(); i++) {
							if (listeners.get(i) instanceof ServerInfoListener) {
								listeners.remove(i);
								break;
							}
						}
					}else if(msg.startsWith(PROTOKOLL.REGISTER_WORLD_INFO_LISTENER)){
						listeners.add(new WorldInfoListener(null));
					}
					else if(msg.startsWith(PROTOKOLL.UNREGISTER_WORLD_INFO_LISTENER)){
						for (int i = 0; i < listeners.size(); i++) {
							if (listeners.get(i) instanceof WorldInfoListener) {
								listeners.remove(i);
								break;
							}
						}
					}
					else if (msg
							.startsWith(PROTOKOLL.UNREGISTER_ALL_LISTENER)) {
						listeners = new ArrayList<StandardListener>();
						System.gc();
					}
					else if(msg.startsWith(PROTOKOLL.CONNECT)){
						JSONObject result=new JSONObject();
						result.put("versionid",Constants.FEATURE_VERSION);
						send(PROTOKOLL.CONNECT_RESULT+" "+result.toString());
					}
					else if(msg.startsWith(PROTOKOLL.DISCONNECT)){
						close();
					}
					else{
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
		} catch (IOException e) {
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

	
	public void tick() {
		for (StandardListener l : listeners) {
			send(l.tick());
		}
	}

}
