package de.maxgb.minecraft.second_screen.info_listener;

import java.util.ArrayList;
import java.util.HashMap;

import net.minecraft.world.WorldServer;
import net.minecraft.world.storage.WorldInfo;

import org.json.JSONArray;
import org.json.JSONObject;

import de.maxgb.minecraft.second_screen.Configs;
import de.maxgb.minecraft.second_screen.StandardListener;
import de.maxgb.minecraft.second_screen.util.Logger;
import de.maxgb.minecraft.second_screen.util.PROTOKOLL;
import de.maxgb.minecraft.second_screen.world.ObservingRegistry;
import de.maxgb.minecraft.second_screen.world.ObservingRegistry.ObservedBlock;

public class WorldInfoListener extends StandardListener {
	/**
	 * MinecraftTime Gets a string with only needed elements. Max time is weeks
	 * 
	 * @param timeInTicks
	 * @return Time in string format
	 */
	public static String parseTime(int timeInTicks) {
		String time = "";
		int weeks = timeInTicks / (168000);
		int remainder = timeInTicks % (168000);
		int days = remainder / 24000;
		remainder = timeInTicks % 24000;
		int hours = remainder / 1000;
		remainder = timeInTicks % 1000;
		int minutes = remainder / 17;

		if (weeks != 0) {
			time += weeks + " weeks ";
		}

		if (days != 0) {
			time += (days < 10 ? "0" : "") + days + " days ";
		}

		if (hours != 0) {
			time += (hours < 10 ? "0" : "") + hours + " h ";
		}

		if (minutes != 0) {
			time += (minutes < 10 ? "0" : "") + minutes + " min ";
		}

		return time;
	}
	HashMap<Integer, WorldServer> worlds;

	private final String TAG = "WorldInfoListener";

	public WorldInfoListener(String params) {
		super(params);
		everyTick = Configs.world_info_update_time;
		worlds = new HashMap<Integer, WorldServer>();

		for (WorldServer s : server.worldServers) {
			worlds.put(s.provider.dimensionId, s);
		}
		Logger.i(TAG, "Worlds: " + worlds.toString());
	}

	@Override
	public String update() {
		JSONObject info = new JSONObject();

		// General Overworldinfo
		JSONObject ow = new JSONObject();

		for (WorldServer w : server.worldServers) {
			if (w.provider.dimensionId == 0) {
				WorldInfo i = w.getWorldInfo();
				ow.put("name", i.getWorldName());
				ow.put("time", parseTime((int) i.getWorldTime() + 6000));
				ow.put("rain", i.isRaining());
				ow.put("timetillrain", parseTime(i.getRainTime()));
				break;
			}
		}
		info.put("overworld", ow);

		// Redstone
		// info---------------------------------------------------------------
		JSONArray redstone = new JSONArray();

		ArrayList<ObservedBlock> blocks = ObservingRegistry.getObservedBlocks();
		for (int i = 0; i < blocks.size(); i++) {
			ObservedBlock block = blocks.get(i);

			WorldServer world = worlds.get(block.dimensionId);

			if (world == null) {
				Logger.w(TAG,
						"Dimension corrosponding to the block not found: "
								+ block.dimensionId);
				ObservingRegistry.removeObservedBlock(block.label);

			} else {
				if (world.getBlock(block.x, block.y, block.z).getMaterial() == net.minecraft.block.material.Material.air) {
					Logger.w(TAG, "Blocks material is air -> remove");
					ObservingRegistry.removeObservedBlock(block.label);
				} else {
					JSONArray in = new JSONArray();
					in.put(block.label).put(
							world.isBlockIndirectlyGettingPowered(block.x,
									block.y, block.z));
					redstone.put(in);
				}

			}
		}

		info.put("redstone", redstone);
		// --------------------------------------------------------------------------------

		return PROTOKOLL.WORLD_INFO_LISTENER + ":" + info.toString();
	}

}
