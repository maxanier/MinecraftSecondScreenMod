package de.maxgb.minecraft.second_screen;

import net.minecraftforge.common.config.Configuration;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.event.FMLServerStoppingEvent;
import de.maxgb.minecraft.second_screen.actions.ActionManager;
import de.maxgb.minecraft.second_screen.commands.GetIPCommand;
import de.maxgb.minecraft.second_screen.commands.GetMSSPortCommand;
import de.maxgb.minecraft.second_screen.commands.MssCommand;
import de.maxgb.minecraft.second_screen.commands.RegisterObserverCommand;
import de.maxgb.minecraft.second_screen.commands.RegisterRedstoneInfoCommand;
import de.maxgb.minecraft.second_screen.commands.RegisterUserCommand;
import de.maxgb.minecraft.second_screen.commands.TestCommand;
import de.maxgb.minecraft.second_screen.data.DataStorageDriver;
import de.maxgb.minecraft.second_screen.data.UserManager;
import de.maxgb.minecraft.second_screen.util.Constants;
import de.maxgb.minecraft.second_screen.util.Logger;
import de.maxgb.minecraft.second_screen.world.ObservingManager;

@Mod(modid = Constants.MOD_ID, name = Constants.NAME, version = Constants.VERSION, dependencies = "required-after:FML")
public class SecondScreenMod {

	public static WebSocketListener webSocketListener;
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

		Configuration config = new Configuration(
				event.getSuggestedConfigurationFile());

		Configs.load(config);

	}

	@EventHandler
	public void serverStarting(FMLServerStartingEvent e) {

		hostname = Configs.hostname;
		port = Configs.port;

		DataStorageDriver.setWorldName(e.getServer().getFolderName());

		ObservingManager.loadObservingMap();

		UserManager.loadUsers();

		ActionManager.registerStandardActions();

		MssCommand mssc = new MssCommand();
		mssc.addSubCommand(new RegisterRedstoneInfoCommand());
		mssc.addSubCommand(new RegisterUserCommand());
		mssc.addSubCommand(new RegisterObserverCommand());
		e.registerServerCommand(mssc);
		e.registerServerCommand(new GetIPCommand());
		e.registerServerCommand(new GetMSSPortCommand());
		e.registerServerCommand(new TestCommand());

		start();
	}

	@EventHandler
	public void serverStopping(FMLServerStoppingEvent e) {
		stop();
		ObservingManager.saveObservingMap();
		UserManager.saveUsers();
		ActionManager.removeAllActions();
	}

	private void start() {
		Logger.i(TAG, "Starting SecondScreenMod");

		webSocketListener = new WebSocketListener();
		webSocketListener.start();
	}

	private void stop() {
		Logger.i(TAG, "Stopping SecondScreenMod");
		webSocketListener.stop();

		webSocketListener=null;
	}

}
