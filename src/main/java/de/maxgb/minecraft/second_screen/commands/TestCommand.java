package de.maxgb.minecraft.second_screen.commands;

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
 * Test command for development purpose
 * @author Max
 *
 */
@SuppressWarnings({ "rawtypes" })
public class TestCommand extends BaseCommand {

	private List aliases;

	public TestCommand() {
		this.aliases = new ArrayList();
	}


	@Override
	public int compareTo(ICommand arg0) {

		return 0;
	}


	@Override
    public String getUsage(ICommandSender var1) {
        return "/msstest";
	}

	@Override
	public boolean isUsernameIndex(String[] var1, int var2) {
		return false;
	}


	@Override
    public String getName() {
        return "msstest";
	}

	@Override
    public List getAliases() {
        return aliases;
	}

	@Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args)
            throws CommandException {
		
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
