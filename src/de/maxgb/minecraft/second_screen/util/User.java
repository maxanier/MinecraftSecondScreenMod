package de.maxgb.minecraft.second_screen.util;

import net.minecraftforge.common.config.Configuration;

public class User {
	public final String username;
	private String password;
	
	public User(String username,String password){
		this.username=username;
		this.password=password;
	}
	
	public String getPassword(){
		return password;
	}
	
	public void setPassword(String p){
		if(p==null){
			p="";
		}
		password=p;
	}
	
	public static User readFromConfig(Configuration config){
		String name=config.get(Configuration.CATEGORY_GENERAL, "username", "").getString();
		String pass =config.get(Configuration.CATEGORY_GENERAL, "password","").getString();
		if(pass.equals("")||name.equals("")){
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
