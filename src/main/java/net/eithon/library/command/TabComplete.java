package net.eithon.library.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import net.eithon.library.command.syntax.CommandSyntaxException;
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
		Queue<String> argumentQueue = new LinkedList<String>();
		argumentQueue.addAll(Arrays.asList(args));
		argumentQueue.poll();
		return tabComplete(sender, this._commandSyntax, argumentQueue);
	}

	private static List<String> tabComplete(CommandSender sender, CommandSyntax commandSyntax, Queue<String> argumentQueue) {
		if (commandSyntax.hasSubCommands()) {
			String command = argumentQueue.poll();
			if ((command == null) || command.isEmpty()) return commandSyntax.getSubCommands();
			CommandSyntax subCommandSyntax = commandSyntax.getSubCommand(command);
			if (subCommandSyntax != null) return tabComplete(sender, subCommandSyntax, argumentQueue);
			if (argumentQueue.isEmpty()) {
				List<String> found = findPartialMatches(command, commandSyntax.getSubCommands());
				if (!found.isEmpty()) return found;
			}
			sender.sendMessage(String.format("Unexpected sub command: %s", command));
			return null;
		}

		for (ParameterSyntax parameterSyntax : commandSyntax.getParameterSyntaxList()) {
			String argument = argumentQueue.poll();
			if ((argument == null) || argument.isEmpty()) return parameterSyntax.getValidValues();
			if (argumentQueue.isEmpty()) {
				List<String> found = findPartialMatches(argument, parameterSyntax.getValidValues());
				if (!found.isEmpty()) return found;			
			}
			try {
				parameterSyntax.parseArguments(argument, null);
			} catch (CommandSyntaxException e) {
				sender.sendMessage(e.getMessage());
				return null;
			}
			if (argumentQueue.isEmpty()) return null;
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
