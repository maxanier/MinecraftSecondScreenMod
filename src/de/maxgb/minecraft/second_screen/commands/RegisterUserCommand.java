package de.maxgb.minecraft.second_screen.commands;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import de.maxgb.minecraft.second_screen.data.UserManager;
import de.maxgb.minecraft.second_screen.util.Logger;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;

public class RegisterUserCommand implements MssCommand.MssSubCommand{

	private static final String TAG = "RegisterUserCommand";

	@Override
	public void processCommand(ICommandSender var1, String[] var2) {
		if(var2==null||var2.length==0){
			BaseCommand.sendMessage(var1, "Missing arguments. Usage: "+getCommandUsage(var1));
			return;
		}
		if(var1 instanceof EntityPlayer){
			if(var2.length!=1){
				BaseCommand.sendMessage(var1, "Missing arguments. Usage: "+getCommandUsage(var1));
				return;
			}
			try {
				MessageDigest md5 =MessageDigest.getInstance("MD5");
				String hash=new String(md5.digest(var2[0].getBytes()));
				UserManager.addUser(var1.getCommandSenderName(), hash);
				BaseCommand.sendMessage(var1, "Added user "+var1.getCommandSenderName());
				Logger.i(TAG, "Added user "+var1.getCommandSenderName()+" with pass: "+hash);
			} catch (NoSuchAlgorithmException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return;

		}
		else if(var1.getCommandSenderName().equals("Rcon")){
			if(var2.length!=2){
				BaseCommand.sendMessage(var1, "Missing arguments. Usage: "+getCommandUsage(var1));
				return;
			}
			try {
				MessageDigest md5 =MessageDigest.getInstance("MD5");
				String hash=new String(md5.digest(var2[1].getBytes()));
				UserManager.addUser(var2[0], hash);
				BaseCommand.sendMessage(var1, "Added user "+var2[0]);
				Logger.i(TAG, "Added user "+var2[0]+" with pass: "+hash);
			} catch (NoSuchAlgorithmException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return;
		}
		else{
			Logger.w(TAG,var1.getCommandSenderName()+" tried to add a user, but what is he?");
		}
		
	}

	@Override
	public boolean canCommandSenderUseCommand(ICommandSender var1) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getCommandUsage(ICommandSender var1) {
		if(var1.getCommandSenderName().equals("Rcon")){
			return "register <username> <password>";
		}
		return "register <password>";
	}

	@Override
	public String getCommandName() {
		return "register";
	}
	
}
