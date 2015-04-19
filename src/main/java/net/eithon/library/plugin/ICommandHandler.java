package net.eithon.library.plugin;

import org.bukkit.command.CommandSender;

public interface ICommandHandler {
	void showCommandSyntax(CommandSender sender, String command);
	boolean onCommand(CommandParser commandParser);
}
