package de.maxgb.minecraft.second_screen.commands;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.command.ICommandSender;
import cpw.mods.fml.common.FMLCommonHandler;

@SuppressWarnings({ "rawtypes" })
public class TestCommand extends BaseCommand {

	private List aliases;

	public TestCommand() {
		this.aliases = new ArrayList();
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
		return "msstest";
	}

	@Override
	public String getCommandUsage(ICommandSender var1) {
		return "msstest";
	}

	@Override
	public boolean isUsernameIndex(String[] var1, int var2) {
		return false;
	}

	@Override
	public void processCommand(ICommandSender var1, String[] var2) {
		sendMessage(var1, FMLCommonHandler.instance().getMinecraftServerInstance().getConfigurationManager()
				.func_152603_m().toString());

	}

}
