package de.maxgb.minecraft.second_screen.info_listener;

import java.util.Collection;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.potion.PotionEffect;

import org.json.JSONArray;
import org.json.JSONObject;

import de.maxgb.minecraft.second_screen.Configs;
import de.maxgb.minecraft.second_screen.StandardListener;
import de.maxgb.minecraft.second_screen.shared.PROTOKOLL;
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
			response.put("eplevel", player.experienceLevel);
			response.put("posx", (int) player.posX);
			response.put("posy", (int) player.posY);
			response.put("posz", (int) player.posZ);
			response.put("ping", player.ping);

			JSONArray potions = new JSONArray();
			Collection<PotionEffect> pot = player.getActivePotionEffects();
			for (PotionEffect i : pot) {
				JSONArray p = new JSONArray();
				p.put(i.getEffectName());
				p.put(i.getDuration() / 20);
				potions.put(p);
			}
			response.put("potions", potions);

			response.put("success", 1);
		}

		return PROTOKOLL.S_PLAYERINFO_LISTENER + "-" + response.toString();

	}

}
