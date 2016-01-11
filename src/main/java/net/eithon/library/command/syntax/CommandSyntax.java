package net.eithon.library.command.syntax;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import org.bukkit.command.CommandSender;

import net.eithon.library.command.CommandArguments;
import net.eithon.library.command.CommandParser;
import net.eithon.library.command.syntax.ArgumentSyntax.ArgumentType;
import net.eithon.library.command.syntax.CommandSyntax.CommandExecutor;

public class CommandSyntax {

	public interface CommandExecutor {
		public void execute(CommandParser commandParser);
	}

	private String _documentation;
	private String _commandName;
	private ArrayList<ArgumentSyntax> _arguments;
	private HashMap<String, CommandSyntax> _subCommands;
	private boolean _argumentsAreOptional;
	private CommandExecutor _executor;
	private String _permission;

	public CommandSyntax(String commandName) {
		this._commandName = commandName;
		this._argumentsAreOptional = false;
		this._arguments = new ArrayList<ArgumentSyntax>();
		this._subCommands = new HashMap<String, CommandSyntax>();
		this._permission = null;
	}

	public String getName() { return this._commandName; }

	public PlayerSyntax addArgumentPlayer(String name) {
		PlayerSyntax playerSyntax = new PlayerSyntax(name);
		return (PlayerSyntax) addArgument(playerSyntax);
	}

	public ArgumentSyntax addArgument(ArgumentType type, String name) {
		ArgumentSyntax argumentSyntax = new ArgumentSyntax(type, name);
		return addArgument(argumentSyntax);
	}

	public ArgumentSyntax addNamedArgument(ArgumentType type, String name) {
		ArgumentSyntax argumentSyntax = new ArgumentSyntax(type, name, true);
		return addArgument(argumentSyntax);
	}

	public ArgumentSyntax addArgument(ArgumentSyntax argumentSyntax) {
		if (this._argumentsAreOptional) argumentSyntax.setOptional();
		this._arguments.add(argumentSyntax);
		return argumentSyntax;
	}

	public void setRestOfArgumentsOptional() {
		this._argumentsAreOptional = true;
	}

	public CommandSyntax addCommand(String name) {
		CommandSyntax commandSyntax = new CommandSyntax(name);
		this._subCommands.put(name, commandSyntax);
		return commandSyntax;
	}

	public CommandSyntax addCommand(String commandName, CommandExecutor commandExecutor) {
		CommandSyntax commandSyntax = addCommand(commandName);
		commandSyntax.setExecutor(commandExecutor);
		return commandSyntax;
	}

	public void setExecutor(CommandExecutor commandExecutor) {
		this._executor = commandExecutor;		
	}

	public void setPermission(String permission) {
		this._permission = permission;
	}

	public CommandExecutor verifyAndGetExecutor(CommandArguments arguments) {
		if (this._subCommands.size() > 0) {
			String command = arguments.getStringAsLowercase();
			CommandSyntax commandSyntax = this._subCommands.get(command);
			if (commandSyntax == null) {
				arguments.getSender().sendMessage(String.format("Unexpected sub command: %s", command));
				return null;
			}
			return commandSyntax.verifyAndGetExecutor(arguments);
		}
		
		CommandArguments argumentsClone = arguments.clone();
		for (ArgumentSyntax argumentSyntax : this._arguments) {
			if (!argumentSyntax.isOk(argumentsClone)) return null;
		}
		return this._executor;
	}
	
	private List<String> getSubCommands() {
		ArrayList<String> subCommands = new ArrayList<String>();
		for (String name : this._subCommands.keySet()) {
			subCommands.add(name);
		}
		subCommands.sort(new Comparator<String>() {
			@Override
			public int compare(String o1, String o2) {
				return o1.compareTo(o2);
			}
		});
		return subCommands;
	}

	public List<String> tabComplete(CommandArguments arguments) {
		if (this._subCommands.size() > 0) {
			String command = arguments.getStringAsLowercase();
			if ((command == null) || command.isEmpty()) return getSubCommands();
			CommandSyntax commandSyntax = this._subCommands.get(command);
			if (commandSyntax == null) {
				if (arguments.hasReachedEnd()) {
					List<String> found = new ArrayList<String>();
					for (String string : getSubCommands()) {
						if (string.startsWith(command)) found.add(string);
					}
					if (found.size() > 0) return found;
				}
				arguments.getSender().sendMessage(String.format("Unexpected sub command: %s", command));
				return null;
			}
			return commandSyntax.tabComplete(arguments);
		}
		
		CommandArguments argumentsClone = arguments.clone();
		for (ArgumentSyntax argumentSyntax : this._arguments) {
			String argument = argumentsClone.getString();
			if ((argument == null) || argument.isEmpty()) return argumentSyntax.getValidValues();
			if (argumentsClone.hasReachedEnd()) {
				List<String> found = new ArrayList<String>();
				for (String string :argumentSyntax.getValidValues()) {
					if (string.startsWith(argument)) found.add(string);
				}
				if (found.size() > 0) return found;			
			}
			argumentsClone.goOneArgumentBack();
			if (!argumentSyntax.isOk(argumentsClone)) return null;
			if (argumentsClone.hasReachedEnd()) return null;
		}
		return null;
	}

}
