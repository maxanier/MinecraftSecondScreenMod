package de.maxgb.minecraft.second_screen.world_observer;

import de.maxgb.minecraft.second_screen.util.Logger;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import org.json.JSONArray;
import org.json.JSONObject;

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

		TileEntity t = world.getTileEntity(block.pos);

		if (t != null && (t instanceof IFluidHandler)) {
			IFluidHandler tank = (IFluidHandler) t;
			try {
				for (IFluidTankProperties tinfo : tank.getTankProperties()) {
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

	private void addTankInfo(JSONObject parent, IFluidTankProperties tank) {
		try {
			parent.put(tank.getContents().getFluid().getLocalizedName(tank.getContents()),
					new JSONArray().put(tank.getContents().amount).put(tank.getCapacity()));
		} catch (NullPointerException e) {
		}
	}

	@Override
	public boolean canObserve(IBlockState block, TileEntity tile) {
		return tile != null && (tile instanceof IFluidHandler || tile instanceof IFluidTank);
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
