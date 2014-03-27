package de.maxgb.minecraft.second_screen;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.ArrayList;

import org.json.JSONObject;

import de.maxgb.minecraft.second_screen.data.UserManager;
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

	/**
	 * Handles the connect message
	 */
	private void onConnectMessage() {
		JSONObject result = new JSONObject();
		result.put("versionid", Constants.FEATURE_VERSION);
		result.put("minecraftversion", Constants.MINECRAFT_VERSION);
		result.put("login_required", Configs.auth_required);
		send(PROTOKOLL.CONNECT_RESULT + " " + result.toString());

	}

	/**
	 * Handles the disconnect message
	 */
	private void onDisconnectMessage() {
		close();
	}

	/**
	 * Handles the login message
	 * 
	 * @param params
	 *            The parameter Json string
	 */
	private void onLoginMessage(String params) {
		JSONObject data = new JSONObject(params);
		if(!data.has("username")){
			Logger.w(TAG, "Login message is missing username.");
			JSONObject result = new JSONObject();
			result.put("success", 0);
			result.put("error", "Username is missing");
			send(PROTOKOLL.LOGIN_RESULT + " " + result.toString());
		}
		String username = data.getString("username");

		if(!data.has("appversion")){
			Logger.w(TAG, "Login message is missing appversion.");
			JSONObject result = new JSONObject();
			result.put("success", 0);
			result.put("error", "Appversion is missing");
			send(PROTOKOLL.LOGIN_RESULT + " " + result.toString());
		}
		int appversion = data.getInt("appversion");

		if (Configs.auth_required) {
			if (!data.has("password")) {
				Logger.w(
						TAG,
						"Login message is missing password. It is set to required in the config options");
				JSONObject result = new JSONObject();
				result.put("success", 0);
				result.put("error", "Password required");
				send(PROTOKOLL.LOGIN_RESULT + " " + result.toString());

				return;
			} else if (!UserManager.auth(username, data.getInt("password"))) {
				Logger.w(TAG,
						"Authentification failed. Username or password is wrong");
				JSONObject result = new JSONObject();
				result.put("success", 0);
				result.put("error", "Username or password wrong");
				send(PROTOKOLL.LOGIN_RESULT + " " + result.toString());
				return;
			}
		}
		user = UserManager.getUser(username);
		JSONObject result = new JSONObject();
		result.put("success", 1);
		result.put("appupdate", Version.isNewestAppVersion(appversion));
		send(PROTOKOLL.LOGIN_RESULT + " " + result.toString());
		Logger.i(TAG, "Sucessfully logged in user " + username);
	}

	/**
	 * Handles the register listener message
	 * 
	 * @param l
	 *            The string representation of the listener which should be
	 *            registered
	 */
	private void onRegisterMessage(final String l) {

		// Check is user object is available and so if the user is authentified
		if (user == null) {
			
			send(PROTOKOLL.ERROR + " " + "Login required. ["+PROTOKOLL.REGISTER_COMMAND_BEGIN+l+"]");
			Logger.w(TAG,
					"Cannot register a listener before login. It is required by the configs");
			return;
		}

		if (l.startsWith(PROTOKOLL.S_PLAYERINFO_LISTENER)) {

			listeners.add(new PlayerInfoListener(user));

		} else if (l.startsWith(PROTOKOLL.PLAYER_INVENTORY_LISTENER)) {

			listeners.add(new PlayerInventoryListener(user));

		} else if (l.startsWith(PROTOKOLL.SERVER_INFO_LISTENER)) {
			listeners.add(new ServerInfoListener(user));

		} else if (l.startsWith(PROTOKOLL.WORLD_INFO_LISTENER)) {
			listeners.add(new WorldInfoListener(user));
		} else if (l.startsWith(PROTOKOLL.CHAT_LISTENER)) {
			listeners.add(new ChatListener(user));
		}
	}

	/**
	 * Handles the unregister listener messages
	 * 
	 * @param l
	 *            The string representation of the listener, which should be
	 *            unregistered
	 */
	private void onUnregisterMessage(String l) {
		int listenercount = listeners.size();
		if (l.startsWith(PROTOKOLL.S_PLAYERINFO_LISTENER)) {

			for (int i = 0; i < listeners.size(); i++) {
				StandardListener sl = listeners.get(i);
				if (sl instanceof PlayerInfoListener) {
					listeners.remove(i);
				}
			}

		} else if (l.startsWith(PROTOKOLL.PLAYER_INVENTORY_LISTENER)) {

			for (int i = 0; i < listeners.size(); i++) {
				StandardListener sl = listeners.get(i);
				if (sl instanceof PlayerInventoryListener) {
					listeners.remove(i);
				}
			}

		} else if (l.startsWith(PROTOKOLL.SERVER_INFO_LISTENER)) {
			for (int i = 0; i < listeners.size(); i++) {
				if (listeners.get(i) instanceof ServerInfoListener) {
					listeners.remove(i);
				}
			}
		} else if (l.startsWith(PROTOKOLL.WORLD_INFO_LISTENER)) {
			for (int i = 0; i < listeners.size(); i++) {
				if (listeners.get(i) instanceof WorldInfoListener) {
					listeners.remove(i);
				}
			}

		} else if (l.startsWith(PROTOKOLL.CHAT_LISTENER)) {
			for (int i = 0; i < listeners.size(); i++) {
				if (listeners.get(i) instanceof ChatListener) {
					listeners.remove(i);
					break;
				}
			}
		} else if (l.startsWith(PROTOKOLL.ALL_LISTENERS)) {
			listeners = new ArrayList<StandardListener>();
			System.gc();
		}
		Logger.i(TAG, "Removed " + (listenercount - listeners.size())
				+ " listeners");
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
					try {
						Logger.i(TAG, "Received Message: " + msg);// TODO Remove
						if (msg.startsWith(PROTOKOLL.REGISTER_COMMAND_BEGIN)) {
							try {
								String listener = msg.replace(
										PROTOKOLL.REGISTER_COMMAND_BEGIN, "")
										.trim();
								onRegisterMessage(listener);
							} catch (Exception e) {
								Logger.e(
										TAG,
										"Failed to parse listener from register command",
										e);
								send(PROTOKOLL.ERROR + " " + "Failed to register listener. ["+msg+"]");
							}

						} else if (msg
								.startsWith(PROTOKOLL.UNREGISTER_COMMAND_BEGIN)) {
							try {
								String listener = msg.replace(
										PROTOKOLL.UNREGISTER_COMMAND_BEGIN, "")
										.trim();
								onUnregisterMessage(listener);
							} catch (Exception e) {
								Logger.e(
										TAG,
										"Failed to parse listener from unregister command",
										e);
								send(PROTOKOLL.ERROR + " " + "Failed to unregister listener. ["+msg+"]");
							}
						} else if (msg.startsWith(PROTOKOLL.CONNECT)) {
							onConnectMessage();
						} else if (msg.startsWith(PROTOKOLL.LOGIN)) {
							String params = msg.substring(PROTOKOLL.LOGIN
									.length() + 1);
							onLoginMessage(params);

						}

						else if (msg.startsWith(PROTOKOLL.DISCONNECT)) {
							onDisconnectMessage();
						} else {
							send(PROTOKOLL.UNKNOWN+" ["+msg+"]");
						}
					} catch (Exception e) {
						Logger.e(TAG, "Failed to process message", e);
					}

				}

			}

		}

	}

	/**
	 * Sends a message to the client in the same thread
	 * 
	 * @param s
	 *            Message
	 */
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

}
