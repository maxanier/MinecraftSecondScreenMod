package de.maxgb.minecraft.second_screen;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.world.WorldEvent;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLInterModComms;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.event.FMLServerStoppingEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import de.maxgb.minecraft.second_screen.actions.ActionManager;
import de.maxgb.minecraft.second_screen.commands.GetIPCommand;
import de.maxgb.minecraft.second_screen.commands.GetMSSPortCommand;
import de.maxgb.minecraft.second_screen.commands.ListInterfacesCommand;
import de.maxgb.minecraft.second_screen.commands.TestCommand;
import de.maxgb.minecraft.second_screen.commands.mss_sub.MssCommand;
import de.maxgb.minecraft.second_screen.commands.mss_sub.RegisterObserverCommand;
import de.maxgb.minecraft.second_screen.commands.mss_sub.RegisterRedstoneInfoCommand;
import de.maxgb.minecraft.second_screen.commands.mss_sub.RegisterUserCommand;
import de.maxgb.minecraft.second_screen.data.ObservingManager;
import de.maxgb.minecraft.second_screen.data.UserManager;
import de.maxgb.minecraft.second_screen.util.Constants;
import de.maxgb.minecraft.second_screen.util.Helper;
import de.maxgb.minecraft.second_screen.util.Logger;

@Mod(modid = Constants.MOD_ID, name = Constants.NAME, version = Constants.VERSION, dependencies = "required-after:FML", guiFactory = Constants.GUI_FACTORY_CLASS, acceptableRemoteVersions="*")
public class SecondScreenMod {

	public WebSocketListener webSocketListener;
	public int port;
	public String hostname;
	public String latestOnlinePlayer="None since last restart";
	
	@Mod.Instance(Constants.MOD_ID)
	public static SecondScreenMod instance;

	private final String TAG = "Main";

	@EventHandler
	public void init(FMLInitializationEvent event) {
		MSSEventHandler handler = new MSSEventHandler();
		FMLCommonHandler.instance().bus().register(new Configs());
		FMLCommonHandler.instance().bus().register(handler);
		MinecraftForge.EVENT_BUS.register(handler);
	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent event) {

	}

	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {

		Configs.init(event.getSuggestedConfigurationFile());

		//Sends message to VersionChecker if installed
		FMLInterModComms.sendRuntimeMessage(Constants.MOD_ID, "VersionChecker", "addVersionCheck",
				Constants.UPDATE_FILE_LINK);

	}

	@EventHandler
	public void serverStarting(FMLServerStartingEvent e) {

		hostname = Configs.hostname;
		port = Configs.port;


		loadData();

		//Register actions
		ActionManager.registerStandardActions();

		//Register Commands
		MssCommand mssc = new MssCommand();
		mssc.addSubCommand(new RegisterRedstoneInfoCommand());
		mssc.addSubCommand(new RegisterUserCommand());
		mssc.addSubCommand(new RegisterObserverCommand());
		e.registerServerCommand(mssc);
		e.registerServerCommand(new GetIPCommand());
		e.registerServerCommand(new GetMSSPortCommand());
		e.registerServerCommand(new TestCommand());
		e.registerServerCommand(new ListInterfacesCommand());

		start();
	}

	@EventHandler
	public void serverStopping(FMLServerStoppingEvent e) {
		stop();
		saveData();
		ActionManager.removeAllActions();
	}

	/**
	 * Creates and starts the websocket 
	 */
	private void start() {
		Logger.i(TAG, "Starting SecondScreenMod");

		webSocketListener = new WebSocketListener();
		webSocketListener.start();
	}

	/**
	 * Stops the websocket
	 */
	private void stop() {
		Logger.i(TAG, "Stopping SecondScreenMod");
		webSocketListener.stop();

		webSocketListener = null;
	}
	
	/**
	 * Loads saved informations from files
	 */
	private void loadData(){
		//Loads observation files
		ObservingManager.loadObservingFile();

		//Load users
		UserManager.loadUsers();
	}
	
	/**
	 * Saves all informations to files
	 */
	public void saveData(){
		ObservingManager.saveObservingFile();
		UserManager.saveUsers();
	}
	
	

}
