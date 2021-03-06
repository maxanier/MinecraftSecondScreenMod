package de.maxgb.minecraft.second_screen.actions;

import de.maxgb.minecraft.second_screen.actions.ActionManager.ActionResultListener;
import de.maxgb.minecraft.second_screen.actions.ActionManager.IAction;
import de.maxgb.minecraft.second_screen.info_listener.ChatListener.RemoteChatMessageEvent;
import de.maxgb.minecraft.second_screen.shared.PROTOKOLL;
import de.maxgb.minecraft.second_screen.util.Helper;
import de.maxgb.minecraft.second_screen.util.User;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class GetLastChatAction implements IAction{
	
	private ArrayList<JSONObject> messages;
	
	public GetLastChatAction(){
		messages=new ArrayList<JSONObject>();
		MinecraftForge.EVENT_BUS.register(this);
	}

	@Override
	public void doAction(JSONObject param, User user, ActionResultListener listener) {
		JSONArray msgs=new JSONArray();
		for(JSONObject j:messages){
			msgs.put(j);
		}
		listener.onActionResult(PROTOKOLL.A_GET_CHAT, new JSONObject().put("messages", msgs).put("success", 1) );
		
	}
	
	private void addMessage(String message,String username){
		JSONObject msg = new JSONObject();
		msg.put("sender", username);
		msg.put("msg", message);

		msg.put("time", Helper.getCurrentTimeString());
		
		if(messages.size()>50){
			messages.subList(0, 10).clear();
		}
		
		messages.add(msg);
	}
	
	
	@SubscribeEvent
	public void onChatMessage(ServerChatEvent e){
        addMessage(e.getMessage(), e.getUsername());
    }
	
	@SubscribeEvent
	public void onRemoteMessage(RemoteChatMessageEvent e){
		addMessage(e.msg,"mss~"+e.username);
	}

}
