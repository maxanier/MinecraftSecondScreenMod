package de.maxgb.minecraft.second_screen.world;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.world.IBlockAccess;

import de.maxgb.minecraft.second_screen.data.DataStorageDriver;
import de.maxgb.minecraft.second_screen.util.Logger;

public class ObservingRegistry {
	public static class ObservedBlock {
		public static ObservedBlock fromString(String s) {
			if (s == null) {
				Logger.w(TAG, "Cannot create ObservedBlock from null String");
				return null;
			}

			String[] parts = s.split(",");
			if (parts.length != 5) {
				Logger.w(TAG, "ObservedBlock Strin has to contain 5 parts");
				return null;
			}

			try {
				String label = parts[0];
				int x = Integer.parseInt(parts[1]);
				int y = Integer.parseInt(parts[2]);
				int z = Integer.parseInt(parts[3]);
				int dimensionId = Integer.parseInt(parts[4]);

				return new ObservedBlock(label, x, y, z, dimensionId);
			} catch (NumberFormatException e) {
				Logger.e(TAG, "Failed to parse coordinates", e);
				return null;
			}
		}
		public String label;

		public int x, y, z, dimensionId;

		public ObservedBlock(String label, int x, int y, int z, int dimensionId) {
			this.x = x;
			this.y = y;
			this.z = z;
			this.label = label;
			this.dimensionId = dimensionId;
		}

		@Override
		public String toString() {
			return label + "," + x + "," + y + "," + z + "," + dimensionId;

		}
		
		public Block getBlock(IBlockAccess world){
			return world.getBlock(x, y, z);
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
			int dimensionId) {
		if (map == null) {
			map = new HashMap<String, ObservedBlock>();
		}
		return (map.put(label, new ObservedBlock(label, x, y, z, dimensionId)) == null);
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

}
