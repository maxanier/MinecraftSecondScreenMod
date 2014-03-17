package de.maxgb.minecraft.second_screen;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import de.maxgb.minecraft.second_screen.util.Logger;

public class Configs {

	public static String hostname;
	public static int port;
	public static int server_info_update_time;
	public static int world_info_update_time;
	public static int player_info_update_time;
	private final static String TAG = "Configs";

	public static void load(Configuration config) {
		config.load();
		try {
			hostname = config.get(Configuration.CATEGORY_GENERAL, "hostname",
					InetAddress.getLocalHost().getHostAddress()).getString();
		} catch (UnknownHostException e) {
			Logger.e(TAG, "Failed to retrieve host address" + e);
			hostname = "localhost";
		}
		port = config.get(Configuration.CATEGORY_GENERAL, "port", 25566)
				.getInt();
		
		Property prop = config.get(Configuration.CATEGORY_GENERAL, "server_info_update_time", 500);
		prop.comment="How often is the server info updated. (Measured in ServerTicks, just try out some values)";
		server_info_update_time = prop.getInt();
		
		prop=config.get(Configuration.CATEGORY_GENERAL, "world_info_update_time", 200);
		prop.comment="How often is the world info updated.  (Measured in ServerTicks, just try out some values)";
		world_info_update_time = prop.getInt();
		
		prop=config.get(Configuration.CATEGORY_GENERAL, "player_info_update_time", 40);
		prop.comment="How often is the player info updated.  (Measured in ServerTicks, just try out some values)";
		player_info_update_time = prop.getInt();
		
		if(config.hasChanged()){
			config.save();
		}
	}

}
