package net.eithon.library.command;

import java.util.HashMap;

import net.eithon.library.command.ArgumentSyntax.ArgumentType;

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
	
	CommandSyntax(String commandName) {
		this._commandName = commandName;
		this._argumentsAreOptional = false;
		this._arguments = new HashMap<String, ArgumentSyntax>();
		this._subCommands = new HashMap<String, CommandSyntax>();
		this._permission = null;
	}

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

}
