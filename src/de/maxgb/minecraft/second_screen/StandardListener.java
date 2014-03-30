package de.maxgb.minecraft.second_screen;

import net.minecraft.server.MinecraftServer;
import cpw.mods.fml.common.FMLCommonHandler;
import de.maxgb.minecraft.second_screen.util.User;

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
	 * @return Message which should be sent, null if none
	 */
	public String tick() {
		if (tick < 1) {
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

}
