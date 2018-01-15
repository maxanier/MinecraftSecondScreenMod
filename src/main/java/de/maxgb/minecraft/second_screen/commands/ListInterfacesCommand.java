package de.maxgb.minecraft.second_screen.commands;

import de.maxgb.minecraft.second_screen.util.Helper;
import net.minecraft.block.Block;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import org.apache.commons.lang3.ClassUtils;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
	public int compareTo(ICommand arg0) {

		return 0;
	}


	@Override
	public String getUsage(ICommandSender var1) {
		return "/listinterfaces";
	}

	@Override
	public boolean isUsernameIndex(String[] var1, int var2) {
		return false;
	}

	@Override
	public String getName() {
		return "listinterfaces";
	}

	@Override
	public List getAliases() {
		return aliases;
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender var1, String[] var2)
			throws CommandException {
		EntityPlayer player;

		if (var1 instanceof EntityPlayer) {
			player = (EntityPlayer) var1;
		} else {
			sendMessage(var1, "Player only command");
			return;
		}

		RayTraceResult p = Helper.getPlayerLookingSpot(player, true);
		if (p == null) {
			sendMessage(var1, "You have to look at a block");
			return;
		}


		Block block = player.getEntityWorld().getBlockState(p.getBlockPos()).getBlock();
		TileEntity tile = player.getEntityWorld().getTileEntity(p.getBlockPos());
		
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

	@Override
	public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
		return true;
	}

	@Override
	public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos) {
		return Collections.emptyList();
	}
}
