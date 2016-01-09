package net.eithon.library.command;

import org.bukkit.command.CommandSender;

public interface ICommandHandler {
	void showCommandSyntax(CommandSender sender, String command);
	boolean onCommand(CommandParser commandParser);
}
