package de.maxgb.minecraft.second_screen.commands;

import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.text.TextComponentString;

/**
 * Basic command on which all other commands should depend on
 * @author Max
 *
 */
public abstract class BaseCommand implements ICommand {
	public static void sendMessage(ICommandSender target, String message) {
		String[] lines = message.split("\\n");
		for (String line : lines) {
            target.sendMessage(new TextComponentString(line));
        }

	}

}
