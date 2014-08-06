package de.maxgb.minecraft.second_screen.actions;


import org.json.JSONObject;

import cpw.mods.fml.common.FMLCommonHandler;
import de.maxgb.minecraft.second_screen.actions.ActionManager.ActionResultListener;
import de.maxgb.minecraft.second_screen.actions.ActionManager.IAction;
import de.maxgb.minecraft.second_screen.info_listener.ChatListener.RemoteChatMessageEvent;
import de.maxgb.minecraft.second_screen.shared.PROTOKOLL;
import de.maxgb.minecraft.second_screen.util.Helper;
import de.maxgb.minecraft.second_screen.util.Logger;
import de.maxgb.minecraft.second_screen.util.User;

/**
 * Action which can show a chat message ingame
 * @author Max
 *
 */
public class ChatMessageAction implements IAction {

	private static final String TAG = "ChatMessage";
	private static final String PERMISSION = "send_chat_message";

	@Override
	public void doAction(JSONObject param, User user, ActionResultListener listener) {
		if (!param.has("msg")) {
			Logger.w(TAG, "Params did not include message");
			JSONObject result = new JSONObject();
			result.put("success", 0);
			result.put("error", "Params did not include message");

			listener.onActionResult(PROTOKOLL.A_CHAT_MSG, result);
			return;
		}
		if (!user.isAllowedTo(PERMISSION, true)) {
			Logger.w(TAG, "User " + user.username + " is not allowed to execute this command");
			JSONObject result = new JSONObject();
			result.put("success", 0);
			result.put("allowed", false);

			listener.onActionResult(PROTOKOLL.A_CHAT_MSG, result);
			return;
		}
		String msg = param.getString("msg");

		Helper.sendChatMessage("[MSS] <" + user.username + "> " + msg);

		FMLCommonHandler.instance().bus().post(new RemoteChatMessageEvent(user.username, msg));

		JSONObject result = new JSONObject();
		result.put("success", 1);
		result.put("allowed", true);

		listener.onActionResult(PROTOKOLL.A_CHAT_MSG, result);
		return;

	}

}
