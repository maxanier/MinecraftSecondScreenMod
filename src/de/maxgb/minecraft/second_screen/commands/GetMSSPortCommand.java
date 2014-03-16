package de.maxgb.minecraft.second_screen.commands;

import java.util.ArrayList;
import java.util.List;

import de.maxgb.minecraft.second_screen.SecondScreenMod;

import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;

public class GetMSSPortCommand implements ICommand {

	private List aliase;
	
	public GetMSSPortCommand(){
		aliase=new ArrayList();
		aliase.add("getmssport");
		aliase.add("getMSSPort");
		aliase.add("getmssPort");
	}
	@Override
	public int compareTo(Object o) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getCommandName() {
		return "getMssPort";
	}

	@Override
	public String getCommandUsage(ICommandSender var1) {
		return "/getMssPort";
	}

	@Override
	public List getCommandAliases() {
		return aliase;
	}

	@Override
	public void processCommand(ICommandSender var1, String[] var2) {
		if(SecondScreenMod.connected){
			sendMessage(var1,"Minecraft Second Screen Port: "+SecondScreenMod.port);
		}
		else{
			if(SecondScreenMod.error==null){
				SecondScreenMod.error="Unknown";
			}
			sendMessage(var1,"Minecraft Second Screen Port: "+SecondScreenMod.port+". But the mod isn´t running. Error: "+SecondScreenMod.error);
		}
		
	}

	@Override
	public boolean canCommandSenderUseCommand(ICommandSender var1) {
		return true;
	}

	@Override
	public List addTabCompletionOptions(ICommandSender var1, String[] var2) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isUsernameIndex(String[] var1, int var2) {
		// TODO Auto-generated method stub
		return false;
	}
	
	private void sendMessage(ICommandSender target,String message){
		target.addChatMessage(new ChatComponentText(message));
	}

}
