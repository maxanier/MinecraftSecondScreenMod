package de.maxgb.minecraft.second_screen.util;

import java.util.Date;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;

import com.mojang.authlib.GameProfile;

import cpw.mods.fml.common.FMLCommonHandler;

/**
 * Helper class
 * @author Max
 *
 */
public class Helper {

	/**
	 * Returns the GameProfile for the given username
	 * 
	 * @param username
	 * @return GameProfile, null if it does not exist
	 */
	public static GameProfile getGameProfile(String username) {
		return MinecraftServer.getServer().func_152358_ax().func_152655_a(username);
	}

	/**
	 * Gets players looking spot.
	 * 
	 * @param player
	 * @param restricts
	 *            Keeps distance to players block reach distance
	 * @return The position as a MovingObjectPosition, null if not existent cf:
	 *         https
	 *         ://github.com/bspkrs/bspkrsCore/blob/master/src/main/java/bspkrs
	 *         /util/CommonUtils.java
	 */
	public static MovingObjectPosition getPlayerLookingSpot(EntityPlayer player, boolean restrict) {
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
		if (player instanceof EntityPlayerMP && restrict) {
			distance = ((EntityPlayerMP) player).theItemInWorldManager.getBlockReachDistance();
		}
		Vec3 vector2 = vector1.addVector(pitchAdjustedSinYaw * distance, sinPitch * distance, pitchAdjustedCosYaw
				* distance);
		return player.worldObj.rayTraceBlocks(vector1, vector2);
	}

	/**
	 * Returns if the user with the given username is opped on this server. Only
	 * usable when online
	 * 
	 * @param username
	 * @return
	 */
	public static boolean isPlayerOpped(String username) {
		GameProfile p = getGameProfile(username);
		if (p == null) {
			Logger.w("Helper.isPlayerOpped", "User: " + username + " is not online");
			return false;
		}
		return MinecraftServer.getServer().getConfigurationManager().func_152596_g(p);
	}
	
	/**
	 * Sends a chat message to server chat
	 * @param msg Message
	 */
	public static void sendChatMessage(String msg){
		sendChatMessage(msg,EnumChatFormatting.WHITE);
	}
	
	/**
	 * Sends a chat message to server chat
	 * @param msg Message
	 * @param color Color
	 */
	public static void sendChatMessage(String msg,EnumChatFormatting color){
		sendChatMessage(msg,color,false,false,false);
	}
	
	/**
	 * Sends a chat message to server chat
	 * @param msg
	 * @param color
	 * @param bold
	 * @param underlined
	 * @param italic
	 */
	public static void sendChatMessage(String msg,EnumChatFormatting color,boolean bold,boolean underlined,boolean italic){
		IChatComponent com=new ChatComponentText(msg);
		ChatStyle style=new ChatStyle().setColor(color);
		style.setBold(bold);
		style.setUnderlined(underlined);
		style.setItalic(italic);
		com.setChatStyle(style);
		
		MinecraftServer.getServer().getConfigurationManager().sendChatMsg(com);
	}
	
	public static String getCurrentTimeString() {
		Date timeDate = new Date(System.currentTimeMillis());
		String min = "" + timeDate.getMinutes();
		if (min.length() < 2) {
			min = "0" + min;
		}
		String h = "" + timeDate.getHours();
		if (h.length() < 2) {
			h = "0" + h;
		}
		return h + ":" + min;
	}

}
