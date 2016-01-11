package net.eithon.library.command.syntax;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import org.bukkit.command.CommandSender;

import net.eithon.library.command.CommandArguments;
import net.eithon.library.command.CommandParser;
import net.eithon.library.command.syntax.ParameterSyntax.ParameterType;
import net.eithon.library.command.syntax.CommandSyntax.CommandExecutor;

public class CommandSyntax {

	public interface CommandExecutor {
		public void execute(CommandParser commandParser);
	}

	private String _documentation;
	private String _commandName;
	private ArrayList<ParameterSyntax> _parameterSyntaxList;
	private HashMap<String, CommandSyntax> _subCommands;
	private boolean _parametersAreOptionalFromThisPoint;
	private CommandExecutor _commandExecutor;
	private String _permission;

	public CommandSyntax(String commandName) {
		this._commandName = commandName;
		this._parametersAreOptionalFromThisPoint = false;
		this._parameterSyntaxList = new ArrayList<ParameterSyntax>();
		this._subCommands = new HashMap<String, CommandSyntax>();
		this._permission = null;
	}

	public String getName() { return this._commandName; }

	public PlayerParameterSyntax addParameterPlayer(String name) {
		PlayerParameterSyntax playerParameterSyntax = new PlayerParameterSyntax(name);
		return (PlayerParameterSyntax) addParameter(playerParameterSyntax);
	}

	public ParameterSyntax addParameter(ParameterType type, String name) {
		ParameterSyntax parameterSyntax = new ParameterSyntax(type, name);
		return addParameter(parameterSyntax);
	}

	public ParameterSyntax addNamedParameter(ParameterType type, String name) {
		ParameterSyntax parameterSyntax = new ParameterSyntax(type, name, true);
		return addParameter(parameterSyntax);
	}

	public ParameterSyntax addParameter(ParameterSyntax parameterSyntax) {
		if (this._parametersAreOptionalFromThisPoint) parameterSyntax.setOptional();
		this._parameterSyntaxList.add(parameterSyntax);
		return parameterSyntax;
	}

	public void setRestOfParametersAsOptional() {
		this._parametersAreOptionalFromThisPoint = true;
	}

	public CommandSyntax addCommand(String name) {
		CommandSyntax commandSyntax = new CommandSyntax(name);
		this._subCommands.put(name, commandSyntax);
		return commandSyntax;
	}

	public CommandSyntax addCommand(String commandName, CommandExecutor commandExecutor) {
		CommandSyntax commandSyntax = addCommand(commandName);
		commandSyntax.setCommandExecutor(commandExecutor);
		return commandSyntax;
	}

	public void setCommandExecutor(CommandExecutor commandExecutor) {
		this._commandExecutor = commandExecutor;		
	}

	public void setPermission(String permission) {
		this._permission = permission;
	}

	public CommandExecutor verifyAndGetCommandExecutor(CommandArguments arguments) {
		if (this._subCommands.size() > 0) {
			String command = arguments.getStringAsLowercase();
			CommandSyntax commandSyntax = this._subCommands.get(command);
			if (commandSyntax == null) {
				arguments.getSender().sendMessage(String.format("Unexpected sub command: %s", command));
				return null;
			}
			return commandSyntax.verifyAndGetCommandExecutor(arguments);
		}
		
		CommandArguments argumentsClone = arguments.clone();
		for (ParameterSyntax parameterSyntax : this._parameterSyntaxList) {
			if (!parameterSyntax.isOk(argumentsClone)) return null;
		}
		return this._commandExecutor;
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
		for (ParameterSyntax parameterSyntax : this._parameterSyntaxList) {
			String argument = argumentsClone.getString();
			if ((argument == null) || argument.isEmpty()) return parameterSyntax.getValidValues();
			if (argumentsClone.hasReachedEnd()) {
				List<String> found = new ArrayList<String>();
				for (String string :parameterSyntax.getValidValues()) {
					if (string.startsWith(argument)) found.add(string);
				}
				if (found.size() > 0) return found;			
			}
			argumentsClone.goOneArgumentBack();
			if (!parameterSyntax.isOk(argumentsClone)) return null;
			if (argumentsClone.hasReachedEnd()) return null;
		}
		return null;
	}

}
