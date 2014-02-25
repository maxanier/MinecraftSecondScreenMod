package de.maxgb.minecraft.second_screen;



import java.util.HashMap;

import org.apache.logging.log4j.Level;


import net.minecraftforge.common.config.Configuration;


import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.event.FMLServerStoppingEvent;

import de.maxgb.minecraft.second_screen.util.Constants;
import de.maxgb.minecraft.second_screen.util.Logger;


@Mod(modid = Constants.MOD_ID, name=Constants.NAME,version = Constants.VERSION,dependencies="required-after:FML")
public class SecondScreenMod
{

    public static SocketListener socketListener;
    public static int port;
    public static String hostname;
    private static int id=0;
    private final String TAG="Main";
    

    
    
	@EventHandler
	public void preInit(FMLPreInitializationEvent event){
		
		Logger.init(event.getModLog());
		
		Logger.d(TAG, "Test");
		Configuration config = new Configuration(
				event.getSuggestedConfigurationFile());
		Configs.load(config);
		
	}
	
    @EventHandler
    public void init(FMLInitializationEvent event)
    {
    	
    }
    
    @EventHandler
    public void serverStarting(FMLServerStartingEvent e){
    	hostname="localhost";
    	port=25565;
    	start();
    }
    
    @EventHandler
    public void serverStopping(FMLServerStoppingEvent e){
    	stop();
    }
    
    private void start(){
    	Logger.i(TAG, "Starting SecondScreenMod");
    	socketListener=new SocketListener();
    }
    
    private void stop(){
    	Logger.i(TAG, "Stopping SecondScreenMod");
    	socketListener.stop();
    	socketListener=null;
    }

	public static int id() {
		return id++;
	}

}

