package de.maxgb.minecraft.second_screen;

import net.minecraft.server.MinecraftServer;
import cpw.mods.fml.common.FMLCommonHandler;
import de.maxgb.minecraft.second_screen.util.User;

/**
 * Abstract class which all Listeners have to extend
 * @author Max
 *
 */
public abstract class StandardListener {

	protected User user;
	protected MinecraftServer server;
	protected int everyTick = 0;
	private int tick;
	private String lastMessage;

	public StandardListener(User user) {
		this.user = user;
		server = FMLCommonHandler.instance().getMinecraftServerInstance();
		tick = 0;
	}

	/**
	 * Is called every tick
	 * 
	 * @param force
	 *            Whether to force an update or to only update if its time this
	 *            tick
	 * @return Message which should be sent, null if none
	 */
	public String tick(boolean force) {
		if (tick < 1 || force) {
			tick = everyTick;
			String newMessage = update();
			if (newMessage == null) {
				return null;
			}
			if (!newMessage.equals(lastMessage)) {
				lastMessage = newMessage;
				return newMessage;
			}
		}
		tick--;
		return null;
	}

	/**
	 * Is called every {@link everyTick} tick. Should check if there are any
	 * updates which need to be sent to the client. The message is only sent if
	 * it is not the same as last time.
	 * 
	 * @return Null if there is no update, otherwise a String which is sent to
	 *         the client listener
	 */
	public abstract String update();
	
	/**
	 * Is called when the listener is removed.
	 * There might be a few situations where this fails.
	 */
	public void onUnregister(){
		
	}

}
