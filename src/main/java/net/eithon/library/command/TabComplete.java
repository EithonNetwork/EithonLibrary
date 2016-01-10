package net.eithon.library.command;

import java.util.List;

import net.eithon.library.command.syntax.CommandSyntax;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

public class TabComplete implements TabCompleter{

	private CommandSyntax _commandSyntax;
	
	public TabComplete(CommandSyntax commandSyntax) {
		this._commandSyntax = commandSyntax;
	}
	
	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
		CommandArguments arguments = new CommandArguments(sender, args);
		arguments.getStringAsLowercase();
		return this._commandSyntax.tabComplete(arguments);
	}
}
