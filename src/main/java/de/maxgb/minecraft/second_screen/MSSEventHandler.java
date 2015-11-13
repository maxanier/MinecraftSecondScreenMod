package de.maxgb.minecraft.second_screen;

import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import de.maxgb.minecraft.second_screen.util.Constants;
import de.maxgb.minecraft.second_screen.util.Helper;
import de.maxgb.minecraft.second_screen.util.Logger;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.world.WorldEvent;

public class MSSEventHandler {
	
	private final static String TAG="EventHandler";
	
	@SubscribeEvent
	public void onWorldSave(WorldEvent.Save e){
		if(e.world.provider.dimensionId==0 && !e.world.isRemote){
			Logger.i(TAG, "Saving data");
			SecondScreenMod.instance.saveData();
		}
	}
	
	@SubscribeEvent
	public void onPlayerLoggedOut(PlayerEvent.PlayerLoggedOutEvent e){
		SecondScreenMod.instance.latestOnlinePlayer=e.player.getDisplayName();
	}
	
	@SubscribeEvent(priority=EventPriority.LOWEST)
	public void onPlayerJoinWorld(EntityJoinWorldEvent e){
		if(e.entity instanceof EntityPlayer && !e.world.isRemote){

			EntityPlayer p = (EntityPlayer) e.entity;
			NBTTagCompound tag = getModTag(p, Constants.MOD_ID);
			if (!tag.getBoolean("has_initial_mss_book")) {
				ItemStack book=Helper.createTutorialBook();
				p.inventory.addItemStackToInventory(book);
				p.inventoryContainer.detectAndSendChanges();

				tag.setBoolean("has_initial_mss_book", true);
			}
		}
	}

	public NBTTagCompound getModTag(EntityPlayer player, String modName) {
		NBTTagCompound tag = player.getEntityData();
		NBTTagCompound persistTag;
		if (tag.hasKey(EntityPlayer.PERSISTED_NBT_TAG))
			persistTag = tag.getCompoundTag(EntityPlayer.PERSISTED_NBT_TAG);
		else {
			persistTag = new NBTTagCompound();
			tag.setTag(EntityPlayer.PERSISTED_NBT_TAG, persistTag);
		}
		NBTTagCompound modTag;
		if (persistTag.hasKey(modName)) {
			modTag = persistTag.getCompoundTag(modName);
		} else {
			modTag = new NBTTagCompound();
			persistTag.setTag(modName, modTag);
		}
		return modTag;
	}
	
}
