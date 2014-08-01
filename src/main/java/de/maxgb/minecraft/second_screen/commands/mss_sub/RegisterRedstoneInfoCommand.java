package de.maxgb.minecraft.second_screen.commands.mss_sub;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLever;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MovingObjectPosition;
import de.maxgb.minecraft.second_screen.commands.BaseCommand;
import de.maxgb.minecraft.second_screen.data.ObservingManager;
import de.maxgb.minecraft.second_screen.util.Helper;
import de.maxgb.minecraft.second_screen.util.Logger;
import de.maxgb.minecraft.second_screen.world.ObservedBlock;
import de.maxgb.minecraft.second_screen.world.ObservingType;

public class RegisterRedstoneInfoCommand implements MssCommand.MssSubCommand {

	private static final String TAG = "RegisterRedstoneCommand";

	public RegisterRedstoneInfoCommand() {

	}

	@Override
	public boolean canCommandSenderUseCommand(ICommandSender var1) {
		if (var1 instanceof EntityPlayer) {
			return true;
		}
		return false;
	}

	@Override
	public String getCommandName() {
		return "redinfo";
	}

	@Override
	public String getCommandUsage(ICommandSender var1) {
		return "redinfo add <label> or redinfo remove <label>";
	}

	@Override
	public void processCommand(ICommandSender var1, String[] var2) {

		if (var2 == null || var2.length < 2) {
			sendMessage(var1, "Invalid arguments. Usage: " + getCommandUsage(var1));
			return;
		}

		if (var2[0].equals("add")) {

			// Get player
			EntityPlayer player;

			if (var1 instanceof EntityPlayer) {
				player = (EntityPlayer) var1;
			} else {
				sendMessage(var1, "Player only command");
				return;
			}

			// If the block should be observed publicly
			boolean publ = false;
			if (var2.length >= 3) {
				if (var2[2].equals("public")) {
					publ = true;
				}
			}

			// Get Block
			MovingObjectPosition p = Helper.getPlayerLookingSpot(player, true);
			if (p == null) {
				sendMessage(var1, "You have to look at a block");
				return;
			}
			Block b = player.worldObj.getBlock(p.blockX, p.blockY, p.blockZ);

			sendMessage(var1,
					"You are looking at: " + p.blockX + "," + p.blockY + "," + p.blockZ + " " + b.getLocalizedName());

			if (b instanceof BlockLever) {
				Logger.i(TAG, "Registering lever");
			} else if (!b.isNormalCube()) {
				Logger.w(TAG, "Block is not a normal cube");
				sendMessage(var1, "You can only register solid blocks or levers");
				return;
			}

			if (ObservingManager.observeBlock(var1.getCommandSenderName(), publ, new ObservedBlock(var2[1], p.blockX,
					p.blockY, p.blockZ, player.worldObj.provider.dimensionId, ObservingType.REDSTONE))) {
				sendMessage(var1, "Successfully added block to observer list.");
			} else {
				sendMessage(var1,
						"Successfully added block to observer list, but overrode another block with the same label");
			}
			// var1.addChatMessage(new
			// ChatComponentText(""+player.worldObj.isBlockIndirectlyGettingPowered(p.blockX,
			// p.blockY, p.blockZ)));
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
