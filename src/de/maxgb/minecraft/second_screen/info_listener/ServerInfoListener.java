package de.maxgb.minecraft.second_screen.info_listener;

import org.json.JSONObject;

import de.maxgb.minecraft.second_screen.StandardListener;
import de.maxgb.minecraft.second_screen.util.PROTOKOLL;

public class ServerInfoListener extends StandardListener {

	public ServerInfoListener(String params) {
		super(params);
		everyTick = 2000;// TODO make config related
	}

	@Override
	public String update() {
		JSONObject info = new JSONObject();
		info.put("motd", server.getMOTD());
		info.put("maxplayer", server.getMaxPlayers());
		info.put("usernames", server.getAllUsernames());
		info.put("playercount", server.getCurrentPlayerCount());
		return PROTOKOLL.SERVER_INFO_LISTENER + ":" + info.toString();
	}

}
