package de.maxgb.minecraft.second_screen;

import net.minecraftforge.common.config.Configuration;

public class Configs {

	public static String hostname;
	public static int port;
	public static void load(Configuration config) {
		config.load();
		hostname=config.get(Configuration.CATEGORY_GENERAL, "hostname", "localhost").getString();
		port=config.get(Configuration.CATEGORY_GENERAL, "port", 25565).getInt();
		config.save();
	}

}
