package de.maxgb.minecraft.second_screen.commands.mss_sub;

import net.minecraft.block.Block;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MovingObjectPosition;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.nodes.INode;
import de.maxgb.minecraft.second_screen.commands.BaseCommand;
import de.maxgb.minecraft.second_screen.data.ObservingManager;
import de.maxgb.minecraft.second_screen.util.Helper;
import de.maxgb.minecraft.second_screen.util.Logger;
import de.maxgb.minecraft.second_screen.world.ObservedBlock;
import de.maxgb.minecraft.second_screen.world.ObservingType;

public class RegisterObserverCommand implements MssCommand.MssSubCommand {

	private final String TAG = "RegisterObserverCommand";

	@Override
	public boolean canCommandSenderUseCommand(ICommandSender var1) {
		if (var1 instanceof EntityPlayer) {
			return true;
		}
		return false;
	}

	@Override
	public String getCommandName() {
		return "observer";
	}

	@Override
	public String getCommandUsage(ICommandSender var1) {
		return "observer add <label> <private/public> or observer remove <label>";
	}

	@Override
	public void processCommand(ICommandSender var1, String[] var2) {

		if (var2 == null || var2.length < 2) {
			sendMessage(var1, "Invalid arguments. Usage: " + getCommandUsage(var1));
			return;
		}

		if (var2[0].equals("add")) {

			EntityPlayer player;

			if (var1 instanceof EntityPlayer) {
				player = (EntityPlayer) var1;
			} else {
				sendMessage(var1, "Player only command");
				return;
			}

			MovingObjectPosition p = Helper.getPlayerLookingSpot(player, true);
			if (p == null || p.typeOfHit!=MovingObjectPosition.MovingObjectType.BLOCK) {
				sendMessage(var1, "You have to look at a block");
				return;
			}
			

			boolean publ = false;
			if (var2.length >= 3) {
				if (var2[2].equals("public")) {
					publ = true;
				}
			}

			Block block = player.worldObj.getBlock(p.blockX, p.blockY, p.blockZ);
			TileEntity tile = player.worldObj.getTileEntity(p.blockX, p.blockY, p.blockZ);
			
			int type=ObservingType.retrieveObservingType(block, tile);
			if(type!=ObservingType.NOTHING){
				if (ObservingManager.observeBlock(var1.getCommandSenderName(), publ, new ObservedBlock(var2[1],
						p.blockX, p.blockY, p.blockZ, player.worldObj.provider.dimensionId, type,p.sideHit))) {
					sendMessage(var1, "Successfully added block to observer list.");
				} else {
					sendMessage(var1,
							"Successfully added block to observer list, but overrode another block with the same label");
				}
			}
			else{
				sendMessage(var1, "This block cannot be observed");
			}
			
			

			

		} else if (var2[0].equals("remove")) {
			if (ObservingManager.removeObservedBlock(var1.getCommandSenderName(), var2[1])) {
				sendMessage(var1, "Successfully removed block from observer list");
			} else {
				sendMessage(var1, "Failed to remove block from observer list. There is no block with this label");
			}
		} else {
			sendMessage(var1, "Invalid arguments. Usage: " + getCommandUsage(var1));
			return;

		}

	}

	private void sendMessage(ICommandSender var1, String msg) {
		BaseCommand.sendMessage(var1, msg);
	}

}
