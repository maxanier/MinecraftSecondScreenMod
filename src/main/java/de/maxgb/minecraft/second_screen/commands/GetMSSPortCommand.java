package de.maxgb.minecraft.second_screen.commands;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.command.ICommandSender;
import de.maxgb.minecraft.second_screen.SecondScreenMod;

/**
 * Returns the port the mod listens to
 * @author Max
 *
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public class GetMSSPortCommand extends BaseCommand {

	private List aliase;

	public GetMSSPortCommand() {
		aliase = new ArrayList();
		aliase.add("getmssport");
		aliase.add("getMSSPort");
		aliase.add("getmssPort");
	}

	@Override
	public List addTabCompletionOptions(ICommandSender var1, String[] var2) {

		return null;
	}

	@Override
	public boolean canCommandSenderUseCommand(ICommandSender var1) {
		return true;
	}

	@Override
	public int compareTo(Object o) {

		return 0;
	}

	@Override
	public List getCommandAliases() {
		return aliase;
	}

	@Override
	public String getCommandName() {
		return "getMssPort";
	}

	@Override
	public String getCommandUsage(ICommandSender var1) {
		return "/getMssPort";
	}

	@Override
	public boolean isUsernameIndex(String[] var1, int var2) {

		return false;
	}

	@Override
	public void processCommand(ICommandSender var1, String[] var2) {
		if (SecondScreenMod.instance.webSocketListener.isRunning()) {
			sendMessage(var1, "Minecraft Second Screen Port: " + SecondScreenMod.instance.port);
		} else {

			sendMessage(var1, "Minecraft Second Screen Port: " + SecondScreenMod.instance.port
					+ ". But the mod isnt running. Error: " + SecondScreenMod.instance.webSocketListener.getError());
		}

	}

}
