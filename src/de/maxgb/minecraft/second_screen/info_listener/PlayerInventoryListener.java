package de.maxgb.minecraft.second_screen.info_listener;

import java.lang.reflect.InvocationTargetException;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

import org.json.JSONArray;
import org.json.JSONObject;

import de.maxgb.minecraft.second_screen.StandardListener;
import de.maxgb.minecraft.second_screen.util.Logger;
import de.maxgb.minecraft.second_screen.util.PROTOKOLL;

public class PlayerInventoryListener extends StandardListener {

	private class FakeIconRegister implements IIconRegister {

		String texture;

		private IIcon createTextureAtlasSprite(String s) {
			try {
				Class<?> c = TextureAtlasSprite.class;
				java.lang.reflect.Constructor constr = c
						.getDeclaredConstructor(String.class);
				constr.setAccessible(true);
				return (IIcon) constr.newInstance(s);
			} catch (NoSuchMethodException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InstantiationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}

		public String getTextureString() {
			return texture;
		}

		@Override
		public IIcon registerIcon(String var1) {
			texture = var1;

			// Should throw a nullpointer
			String n = null;
			n.charAt(1);

			return createTextureAtlasSprite(texture);

		}

	}
	private final String TAG = "PlayerInventoryListener";

	private String username;

	public PlayerInventoryListener(String params) {
		super(params);
		setParams(params);
		everyTick = 200;// TODO Make config related
	}

	private String getTextureString(Item i) {

		try {
			ItemBlock ib = (ItemBlock) i;
		} catch (java.lang.ClassCastException e) {

			try {
				FakeIconRegister register = new FakeIconRegister();
				i.registerIcons(register);
				return register.getTextureString();
			} catch (NullPointerException e1) {
			}
		}

		return null;

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
						Item it = s.getItem();
						if(it!=null){
							stack.put("tab", it.getCreativeTab().getTabLabel());
						}
						
						//stack.put("icon", getTextureString(s.getItem()));
						items.put(stack);
					}
				}

				response.put("inventory", items);

				response.put("success", 1);
			}
		}
		return PROTOKOLL.PLAYER_INVENTORY_LISTENER + ":" + response.toString();

	}

}
