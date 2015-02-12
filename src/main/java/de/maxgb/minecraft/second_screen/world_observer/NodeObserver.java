package de.maxgb.minecraft.second_screen.world_observer;

/*
import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import org.json.JSONArray;
import org.json.JSONObject;

import scala.collection.concurrent.INode;
import de.maxgb.minecraft.second_screen.util.Logger;

//@formatter:off
/* JSONStructure:
 * <"th_node":JSONArray>
 * which contains one JSONObject for each block
 * JSONObject contains:
 * 	<"label":String>
 * 	<"aspects":JSONObject>
 * 		which contains multiple
 * 		<aspectname:aspectamount(int)
 *
//@formatter:on

/**
 * Observer class which is designed to observe Thaumcraft Nodes which implement INode
 * @author Max
 *
 *

public class NodeObserver implements ObservedBlock.ObservingType {

	private final int ID = 2;
	private JSONArray info;

	@Override
	public boolean addInfoForBlock(World world, ObservedBlock block) {
		if (info == null) {
			info = new JSONArray();
		}

		JSONObject in = new JSONObject();
		TileEntity node = world.getTileEntity(block.x, block.y, block.z);

		if (node != null && node instanceof INode) {
			in.put("label", block.label);
			JSONObject aspects = new JSONObject();
			for (Aspect a : ((INode) node).getAspects().getAspects()) {
				aspects.put(a.getName(), ((INode) node).getAspects().getAmount(a));
			}
			in.put("aspects", aspects);
			info.put(in);
			return true;
		} else {
			Logger.w("Th_Node Observer", "Observed Block is no node");
			return false;
		}
	}

	@Override
	public boolean canObserve(Block block, TileEntity tile) {

		if (tile != null && tile instanceof INode)
			return true;

		return false;
	}

	@Override
	public void finishInfoCreation(JSONObject parent) {
		if (info != null && info.length() > 0) {
			parent.put("th_node", info);
			info = null;
		}

	}

	@Override
	public int getId() {
		return ID;
	}

	@Override
	public String getIdentifier() {
		return "Thaumcraft_Node";
	}

	@Override
	public String getShortIndentifier() {
		return "node";
	}

}
*/
