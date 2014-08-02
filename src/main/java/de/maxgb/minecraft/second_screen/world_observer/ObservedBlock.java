package de.maxgb.minecraft.second_screen.world_observer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import de.maxgb.minecraft.second_screen.data.ObservingManager;
import de.maxgb.minecraft.second_screen.util.Logger;

public class ObservedBlock {

	public static interface ObservingType {
		/**
		 * Adds the informations about the given block to the next update
		 * 
		 * @param block
		 * @param world
		 *            World the block is in
		 * @return false if the block should be removed from the list
		 */
		public boolean addInfoForBlock(World world, ObservedBlock block);

		/**
		 * Tests if this block/tile can be observed by this observer type
		 * 
		 * @param block
		 * @param tile
		 *            tile can be null
		 * @return
		 */
		public boolean canObserve(Block block, TileEntity tile);

		/**
		 * Adds all information collected by
		 * {@link #addInfoForBlock(ObservedBlock) addInfoForBlock} method. Does
		 * not change anything if nothing to add
		 * 
		 * @param parent
		 *            JSONObject the info shall be added to
		 */
		public void finishInfoCreation(JSONObject parent);

		/**
		 * 
		 * @return ID for this type
		 */
		public int getId();

		/**
		 * 
		 * @return A string which can be used to indentify this type in commands
		 *         etc
		 */
		public String getIdentifier();

		/**
		 * 
		 * @return A short string which can be used to indentify this type in
		 *         commands etc
		 */
		public String getShortIndentifier();
	}

	private final static String TAG = "ObservedBlock";

	private static List<ObservingType> observingTypes;

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
					for (ObservingType t : getObservingTypes()) {
						if (t.getId() == block.type) {
							if (!t.addInfoForBlock(world, block)) {
								ObservingManager.removeObservedBlock(username, block.label);
							}
							break;
						}

					}
				}
			}
		}

		for (ObservingType t : getObservingTypes()) {
			t.finishInfoCreation(parent);
		}

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

			if (json.has("side")) {
				b.side = json.getInt("side");
			} else {
				b.side = -1;
			}

			return b;
		} catch (JSONException e) {
			Logger.e(TAG, "Failed to parse block", e);
			return null;
		}

	}

	public static List<ObservingType> getObservingTypes() {
		if (observingTypes == null) {
			observingTypes = new ArrayList<ObservingType>();
			observingTypes.add(new RedstoneObserver());
			observingTypes.add(new InventoryObserver());
			observingTypes.add(new FluidTankObserver());
			observingTypes.add(new NodeObserver());
			observingTypes.add(new RFEnergyStorageObserver());
		}

		return observingTypes;
	}

	protected String label;

	protected int x, y, z, dimensionId, side;

	protected int type;

	private ObservedBlock() {

	}

	public ObservedBlock(String label, int x, int y, int z, int dimensionId, int type, int side) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.label = label;
		this.dimensionId = dimensionId;
		this.type = type;
		this.side = side;
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
		json.put("side", side);

		json.put("type", type);

		return json;
	}
}
