package de.maxgb.minecraft.second_screen.commands.mss_sub;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import de.maxgb.minecraft.second_screen.commands.BaseCommand;
import de.maxgb.minecraft.second_screen.data.UserManager;
import de.maxgb.minecraft.second_screen.util.Logger;

/**
 * Command to register a second screen user
 * Only used if auth_required is enabled
 * User can set a pass for his username
 * Server or Rcon can set pass for any username
 * @author Max
 *
 */
public class RegisterUserCommand implements MssCommand.MssSubCommand {

	private static final String TAG = "RegisterUserCommand";

	@Override
	public boolean canCommandSenderUseCommand(ICommandSender var1) {

		return false;
	}

	@Override
	public String getCommandName() {
		return "register";
	}

	@Override
	public void sendCommandUsage(ICommandSender var1) {
		if (var1.getName().equals("Rcon") || var1.getName().equals("Server")) {
			sendMessage(var1, "register <username> <password>");
		}
		else{
			sendMessage(var1,"register <password>");
		}
	}

	@Override
	public void processCommand(ICommandSender var1, String[] var2) {
		if (var2 == null || var2.length == 0) {
			sendMessage(var1, "Missing arguments. Usage:");
			sendCommandUsage(var1);
			return;
		}
		if (var1 instanceof EntityPlayer) {
			if (var2.length != 1) {
				sendMessage(var1, "Wrong arguments. Usage:");
				sendCommandUsage(var1);
				return;
			}

			int hash = var2[0].hashCode();
			UserManager.addUser(var1.getName(), hash);
			BaseCommand.sendMessage(var1, "Added user " + var1.getName());
			Logger.i(TAG, "Added user " + var1.getName() + " with pass: " + hash);

			return;

		} else if (var1.getName().equals("Rcon") || var1.getName().equals("Server")) {
			if (var2.length != 2) {
				sendMessage(var1, "Wrong arguments. Usage:");
				sendCommandUsage(var1);
				return;
			}
			int hash = var2[1].hashCode();
			UserManager.addUser(var2[0], hash);
			BaseCommand.sendMessage(var1, "Added user " + var2[0]);
			Logger.i(TAG, "Added user " + var2[0] + " with pass: " + hash);

			return;
		} else {
			Logger.w(TAG, var1.getName() + " tried to add a user, but what is he?");
		}

	}
	
	private void sendMessage(ICommandSender var1,String msg){
		BaseCommand.sendMessage(var1, msg);
	}

}
