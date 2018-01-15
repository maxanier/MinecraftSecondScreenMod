package de.maxgb.minecraft.second_screen;

import de.maxgb.minecraft.second_screen.util.Helper;
import de.maxgb.minecraft.second_screen.util.Logger;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;

public class MSSEventHandler {
	
	private final static String TAG="EventHandler";
	
	@SubscribeEvent
	public void onWorldSave(WorldEvent.Save e){
        if (e.getWorld().provider.getDimension() == 0 && !e.getWorld().isRemote) {
            Logger.i(TAG, "Saving data");
			SecondScreenMod.instance.saveData();
		}
	}
	
	@SubscribeEvent
	public void onPlayerLoggedOut(PlayerEvent.PlayerLoggedOutEvent e){
		SecondScreenMod.instance.latestOnlinePlayer=e.player.getDisplayName().getFormattedText();
	}
	
	@SubscribeEvent(priority=EventPriority.LOWEST)
	public void onPlayerJoinWorld(EntityJoinWorldEvent e){
        if (e.getEntity() instanceof EntityPlayer && !e.getWorld().isRemote) {
            EntityPlayer p = (EntityPlayer) e.getEntity();
            if(!p.getEntityData().getBoolean("mss_book")){
				ItemStack book=Helper.createTutorialBook();
                Helper.dropItemStackInWorld(e.getWorld(), p.posX, p.posY, p.posZ, new ItemStack(Items.APPLE));
                if(!p.inventory.addItemStackToInventory(book)){
					Logger.i("EntityJoinWorld-Main","Playerinventory full, dropping manual to the ground");
                    Helper.dropItemStackInWorld(e.getWorld(), p.posX, p.posY, p.posZ, book);
                }
				
				p.getEntityData().setBoolean("mss_book", true);
			}
		}
	}
	
}
