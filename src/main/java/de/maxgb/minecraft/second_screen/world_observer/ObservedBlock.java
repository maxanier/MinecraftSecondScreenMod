package de.maxgb.minecraft.second_screen.world_observer;

import de.maxgb.minecraft.second_screen.data.ObservingManager;
import de.maxgb.minecraft.second_screen.util.Logger;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ObservedBlock {

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
				if (world.getBlockState(block.pos).getMaterial() == Material.AIR) {
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

	private final static String TAG = "ObservedBlock";

	private static List<ObservingType> observingTypes;

	/**
	 * Interface for observingtypes e.g. InventoryObserver or FluidTankObserver
	 *
	 * @author Max
	 */
	public interface ObservingType {
		/**
		 * Adds the informations about the given block to the next update
		 *
		 * @param block
		 * @param world World the block is in
		 * @return false if the block should be removed from the list
		 */
		boolean addInfoForBlock(World world, ObservedBlock block);

		/**
		 * Tests if this block/tile can be observed by this observer type
		 *
		 * @param block
		 * @param tile  tile can be null
		 * @return
		 */
		boolean canObserve(IBlockState block, TileEntity tile);

		/**
		 * Adds all information collected by
		 * {@link #addInfoForBlock(ObservedBlock) addInfoForBlock} method. Does
		 * not change anything if nothing to add
		 *
		 * @param parent JSONObject the info shall be added to
		 */
		void finishInfoCreation(JSONObject parent);

		/**
		 * @return ID for this type
		 */
		int getId();

		/**
		 * @return A string which can be used to indentify this type in commands
		 * etc
		 */
		String getIdentifier();

		/**
		 * @return A short string which can be used to indentify this type in
		 * commands etc
		 */
		String getShortIndentifier();
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
			b.pos=new BlockPos(coord.getInt(0),coord.getInt(1),coord.getInt(2));
			b.dimensionId = coord.getInt(3);

			b.type = json.getInt("type");

			if (json.has("side_str")) {
				b.side = EnumFacing.byName(json.getString("side_str"));
			} else {
				b.side = EnumFacing.UP;
			}

			return b;
		} catch (JSONException e) {
			Logger.e(TAG, "Failed to parse block", e);
			return null;
		}

	}

	/**
	 * @return A list of all available ObservingTypes
	 */
	public static List<ObservingType> getObservingTypes() {
		if (observingTypes == null) {
			observingTypes = new ArrayList<ObservingType>();
			observingTypes.add(new RedstoneObserver());
			observingTypes.add(new InventoryObserver());
			observingTypes.add(new FluidTankObserver());
			//observingTypes.add(new NodeObserver());
			//observingTypes.add(new RFEnergyStorageObserver());
		}

		return observingTypes;
	}

	protected String label;

	protected int dimensionId;
	
	protected BlockPos pos;
	
	protected EnumFacing side;

	protected int type;

	private ObservedBlock() {

	}

	/**
	 * Creates a ObservedBlock
	 * @param label Name/Label
	 * @param x X-Coord
	 * @param y Y-Coord
	 * @param z Z-Coord
	 * @param dimensionId Worlddimension id
	 * @param type ObservingType given by the corrosponding observer class
	 * @param sideHit Side it was registered from
	 */
	public ObservedBlock(String label, BlockPos pos, int dimensionId, int type, EnumFacing sideHit) {
		this.pos=pos;
		this.label = label;
		this.dimensionId = dimensionId;
		this.type = type;
		this.side = sideHit;
	}

	/**
	 * Returns the block assosiated with the coordinated in the given world
	 * 
	 * @param world
	 *            world
	 * @return block
	 */
	public Block getBlock(IBlockAccess world) {
		return world.getBlockState(pos).getBlock();
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
		coord.put(pos.getX());
		coord.put(pos.getY());
		coord.put(pos.getZ());
		coord.put(dimensionId);

		json.put("coord", coord);
		json.put("side_str", side.getName());

		json.put("type", type);

		return json;
	}
}
