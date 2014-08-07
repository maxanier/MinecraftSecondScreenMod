package de.maxgb.minecraft.second_screen.commands;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.ClassUtils;

import net.minecraft.block.Block;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MovingObjectPosition;
import cpw.mods.fml.common.FMLCommonHandler;
import de.maxgb.minecraft.second_screen.util.Helper;

/**
 * Command for development purpose
 * Returns all classes and interfaces the looked at block extends/implements
 * @author Max
 *
 */
@SuppressWarnings({ "rawtypes" })
public class ListInterfacesCommand extends BaseCommand {

	private List aliases;

	public ListInterfacesCommand() {
		this.aliases = new ArrayList();
		aliases.add("liint");
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
		return "listinterfaces";
	}

	@Override
	public String getCommandUsage(ICommandSender var1) {
		return "/listinterfaces";
	}

	@Override
	public boolean isUsernameIndex(String[] var1, int var2) {
		return false;
	}

	@Override
	public void processCommand(ICommandSender var1, String[] var2) {
		EntityPlayer player;

		if (var1 instanceof EntityPlayer) {
			player = (EntityPlayer) var1;
		} else {
			sendMessage(var1, "Player only command");
			return;
		}

		MovingObjectPosition p = Helper.getPlayerLookingSpot(player, true);
		if (p == null) {
			sendMessage(var1, "You have to look at a block");
			return;
		}


		Block block = player.worldObj.getBlock(p.blockX, p.blockY, p.blockZ);
		TileEntity tile = player.worldObj.getTileEntity(p.blockX, p.blockY, p.blockZ);
		
		if(block!=null){
			sendMessage(var1,block.getClass().getName());
			for(Class c : ClassUtils.getAllInterfaces(block.getClass())){
				sendMessage(var1,c.getName());
			}
		}
		if(tile!=null){
			sendMessage(var1,tile.getClass().getName());
			for(Class c : ClassUtils.getAllInterfaces(tile.getClass())){
				sendMessage(var1,c.getName());
			}
		}

	}

}
