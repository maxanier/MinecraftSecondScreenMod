package de.maxgb.minecraft.second_screen.commands;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import de.maxgb.minecraft.second_screen.data.UserManager;
import de.maxgb.minecraft.second_screen.util.Logger;

public class RegisterUserCommand implements MssCommand.MssSubCommand {

	private static final String TAG = "RegisterUserCommand";

	@Override
	public boolean canCommandSenderUseCommand(ICommandSender var1) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getCommandName() {
		return "register";
	}

	@Override
	public String getCommandUsage(ICommandSender var1) {
		if (var1.getCommandSenderName().equals("Rcon")
				|| var1.getCommandSenderName().equals("Server")) {
			return "register <username> <password>";
		}
		return "register <password>";
	}

	@Override
	public void processCommand(ICommandSender var1, String[] var2) {
		if (var2 == null || var2.length == 0) {
			BaseCommand.sendMessage(var1, "Missing arguments. Usage: "
					+ getCommandUsage(var1));
			return;
		}
		if (var1 instanceof EntityPlayer) {
			if (var2.length != 1) {
				BaseCommand.sendMessage(var1, "Wrong arguments. Usage: "
						+ getCommandUsage(var1));
				return;
			}

			int hash = var2[0].hashCode();
			UserManager.addUser(var1.getCommandSenderName(), hash);
			BaseCommand.sendMessage(var1,
					"Added user " + var1.getCommandSenderName());
			Logger.i(TAG, "Added user " + var1.getCommandSenderName()
					+ " with pass: " + hash);

			return;

		} else if (var1.getCommandSenderName().equals("Rcon")
				|| var1.getCommandSenderName().equals("Server")) {
			if (var2.length != 2) {
				BaseCommand.sendMessage(var1, "Wrong arguments. Usage: "
						+ getCommandUsage(var1));
				return;
			}
			int hash = var2[1].hashCode();
			UserManager.addUser(var2[0], hash);
			BaseCommand.sendMessage(var1, "Added user " + var2[0]);
			Logger.i(TAG, "Added user " + var2[0] + " with pass: " + hash);

			return;
		} else {
			Logger.w(TAG, var1.getCommandSenderName()
					+ " tried to add a user, but what is he?");
		}

	}

}
