package de.maxgb.minecraft.second_screen.util;

import net.minecraftforge.common.config.Configuration;

public class User {
	public final String username;
	private int password;
	
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
}
