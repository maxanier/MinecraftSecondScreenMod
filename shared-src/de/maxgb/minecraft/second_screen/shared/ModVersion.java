package de.maxgb.minecraft.second_screen.shared;



/**
 * Version class which should be used in the clients to determine which features are available with the mod feature id which is sent on connect
 * @author Max
 *
 */
public class ModVersion {
	public static ModVersion getVersion(int feature_id) {
		//Set to newest known version if higher than known
		if(feature_id>3){
			feature_id=3;
		}
		ModVersion v = new ModVersion();
		//Set Features
		if(feature_id>=3){
			v.chat=true;
			v.newest=true;
		}
		if(feature_id>=2){
			v.inventoryinfo_simple=true;
			
		}
		if(feature_id>=1){
			v.versionid = feature_id;
			v.playerinfo = true;
			v.serverinfo = true;
			v.worldinfo = true;
		}
		return v;
	}
	private boolean playerinfo = false;
	private boolean serverinfo = false;
	private boolean worldinfo = false;
	private boolean inventoryinfo_simple = false;
	private boolean chat =false;
	private int versionid = 0;
	private boolean newest = false;


	private ModVersion() {

	}

	public int getVersionId() {
		return versionid;
	}

	public boolean isInventoryinfo() {
		return inventoryinfo_simple;
	}

	public boolean isNewest() {
		return newest;
	}

	public boolean isPlayerinfo() {
		return playerinfo;
	}

	public boolean isServerinfo() {
		return serverinfo;
	}

	public boolean isWorldinfo() {
		return worldinfo;
	}
	
	public boolean isChat(){
		return chat;
	}
}
