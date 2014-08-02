package de.maxgb.minecraft.second_screen.commands.mss_sub;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MovingObjectPosition;
import de.maxgb.minecraft.second_screen.commands.BaseCommand;
import de.maxgb.minecraft.second_screen.data.ObservingManager;
import de.maxgb.minecraft.second_screen.util.Helper;
import de.maxgb.minecraft.second_screen.world_observer.ObservedBlock;
import de.maxgb.minecraft.second_screen.world_observer.RedstoneObserver;
import de.maxgb.minecraft.second_screen.world_observer.ObservedBlock.ObservingType;

public class RegisterObserverCommand implements MssCommand.MssSubCommand {

	private final String TAG = "RegisterObserverCommand";

	@Override
	public boolean canCommandSenderUseCommand(ICommandSender var1) {
		if (var1 instanceof EntityPlayer) {
			return true;
		}
		return false;
	}

	@Override
	public String getCommandName() {
		return "observer";
	}

	@Override
	public void processCommand(ICommandSender var1, String[] var2) {

		if (var2 == null || var2.length < 2 || var2.length > 4) {
			sendMessage(var1, "Invalid arguments. Usage:");
			sendCommandUsage(var1);
			return;
		}

		if (var2[0].equals("add")) {

			EntityPlayer player;

			if (var1 instanceof EntityPlayer) {
				player = (EntityPlayer) var1;
			} else {
				sendMessage(var1, "Player only command");
				return;
			}

			MovingObjectPosition p = Helper.getPlayerLookingSpot(player, true);
			if (p == null || p.typeOfHit != MovingObjectPosition.MovingObjectType.BLOCK) {
				sendMessage(var1, "You have to look at a block");
				return;
			}
			Block block = player.worldObj.getBlock(p.blockX, p.blockY, p.blockZ);
			TileEntity tile = player.worldObj.getTileEntity(p.blockX, p.blockY, p.blockZ);

			boolean publ = false;
			ObservingType type = null;

			if (var2.length >= 3) {
				ArrayList<String> params = new ArrayList<String>();
				for (int i = 2; i < var2.length; i++) {
					params.add(var2[i]);
				}

				if (params.contains("public")) {
					publ = true;
					params.remove("public");
				}
				params.remove("private");

				// Check if a type is specified in command
				if (params.size() > 0) {
					for (ObservingType t : ObservedBlock.getObservingTypes()) {
						if (t.getIdentifier().equalsIgnoreCase(params.get(0))
								|| t.getShortIndentifier().equals(params.get(0))) {
							if (t.canObserve(block, tile)) {
								type = t;
							} else {
								sendMessage(var1, "This block cannot be observed with " + t.getIdentifier());
								return;
							}
						}
					}
				}

			}

			List<ObservingType> types = new ArrayList<ObservingType>();

			if (type != null) {
				types.add(type);
			} else {
				for (ObservingType t : ObservedBlock.getObservingTypes()) {
					if (t.canObserve(block, tile)) {
						if (!(t instanceof RedstoneObserver)) { // Dont add
																// redstone
																// observer to
																// the list,
																// redinfo
																// should be
																// used
							types.add(t);
						}

					}
				}
			}

			if (types.size() == 1) {
				if (ObservingManager.observeBlock(var1.getCommandSenderName(), publ, new ObservedBlock(var2[1],
						p.blockX, p.blockY, p.blockZ, player.worldObj.provider.dimensionId, types.get(0).getId(),
						p.sideHit))) {
					sendMessage(var1, "Successfully added block to observer list (" + types.get(0).getIdentifier()
							+ ")");
				} else {
					sendMessage(var1, "Successfully added block to observer list (" + types.get(0).getIdentifier()
							+ "), but overrode another block with the same label");
				}
			} else if (types.size() == 0) {
				sendMessage(var1, "This block cannot be observed");
			} else {
				sendMessage(var1, "There are multiple possible observing types");
				sendMessage(var1, "Specify the wanted type by adding it's (short) identifier at the end of the command");
				sendMessage(var1, "Possible types:");
				for (ObservingType t : types) {
					sendMessage(var1, t.getIdentifier() + " (short: " + t.getShortIndentifier() + ")");
				}
			}

		} else if (var2[0].equals("remove")) {
			if (ObservingManager.removeObservedBlock(var1.getCommandSenderName(), var2[1])) {
				sendMessage(var1, "Successfully removed block from observer list");
			} else {
				sendMessage(var1, "Failed to remove block from observer list. There is no block with this label");
			}
		} else if (var2[0].equals("list")) {
			sendMessage(var1, "Observation Types");
			for (ObservingType t : ObservedBlock.getObservingTypes()) {
				if (!(t instanceof RedstoneObserver)) {
					sendMessage(var1, t.getIdentifier() + " (short: " + t.getShortIndentifier() + ")");
				}
			}
		} else {

			sendMessage(var1, "Invalid arguments. Usage:");
			sendCommandUsage(var1);
			return;
		}

	}

	@Override
	public void sendCommandUsage(ICommandSender var1) {
		sendMessage(var1, "observer add <label> (private/public) (<indentifier>)");
		sendMessage(var1, "observer remove <label>");
		sendMessage(var1, "observer list");
	}

	private void sendMessage(ICommandSender var1, String msg) {
		BaseCommand.sendMessage(var1, msg);
	}

}
