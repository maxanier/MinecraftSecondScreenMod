package de.maxgb.minecraft.second_screen.info_listener;

import net.minecraft.entity.player.EntityPlayerMP;

import org.json.JSONObject;

import de.maxgb.minecraft.second_screen.StandardListener;
import de.maxgb.minecraft.second_screen.util.Logger;
import de.maxgb.minecraft.second_screen.util.PROTOKOLL;

public class PlayerInfoListener extends StandardListener {

	private String params;
	private String username;
	private final String TAG = "PlayerInfoListener";

	public PlayerInfoListener(String params) {
		super(params);
		setParams(params);
		everyTick = 40;// TODO make config related
		Logger.i(TAG, "Created Playerlistener");// TODO remove
	}

	public void setParams(String params) {
		Logger.i(TAG, "Settings params: \"" + params + "\"");
		try {
			username = params.trim();
		} catch (Exception e) {
			Logger.e(TAG, "Failed to retrieve username from params", e);
		}

	}

	@Override
	public String update() {
		JSONObject response = new JSONObject();

		if (username == null || username.equals("")) {
			response.put("success", 0);
			response.put("error", "username required");
		} else {
			EntityPlayerMP player = server.getConfigurationManager()
					.getPlayerForUsername(username);

			if (player == null) {
				response.put("success", 0).put("error",
						"User " + username + " not online");
			} else {
				response.put("health", player.getHealth());
				response.put("foodlevel", player.getFoodStats().getFoodLevel());

				response.put("success", 1);
			}
		}
		return PROTOKOLL.S_PLAYERINFO_LISTENER + ":" + response.toString();

	}

}
