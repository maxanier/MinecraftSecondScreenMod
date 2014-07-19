package de.maxgb.minecraft.second_screen.world;

import net.minecraft.block.BlockLever;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

import org.json.JSONArray;
import org.json.JSONObject;

import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.nodes.INode;
import cpw.mods.fml.common.FMLCommonHandler;
import de.maxgb.minecraft.second_screen.util.Logger;

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
	
	/**
	 * Sets the state of a lever
	 * 
	 * @param b
	 *            Block
	 * @param state
	 *            State
	 * @return Whether the block is a lever or not
	 */
	public static boolean setLeverState(ObservedBlock b,boolean state){
		if(b.type==REDSTONE){
			World w = FMLCommonHandler.instance()
					.getMinecraftServerInstance()
					.worldServerForDimension(b.dimensionId);
			
			if (w.getBlock(b.x, b.y, b.z) instanceof BlockLever) {
				int meta = w.getBlockMetadata(b.x, b.y, b.z);
				if (state) {
					meta = meta | 0x8;
	
				} else {
					meta = meta ^ 0x8;
				}
				w.setBlockMetadataWithNotify(b.x, b.y, b.z, meta, 3);
				w.spawnParticle("reddust", b.x, b.y + 1, b.z, 0.0D, 255.0D, 0.0D);
	
				return true;
			}
		}
		return false;
	}
}
