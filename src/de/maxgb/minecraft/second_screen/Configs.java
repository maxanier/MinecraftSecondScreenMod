package de.maxgb.minecraft.second_screen;

import java.net.Inet4Address;
import java.net.UnknownHostException;

import de.maxgb.minecraft.second_screen.util.Logger;

import net.minecraftforge.common.config.Configuration;

public class Configs {

	public static String hostname;
	public static int port;
	private final static String TAG="Configs";
	
	public static void load(Configuration config) {
		config.load();
		try {
			hostname=config.get(Configuration.CATEGORY_GENERAL, "hostname", Inet4Address.getLocalHost().getHostAddress()).getString();
		} catch (UnknownHostException e) {
			Logger.e(TAG, "Failed to retrieve host address"+e);
			hostname="localhost";
		}
		port=config.get(Configuration.CATEGORY_GENERAL, "port", 25566).getInt();
		config.save();
	}

}
