package de.maxgb.minecraft.second_screen.world;

import java.util.ArrayList;
import java.util.HashMap;

import net.minecraft.block.Block;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.WorldServer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import de.maxgb.minecraft.second_screen.data.ObservingManager;
import de.maxgb.minecraft.second_screen.util.Logger;

public class ObservedBlock {

	private final static String TAG = "ObservedBlock";

	/**
	 * Collects the data from all observed blocks, seperates them by types and
	 * puts the results in the parent JSON
	 * 
	 * @param parent
	 *            JSONObject to store data
	 * @param worlds
	 *            Minecraftworlds which contain the blocks
	 */
	public static void addObservingInfo(JSONObject parent, HashMap<Integer, WorldServer> worlds, String username) {

		JSONArray redstone = new JSONArray();
		JSONArray th_node = new JSONArray();
		JSONObject inventory = new JSONObject();

		ArrayList<ObservedBlock> blocks = ObservingManager.getObservedBlocks(username, true);
		for (int i = 0; i < blocks.size(); i++) {
			ObservedBlock block = blocks.get(i);

			WorldServer world = worlds.get(block.dimensionId);

			if (world == null) {
				Logger.w(TAG, "Dimension corrosponding to the block not found: " + block.dimensionId);
				ObservingManager.removeObservedBlock(username, block.label);

			} else {
				if (world.getBlock(block.x, block.y, block.z).getMaterial() == net.minecraft.block.material.Material.air) {
					Logger.w(TAG, "Blocks material is air -> remove");
					ObservingManager.removeObservedBlock(username, block.label);
				} else {
					switch (block.type) {
					case ObservingType.REDSTONE:
						redstone.put(ObservingType.infoRedstone(world, block));
						break;
					case ObservingType.NODE:
						JSONObject in = ObservingType.infoTh_Node(world, block);
						if (in != null) {
							th_node.put(in);
						}
						break;
					case ObservingType.INVENTORY:
						JSONArray inv = ObservingType.infoInventory(world, block);
						if (inv != null) {
							inventory.put(block.label, inv);
						}
						break;
					}
				}
			}
		}

		parent.put("redstone", redstone);
		parent.put("th_node", th_node);
		parent.put("inv", inventory);
	}

	/**
	 * Creates an ObservedBlock from its json save data
	 * 
	 * @param json
	 * @return
	 */
	public static ObservedBlock createFromJson(JSONObject json) {
		try {
			ObservedBlock b = new ObservedBlock();
			b.label = json.getString("label");

			JSONArray coord = json.getJSONArray("coord");
			b.x = coord.getInt(0);
			b.y = coord.getInt(1);
			b.z = coord.getInt(2);
			b.dimensionId = coord.getInt(3);

			b.type = json.getInt("type");

			return b;
		} catch (JSONException e) {
			Logger.e(TAG, "Failed to parse block", e);
			return null;
		}

	}

	protected String label;

	protected int x, y, z, dimensionId;

	protected int type;

	private ObservedBlock() {

	}

	public ObservedBlock(String label, int x, int y, int z, int dimensionId, int type) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.label = label;
		this.dimensionId = dimensionId;
		this.type = type;
	}

	/**
	 * Returns the block assosiated with the coordinated in the given world
	 * 
	 * @param world
	 *            world
	 * @return block
	 */
	public Block getBlock(IBlockAccess world) {
		return world.getBlock(x, y, z);
	}

	public String getLabel() {
		return label;
	}

	/**
	 * Created a JsonObject representing this block
	 * 
	 * @return
	 */
	public JSONObject toJSON() {
		JSONObject json = new JSONObject();
		json.put("label", label);

		JSONArray coord = new JSONArray();
		coord.put(x);
		coord.put(y);
		coord.put(z);
		coord.put(dimensionId);

		json.put("coord", coord);

		json.put("type", type);

		return json;
	}
}
