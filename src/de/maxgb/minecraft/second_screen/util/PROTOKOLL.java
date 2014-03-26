package de.maxgb.minecraft.second_screen.util;

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

	// Connection
	public static final String DISCONNECT = "disconnect";

	public static final String CONNECT = "connecting";
	public static final String CONNECT_RESULT = "conncted";// <json> (json
															// contains
															// versionid)
	public static final String LOGIN = "login";// <json> (json contains
												// username,password)
	public static final String LOGIN_RESULT = "login_result";// <json> (json
																// contains
																// success
																// (0/1),error
																// if success =
																// 0,
																// user(userobject))
	public static final String ERROR = "error";

	public static final String SERVER_STOPPING = "server_stopping";

	// Etc
	public static final String UNKNOWN = "unknown";
}
