package de.maxgb.minecraft.second_screen.commands.mss_sub;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.command.ICommandSender;
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
		return "mss";
	}

	@Override
	public String getCommandUsage(ICommandSender var1) {

		return "/mss <action> <params>";

	}

	@Override
	public boolean isUsernameIndex(String[] var1, int var2) {

		return false;
	}

	@Override
	public void processCommand(ICommandSender var1, String[] var2) {
		if (var2 == null || var2.length == 0) {
			BaseCommand.sendMessage(var1, "Usage: " + getCommandUsage(var1));
			return;
		}
		//Tests for the corrosponding subcommand and calls it with the reduced amount of params
		for (MssSubCommand c : commands) {
			if (var2[0].equals(c.getCommandName())) {
				String[] var;
				if (var2.length == 1) {
					var = null;
				} else {
					var = new String[var2.length - 1];
					for (int i = 1; i < var2.length; i++) {
						var[i - 1] = var2[i];
					}
				}

				c.processCommand(var1, var);
				return;
			}
		}
		if (!var2[0].equals("help")) {
			BaseCommand.sendMessage(var1, "Action not found.");
		}
		sendActions(var1);

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

}
