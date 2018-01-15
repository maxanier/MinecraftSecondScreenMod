package de.maxgb.minecraft.second_screen.commands;

import de.maxgb.minecraft.second_screen.SecondScreenMod;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Returns the port the mod listens to
 * @author Max
 *
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public class GetMSSPortCommand extends BaseCommand {

	private List aliase;

	public GetMSSPortCommand() {
		aliase = new ArrayList();
		aliase.add("getmssport");
		aliase.add("getMSSPort");
		aliase.add("getmssPort");
	}

	@Override
	public int compareTo(ICommand o) {

		return 0;
	}


	@Override
    public String getUsage(ICommandSender var1) {
        return "/getMssPort";
	}

	@Override
	public boolean isUsernameIndex(String[] var1, int var2) {

		return false;
	}

	@Override
    public String getName() {
        return "getMssPort";
	}

	@Override
    public List getAliases() {
        return aliase;
	}

	@Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args)
            throws CommandException {
		if (SecondScreenMod.instance.webSocketListener.isRunning()) {
			sendMessage(sender, "Minecraft Second Screen Port: " + SecondScreenMod.instance.port);
		} else {

			sendMessage(sender, "Minecraft Second Screen Port: " + SecondScreenMod.instance.port
					+ ". But the mod isnt running. Error: " + SecondScreenMod.instance.webSocketListener.getError());
		}
		
	}

	@Override
    public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
        return true;
	}

	@Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos) {
        return Collections.emptyList();
    }
}
