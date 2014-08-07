package de.maxgb.minecraft.second_screen.world_observer;

import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;
import net.minecraftforge.fluids.IFluidTank;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import de.maxgb.minecraft.second_screen.util.Logger;

//@formatter:off
/*JSONStructure: 
 * <"fluidtank":JSONArray>
 * 	contains one JSONObject foreach block 
 * 	JSONObject each contain:
 * 		<"label":String>
 * 		<Fluidname(String):JSONArray>
 * 			each JSONArray contains
 * 			<0=fluidamount;int>
 * 			<1=fluidcapacity:int>
 */
//@formatter:on

/**
 * Observer class which is desinged to observe tanks which implement the
 * IFluidHandler or IFluidTank interface 
 * 
 * 
 * @author Max
 * 
 * 
 */
public class FluidTankObserver implements ObservedBlock.ObservingType {

	private final int ID = 5;
	private JSONArray info;

	@Override
	public boolean addInfoForBlock(World world, ObservedBlock block) {
		if (info == null) {
			info = new JSONArray();
		}

		JSONObject ti = new JSONObject();

		TileEntity t = world.getTileEntity(block.x, block.y, block.z);

		if (t != null && (t instanceof IFluidHandler)) {
			IFluidHandler tank = (IFluidHandler) t;
			try {
				for (FluidTankInfo tinfo : tank.getTankInfo(ForgeDirection.getOrientation(block.side))) {
					addTankInfo(ti, tinfo);
				}
			} catch (NullPointerException e) {
			}

		} else if (t != null && t instanceof IFluidTank) {
			addTankInfo(ti, ((IFluidTank) t).getInfo());
		} else {
			Logger.w("TankObserver", "No tank found -> remove");
			return false;
		}
		if (ti.length() == 0) {
			ti.put("Nothing", new JSONArray().put(0).put(0));
		}
		ti.put("label", block.label);
		info.put(ti);
		return true;

	}

	private void addTankInfo(JSONObject parent, FluidTankInfo tank) {
		try {
			parent.put(tank.fluid.getFluid().getLocalizedName(tank.fluid),
					new JSONArray().put(tank.fluid.amount).put(tank.capacity));
		} catch (NullPointerException e) {
		}
	}

	@Override
	public boolean canObserve(Block block, TileEntity tile) {
		if (tile != null && (tile instanceof IFluidHandler || tile instanceof IFluidTank)) {
			return true;
		}
		return false;
	}

	@Override
	public void finishInfoCreation(JSONObject parent) {
		if (info != null && info.length() > 0) {
			parent.put("fluidtank", info);
			info = null;
		}

	}

	@Override
	public int getId() {
		return ID;
	}

	@Override
	public String getIdentifier() {
		return "fluid_tank";
	}

	@Override
	public String getShortIndentifier() {
		return "t";
	}

}
