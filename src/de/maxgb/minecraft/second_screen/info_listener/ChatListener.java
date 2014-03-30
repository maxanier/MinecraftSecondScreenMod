package de.maxgb.minecraft.second_screen.info_listener;

import java.util.Date;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.event.entity.player.PlayerDropsEvent;

import org.json.JSONArray;
import org.json.JSONObject;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.Event;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;

import de.maxgb.minecraft.second_screen.Configs;
import de.maxgb.minecraft.second_screen.StandardListener;
import de.maxgb.minecraft.second_screen.shared.PROTOKOLL;
import de.maxgb.minecraft.second_screen.util.User;

public class ChatListener extends StandardListener {

	private JSONArray buffer;
	
	public static class RemoteChatMessageEvent extends Event{
		public final String username;
		public final String msg;
		
		public RemoteChatMessageEvent(String username,String message){
			this.username=username;
			this.msg=message;
		}
	}
	
	public ChatListener(User user) {
		super(user);
		buffer = new JSONArray();
		MinecraftForge.EVENT_BUS.register(this);
		FMLCommonHandler.instance().bus().register(this);
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
	public void mssChatMessage(RemoteChatMessageEvent e){
		JSONObject msg = new JSONObject();
		msg.put("sender", "mss~"+e.username);
		msg.put("msg", e.msg);
		
		
		msg.put("time", getCurrentTimeString());
		buffer.put(msg);
	}
	
	@SubscribeEvent
	public void chatMessage(ServerChatEvent e){
		
		JSONObject msg = new JSONObject();
		msg.put("sender", e.username);
		msg.put("msg", e.message);
		
		
		msg.put("time", getCurrentTimeString());
		buffer.put(msg);
	}
	
	@SubscribeEvent
	public void playerDies(PlayerDropsEvent e){
		JSONObject msg = new JSONObject();
		msg.put("info",true);
		msg.put("color", "orange");
		msg.put("time", getCurrentTimeString());
		msg.put("msg", e.entityPlayer.getDisplayName()+" died");
		msg.put("success", 1);
		buffer.put(msg);
	}
	
	@SubscribeEvent
	public void playerJoined(PlayerEvent.PlayerLoggedInEvent e){
		JSONObject msg = new JSONObject();
		msg.put("info",true);
		msg.put("color", "orange");
		msg.put("time", getCurrentTimeString());
		msg.put("msg", e.player.getDisplayName()+" joined");
		msg.put("success", 1);
		buffer.put(msg);
		
	}
	
	@SubscribeEvent
	public void playerLeft(PlayerEvent.PlayerLoggedOutEvent e){
		JSONObject msg = new JSONObject();
		msg.put("info",true);
		msg.put("color", "orange");
		msg.put("time", getCurrentTimeString());
		msg.put("msg", e.player.getDisplayName()+" left");
		msg.put("success", 1);
		buffer.put(msg);
		
	}
	
	private String getCurrentTimeString(){
		Date timeDate=new Date(System.currentTimeMillis());
		String min=""+timeDate.getMinutes();
		if(min.length()<2){
			min="0"+min;
		}
		String h=""+timeDate.getHours();
		if(h.length()<2){
			h="0"+h;
		}
		return h+":"+min;
	}
	

}
