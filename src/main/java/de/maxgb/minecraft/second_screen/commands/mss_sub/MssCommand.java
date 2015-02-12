package de.maxgb.minecraft.second_screen.commands.mss_sub;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.BlockPos;
import de.maxgb.minecraft.second_screen.commands.BaseCommand;

/**
 * Manages all commands which start with mss
 * @author Max
 *
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public class MssCommand extends BaseCommand {
	protected interface MssSubCommand {
		public boolean canCommandSenderUseCommand(ICommandSender var1);

		public String getCommandName();

		public void processCommand(ICommandSender var1, String[] var2);

		public void sendCommandUsage(ICommandSender var1);
	}

	private List aliases;

	private ArrayList<MssSubCommand> commands;

	public MssCommand() {
		this.aliases = new ArrayList();
		this.aliases.add("mss");
		this.aliases.add("minecraftsecondscreen");
		this.aliases.add("secondscreen");
		commands = new ArrayList<MssSubCommand>();
	}

	public void addSubCommand(MssSubCommand c) {
		commands.add(c);
	}


	@Override
	public int compareTo(Object arg0) {

		return 0;
	}


	@Override
	public String getCommandUsage(ICommandSender var1) {

		return "/mss <action> <params>";

	}

	@Override
	public boolean isUsernameIndex(String[] var1, int var2) {

		return false;
	}


	/**
	 * Prints the available actions/subcommands to the command sender
	 * @param var1
	 */
	private void sendActions(ICommandSender var1) {
		sendMessage(var1, getCommandUsage(var1));
		sendMessage(var1, "Actions:");
		for (MssSubCommand c : commands) {
			c.sendCommandUsage(var1);
		}
	}

	@Override
	public String getName() {
		return "mss";
	}

	@Override
	public List getAliases() {
		return aliases;
	}

	@Override
	public void execute(ICommandSender sender, String[] args)
			throws CommandException {
		if (args == null || args.length == 0) {
			BaseCommand.sendMessage(sender, "Usage: " + getCommandUsage(sender));
			return;
		}
		//Tests for the corrosponding subcommand and calls it with the reduced amount of params
		for (MssSubCommand c : commands) {
			if (args[0].equals(c.getCommandName())) {
				String[] var;
				if (args.length == 1) {
					var = null;
				} else {
					var = new String[args.length - 1];
					for (int i = 1; i < args.length; i++) {
						var[i - 1] = args[i];
					}
				}

				c.processCommand(sender, var);
				return;
			}
		}
		if (!args[0].equals("help")) {
			BaseCommand.sendMessage(sender, "Action not found.");
		}
		sendActions(sender);
		
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
