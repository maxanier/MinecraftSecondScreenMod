package de.maxgb.minecraft.second_screen;

import net.minecraftforge.common.config.Configuration;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.event.FMLServerStoppingEvent;
import de.maxgb.minecraft.second_screen.util.Constants;
import de.maxgb.minecraft.second_screen.util.Logger;
import de.maxgb.minecraft.second_screen.world.RegisterRedstoneInfoCommand;

@Mod(modid = Constants.MOD_ID, name = Constants.NAME, version = Constants.VERSION, dependencies = "required-after:FML")
public class SecondScreenMod {

	public static SocketListener socketListener;
	public static int port;
	public static String hostname;
	private static int id = 0;

	public static int id() {
		return id++;
	}

	private final String TAG = "Main";

	@EventHandler
	public void init(FMLInitializationEvent event) {

	}

	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {

		Logger.init(event.getModLog());

		Logger.d(TAG, "Test");
		Configuration config = new Configuration(
				event.getSuggestedConfigurationFile());
		
		Configs.load(config);
		

	}

	@EventHandler
	public void serverStarting(FMLServerStartingEvent e) {
		e.registerServerCommand( new RegisterRedstoneInfoCommand());
		hostname = Configs.hostname;
		port = Configs.port;
		start();
	}

	@EventHandler
	public void serverStopping(FMLServerStoppingEvent e) {
		stop();
	}

	private void start() {
		Logger.i(TAG, "Starting SecondScreenMod");
		socketListener = new SocketListener();
	}

	private void stop() {
		Logger.i(TAG, "Stopping SecondScreenMod");
		socketListener.stop();
		socketListener = null;
	}

}
