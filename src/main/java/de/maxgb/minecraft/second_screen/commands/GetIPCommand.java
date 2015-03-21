package de.maxgb.minecraft.second_screen.commands;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.BlockPos;
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
	public int compareTo(Object arg0) {

		return 0;
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
	public String getName() {
		return "getIP";
	}

	@Override
	public List getAliases() {
		return aliases;
	}

	@Override
	public void execute(ICommandSender sender, String[] args)
			throws CommandException {
		if (SecondScreenMod.instance.webSocketListener.isRunning()) {
			sendMessage(sender, "IP: " + SecondScreenMod.instance.hostname);
		} else {
			
			sendMessage(sender, "IP: " + SecondScreenMod.instance.hostname + ". But the mod isnt running. Error: "
					+ SecondScreenMod.instance.webSocketListener.getError());
		}
		
	}

	@Override
	public boolean canCommandSenderUse(ICommandSender sender) {
		return true;
	}

	@Override
	public List addTabCompletionOptions(ICommandSender sender, String[] args,
			BlockPos pos) {
		return null;
	}

}
