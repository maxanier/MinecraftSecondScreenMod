package de.maxgb.minecraft.second_screen.commands;

import de.maxgb.minecraft.second_screen.SecondScreenMod;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.BlockPos;

import java.util.ArrayList;
import java.util.List;

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
	public int compareTo(ICommand o) {

		return 0;
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
	public String getCommandName() {
		return "getMssPort";
	}

	@Override
	public List getCommandAliases() {
		return aliase;
	}

	@Override
	public void processCommand(ICommandSender sender, String[] args)
			throws CommandException {
		if (SecondScreenMod.instance.webSocketListener.isRunning()) {
			sendMessage(sender, "Minecraft Second Screen Port: " + SecondScreenMod.instance.port);
		} else {

			sendMessage(sender, "Minecraft Second Screen Port: " + SecondScreenMod.instance.port
					+ ". But the mod isnt running. Error: " + SecondScreenMod.instance.webSocketListener.getError());
		}
		
	}

	@Override
	public boolean canCommandSenderUseCommand(ICommandSender sender) {
		return true;
	}

	@Override
	public List addTabCompletionOptions(ICommandSender sender, String[] args,
			BlockPos pos) {
		return null;
	}

}
