package de.maxgb.minecraft.second_screen.util;

import java.util.Date;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.util.Vec3i;
import net.minecraft.world.World;

import com.mojang.authlib.GameProfile;


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
		for(GameProfile p:MinecraftServer.getServer().getGameProfiles()){
			if(username.equals(p.getName())){
				return p;
			}
		}
		return null;
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
		double y = player.prevPosY + (player.posY - player.prevPosY) * scale + 1.62D - player.getYOffset();
		double z = player.prevPosZ + (player.posZ - player.prevPosZ) * scale;
		Vec3 vector1 = new Vec3(x, y, z);
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
	 * Returns if the user with the given username is opped on this server.
	 * 
	 * @param username
	 * @return
	 */
	public static boolean isPlayerOpped(String username) {
		for(String s : MinecraftServer.getServer().getConfigurationManager().getOppedPlayerNames()){
			if(s.equals(username)){
				return true;
			}
		}
		return false;
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
	
	public static ItemStack createTutorialBook(){
		ItemStack bookStack = new ItemStack(Items.written_book);
		NBTTagList bookPages = new NBTTagList();
		bookPages.appendTag(new NBTTagString("Welcome to the MinecraftSecondScreen mod manual!"
				+ "\nThis mod allows you to use your mobile device/second screen as a 'Second Screen' or 'Companion app' for Minecraft. \nIt also allows you to control certain things in game. "));
		bookPages.appendTag(new NBTTagString("Currently there is a native Android (4.0+) app and a universal webapp which can be used on any device with a modern internet browser,\nbut the universal app is currently lacking a few features."));
		bookPages.appendTag(new NBTTagString("Download of the apps:\nhttp://maxgb.de/minecraftsecondscreen/files\n\nUsage:\nhttp://maxgb.de/minecraftsecondscreen/usage.html"));
		
		bookStack.setTagInfo("pages", bookPages);
		bookStack.setTagInfo("author", new NBTTagString("maxanier"));
		bookStack.setTagInfo("title", new NBTTagString("Second Screen Mod Manual"));
		
		return bookStack;
		
	}
	
	/**
	 * Drops Itemstack in world.
	 * Copied from OpenModsLib and edited
	 * @param worldObj
	 * @param x
	 * @param y
	 * @param z
	 * @param stack
	 * @return
	 */
	public static EntityItem dropItemStackInWorld(World worldObj, double x, double y, double z, ItemStack stack) {
		float f = 0.7F;
		float d0 = worldObj.rand.nextFloat() * f + (1.0F - f) * 0.5F;
		float d1 = worldObj.rand.nextFloat() * f + (1.0F - f) * 0.5F;
		float d2 = worldObj.rand.nextFloat() * f + (1.0F - f) * 0.5F;
		EntityItem entityitem = new EntityItem(worldObj, x + d0, y + d1, z + d2, stack);
		entityitem.setDefaultPickupDelay();
		if (stack.hasTagCompound()) {
			entityitem.getEntityItem().setTagCompound((NBTTagCompound)stack.getTagCompound().copy());
		}
		worldObj.spawnEntityInWorld(entityitem);
		return entityitem;
}

}
