package de.maxgb.minecraft.second_screen.commands;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.command.ICommandSender;


public class MssCommand extends BaseCommand{
	private List aliases;
	private ArrayList<MssSubCommand> commands;

	public MssCommand(){
		this.aliases = new ArrayList();
		this.aliases.add("mss");
		this.aliases.add("minecraftsecondscreen");
		this.aliases.add("secondscreen");
		commands = new ArrayList<MssSubCommand>();
	}
	
	public void addSubCommand(MssSubCommand c){
		commands.add(c);
	}
	@Override
	public String getCommandName() {
		return "mss";
	}

	@Override
	public String getCommandUsage(ICommandSender var1) {
			String usage="/mss <action> <params>\nActions:\n";
			for(MssSubCommand c : commands){
				usage+=c.getCommandUsage(var1)+"\n";
			}
			
			return usage;

	}

	@Override
	public List getCommandAliases() {
		return aliases;
	}

	@Override
	public void processCommand(ICommandSender var1, String[] var2) {
		if(var2==null||var2.length==0){
			BaseCommand.sendMessage(var1,"Usage: "+getCommandUsage(var1));
			return;
		}
		for(MssSubCommand c : commands){
			if(var2[0].equals(c.getCommandName())){
				String[] var;
				if(var2.length==1){
					var=null;
				}
				else{
					var = new String[var2.length-1];
					for(int i=1;i<var2.length;i++){
						var[i-1]=var2[i];
					}
				}

				c.processCommand(var1, var);
				break;
			}
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

	@Override
	public int compareTo(Object arg0) {
		// TODO Auto-generated method stub
		return 0;
	}
	
	protected interface MssSubCommand{
		public void processCommand(ICommandSender var1,String[] var2);
		public boolean canCommandSenderUseCommand(ICommandSender var1);
		public String getCommandUsage(ICommandSender var1);
		public String getCommandName();
	}
	


}
