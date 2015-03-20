package de.maxgb.minecraft.second_screen.world_observer;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLever;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Map;

import cpw.mods.fml.common.FMLCommonHandler;

public class RedstoneObserver implements ObservedBlock.ObservingType {

	public final static int ID = 1;
	public static boolean canObserve(Block block) {
		if (block != null && block instanceof BlockLever) {
			return true;
		} else if (block != null && block.isNormalCube()) {
			return true;
		}
		return false;
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
		if (b.type == ID) {
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

	private JSONArray info;

	@Override
	public boolean addInfoForBlock(World world, ObservedBlock block) {
		if (info == null) {
			info = new JSONArray();
		}
		JSONArray in = new JSONArray();
		in.put(block.label).put(world.isBlockIndirectlyGettingPowered(block.x, block.y, block.z))
				.put(block.getBlock(world) instanceof BlockLever);

		info.put(in);
		return true;
	}

	@Override
	public boolean canObserve(Block block, TileEntity tile) {
		return RedstoneObserver.canObserve(block);
	}

	@Override
	public void finishInfoCreation(JSONObject parent) {
		if (info != null && info.length() > 0) {
			parent.put("redstone", info);
			info = null;
		}

	}

	@Override
	public int getId() {
		return RedstoneObserver.ID;
	}

	@Override
	public String getIdentifier() {
		return "redstone";
	}

	@Override
	public String getShortIndentifier() {
		return "r";
	}

}
