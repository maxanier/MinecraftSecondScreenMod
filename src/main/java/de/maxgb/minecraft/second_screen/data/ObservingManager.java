package de.maxgb.minecraft.second_screen.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import de.maxgb.minecraft.second_screen.Configs;
import de.maxgb.minecraft.second_screen.util.Constants;
import de.maxgb.minecraft.second_screen.util.Helper;
import de.maxgb.minecraft.second_screen.util.Logger;
import de.maxgb.minecraft.second_screen.world_observer.ObservedBlock;

/**
 * Manages all observed blocks
 * Saves and loads them and makes sure that every player gets his private and the public block infos
 * @author Max
 *
 */
public class ObservingManager {

	private static final String TAG = "ObservingManager";
	private static final String PUBLIC_USER = "msspublic";
	private static HashMap<String, HashMap<String, ObservedBlock>> map;

	/**
	 * Returns all blocks observed by that user
	 * 
	 * @param username
	 *            player
	 * @param publ
	 *            if true, the public blocks are returned as well
	 * @return if none, an empty list is returned
	 */
	public static ArrayList<ObservedBlock> getObservedBlocks(String username, boolean publ) {
		ArrayList<ObservedBlock> blocks = new ArrayList<ObservedBlock>();

		if (map != null && map.get(username) != null) {
			blocks.addAll(map.get(username).values());
		}
		if (publ) {
			blocks.addAll(getObservedBlocks(PUBLIC_USER, false));
		}
		return blocks;
	}

	/**
	 * Loads the observation map from a jsonfile
	 */
	public static void loadObservingFile() {
		ArrayList<String> lines = DataStorageDriver.readFromWorldFile(Constants.OBSERVER_FILE_NAME);
		if (lines == null || lines.size() == 0) {
			Logger.w(TAG, "No saved data found");
			return;
		}

		map = new HashMap<String, HashMap<String, ObservedBlock>>();

		/*
		 * JSON structure: {
		 * "observers":{"msspublic":[<blockjsonobject>,<block2jsonobject>]
		 * ,"maxanier":[]} }
		 */

		try {
			JSONObject data = new JSONObject(lines.get(0));
			JSONObject observers = data.getJSONObject("observers");

			for (String k : JSONObject.getNames(observers)) {
				JSONArray blocks = observers.getJSONArray(k);
				HashMap<String, ObservedBlock> map = new HashMap<String, ObservedBlock>();

				for (int i = 0; i < blocks.length(); i++) {
					ObservedBlock b = ObservedBlock.createFromJson(blocks.getJSONObject(i));
					if (b != null) {
						map.put(b.getLabel(), b);
					}

				}
				ObservingManager.map.put(k, map);
			}
		} catch (JSONException e) {
			Logger.e(TAG, "Failed to parse observer json file", e);
		} catch (NullPointerException e) {
			Logger.e(TAG, "Failed to parse observer json file", e);
		}

	}

	/**
	 * Adds a new Block to the observing map
	 * 
	 * @param username
	 *            player who create the observation
	 * @param publ
	 *            if everyone should see this, the method test if this is allowed
	 * @param block
	 *            block to add
	 * @return false if an old observation was overriden
	 */
	public static boolean observeBlock(String username, boolean publ, ObservedBlock block) {
		if (publ) {
			if (Configs.obs_publ_admin) {
				if (Helper.isPlayerOpped(username)) {
					username = PUBLIC_USER;
				}
			} else {
				username = PUBLIC_USER;
			}
		}

		if (map == null) {
			map = new HashMap<String, HashMap<String, ObservedBlock>>();
		}

		if (!map.containsKey(username)) {
			map.put(username, new HashMap<String, ObservedBlock>());
		}

		if (map.get(username).put(block.getLabel(), block) == null) {
			return true;
		}
		return false;
	}

	/**
	 * Removes the block with the given label from the observing list
	 * 
	 * @param username
	 *            playername
	 * @param label
	 * @return if block was removed
	 */
	public static boolean removeObservedBlock(String username, String label) {
		try {
			if (map.get(username) != null) {
				if (map.get(username).remove(label) != null) {
					return true;
				}
			}
		} catch (NullPointerException e) {
		}

		try {
			if (!Configs.obs_publ_admin || Helper.isPlayerOpped(username)) {
				if (map.get(PUBLIC_USER).remove(label) != null) {
					return true;
				}
			}
		} catch (NullPointerException e) {

		}

		return false;
	}

	/**
	 * Saves the observation map to a file in json format
	 */
	public static void saveObservingFile() {
		JSONObject data = new JSONObject();

		JSONObject observers = new JSONObject();

		try {
			for (Map.Entry<String, HashMap<String, ObservedBlock>> e : map.entrySet()) {

				JSONArray blocks = new JSONArray();
				for (ObservedBlock b : e.getValue().values()) {
					blocks.put(b.toJSON());
				}

				observers.put(e.getKey(), blocks);
			}
		} catch (JSONException e) {
			Logger.e(TAG, "JsonException while saving", e);
		} catch (NullPointerException e) {
			Logger.e(TAG, "Nullpointer while saving", e);
		}

		data.put("observers", observers);

		ArrayList<String> lines = new ArrayList<String>();
		lines.add(data.toString());

		DataStorageDriver.writeToWorldFile(Constants.OBSERVER_FILE_NAME, lines);
	}

}
