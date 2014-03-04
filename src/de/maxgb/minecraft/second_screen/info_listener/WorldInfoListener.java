package de.maxgb.minecraft.second_screen.info_listener;

import java.util.ArrayList;
import java.util.HashMap;

import net.minecraft.block.Block;
import net.minecraft.world.WorldServer;

import org.json.JSONObject;

import de.maxgb.minecraft.second_screen.StandardListener;
import de.maxgb.minecraft.second_screen.util.Logger;
import de.maxgb.minecraft.second_screen.util.PROTOKOLL;
import de.maxgb.minecraft.second_screen.world.ObservingRegistry;
import de.maxgb.minecraft.second_screen.world.ObservingRegistry.ObservedBlock;

public class WorldInfoListener extends StandardListener {
	HashMap<Integer,WorldServer> worlds;
	private final String TAG = "WorldInfoListener";
	
	public WorldInfoListener(String params) {
		super(params);
		everyTick = 2000;// TODO make config related
		worlds=new HashMap<Integer,WorldServer>();
		
		for( WorldServer s: server.worldServers){
			worlds.put(s.provider.dimensionId, s);
		}
	}

	@Override
	public String update() {
		JSONObject info = new JSONObject();
		
		JSONObject redstone=new JSONObject();
		
		ArrayList<ObservedBlock> blocks =ObservingRegistry.getObservedBlocks();
		for(int i=0;i<blocks.size();i++){
			ObservedBlock block = blocks.get(i);
			WorldServer world=worlds.get(block.dimensionId);
			if(world==null){
				Logger.w(TAG, "Dimension corrosponding to the block not found");
				blocks.remove(i);
				i--;
			}
			else{
				if(world.getBlock(block.x, block.y, block.z).getMaterial()==net.minecraft.block.material.Material.air){
					Logger.w(TAG, "Block´s material is air -> remove");
					blocks.remove(i);
					i--;
				}
				else{
					redstone.put(block.label,world.isBlockIndirectlyGettingPowered(block.x, block.y, block.z));
				}
				
			}
		}
		
		info.put("redstone", redstone);
		return PROTOKOLL.WORLD_INFO_LISTENER+":"+info.toString();
	}

}
