package de.maxgb.minecraft.second_screen;

import net.minecraftforge.common.config.Configuration;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.event.FMLServerStoppingEvent;
import de.maxgb.minecraft.second_screen.commands.GetIPCommand;
import de.maxgb.minecraft.second_screen.commands.GetMSSPortCommand;
import de.maxgb.minecraft.second_screen.commands.RegisterRedstoneInfoCommand;
import de.maxgb.minecraft.second_screen.data.DataStorageDriver;
import de.maxgb.minecraft.second_screen.util.Constants;
import de.maxgb.minecraft.second_screen.util.Logger;
import de.maxgb.minecraft.second_screen.world.ObservingRegistry;

@Mod(modid = Constants.MOD_ID, name = Constants.NAME, version = Constants.VERSION, dependencies = "required-after:FML")
public class SecondScreenMod {

	public static SocketListener socketListener;
	public static int port;
	public static String hostname;
	private static int id = 0;
	public static boolean connected;
	public static String error;

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

		hostname = Configs.hostname;
		port = Configs.port;

		DataStorageDriver.setWorldName(e.getServer().getFolderName());

		ObservingRegistry.loadObservingMap();

		e.registerServerCommand(new RegisterRedstoneInfoCommand());
		e.registerServerCommand(new GetIPCommand());
		e.registerServerCommand(new GetMSSPortCommand());

		start();
	}

	@EventHandler
	public void serverStopping(FMLServerStoppingEvent e) {
		stop();
		ObservingRegistry.saveObservingMap();
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
