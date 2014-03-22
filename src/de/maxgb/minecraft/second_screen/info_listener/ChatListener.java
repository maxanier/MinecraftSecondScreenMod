package de.maxgb.minecraft.second_screen.info_listener;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ServerChatEvent;

import org.json.JSONArray;
import org.json.JSONObject;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;

import de.maxgb.minecraft.second_screen.Configs;
import de.maxgb.minecraft.second_screen.StandardListener;
import de.maxgb.minecraft.second_screen.util.PROTOKOLL;

public class ChatListener extends StandardListener {

	private JSONArray buffer;
	
	public ChatListener(String params) {
		super(params);
		buffer = new JSONArray();
		MinecraftForge.EVENT_BUS.register(this);
		everyTick=Configs.chat_update_time;
	}

	@Override
	public String update() {
		if(buffer.length()==0){
			return null;
		}
		JSONObject response = new JSONObject();
		response.put("success", 1);
		response.put("messages", buffer);
		buffer=new JSONArray();
		
		return PROTOKOLL.CHAT_LISTENER+":"+response.toString();
	}
	
	
	@SubscribeEvent
	public void chatMessage(ServerChatEvent e){
		JSONObject msg = new JSONObject();
		msg.put("sender", e.username);
		msg.put("msg", e.message);
		msg.put("time", System.currentTimeMillis());
		buffer.put(msg);
	}
	
	
	

}
