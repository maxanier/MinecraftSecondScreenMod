package de.maxgb.minecraft.second_screen.world_observer;

import net.minecraft.block.Block;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import org.json.JSONArray;
import org.json.JSONObject;

import de.maxgb.minecraft.second_screen.util.Logger;

//@formatter:off
/*JSONStructure:
 * <"inv":JSONObject>
 * 	which contains for each block:
 * 	<label:JSONArray>
 * 		which contains JSONArrays with
 * 			<0=displayname:String>
 * 			<1=stacksize:int>
 */
//@formatter:on

/**
 * Observer which is designed to observe any inventory which implements IInventory
 * @author Max
 *
 */
public class InventoryObserver implements ObservedBlock.ObservingType {

	private final int ID = 3;
	private JSONObject info;

	@Override
	public boolean addInfoForBlock(World world, ObservedBlock block) {
		if (info == null) {
			info = new JSONObject();
		}
		try {
			JSONArray inv = new JSONArray();
			IInventory chest = (IInventory) world.getTileEntity(block.x, block.y, block.z);
			for (int i = 0; i < chest.getSizeInventory(); i++) {
				ItemStack s = chest.getStackInSlot(i);
				if (s != null) {
					inv.put(new JSONArray().put(s.getDisplayName()).put(s.stackSize));
				}
			}
			info.put(block.label, inv);
			return true;
		} catch (Exception e) {
			Logger.e("Inventory Observer", "Failed to gather block info -> remove", e);
			return false;
		}
	}

	@Override
	public boolean canObserve(Block block, TileEntity tile) {
		if (tile != null && tile instanceof IInventory) {

			return true;

		}
		return false;
	}

	@Override
	public void finishInfoCreation(JSONObject parent) {
		if (info != null && info.length() > 0) {
			parent.put("inv", info);
			info = null;
		}

	}

	@Override
	public int getId() {
		return ID;
	}

	@Override
	public String getIdentifier() {
		return "Inventory";
	}

	@Override
	public String getShortIndentifier() {
		return "i";
	}

}
