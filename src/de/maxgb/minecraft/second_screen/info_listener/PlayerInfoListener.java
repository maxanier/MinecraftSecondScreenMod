package de.maxgb.minecraft.second_screen.info_listener;

import net.minecraft.entity.player.EntityPlayerMP;

import org.json.JSONObject;

import de.maxgb.minecraft.second_screen.Configs;
import de.maxgb.minecraft.second_screen.StandardListener;
import de.maxgb.minecraft.second_screen.shared.PROTOKOLL;
import de.maxgb.minecraft.second_screen.util.Logger;
import de.maxgb.minecraft.second_screen.util.User;

public class PlayerInfoListener extends StandardListener {


	private final String TAG = "PlayerInfoListener";

	public PlayerInfoListener(User user) {
		super(user);
		everyTick = Configs.player_info_update_time;

	}



	@Override
	public String update() {
		JSONObject response = new JSONObject();

			EntityPlayerMP player = user.getPlayer(server);

			if (player == null) {
				response.put("success", 0).put("error",
						"User " + user.username + " not online");
			} else {
				response.put("health", player.getHealth());
				response.put("foodlevel", player.getFoodStats().getFoodLevel());

				response.put("success", 1);
			}

		return PROTOKOLL.S_PLAYERINFO_LISTENER + ":" + response.toString();

	}

}
