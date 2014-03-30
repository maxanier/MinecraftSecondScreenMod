package de.maxgb.minecraft.second_screen.util;

import java.util.HashMap;
import java.util.Map.Entry;

import de.maxgb.minecraft.second_screen.shared.ClientVersion;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

public class User {
	
	public final String username;
	private int password;
	private final String TAG="UserObject";
	private static final String perm_category="permissions";
	public ClientVersion.ClientInfo getClient() {
		return client;
	}

	public void setClient(ClientVersion.ClientInfo client) {
		this.client = client;
	}
	private HashMap<String,Boolean> perm;
	private boolean all_allowed;
	private ClientVersion.ClientInfo client;
	
	public User(String username,int password,HashMap<String,Boolean> perm){
		this.username=username;
		this.password=password;
		if(perm==null){
			perm=new HashMap<String,Boolean>();
		}
		this.perm=perm;
	}
	
	public User(String username,int password){
		this(username,password,null);
	}
	
	public int getPassword(){
		return password;
	}
	
	public void setPassword(int p){

		password=p;
	}
	/**
	 * Give the user all permissions
	 * @param allowed
	 */
	public void setAllAllowed(boolean allowed){
		this.all_allowed=allowed;
	}
	
	/**
	 * Checks if the user has the permission
	 * @param perm Permission which should be checked
	 * @param default_value Default value, if nothing is set
	 * @return
	 */
	public boolean isAllowedTo(String perm,boolean default_value){
		if(all_allowed){
			return true;
		}
		if(this.perm.containsKey(perm)){
			return this.perm.get(perm);
		}
		else{
			this.perm.put(perm, default_value);
			return default_value;
		}
	}
	
	public static User readFromConfig(Configuration config){
		String name=config.get(Configuration.CATEGORY_GENERAL, "username", "").getString();
		int pass =config.get(Configuration.CATEGORY_GENERAL, "password",0).getInt();
		if(pass==0){
			return null;
		}
		HashMap<String,Boolean> perm=new HashMap<String, Boolean>();
		ConfigCategory p=config.getCategory(perm_category);
		for(Entry<String,Property> e :p.entrySet()){
			perm.put(e.getKey(), e.getValue().getBoolean(false));
		}
		return new User(name,pass,perm);
	}
	public boolean saveToConfig(Configuration config){
		config.get(Configuration.CATEGORY_GENERAL, "username", username).set(username);
		config.get(Configuration.CATEGORY_GENERAL, "password", password).set(password);
		for(Entry<String,Boolean> e : perm.entrySet()){
			config.get(perm_category, e.getKey(), e.getValue()).set( e.getValue());
		}
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
