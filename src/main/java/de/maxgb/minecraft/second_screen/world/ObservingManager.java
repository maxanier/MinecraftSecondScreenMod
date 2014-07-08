package de.maxgb.minecraft.second_screen.world;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLever;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.WorldServer;
import de.maxgb.minecraft.second_screen.data.DataStorageDriver;
import de.maxgb.minecraft.second_screen.util.Logger;

public class ObservingManager {
	public static class ObservedBlock {
		public static ObservedBlock fromString(String s) {
			if (s == null) {
				Logger.w(TAG, "Cannot create ObservedBlock from null String");
				return null;
			}
			
			//Contains 5 parts before Thaumcraft implementation, 7 parts after
			String[] parts = s.split(",");
			if (parts.length < 5) {
				Logger.w(TAG, "ObservedBlock String has to contain at least 5 parts");
				return null;
			}

			try {
				String label = parts[0];
				int x = Integer.parseInt(parts[1]);
				int y = Integer.parseInt(parts[2]);
				int z = Integer.parseInt(parts[3]);
				int dimensionId = Integer.parseInt(parts[4]);
				int type=ObservingType.REDSTONE;
				if(parts.length>=6){
					type=Integer.parseInt(parts[5]);
				}
				String extra="";
				if(parts.length>=7){
					extra=parts[6];
				}

				return new ObservedBlock(label, x, y, z, dimensionId,type,extra);
			} catch (NumberFormatException e) {
				Logger.e(TAG, "Failed to parse coordinates", e);
				return null;
			}
		}

		public String label;

		public int x, y, z, dimensionId;
		
		public int type;
		
		public String extra;

		public ObservedBlock(String label, int x, int y, int z, int dimensionId,int type,String extra) {
			this.x = x;
			this.y = y;
			this.z = z;
			this.label = label;
			this.dimensionId = dimensionId;
			this.type=type;
			this.extra=extra;
		}

		public Block getBlock(IBlockAccess world) {
			return world.getBlock(x, y, z);
		}

		@Override
		public String toString() {
			return label + "," + x + "," + y + "," + z + "," + dimensionId+","+type+","+extra;

		}

	}

	private static HashMap<String, ObservedBlock> map;
	private final static String TAG = "OberservingRegistry";

	private final static String FILE = "observingMap.txt";

	public static ArrayList<ObservedBlock> getObservedBlocks() {
		if (map == null) {
			map = new HashMap<String, ObservedBlock>();
		}

		ArrayList<ObservedBlock> blocks = new ArrayList<ObservedBlock>();
		blocks.addAll(map.values());
		return blocks;
	}

	/**
	 * Loads the HashMap<String,ObservedBlock>, creates a new one if none exists
	 */
	public static void loadObservingMap() {
		map = new HashMap<String, ObservedBlock>();
		ArrayList<String> lines = DataStorageDriver.readFromWorldFile(FILE);
		if (lines == null) {
			Logger.i(TAG, "No saved data found");
			return;
		} else {
			for (String line : lines) {
				String[] part = line.split(":");
				ObservedBlock b = ObservedBlock.fromString(part[1]);
				if (b != null) {
					map.put(part[0], b);
				}
			}
		}
	}

	/**
	 * Adds a block to the observing list, overrides blocks with the same label
	 * 
	 * @param label
	 *            Label
	 * @param x
	 * @param y
	 * @param z
	 * @return false if there already was a observed block with that label
	 */
	public static boolean observeBlock(String label, int x, int y, int z,
			int dimensionId,int type,String extra) {
		if (map == null) {
			map = new HashMap<String, ObservedBlock>();
		}
		return (map.put(label, new ObservedBlock(label, x, y, z, dimensionId,type,extra)) == null);
	}

	/**
	 * Removes the block with the given label from the observing list
	 * 
	 * @param label
	 * @return if block was removed
	 */
	public static boolean removeObservedBlock(String label) {
		return (map.remove(label) != null);
	}

	/**
	 * Saves the HashMap<String,ObservedBlock>
	 */
	public static void saveObservingMap() {
		ArrayList<String> lines = new ArrayList<String>();

		for (Map.Entry<String, ObservedBlock> entry : map.entrySet()) {
			lines.add(entry.getKey() + ":" + entry.getValue().toString());
		}
		DataStorageDriver.writeToWorldFile(FILE, lines);
	}
	
	/**
	 * Collects the data from all observed blocks, seperates them by types and puts the results in the parent JSON
	 * @param parent JSONObject to store data
	 * @param worlds Minecraftworlds which contain the blocks
	 */
	public static void addObservingInfo(JSONObject parent,HashMap<Integer, WorldServer> worlds){
		
		JSONArray redstone=new JSONArray();
		JSONArray th_node=new JSONArray();
		
		ArrayList<ObservedBlock> blocks = ObservingManager.getObservedBlocks();
		for (int i = 0; i < blocks.size(); i++) {
			ObservedBlock block = blocks.get(i);

			WorldServer world = worlds.get(block.dimensionId);

			if (world == null) {
				Logger.w(TAG,
						"Dimension corrosponding to the block not found: "
								+ block.dimensionId);
				ObservingManager.removeObservedBlock(block.label);

			} else {
				if (world.getBlock(block.x, block.y, block.z).getMaterial() == net.minecraft.block.material.Material.air) {
					Logger.w(TAG, "Blocks material is air -> remove");
					ObservingManager.removeObservedBlock(block.label);
				} else {
					switch (block.type){
					case ObservingType.REDSTONE:
						redstone.put(ObservingType.infoRedstone(world, block));
						break;
					case ObservingType.NODE:
						JSONObject in=ObservingType.infoTh_Node(world,block);
						if(in!=null){
							th_node.put(in);
						}
					}
				}
			}
		}
		
		parent.put("redstone", redstone);
		parent.put("th_node", th_node);
		
	}
	
	

}
