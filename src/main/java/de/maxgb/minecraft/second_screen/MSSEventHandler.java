package de.maxgb.minecraft.second_screen;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.world.WorldEvent;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import de.maxgb.minecraft.second_screen.util.Helper;
import de.maxgb.minecraft.second_screen.util.Logger;

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
			if(!p.getEntityData().getBoolean("mss_book")){
				ItemStack book=Helper.createTutorialBook();
				Helper.dropItemStackInWorld(e.world, p.posX, p.posY, p.posZ, new ItemStack(Items.apple));
				if(!p.inventory.addItemStackToInventory(book)){
					Logger.i("EntityJoinWorld-Main","Playerinventory full, dropping manual to the ground");
					Helper.dropItemStackInWorld(e.world, p.posX, p.posY, p.posZ, book);
				}
				
				p.getEntityData().setBoolean("mss_book", true);
			}
		}
	}
	
}
