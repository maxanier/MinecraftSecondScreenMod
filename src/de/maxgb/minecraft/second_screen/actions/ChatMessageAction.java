package de.maxgb.minecraft.second_screen.actions;

import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;

import org.json.JSONObject;

import cpw.mods.fml.common.FMLCommonHandler;

import de.maxgb.minecraft.second_screen.actions.ActionManager.ActionResultListener;
import de.maxgb.minecraft.second_screen.actions.ActionManager.IAction;
import de.maxgb.minecraft.second_screen.shared.PROTOKOLL;
import de.maxgb.minecraft.second_screen.util.Logger;
import de.maxgb.minecraft.second_screen.util.User;

public class ChatMessageAction implements IAction{



	private static final String TAG = "ChatMessage";
	private static final String PERMISSION="send_chat_message";

	@Override
	public void doAction(JSONObject param, User user,
			ActionResultListener listener) {
		if(!param.has("msg")){
			Logger.w(TAG, "Params did not include message");
			JSONObject result=new JSONObject();
			result.put("success", 0);
			result.put("error", "Params did not include message");
			
			listener.onActionResult(PROTOKOLL.A_CHAT_MSG, result);
			return;
		}
		if(!user.isAllowedTo(PERMISSION,true)){
			Logger.w(TAG, "User "+user.username+" is not allowed to execute this command");
			JSONObject result=new JSONObject();
			result.put("success", 0);
			result.put("allowed",false);
			
			listener.onActionResult(PROTOKOLL.A_CHAT_MSG, result);
			return;
		}
		String msg=param.getString("msg");
		MinecraftServer server=FMLCommonHandler.instance().getMinecraftServerInstance();
		server.addChatMessage(new ChatComponentText(msg));
		
		JSONObject result=new JSONObject();
		result.put("success", 1);
		result.put("allowed",true);
		
		listener.onActionResult(PROTOKOLL.A_CHAT_MSG, result);
		return;
		
	}


}
