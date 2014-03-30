package de.maxgb.minecraft.second_screen.shared;



public class PROTOKOLL {

	// Listener managment commands
	public static final String REGISTER_COMMAND_BEGIN = "r-";
	public static final String UNREGISTER_COMMAND_BEGIN = "ur-";

	// Listeners
	public static final String S_PLAYERINFO_LISTENER = "spil";
	public static final String PLAYER_INVENTORY_LISTENER = "pil";
	public static final String SERVER_INFO_LISTENER = "sil";
	public static final String WORLD_INFO_LISTENER = "wil";
	public static final String CHAT_LISTENER = "cl";
	public static final String ALL_LISTENERS = "all";
	
	//Action commands
	public static final String ACTION_COMMAND_BEGIN ="do-";
	public static final String ACTION_RESULT_BEGIN="doresult-";
	
	/**
	 * Chat message action. Followed by Json string containing "msg"(String)
	 */
	public static final String A_CHAT_MSG="cm";
	public static final String A_RED_CONTROL="rc";

	// Connection

	/**
	 * Connect message Connection procedure: C-S: CONNECT S-C: CONNECT_RESULT
	 * C-S: LOGIN (if auth is required, with password hash) S-C: LOGIN_RESULT
	 * S-C or C-S DISCONNECT or SERVER_STOPPING
	 * All messages which contain success=0 should contain an "error" field with an error message
	 */
	public static final String CONNECT = "connecting";

	/**
	 * Connect result send to the client. Followed by Json string containing:
	 * "versionid"(int),"minecraftversion"(String),"login_required"(boolean)
	 */
	public static final String CONNECT_RESULT = "conncted";
	/**
	 * Login message send to the server. Followed by Json string containing:
	 * "username"(String),"password"(md5 hash of password as
	 * String),"clientid"(String),"clientversion"(int)
	 */
	public static final String LOGIN = "login";
	/**
	 * Login result message send to the client. Followed by Json string
	 * containing: "success"(int [0/1]),"appupdate"(boolean)
	 */
	public static final String LOGIN_RESULT = "login_result";

	public static final String SERVER_STOPPING = "server_stopping";
	public static final String DISCONNECT = "disconnect";

	// Etc
	/**
	 * If the message is unknown. Followed by " ["+orginalmessage+"]"
	 */
	public static final String UNKNOWN = "unknown";
	
	/**
	 * If an error occured, while processing a message. Followed by error message and by " ["+orginalmessage+"]"
	 */
	public static final String ERROR = "error";
}
