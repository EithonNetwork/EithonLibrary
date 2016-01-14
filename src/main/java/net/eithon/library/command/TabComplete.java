package net.eithon.library.command;

import java.util.ArrayList;
import java.util.List;

import net.eithon.library.command.syntax.CommandSyntax;
import net.eithon.library.command.syntax.ParameterSyntax;

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
		return tabComplete(this._commandSyntax, arguments);
	}

	private static List<String> tabComplete(CommandSyntax commandSyntax, CommandArguments arguments) {
		if (commandSyntax.hasSubCommands()) {
			String command = arguments.getStringAsLowercase();
			if ((command == null) || command.isEmpty()) return commandSyntax.getSubCommands();
			CommandSyntax subCommandSyntax = commandSyntax.getSubCommand(command);
			if (subCommandSyntax != null) return tabComplete(subCommandSyntax, arguments);
			if (arguments.hasReachedEnd()) {
				List<String> found = findPartialMatches(command, commandSyntax.getSubCommands());
				if (!found.isEmpty()) return found;
			}
			arguments.getSender().sendMessage(String.format("Unexpected sub command: %s", command));
			return null;
		}

		CommandArguments argumentsClone = arguments.clone();
		for (ParameterSyntax parameterSyntax : commandSyntax.getParameterSyntaxList()) {
			String argument = argumentsClone.getString();
			if ((argument == null) || argument.isEmpty()) return parameterSyntax.getValidValues();
			if (argumentsClone.hasReachedEnd()) {
				List<String> found = findPartialMatches(argument, parameterSyntax.getValidValues());
				if (!found.isEmpty()) return found;			
			}
			argumentsClone.goOneArgumentBack();
			if (!parameterSyntax.parse(argumentsClone, null)) return null;
			if (argumentsClone.hasReachedEnd()) return null;
		}
		return null;
	}

	private static List<String> findPartialMatches(String partial, List<String> valueList) {
		List<String> found = new ArrayList<String>();
		for (String value : valueList) {
			if (value.startsWith(partial)) found.add(value);
		}
		return found;
	}
}
