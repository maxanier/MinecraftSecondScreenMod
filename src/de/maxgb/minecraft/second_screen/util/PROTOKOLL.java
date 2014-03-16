package de.maxgb.minecraft.second_screen.util;

public class PROTOKOLL {

	// Listener
	public static final String REGISTER_S_PLAYERINFO_LISTENER = "r-spil"; // <username>
	public static final String UNREGISTER_S_PLAYERINFO_LISTENER = "ur-spil"; // <username>
	public static final String UNREGISTER_ALL_LISTENER = "ur-all";
	public static final String S_PLAYERINFO_LISTENER = "spil";
	public static final String REGISTER_PLAYER_INVENTORY_LISTENER = "r-pil"; // <username>
	public static final String UNREGISTER_PLAYER_INVENTORY_LISTENER = "ur-pil";// <username>
	public static final String PLAYER_INVENTORY_LISTENER = "pil";
	public static final String REGISTER_SERVER_INFO_LISTENER = "r-sil";
	public static final String UNREGISTER_SERVER_INFO_LISTENER = "ur-sil";
	public static final String SERVER_INFO_LISTENER = "sil";
	public static final String REGISTER_WORLD_INFO_LISTENER = "r-wil";
	public static final String UNREGISTER_WORLD_INFO_LISTENER = "ur-wil";
	public static final String WORLD_INFO_LISTENER = "wil";

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

	public static final String SERVER_STOPPING = "server_stopping";

	// Etc
	public static final String UNKNOWN = "unknown";
}
