package de.maxgb.minecraft.second_screen.shared;

/**
 * Version class which should be used in the clients to determine which features
 * are available with the mod feature id which is sent on connect
 * 
 * @author Max
 * 
 */
public class ModVersion {
	public static ModVersion getVersion(int feature_id) {
		// Set to newest known version if higher than known
		if (feature_id > 5) {
			feature_id = 5;
		}
		ModVersion v = new ModVersion();
		// Set Features
		if(feature_id>=5){
			v.getchat=true;
		}
		if (feature_id >= 4) {
			v.sendchat = true;
			v.controlRed = true;
			v.newest = true;

		}
		if (feature_id >= 3) {
			v.chat = true;

		}
		if (feature_id >= 2) {
			v.inventoryinfo_simple = true;

		}
		if (feature_id >= 1) {
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
	private boolean chat = false;
	private int versionid = 0;
	private boolean newest = false;
	private boolean sendchat = false;
	private boolean controlRed = false;
	private boolean getchat=false;

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

	public boolean isChat() {
		return chat;
	}

	public boolean isSendChat() {
		return sendchat;
	}

	public boolean isControlRed() {
		return this.controlRed;
	}
	
	public boolean isGetChat(){
		return this.getchat;
	}
}
