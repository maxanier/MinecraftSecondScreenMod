package de.maxgb.minecraft.second_screen.world;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLever;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.util.ForgeDirection;

import org.json.JSONArray;
import org.json.JSONObject;

import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.nodes.INode;
import cpw.mods.fml.common.FMLCommonHandler;
import de.maxgb.minecraft.second_screen.data.ObservingManager;
import de.maxgb.minecraft.second_screen.util.Logger;
import cofh.api.energy.IEnergyHandler;
import cofh.api.energy.IEnergyStorage;;

public class ObservingType {
	public final static int NOTHING = 0;
	public final static int REDSTONE = 1;
	public final static int NODE = 2;
	public final static int INVENTORY = 3;
	public final static int RF_ENERGY_STORAGE=4;
	
	private final static String TAG = "ObservingType";
	
	public static int retrieveObservingType(Block block, TileEntity tile){
		if (tile != null) {
			if (tile instanceof INode) {
				Logger.i(TAG, "Found a Node");

				return NODE;
			}

			if (tile instanceof IEnergyHandler || tile instanceof IEnergyStorage){
				
				Logger.i(TAG, "Found an Energystorage");
				
				return RF_ENERGY_STORAGE;
			}
			
			if (tile instanceof IInventory) {
				Logger.i(TAG, "Found a Inventory");

				return INVENTORY;

			}
			
		}
		return NOTHING;
	}

	public static JSONArray infoInventory(WorldServer world, ObservedBlock b) {
		JSONArray inv = new JSONArray();
		IInventory chest = (IInventory) world.getTileEntity(b.x, b.y, b.z);
		for (int i = 0; i < chest.getSizeInventory(); i++) {
			ItemStack s = chest.getStackInSlot(i);
			if (s != null) {
				inv.put(new JSONArray().put(s.getDisplayName()).put(s.stackSize));
			}
		}
		return inv;

	}

	public static JSONArray infoRedstone(World world, ObservedBlock block) {
		JSONArray in = new JSONArray();
		in.put(block.label).put(world.isBlockIndirectlyGettingPowered(block.x, block.y, block.z))
				.put(block.getBlock(world) instanceof BlockLever);
		return in;

	}

	public static JSONObject infoTh_Node(WorldServer world, ObservedBlock b) {
		JSONObject in = new JSONObject();
		TileEntity node = world.getTileEntity(b.x, b.y, b.z);

		if (node != null && node instanceof INode) {
			in.put("label", b.label);
			JSONObject aspects = new JSONObject();
			for (Aspect a : ((INode) node).getAspects().getAspects()) {
				aspects.put(a.getName(), ((INode) node).getAspects().getAmount(a));
			}
			in.put("aspects", aspects);
			return in;
		} else {
			Logger.w("Th_Node Info", "Observed Block is no node");
			return null;
		}
	}
	
	public static JSONArray infoRF_Energy_Storage(World world,ObservedBlock block){
		if(block.side==-1){
			return null;
		}
		JSONArray es = new JSONArray();
		es.put(block.label);
		
		TileEntity storage = world.getTileEntity(block.x, block.y, block.z);
		if(storage!=null&&storage instanceof IEnergyHandler){
			IEnergyHandler s = (IEnergyHandler) storage;
			es.put(s.getEnergyStored(ForgeDirection.getOrientation(block.side))).put(s.getMaxEnergyStored(ForgeDirection.getOrientation(block.side)));
			return es;
		}
		else if(storage!=null&&storage instanceof IEnergyStorage){
			IEnergyStorage s= (IEnergyStorage) storage;
			es.put(s.getEnergyStored()).put(s.getMaxEnergyStored());
			return es;
		}
		else{
			Logger.w("RF_Energy_Storage Info", "Observed Block is no energy storage");
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
	public static boolean setLeverState(ObservedBlock b, boolean state) {
		if (b.type == REDSTONE) {
			World w = FMLCommonHandler.instance().getMinecraftServerInstance().worldServerForDimension(b.dimensionId);

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
