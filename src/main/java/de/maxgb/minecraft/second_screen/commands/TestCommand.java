package de.maxgb.minecraft.second_screen.commands;

import java.util.ArrayList;
import java.util.List;

import cpw.mods.fml.common.FMLCommonHandler;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import de.maxgb.minecraft.second_screen.data.ObservingManager;
import de.maxgb.minecraft.second_screen.world.ObservedBlock;

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
		sendMessage(var1,FMLCommonHandler.instance()
		.getMinecraftServerInstance().getConfigurationManager().func_152603_m().toString());
		
		
		

	}
	


}
