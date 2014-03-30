package de.maxgb.minecraft.second_screen.commands;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.command.ICommandSender;
import de.maxgb.minecraft.second_screen.SecondScreenMod;

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
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean canCommandSenderUseCommand(ICommandSender var1) {
		return true;
	}

	@Override
	public int compareTo(Object o) {
		// TODO Auto-generated method stub
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
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void processCommand(ICommandSender var1, String[] var2) {
		if (SecondScreenMod.connected) {
			sendMessage(var1, "Minecraft Second Screen Port: "
					+ SecondScreenMod.port);
		} else {
			if (SecondScreenMod.error == null) {
				SecondScreenMod.error = "Unknown";
			}
			sendMessage(var1, "Minecraft Second Screen Port: "
					+ SecondScreenMod.port
					+ ". But the mod isnt running. Error: "
					+ SecondScreenMod.error);
		}

	}

}
