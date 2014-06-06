package de.maxgb.minecraft.second_screen.info_listener;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import org.json.JSONArray;
import org.json.JSONObject;

import de.maxgb.minecraft.second_screen.Configs;
import de.maxgb.minecraft.second_screen.StandardListener;
import de.maxgb.minecraft.second_screen.shared.PROTOKOLL;
import de.maxgb.minecraft.second_screen.util.User;

public class PlayerInventoryListener extends StandardListener {
	/*
	 * private class FakeIconRegister implements IIconRegister {
	 * 
	 * String texture;
	 * 
	 * private IIcon createTextureAtlasSprite(String s) { try { Class<?> c =
	 * TextureAtlasSprite.class; java.lang.reflect.Constructor constr = c
	 * .getDeclaredConstructor(String.class); constr.setAccessible(true); return
	 * (IIcon) constr.newInstance(s); } catch (NoSuchMethodException e) { //
	 * TODO Auto-generated catch block e.printStackTrace(); } catch
	 * (SecurityException e) { // TODO Auto-generated catch block
	 * e.printStackTrace(); } catch (InstantiationException e) { // TODO
	 * Auto-generated catch block e.printStackTrace(); } catch
	 * (IllegalAccessException e) { // TODO Auto-generated catch block
	 * e.printStackTrace(); } catch (IllegalArgumentException e) { // TODO
	 * Auto-generated catch block e.printStackTrace(); } catch
	 * (InvocationTargetException e) { // TODO Auto-generated catch block
	 * e.printStackTrace(); } return null; }
	 * 
	 * public String getTextureString() { return texture; }
	 * 
	 * @Override public IIcon registerIcon(String var1) { texture = var1;
	 * 
	 * // Should throw a nullpointer String n = null; n.charAt(1);
	 * 
	 * return createTextureAtlasSprite(texture);
	 * 
	 * } private String getTextureString(Item i) {
	 * 
	 * try { ItemBlock ib = (ItemBlock) i; } catch (java.lang.ClassCastException
	 * e) {
	 * 
	 * try { FakeIconRegister register = new FakeIconRegister();
	 * i.registerIcons(register); return register.getTextureString(); } catch
	 * (NullPointerException e1) { } }
	 * 
	 * return null;
	 * 
	 * } }
	 */
	private final String TAG = "PlayerInventoryListener";

	public PlayerInventoryListener(User user) {
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
			JSONArray items = new JSONArray();

			for (int i = 0; i < player.inventory.mainInventory.length; i++) {
				ItemStack s = player.inventory.mainInventory[i];
				if (s != null) {
					JSONObject stack = new JSONObject();

					stack.put("displayname", s.getDisplayName());
					stack.put("size", s.stackSize);
					Item it = s.getItem();
					if (it != null) {
						CreativeTabs tab = null;
						try {
							tab = it.getCreativeTab();
						} catch (Exception e) {
							e.printStackTrace();
						}
						catch(NoSuchMethodError e){
							
						}
						if (tab != null) {
							stack.put("tab", it.getCreativeTab().getTabLabel());
						} else {
							stack.put("tab", "otherMatters");
						}
					}

					// stack.put("icon", getTextureString(s.getItem()));
					items.put(stack);
				}
			}

			response.put("inventory", items);

			response.put("success", 1);
		}

		return PROTOKOLL.PLAYER_INVENTORY_LISTENER + "-" + response.toString();

	}

}
