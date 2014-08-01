package de.maxgb.minecraft.second_screen.util;

import com.mojang.authlib.GameProfile;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import cpw.mods.fml.common.FMLCommonHandler;

public class Helper {
	
	/**
	 * Gets players looking spot.
	 * 
	 * @param player
	 * @param restricts
	 *            Keeps distance to players block reach distance
	 * @return The position as a MovingObjectPosition, null if not existent cf:
	 *         https://github.com/bspkrs/bspkrsCore/blob/master/src/main/java/bspkrs/util/CommonUtils.java
	 */
    public static MovingObjectPosition getPlayerLookingSpot(EntityPlayer player, boolean restrict)
    {
        float scale = 1.0F;
        float pitch = player.prevRotationPitch + (player.rotationPitch - player.prevRotationPitch) * scale;
        float yaw = player.prevRotationYaw + (player.rotationYaw - player.prevRotationYaw) * scale;
        double x = player.prevPosX + (player.posX - player.prevPosX) * scale;
        double y = player.prevPosY + (player.posY - player.prevPosY) * scale + 1.62D - player.yOffset;
        double z = player.prevPosZ + (player.posZ - player.prevPosZ) * scale;
        Vec3 vector1 = Vec3.createVectorHelper(x, y, z);
        float cosYaw = MathHelper.cos(-yaw * 0.017453292F - (float) Math.PI);
        float sinYaw = MathHelper.sin(-yaw * 0.017453292F - (float) Math.PI);
        float cosPitch = -MathHelper.cos(-pitch * 0.017453292F);
        float sinPitch = MathHelper.sin(-pitch * 0.017453292F);
        float pitchAdjustedSinYaw = sinYaw * cosPitch;
        float pitchAdjustedCosYaw = cosYaw * cosPitch;
        double distance = 500D;
        if (player instanceof EntityPlayerMP && restrict)
        {
            distance = ((EntityPlayerMP) player).theItemInWorldManager.getBlockReachDistance();
        }
        Vec3 vector2 = vector1.addVector(pitchAdjustedSinYaw * distance, sinPitch * distance, pitchAdjustedCosYaw * distance);
        return player.worldObj.rayTraceBlocks(vector1, vector2);
    }
	
    /**
     * Returns if the user with the given username is opped on this server. Only usable when online
     * @param username
     * @return
     */
	public static boolean isPlayerOpped(String username){
		GameProfile p = getGameProfile(username);
		if(p==null){
			Logger.w("Helper.isPlayerOpped","User: "+username+ " is not online");
			return false;
		}
		return MinecraftServer.getServer().getConfigurationManager().func_152596_g(p);
	}
	
	/**
	 * Returns the GameProfile for the given username
	 * @param username
	 * @return GameProfile, null if it does not exist
	 */
	public static GameProfile getGameProfile(String username){
		return MinecraftServer.getServer().func_152358_ax().func_152655_a(username);
	}
}
