package de.maxgb.minecraft.second_screen.info_listener;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;

import org.json.JSONArray;
import org.json.JSONObject;

import de.maxgb.minecraft.second_screen.StandardListener;
import de.maxgb.minecraft.second_screen.util.Logger;
import de.maxgb.minecraft.second_screen.util.PROTOKOLL;

public class PlayerInventoryListener extends StandardListener {

	private final String TAG = "PlayerInventoryListener";
	private String username;

	public PlayerInventoryListener(String params) {
		super(params);
		setParams(params);
		everyTick = 200;// TODO Make config related
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
				JSONArray items = new JSONArray();

				for (int i = 0; i < player.inventory.mainInventory.length; i++) {
					ItemStack s = player.inventory.mainInventory[i];
					if (s != null) {
						JSONObject stack = new JSONObject();

						stack.put("displayname", s.getDisplayName());
						stack.put("size", s.stackSize);
						items.put(stack);
					}
				}

				response.put("inventory", items);

				response.put("success", 1);
			}
		}
		return PROTOKOLL.S_PLAYERINFO_LISTENER + ":" + response.toString();

	}

}
