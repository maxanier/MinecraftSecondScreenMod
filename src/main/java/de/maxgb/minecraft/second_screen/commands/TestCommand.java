package de.maxgb.minecraft.second_screen.commands;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.BlockPos;

import java.util.ArrayList;
import java.util.List;

/**
 * Test command for developent purpose
 * @author Max
 *
 */
@SuppressWarnings({ "rawtypes" })
public class TestCommand extends BaseCommand {

	private List aliases;

	public TestCommand() {
		this.aliases = new ArrayList();
	}


	@Override
	public int compareTo(ICommand arg0) {

		return 0;
	}


	@Override
	public String getCommandUsage(ICommandSender var1) {
		return "/msstest";
	}

	@Override
	public boolean isUsernameIndex(String[] var1, int var2) {
		return false;
	}


	@Override
	public String getCommandName() {
		return "msstest";
	}

	@Override
	public List getCommandAliases() {
		return aliases;
	}

	@Override
	public void processCommand(ICommandSender sender, String[] args)
			throws CommandException {
		
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
