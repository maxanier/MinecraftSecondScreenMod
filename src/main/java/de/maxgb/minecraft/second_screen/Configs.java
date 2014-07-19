package de.maxgb.minecraft.second_screen;

import java.net.InetAddress;
import java.net.UnknownHostException;

import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import de.maxgb.minecraft.second_screen.util.Logger;

public class Configs {

	public static String hostname;
	public static int port;
	public static int server_info_update_time;
	public static int world_info_update_time;
	public static int player_info_update_time;
	public static int chat_update_time;
	public static boolean auth_required;
	public static boolean obs_publ_admin;
	private final static String TAG = "Configs";

	public static void load(Configuration config) {
		config.load();
		
		//Categegories
		ConfigCategory update_times = config.getCategory("update times");
		update_times
				.setComment("How often are the information updated (Measured in ServerTicks, just try out some values).");
		ConfigCategory con = config.getCategory("connection settings");
		con.setComment("On what Ip and port should the mod listen");
		
		//Connection settings
		try {
			hostname = config.get(con.getQualifiedName(), "hostname",
					InetAddress.getLocalHost().getHostAddress()).getString();
		} catch (UnknownHostException e) {
			Logger.e(TAG, "Failed to retrieve host address" + e);
			hostname = "localhost";
		}
		port = config.get(con.getQualifiedName(), "port", 25566).getInt();

		//Update times
		Property prop = config.get(update_times.getQualifiedName(),
				"server_info_update_time", 500);
		prop.comment = "General server info";
		server_info_update_time = prop.getInt();

		prop = config.get(update_times.getQualifiedName(),
				"world_info_update_time", 200);
		prop.comment = "World info";
		world_info_update_time = prop.getInt();

		prop = config.get(update_times.getQualifiedName(),
				"player_info_update_time", 40);
		prop.comment = "Player info";
		player_info_update_time = prop.getInt();

		prop = config.get(update_times.getQualifiedName(), "chat_update_time",
				10);
		prop.comment = "Chat";
		chat_update_time = prop.getInt();
		
		//General configs

		prop = config.get(Configuration.CATEGORY_GENERAL, "auth_required",
				false);
		prop.comment = "Whether the second screen user need to login with username and password, which can be set in game";
		auth_required = prop.getBoolean(true);
		
		prop = config.get(Configuration.CATEGORY_GENERAL, "public_observer_admin_only", false);
		prop.comment="If true, only admins can create public block observations";
		obs_publ_admin= prop.getBoolean(false);

		if (config.hasChanged()) {
			config.save();
		}
	}

}
