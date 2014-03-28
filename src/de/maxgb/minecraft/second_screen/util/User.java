package de.maxgb.minecraft.second_screen.util;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.config.Configuration;

public class User {
	public final String username;
	private int password;
	private final String TAG="UserObject";
	
	public User(String username,int password){
		this.username=username;
		this.password=password;
	}
	
	public int getPassword(){
		return password;
	}
	
	public void setPassword(int p){

		password=p;
	}
	
	public static User readFromConfig(Configuration config){
		String name=config.get(Configuration.CATEGORY_GENERAL, "username", "").getString();
		int pass =config.get(Configuration.CATEGORY_GENERAL, "password",0).getInt();
		if(pass==0){
			return null;
		}
		return new User(name,pass);
	}
	public boolean saveToConfig(Configuration config){
		config.get(Configuration.CATEGORY_GENERAL, "username", username).set(username);
		config.get(Configuration.CATEGORY_GENERAL, "password", password).set(password);

		if(config.hasChanged()){
			config.save();
		}
		return true;
	}
	public EntityPlayerMP getPlayer(MinecraftServer s){
		if(s==null){
			Logger.w(TAG, "Server null, cant find user");
			return null;
		}
		return s.getConfigurationManager().getPlayerForUsername(username);
	}
}
