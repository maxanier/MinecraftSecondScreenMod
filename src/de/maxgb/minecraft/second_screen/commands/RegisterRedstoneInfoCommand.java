package de.maxgb.minecraft.second_screen.commands;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import de.maxgb.minecraft.second_screen.world.ObservingRegistry;

public class RegisterRedstoneInfoCommand implements ICommand {

	/**
	 * Gets player´s looking spot.
	 * 
	 * @param player
	 * @param restricts
	 *            Keeps distance to players block reach distance
	 * @return The position as a MovingObjectPosition, null if not existent cf:
	 *         https
	 *         ://github.com/ForgeEssentials/ForgeEssentialsMain/blob/master
	 *         /src/main/java/com/forgeessentials/util/FunctionHelper.java
	 */
	public static MovingObjectPosition getPlayerLookingSpot(
			EntityPlayer player, boolean restrict) {
		float var4 = 1.0F;
		float var5 = player.prevRotationPitch
				+ (player.rotationPitch - player.prevRotationPitch) * var4;
		float var6 = player.prevRotationYaw
				+ (player.rotationYaw - player.prevRotationYaw) * var4;
		double var7 = player.prevPosX + (player.posX - player.prevPosX) * var4;
		double var9 = player.prevPosY + (player.posY - player.prevPosY) * var4
				+ 1.62D - player.yOffset;
		double var11 = player.prevPosZ + (player.posZ - player.prevPosZ) * var4;
		Vec3 var13 = player.worldObj.getWorldVec3Pool().getVecFromPool(var7,
				var9, var11);
		float var14 = MathHelper.cos(-var6 * 0.017453292F - (float) Math.PI);
		float var15 = MathHelper.sin(-var6 * 0.017453292F - (float) Math.PI);
		float var16 = -MathHelper.cos(-var5 * 0.017453292F);
		float var17 = MathHelper.sin(-var5 * 0.017453292F);
		float var18 = var15 * var16;
		float var20 = var14 * var16;
		double var21 = 500D;
		if (player instanceof EntityPlayerMP && restrict) {
			var21 = ((EntityPlayerMP) player).theItemInWorldManager
					.getBlockReachDistance();
		}
		Vec3 var23 = var13.addVector(var18 * var21, var17 * var21, var20
				* var21);
		return player.worldObj.rayTraceBlocks(var13, var23);
		// return player.worldObj.rayTraceBlocks_do_do(var13, var23, false,
		// !true);
	}

	private List aliases;

	public RegisterRedstoneInfoCommand() {
		this.aliases = new ArrayList();
		this.aliases.add("redstoneinfo");
		this.aliases.add("redinfo");
	}

	@Override
	public List addTabCompletionOptions(ICommandSender var1, String[] var2) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean canCommandSenderUseCommand(ICommandSender var1) {
		if (var1.getCommandSenderName() == "Rcon") {
			return false;
		}
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
		return "redstoneinfo";
	}

	@Override
	public String getCommandUsage(ICommandSender var1) {
		return "/redstoneinfo add <label> or /redstoneinfo remove <label>";
	}

	@Override
	public boolean isUsernameIndex(String[] var1, int var2) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void processCommand(ICommandSender var1, String[] var2) {

		if (var2.length < 2) {
			sendMessage(var1, "Invalid arguments. Usage: "
					+ getCommandUsage(var1));
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

			MovingObjectPosition p = getPlayerLookingSpot(player, true);
			if (p == null) {
				sendMessage(var1, "You have to look at a block");
				return;
			}

			sendMessage(var1, "You are looking at: " + p.blockX + ","
					+ p.blockY + "," + p.blockZ);

			if (ObservingRegistry.observeBlock(var2[1], p.blockX, p.blockY,
					p.blockZ, player.worldObj.provider.dimensionId)) {
				sendMessage(var1, "Successfully added block to observer list.");
			} else {
				sendMessage(
						var1,
						"Successfully added block to observer list, but overrode another block with the same label");
			}
			// var1.addChatMessage(new
			// ChatComponentText(""+player.worldObj.isBlockIndirectlyGettingPowered(p.blockX,
			// p.blockY, p.blockZ)));
		} else if (var2[0].equals("remove")) {
			if (ObservingRegistry.removeObservedBlock(var2[1])) {
				sendMessage(var1,
						"Successfully removed block from observer list");
			} else {
				sendMessage(var1,
						"Failed to remove block from observer list. There is no block with this label");
			}
		} else {
			sendMessage(var1, "Invalid arguments. Usage: "
					+ getCommandUsage(var1));
			return;

		}

	}

	private void sendMessage(ICommandSender target, String message) {
		target.addChatMessage(new ChatComponentText(message));
	}

}
