package de.maxgb.minecraft.second_screen.util;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;

public class Helper {
	
	/**
	 * Gets players looking spot.
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
}
