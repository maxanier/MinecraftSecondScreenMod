package de.maxgb.minecraft.second_screen.world_observer;

import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import org.json.JSONArray;
import org.json.JSONObject;

import cofh.api.energy.IEnergyHandler;
import cofh.api.energy.IEnergyStorage;
import de.maxgb.minecraft.second_screen.util.Logger;

//@formatter:off
/*
 * JSONStructure:
 * <"rf_es":JSONArray>
 * 	contains one JSONArray for each block
 * 	Array contains:
 * 		<0=label:String>
 * 		<1=energystored:int>
 * 		<2=maxenergystored:int>
 */
//@formatter:on

/**
 * Observer class which is designed to observe redstone flux energy storage container which implement IEnergyStorage or IEngeryHandler
 * @author Max
 *
 */
public class RFEnergyStorageObserver implements ObservedBlock.ObservingType {

	private final int ID = 4;
	private JSONArray info;

	@Override
	public boolean addInfoForBlock(World world, ObservedBlock block) {
		if (info == null) {
			info = new JSONArray();
		}
		if (block.side == -1) {
			return false;
		}
		JSONArray es = new JSONArray();
		es.put(block.label);

		TileEntity storage = world.getTileEntity(block.x, block.y, block.z);
		if (storage != null && storage instanceof IEnergyHandler) {
			IEnergyHandler s = (IEnergyHandler) storage;
			es.put(s.getEnergyStored(ForgeDirection.getOrientation(block.side))).put(
					s.getMaxEnergyStored(ForgeDirection.getOrientation(block.side)));
			info.put(es);
			return true;
		} else if (storage != null && storage instanceof IEnergyStorage) {
			IEnergyStorage s = (IEnergyStorage) storage;
			es.put(s.getEnergyStored()).put(s.getMaxEnergyStored());
			info.put(es);
			return true;
		} else {
			Logger.w("RF_Energy_Storage Observer", "Observed Block is no energy storage");
			return false;
		}
	}

	@Override
	public boolean canObserve(Block block, TileEntity tile) {
		if (tile != null && (tile instanceof IEnergyHandler || tile instanceof IEnergyStorage)) {

			return true;
		}
		return false;
	}

	@Override
	public void finishInfoCreation(JSONObject parent) {
		if (info != null && info.length() > 0) {
			parent.put("rf_es", info);
			info = null;
		}

	}

	@Override
	public int getId() {
		return ID;
	}

	@Override
	public String getIdentifier() {
		return "RF_energy_storage";
	}

	@Override
	public String getShortIndentifier() {
		return "rf_es";
	}

}
