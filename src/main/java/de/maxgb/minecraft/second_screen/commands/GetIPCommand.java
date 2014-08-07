package de.maxgb.minecraft.second_screen.commands;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.command.ICommandSender;
import de.maxgb.minecraft.second_screen.SecondScreenMod;

/**
 * Returns the IP the mod is running on or if not running the error message
 * @author Max
 *
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public class GetIPCommand extends BaseCommand {

	private List aliases;

	
	public GetIPCommand() {
		this.aliases = new ArrayList();
		this.aliases.add("getIP");
		this.aliases.add("getip");
		this.aliases.add("getIp");
		this.aliases.add("gethostname");
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
	public int compareTo(Object arg0) {

		return 0;
	}

	@Override
	public List getCommandAliases() {
		return aliases;
	}

	@Override
	public String getCommandName() {
		return "getIP";
	}

	@Override
	public String getCommandUsage(ICommandSender var1) {
		return "/getIP";
	}

	@Override
	public boolean isUsernameIndex(String[] var1, int var2) {
		return false;
	}

	@Override
	public void processCommand(ICommandSender var1, String[] var2) {
		if (SecondScreenMod.instance.webSocketListener.isRunning()) {
			sendMessage(var1, "IP: " + SecondScreenMod.instance.hostname);
		} else {
			
			sendMessage(var1, "IP: " + SecondScreenMod.instance.hostname + ". But the mod isnt running. Error: "
					+ SecondScreenMod.instance.webSocketListener.getError());
		}

	}

}
