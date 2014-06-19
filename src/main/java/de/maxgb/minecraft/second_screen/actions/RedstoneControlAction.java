package de.maxgb.minecraft.second_screen.actions;

import net.minecraft.block.BlockLever;
import net.minecraft.world.World;

import org.json.JSONObject;

import cpw.mods.fml.common.FMLCommonHandler;
import de.maxgb.minecraft.second_screen.actions.ActionManager.ActionResultListener;
import de.maxgb.minecraft.second_screen.actions.ActionManager.IAction;
import de.maxgb.minecraft.second_screen.info_listener.WorldInfoListener;
import de.maxgb.minecraft.second_screen.shared.PROTOKOLL;
import de.maxgb.minecraft.second_screen.util.ForceUpdateEvent;
import de.maxgb.minecraft.second_screen.util.Logger;
import de.maxgb.minecraft.second_screen.util.User;
import de.maxgb.minecraft.second_screen.world.ObservingRegistry;
import de.maxgb.minecraft.second_screen.world.ObservingRegistry.ObservedBlock;

public class RedstoneControlAction implements IAction {

	private static final String TAG = "RedstoneControlAction";
	private static final String PERMISSION = "control_redstone";

	@Override
	public void doAction(JSONObject param, User user,
			ActionResultListener listener) {
		if (!param.has("label")) {
			Logger.w(TAG, "Params did not include label");
			JSONObject result = new JSONObject();
			result.put("success", 0);
			result.put("error", "Params did not include label");

			listener.onActionResult(PROTOKOLL.A_RED_CONTROL, result);
			return;
		}
		if (!param.has("state")) {
			Logger.w(TAG, "Params did not include state");
			JSONObject result = new JSONObject();
			result.put("success", 0);
			result.put("error", "Params did not include state");

			listener.onActionResult(PROTOKOLL.A_RED_CONTROL, result);
			return;
		}
		if (!user.isAllowedTo(PERMISSION, true)) {
			Logger.w(TAG, "User " + user.username
					+ " is not allowed to execute this command");
			JSONObject result = new JSONObject();
			result.put("success", 0);
			result.put("allowed", false);

			listener.onActionResult(PROTOKOLL.A_RED_CONTROL, result);
			return;
		}
		String label = param.getString("label");
		boolean state = param.getBoolean("state");
		for (ObservedBlock o : ObservingRegistry.getObservedBlocks()) {
			if (o.label.equals(label)) {
				World w = FMLCommonHandler.instance()
						.getMinecraftServerInstance()
						.worldServerForDimension(o.dimensionId);
				if (setState(o, w, state)) {
					JSONObject result = new JSONObject();
					result.put("success", 1);
					result.put("allowed", true);

					listener.onActionResult(PROTOKOLL.A_RED_CONTROL, result);
					FMLCommonHandler
							.instance()
							.bus()
							.post(new ForceUpdateEvent(WorldInfoListener.class));
					return;
				} else {
					Logger.w(TAG,
							"Block connected with this label is not a lever");
					JSONObject result = new JSONObject();
					result.put("success", 0);
					result.put("error",
							"Block connected with this label is not a lever");

					listener.onActionResult(PROTOKOLL.A_RED_CONTROL, result);

					return;
				}
			}
		}

	}

	/**
	 * Sets the state of a lever
	 * 
	 * @param b
	 *            Block
	 * @param w
	 *            World
	 * @param state
	 *            State
	 * @return Whether the block is a lever or not
	 */
	private boolean setState(ObservedBlock b, World w, boolean state) {
		if (w.getBlock(b.x, b.y, b.z) instanceof BlockLever) {
			int meta = w.getBlockMetadata(b.x, b.y, b.z);
			if (state) {
				meta = meta | 0x8;

			} else {
				meta = meta ^ 0x8;
			}
			w.setBlockMetadataWithNotify(b.x, b.y, b.z, meta, 3);
			Logger.i(TAG, "Spawning particle");
			w.spawnParticle("reddust", b.x, b.y + 1, b.z, 0.0D, 255.0D, 0.0D);

			return true;
		}
		return false;
	}

}
