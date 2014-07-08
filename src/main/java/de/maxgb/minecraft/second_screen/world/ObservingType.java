package de.maxgb.minecraft.second_screen.world;

import java.util.Collection;

import org.json.JSONArray;
import org.json.JSONObject;

import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.nodes.INode;


import de.maxgb.minecraft.second_screen.util.Logger;
import de.maxgb.minecraft.second_screen.world.ObservingManager.ObservedBlock;

import net.minecraft.block.BlockLever;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

public class ObservingType {
	public final static int REDSTONE=1;
	public final static int NODE=2;
	
	public static JSONArray infoRedstone(World world,ObservedBlock block){
		JSONArray in = new JSONArray();
		in.put(block.label)
				.put(world.isBlockIndirectlyGettingPowered(block.x,
						block.y, block.z))
				.put(block.getBlock(world) instanceof BlockLever);
		return in;
		
	}

	public static JSONObject infoTh_Node(WorldServer world, ObservedBlock b) {
		Logger.i("Update", "Checking node");
		JSONObject in= new JSONObject();
		TileEntity node=world.getTileEntity(b.x,b.y,b.z);
		
		if(node!=null&&node instanceof INode){
			in.put("label", b.label);
			JSONObject aspects=new JSONObject();
			for(Aspect a:((INode) node).getAspects().getAspects()){
				aspects.put(a.getName(), ((INode)node).getAspects().getAmount(a));
			}
			in.put("aspects", aspects);
			return in;
		}
		else{
			Logger.w("Th_Node Info","Observed Block is no node");
			return null;
		}
	}
}
