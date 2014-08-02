package de.maxgb.minecraft.second_screen.actions;

import java.util.HashMap;

import org.json.JSONObject;

import de.maxgb.minecraft.second_screen.shared.PROTOKOLL;
import de.maxgb.minecraft.second_screen.util.Logger;
import de.maxgb.minecraft.second_screen.util.User;

/**
 * Manages all possible actions
 * @author Max
 *
 */
public class ActionManager {

	public interface ActionResultListener {
		public void onActionResult(String command, JSONObject r);
	}

	/**
	 * Interface for actions, which can be executed by the client
	 * 
	 * @author Max
	 * 
	 */
	public interface IAction {
		/**
		 * Should execute the action
		 * 
		 * @param param
		 *            Params
		 * @param listener
		 *            Listener which should get the action result
		 * @return
		 */
		public void doAction(JSONObject param, User user, ActionResultListener listener);
	}

	private static HashMap<String, IAction> actions;
	private static final String TAG = "ActionManager";

	/**
	 * Executes an action with the given params. Actionresult is send to the
	 * listener
	 * 
	 * @param command
	 *            Command which corrosponds to the action
	 * @param params
	 *            Params
	 * @param listener
	 *            Listener to receive the action result
	 * @return whether the action was found or not
	 */
	public static boolean doAction(String command, final JSONObject params, final User user,
			final ActionResultListener listener) {
		if (!actions.containsKey(command)) {
			Logger.w(TAG, "No action fitting: " + command + " found");
			return false;
		}
		final IAction a = actions.get(command);
		Thread action = new Thread(new Runnable() {

			@Override
			public void run() {
				a.doAction(params, user, listener);

			}

		});
		action.start();

		return true;
	}

	/**
	 * Registers a action, which can then be executed
	 * 
	 * @param command
	 *            The command which represents the action
	 * @param action
	 *            The action
	 */
	public static void registerAction(String command, IAction action) {
		if (actions == null) {
			actions = new HashMap<String, IAction>();
		}
		actions.put(command, action);
	}

	/**
	 * Registers all standard actions
	 */
	public static void registerStandardActions() {
		registerAction(PROTOKOLL.A_CHAT_MSG, new ChatMessageAction());
		registerAction(PROTOKOLL.A_RED_CONTROL, new RedstoneControlAction());
	}

	public static void removeAllActions() {
		actions = new HashMap<String, IAction>();
	}
}
