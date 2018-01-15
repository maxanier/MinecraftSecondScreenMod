package de.maxgb.minecraft.second_screen.info_listener;

import de.maxgb.minecraft.second_screen.Configs;
import de.maxgb.minecraft.second_screen.StandardListener;
import de.maxgb.minecraft.second_screen.shared.PROTOKOLL;
import de.maxgb.minecraft.second_screen.util.Helper;
import de.maxgb.minecraft.second_screen.util.User;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.event.entity.player.PlayerDropsEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Listener which listens to chat messages and other chat related events
 * @author Max
 *
 */
public class ChatListener extends StandardListener {

	/**
	 * Is fired on MinecraftForge.Bus
	 *
	 */
	public static class RemoteChatMessageEvent extends Event {
		public final String username;
		public final String msg;

		public RemoteChatMessageEvent(String username, String message) {
			this.username = username;
			this.msg = message;
		}
	}

	private JSONArray buffer;

	public ChatListener(User user) {
		super(user);
		buffer = new JSONArray();
		MinecraftForge.EVENT_BUS.register(this);
		FMLCommonHandler.instance().bus().register(this);
		everyTick = Configs.chat_update_time;

		JSONObject msg = new JSONObject();
		msg.put("info", true);
		msg.put("color", "orange");
		msg.put("time", Helper.getCurrentTimeString());
		msg.put("msg", "Connected to chat");
		buffer.put(msg);

        Helper.sendChatMessage("[MSS] " + user.username + " connected to second screen chat", TextFormatting.YELLOW);
    }

	@SubscribeEvent
	public void chatMessage(ServerChatEvent e) {

		JSONObject msg = new JSONObject();
        msg.put("sender", e.getUsername());
        msg.put("msg", e.getMessage());

		msg.put("time", Helper.getCurrentTimeString());
		buffer.put(msg);
	}


	@SubscribeEvent
	public void mssChatMessage(RemoteChatMessageEvent e) {
		JSONObject msg = new JSONObject();
		msg.put("sender", "mss~" + e.username);
		msg.put("msg", e.msg);

		msg.put("time", Helper.getCurrentTimeString());
		buffer.put(msg);
		
	}

	@SubscribeEvent
	public void playerDies(PlayerDropsEvent e) {
		JSONObject msg = new JSONObject();
		msg.put("info", true);
		msg.put("color", "orange");
		msg.put("time", Helper.getCurrentTimeString());
        msg.put("msg", e.getEntityPlayer().getDisplayName() + " died");
        buffer.put(msg);
	}

	@SubscribeEvent
	public void playerJoined(PlayerEvent.PlayerLoggedInEvent e) {
		JSONObject msg = new JSONObject();
		msg.put("info", true);
		msg.put("color", "orange");
		msg.put("time", Helper.getCurrentTimeString());
		msg.put("msg", e.player.getDisplayName() + " joined");
		buffer.put(msg);

	}

	@SubscribeEvent
	public void playerLeft(PlayerEvent.PlayerLoggedOutEvent e) {
		JSONObject msg = new JSONObject();
		msg.put("info", true);
		msg.put("color", "orange");
		msg.put("time", Helper.getCurrentTimeString());
		msg.put("msg", e.player.getDisplayName() + " left");
		msg.put("success", 1);
		buffer.put(msg);

	}

	@Override
	public String update() {
		if (buffer.length() == 0) {
			return null;
		}
		JSONObject response = new JSONObject();
		response.put("success", 1);
		response.put("messages", buffer);
		buffer = new JSONArray();

		return PROTOKOLL.CHAT_LISTENER + "-" + response.toString();
	}
	
	@Override
	public void onUnregister(){
        Helper.sendChatMessage("[MSS] " + user.username + " disconnected from second screen chat", TextFormatting.YELLOW);
    }

}
