package de.maxgb.minecraft.second_screen;

import de.maxgb.minecraft.second_screen.util.Constants;
import de.maxgb.minecraft.second_screen.util.Logger;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class Configs {

	public static final String CATEGORY_UPDATE_TIMES = "update times";
	public static final String CATEGORY_CONNECTION_SETTINGS = "connection settings";
	public static final String CATEGORY_GENERAL = Configuration.CATEGORY_GENERAL;
	public static String hostname;
	public static int port;
	public static int server_info_update_time;
	public static int world_info_update_time;
	public static int player_info_update_time;
	public static int chat_update_time;
	public static boolean auth_required;
	public static boolean obs_publ_admin;
	public static boolean debug_mode;
	public static Configuration config;
	private static String TAG = "Configs";

	/**
	 * Creates a Configuration from the given config file and loads configs afterwards
	 * @param configFile
	 */
	public static void init(File configFile) {
		if (config == null) {
			config = new Configuration(configFile);
		}

		loadConfiguration();

	}

	/**
	 * Loads/refreshes the configuration and adds comments if there aren't any
	 * {@link #init(File) init} has to be called once before using this
	 */
	public static void loadConfiguration() {

		// Categegories
		ConfigCategory update_times = config.getCategory(CATEGORY_UPDATE_TIMES);
		update_times
				.setComment("How often are the information updated (Measured in ServerTicks, just try out some values).");
		ConfigCategory con = config.getCategory(CATEGORY_CONNECTION_SETTINGS);
		con.setComment("On what Ip and port should the mod listen");

		// Connection settings
		try {
			hostname = config.get(con.getQualifiedName(), "hostname", InetAddress.getLocalHost().getHostAddress())
					.getString();
		} catch (UnknownHostException e) {
			Logger.e(TAG, "Failed to retrieve host address" + e);
			hostname = "localhost";
		}
		port = config.get(con.getQualifiedName(), "port", 25566).getInt();

		// Update times
		Property prop = config.get(update_times.getQualifiedName(), "server_info_update_time", 500);
        prop.setComment("General server info");
        server_info_update_time = prop.getInt();

		prop = config.get(update_times.getQualifiedName(), "world_info_update_time", 200);
        prop.setComment("World info");
        world_info_update_time = prop.getInt();

		prop = config.get(update_times.getQualifiedName(), "player_info_update_time", 40);
        prop.setComment("Player info");
        player_info_update_time = prop.getInt();

		prop = config.get(update_times.getQualifiedName(), "chat_update_time", 10);
        prop.setComment("Chat");
        chat_update_time = prop.getInt();

		// General configs

		prop = config.get(CATEGORY_GENERAL, "auth_required", false);
        prop.setComment("Whether the second screen user need to login with username and password, which can be set in game");
        auth_required = prop.getBoolean(true);

		prop = config.get(CATEGORY_GENERAL, "public_observer_admin_only", false);
        prop.setComment("If true, only admins can create public block observations");
        obs_publ_admin = prop.getBoolean(false);
		
		prop = config.get(CATEGORY_GENERAL, "debug_mode", false);
        prop.setComment("Enable logging debug messages to file");
        debug_mode=prop.getBoolean(false);

		if (config.hasChanged()) {
			config.save();
		}
	}

	@SubscribeEvent
	public void onConfigurationChanged(ConfigChangedEvent.OnConfigChangedEvent e) {
        if (e.getModID().equalsIgnoreCase(Constants.MOD_ID)) {
            // Resync configs
			Logger.i(TAG, "Configuration has changed");
			Configs.loadConfiguration();
		}
	}

}
