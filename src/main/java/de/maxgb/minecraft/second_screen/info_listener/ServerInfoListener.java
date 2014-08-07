package de.maxgb.minecraft.second_screen.info_listener;

import org.json.JSONObject;

import de.maxgb.minecraft.second_screen.Configs;
import de.maxgb.minecraft.second_screen.SecondScreenMod;
import de.maxgb.minecraft.second_screen.StandardListener;
import de.maxgb.minecraft.second_screen.shared.PROTOKOLL;
import de.maxgb.minecraft.second_screen.util.Logger;
import de.maxgb.minecraft.second_screen.util.User;

/**
 * Listener which listens to server informations like online players
 * @author Max
 *
 */
public class ServerInfoListener extends StandardListener {

	public ServerInfoListener(User user) {
		super(user);
		everyTick = Configs.server_info_update_time;
	}

	@Override
	public String update() {
		JSONObject info = new JSONObject();
		info.put("motd", server.getMOTD());
		info.put("maxplayer", server.getMaxPlayers());
		info.put("usernames", server.getAllUsernames());
		Logger.i("ServerInfo", ""+server.getAllUsernames().length);
		if(server.getAllUsernames().length==0){
			info.put("lastonline", SecondScreenMod.instance.latestOnlinePlayer);
		}
		info.put("playercount", server.getCurrentPlayerCount());
		return PROTOKOLL.SERVER_INFO_LISTENER + "-" + info.toString();
	}

}
