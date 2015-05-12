package de.maxgb.minecraft.second_screen;

import java.net.InetSocketAddress;
import java.util.ArrayList;

import org.java_websocket.WebSocket;
import org.json.JSONObject;

import de.maxgb.minecraft.second_screen.actions.ActionManager;
import de.maxgb.minecraft.second_screen.actions.ActionManager.ActionResultListener;
import de.maxgb.minecraft.second_screen.data.UserManager;
import de.maxgb.minecraft.second_screen.info_listener.ChatListener;
import de.maxgb.minecraft.second_screen.info_listener.PlayerInfoListener;
import de.maxgb.minecraft.second_screen.info_listener.PlayerInventoryListener;
import de.maxgb.minecraft.second_screen.info_listener.ServerInfoListener;
import de.maxgb.minecraft.second_screen.info_listener.WorldInfoListener;
import de.maxgb.minecraft.second_screen.shared.ClientVersion;
import de.maxgb.minecraft.second_screen.shared.PROTOKOLL;
import de.maxgb.minecraft.second_screen.util.Constants;
import de.maxgb.minecraft.second_screen.util.ForceUpdateEvent;
import de.maxgb.minecraft.second_screen.util.Logger;
import de.maxgb.minecraft.second_screen.util.User;

/**
 * Handles the communication with a specific client
 * @author Max
 *
 */
public class WebSocketHandler implements ActionResultListener {
	private WebSocket socket;
	public final InetSocketAddress address;
	private static String TAG = "WebSocketHandler-";
	private ArrayList<StandardListener> listeners;
	private User user;
	/**
	 * Indicates if the Handler will be removed. Should only be modified by {@link #markForRemoval()}
	 */
	private boolean gettingRemoved;

	public boolean isGettingRemoved() {
		return gettingRemoved;
	}

	public WebSocketHandler(WebSocket socket) {
		gettingRemoved=false;
		address = socket.getRemoteSocketAddress();
		this.socket = socket;
		TAG += WebSocketListener.getNewHandlerID();
		listeners = new ArrayList<StandardListener>();
	}

	public void close() {
		for(StandardListener l:listeners){
			l.onUnregister();
		}
		socket.close();

		this.markForRemoval();
		
		Logger.d(TAG, "Closing this listener "+address);

	}

	public void forceUpdate(ForceUpdateEvent e) {
		for (StandardListener l : listeners) {
			if (l.getClass().equals(e.listener)) {
				send(l.tick(true));
			}
		}
	}

	private void onActionMessage(String action, String params) {
		Logger.d(TAG, "Received action result message");
		// Check is user object is available and so if the user is authentified
		if (user == null) {

			send(PROTOKOLL.ERROR + "-" + "Login required. [" + PROTOKOLL.ACTION_COMMAND_BEGIN + action + "-" + params
					+ "]");
			Logger.w(TAG, "Cannot execute action before login. ");
			return;
		}

		JSONObject p = new JSONObject(params);
		if (!ActionManager.doAction(action, p, user, this)) {
			send(PROTOKOLL.ERROR + "-" + "Action not found. [" + PROTOKOLL.ACTION_COMMAND_BEGIN + action + "-" + params
					+ "]");
		}
	}

	@Override
	public void onActionResult(String command, JSONObject r) {
		send(PROTOKOLL.ACTION_RESULT_BEGIN + command + "-" + r.toString());

	}

	/**
	 * Handles the connect message
	 */
	private void onConnectMessage() {
		Logger.d(TAG, "Received connect message");
		JSONObject result = new JSONObject();
		result.put("versionid", Constants.FEATURE_VERSION);
		result.put("minecraftversion", Constants.MINECRAFT_VERSION);
		result.put("login_required", Configs.auth_required);

		send(PROTOKOLL.CONNECT_RESULT + "-" + result.toString());

	}

	/**
	 * Handles the disconnect message
	 */
	private void onDisconnectMessage() {
		Logger.d(TAG, "Received disconnect message");
		close();
	}

	/**
	 * Handles the login message
	 * 
	 * @param params
	 *            The parameter Json string
	 */
	private void onLoginMessage(String params) {
		Logger.d(TAG, "Received login message");
		JSONObject data = new JSONObject(params);

		if (!data.has("username")) {
			Logger.w(TAG, "Login message is missing username.");
			JSONObject result = new JSONObject();
			result.put("success", 0);
			result.put("error", "Username is missing");
			send(PROTOKOLL.LOGIN_RESULT + "-" + result.toString());
		}
		String username = data.getString("username");

		if (Configs.auth_required) {
			if (!data.has("password")) {
				Logger.w(TAG, "Login message is missing password. It is set to required in the config options");
				JSONObject result = new JSONObject();
				result.put("success", 0);
				result.put("error", "Password required");
				send(PROTOKOLL.LOGIN_RESULT + "-" + result.toString());

				return;
			} else if (!UserManager.auth(username, data.getInt("password"))) {
				Logger.w(TAG, "Authentification failed. Username or password is wrong");
				JSONObject result = new JSONObject();
				result.put("success", 0);
				result.put("error", "Username or password wrong");
				send(PROTOKOLL.LOGIN_RESULT + "-" + result.toString());
				return;
			}
		}
		user = UserManager.getUser(username);
		JSONObject result = new JSONObject();
		result.put("success", 1);

		if (data.has("clientid") && data.has("clientversion")) {
			String id = data.getString("clientid");
			int v = data.getInt("clientversion");
			Logger.i(TAG, "Clientinfo: " + id + " Version: " + v);
			result.put("clientupdate", ClientVersion.isUpdateAvailable(id, v));
			result.put("clientupdatenecessary", ClientVersion.isUpdateNecessary(id, v));
			user.setClient(new ClientVersion.ClientInfo(id, v));
		} else {
			Logger.w(TAG, "Login message is missing client information.");
		}

		send(PROTOKOLL.LOGIN_RESULT + "-" + result.toString());
		Logger.i(TAG, "Sucessfully logged in user " + username);
	}

	public void onMessage(String msg) {
		try {
			Logger.i(TAG, "Received Message: " + msg);// TODO Remove
			if (msg.trim().startsWith(PROTOKOLL.REGISTER_COMMAND_BEGIN)) {
				try {
					String listener = msg.replace(PROTOKOLL.REGISTER_COMMAND_BEGIN, "").trim();
					onRegisterMessage(listener);
				} catch (Exception e) {
					Logger.e(TAG, "Failed to parse listener from register command", e);
					send(PROTOKOLL.ERROR + "-" + "Failed to register listener. [" + msg + "]");
				}

			} else if (msg.trim().startsWith(PROTOKOLL.UNREGISTER_COMMAND_BEGIN)) {
				try {
					String listener = msg.replace(PROTOKOLL.UNREGISTER_COMMAND_BEGIN, "").trim();
					onUnregisterMessage(listener);
				} catch (Exception e) {
					Logger.e(TAG, "Failed to parse listener from unregister command", e);
					send(PROTOKOLL.ERROR + "-" + "Failed to unregister listener. [" + msg + "]");
				}
			} else if (msg.trim().startsWith(PROTOKOLL.ACTION_COMMAND_BEGIN)) {
				try {
					String s = msg.replace(PROTOKOLL.ACTION_COMMAND_BEGIN, "");
					String action = s.substring(0, s.indexOf('-'));
					String params = s.substring(s.indexOf('-') + 1);
					onActionMessage(action, params);
				} catch (Exception e) {
					Logger.e(TAG, "Failed processing action command", e);
					send(PROTOKOLL.ERROR + "-" + "Failed processing action command. [" + msg + "]");
				}
			} else if (msg.trim().startsWith(PROTOKOLL.CONNECT)) {

				onConnectMessage();
			} else if (msg.trim().startsWith(PROTOKOLL.LOGIN)) {
				String params = msg.substring(PROTOKOLL.LOGIN.length() + 1);
				onLoginMessage(params);

			}

			else if (msg.trim().startsWith(PROTOKOLL.DISCONNECT)) {
				onDisconnectMessage();
			} else {
				String m="";
				for(int i=0;i<msg.length();i++){
					m+="'"+msg.charAt(i)+"',";
				}
				Logger.d(TAG, "Did not find any matching command in the protokoll for "+m);
				send(PROTOKOLL.UNKNOWN + " [" + msg + "]");
			}
		} catch (Exception e) {
			Logger.e(TAG, "Failed to process message", e);
			send(PROTOKOLL.ERROR + "-" + "Failed to process message. [" + msg + "]");
		}
		finally{
			Logger.d(TAG, "Processed message");
		}

	}

	/**
	 * Handles the register listener message
	 * 
	 * @param l
	 *            The string representation of the listener which should be
	 *            registered
	 */
	private void onRegisterMessage(final String l) {
		Logger.d(TAG, "Received register message");
		// Check is user object is available and so if the user is authentified
		if (user == null) {

			send(PROTOKOLL.ERROR + "-" + "Login required. [" + PROTOKOLL.REGISTER_COMMAND_BEGIN + l + "]");
			Logger.w(TAG, "Cannot register a listener before login.");
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
		Logger.d(TAG, "Received unregister message");
		int listenercount = listeners.size();
		if (l.startsWith(PROTOKOLL.S_PLAYERINFO_LISTENER)) {

			for (int i = 0; i < listeners.size(); i++) {
				StandardListener sl = listeners.get(i);
				if (sl instanceof PlayerInfoListener) {
					listeners.get(i).onUnregister();
					listeners.remove(i);
				}
			}

		} else if (l.startsWith(PROTOKOLL.PLAYER_INVENTORY_LISTENER)) {

			for (int i = 0; i < listeners.size(); i++) {
				StandardListener sl = listeners.get(i);
				if (sl instanceof PlayerInventoryListener) {
					listeners.get(i).onUnregister();
					listeners.remove(i);
				}
			}

		} else if (l.startsWith(PROTOKOLL.SERVER_INFO_LISTENER)) {
			for (int i = 0; i < listeners.size(); i++) {
				if (listeners.get(i) instanceof ServerInfoListener) {
					listeners.get(i).onUnregister();
					listeners.remove(i);
				}
			}
		} else if (l.startsWith(PROTOKOLL.WORLD_INFO_LISTENER)) {
			for (int i = 0; i < listeners.size(); i++) {
				if (listeners.get(i) instanceof WorldInfoListener) {
					listeners.get(i).onUnregister();
					listeners.remove(i);
				}
			}

		} else if (l.startsWith(PROTOKOLL.CHAT_LISTENER)) {
			for (int i = 0; i < listeners.size(); i++) {
				if (listeners.get(i) instanceof ChatListener) {
					listeners.get(i).onUnregister();
					listeners.remove(i);
					break;
				}
			}
		} else if (l.startsWith(PROTOKOLL.ALL_LISTENERS)) {
			for(StandardListener li:listeners){
				li.onUnregister();
			}
			listeners = new ArrayList<StandardListener>();
			System.gc();
		}
		Logger.i(TAG, "Removed " + (listenercount - listeners.size()) + " listeners");
	}

	private void send(String msg) {
		if (msg == null || isGettingRemoved()) {
			return;
		}
		Logger.d(TAG, "Sending "+msg);
		socket.send(msg);
		// TODO make async test etc
	}

	public void tick() {
		for (StandardListener l : listeners) {
			send(l.tick(false));
		}
	}
	
	/**
	 * Marks the method ready for removal
	 */
	public void markForRemoval(){
		Logger.d(TAG, "Marking ready for removal");
		this.gettingRemoved=true;
	}
}
