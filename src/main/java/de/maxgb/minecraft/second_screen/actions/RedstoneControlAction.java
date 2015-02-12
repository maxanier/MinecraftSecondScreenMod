package de.maxgb.minecraft.second_screen.actions;

import net.minecraftforge.fml.common.FMLCommonHandler;

import org.json.JSONObject;

import de.maxgb.minecraft.second_screen.actions.ActionManager.ActionResultListener;
import de.maxgb.minecraft.second_screen.actions.ActionManager.IAction;
import de.maxgb.minecraft.second_screen.data.ObservingManager;
import de.maxgb.minecraft.second_screen.info_listener.WorldInfoListener;
import de.maxgb.minecraft.second_screen.shared.PROTOKOLL;
import de.maxgb.minecraft.second_screen.util.ForceUpdateEvent;
import de.maxgb.minecraft.second_screen.util.Logger;
import de.maxgb.minecraft.second_screen.util.User;
import de.maxgb.minecraft.second_screen.world_observer.ObservedBlock;
import de.maxgb.minecraft.second_screen.world_observer.RedstoneObserver;

/**
 * Action which can change levers ingame
 * @author Max
 *
 */
public class RedstoneControlAction implements IAction {

	private static final String TAG = "RedstoneControlAction";
	private static final String PERMISSION = "control_redstone";

	@Override
	public void doAction(JSONObject param, User user, ActionResultListener listener) {
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
			Logger.w(TAG, "User " + user.username + " is not allowed to execute this command");
			JSONObject result = new JSONObject();
			result.put("success", 0);
			result.put("allowed", false);

			listener.onActionResult(PROTOKOLL.A_RED_CONTROL, result);
			return;
		}
		String label = param.getString("label");
		boolean state = param.getBoolean("state");
		for (ObservedBlock o : ObservingManager.getObservedBlocks(user.username, true)) {
			if (o.getLabel().equals(label)) {

				if (RedstoneObserver.setLeverState(o, state)) {
					JSONObject result = new JSONObject();
					result.put("success", 1);
					result.put("allowed", true);

					listener.onActionResult(PROTOKOLL.A_RED_CONTROL, result);
					FMLCommonHandler.instance().bus().post(new ForceUpdateEvent(WorldInfoListener.class));
					return;
				} else {
					Logger.w(TAG, "Block connected with this label is not a lever");
					JSONObject result = new JSONObject();
					result.put("success", 0);
					result.put("error", "Block connected with this label is not a lever");

					listener.onActionResult(PROTOKOLL.A_RED_CONTROL, result);

					return;
				}
			}
		}

	}

}
