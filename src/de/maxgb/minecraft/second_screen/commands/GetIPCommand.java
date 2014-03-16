package de.maxgb.minecraft.second_screen.commands;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;
import de.maxgb.minecraft.second_screen.SecondScreenMod;

public class GetIPCommand implements ICommand {

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
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean canCommandSenderUseCommand(ICommandSender var1) {
		return true;
	}

	@Override
	public int compareTo(Object arg0) {
		// TODO Auto-generated method stub
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
		return "getIP";
	}

	@Override
	public boolean isUsernameIndex(String[] var1, int var2) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void processCommand(ICommandSender var1, String[] var2) {
		if (SecondScreenMod.connected) {
			sendMessage(var1, "IP: " + SecondScreenMod.hostname);
		} else {
			if (SecondScreenMod.error == null) {
				SecondScreenMod.error = "Unknown";
			}
			sendMessage(var1, "IP: " + SecondScreenMod.hostname
					+ ". But the mod isn´t running. Error: "
					+ SecondScreenMod.error);
		}

	}

	private void sendMessage(ICommandSender target, String message) {
		target.addChatMessage(new ChatComponentText(message));
	}

}
