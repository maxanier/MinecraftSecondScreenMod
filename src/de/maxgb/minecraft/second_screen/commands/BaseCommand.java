package de.maxgb.minecraft.second_screen.commands;

import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;

public abstract class BaseCommand implements ICommand {
	protected static void sendMessage(ICommandSender target, String message) {
		target.addChatMessage(new ChatComponentText(message));
	}
}
