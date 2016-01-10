package net.eithon.library.command.syntax;

import java.util.HashMap;

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
	private HashMap<String, ArgumentSyntax> _arguments;
	private HashMap<String, CommandSyntax> _subCommands;
	private boolean _argumentsAreOptional;
	private CommandExecutor _executor;
	private String _permission;
	
	public CommandSyntax(String commandName) {
		this._commandName = commandName;
		this._argumentsAreOptional = false;
		this._arguments = new HashMap<String, ArgumentSyntax>();
		this._subCommands = new HashMap<String, CommandSyntax>();
		this._permission = null;
	}

	public String getName() { return this._commandName; }

	public PlayerSyntax addArgumentPlayer(String name) {
		PlayerSyntax playerSyntax = new PlayerSyntax(name);
		addArgument(playerSyntax);
		return playerSyntax;
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
		this._arguments.put(argumentSyntax.getName(), argumentSyntax);
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

	public CommandExecutor getExecutor(CommandSender sender, CommandArguments arguments) {
		if (this._subCommands != null) {
			String command = arguments.getStringAsLowercase();
			CommandSyntax commandSyntax = this._subCommands.get(command);
			if (commandSyntax == null) {
				sender.sendMessage(String.format("Unexpected sub command: %s", command));
				return null;
			}
			return commandSyntax.getExecutor(sender, arguments);
		} 
		if (this._arguments != null) {
			for (ArgumentSyntax argumentSyntax : this._arguments.values()) {
				if (!argumentSyntax.isOk(sender, arguments)) return null;
			}
		}
		return this._executor;
	}

}
