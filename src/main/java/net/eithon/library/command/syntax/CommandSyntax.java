package net.eithon.library.command.syntax;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import net.eithon.library.command.CommandArguments;
import net.eithon.library.command.CommandParser;
import net.eithon.library.command.ParameterValue;
import net.eithon.library.command.syntax.ParameterSyntax.ParameterType;

public class CommandSyntax extends Syntax {

	public interface CommandExecutor {
		public void execute(CommandParser commandParser);
	}

	private ArrayList<ParameterSyntax> _parameterSyntaxList;
	private HashMap<String, CommandSyntax> _subCommands;
	private boolean _parametersAreOptionalFromThisPoint;
	private CommandExecutor _commandExecutor;
	private String _permission;

	public CommandSyntax(String name, String permission) {
		super(name);
		this._permission = permission;
		this._parametersAreOptionalFromThisPoint = false;
		this._parameterSyntaxList = new ArrayList<ParameterSyntax>();
		this._subCommands = new HashMap<String, CommandSyntax>();
	}
	
	public CommandSyntax(String name) {
		this(name, null);
	}

	public boolean hasParameters() { return this._parameterSyntaxList.size() > 0; }

	public PlayerParameterSyntax addParameterPlayer(String name) {
		PlayerParameterSyntax playerParameterSyntax = new PlayerParameterSyntax(name);
		return (PlayerParameterSyntax) addParameter(playerParameterSyntax);
	}

	public ParameterSyntax addParameter(String name) {
		return addParameter(ParameterType.STRING, name);
	}

	public ParameterSyntax addParameter(ParameterType type, String name) {
		ParameterSyntax parameterSyntax = new ParameterSyntax(type, name);
		return addParameter(parameterSyntax);
	}

	public ParameterSyntax addNamedParameter(String name, String parameterName) {
		return addNamedParameter(ParameterType.STRING, name, parameterName);
	}

	public ParameterSyntax addNamedParameter(ParameterType type, String name, String parameterName) {
		ParameterSyntax parameterSyntax = new ParameterSyntax(type, name, parameterName);
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
		return addCommand(name, null, null);
	}

	public CommandSyntax addCommand(String name, CommandExecutor commandExecutor) {
		return addCommand(name, null, commandExecutor);
	}

	public CommandSyntax addCommand(String name, String permission, CommandExecutor commandExecutor) {
		CommandSyntax commandSyntax = new CommandSyntax(name);
		this._subCommands.put(name, commandSyntax);
		commandSyntax.setPermission(permission);
		commandSyntax.setCommandExecutor(commandExecutor);
		return commandSyntax;
	}

	public void setCommandExecutor(CommandExecutor commandExecutor) {
		this._commandExecutor = commandExecutor;		
	}

	public void setPermission(String permission) {
		this._permission = permission;
	}

	public CommandExecutor parse(CommandArguments arguments, HashMap<String, ParameterValue> parameterValues) {
		if (this._subCommands.size() > 0) {
			String command = arguments.getStringAsLowercase();
			CommandSyntax commandSyntax = this._subCommands.get(command);
			if (commandSyntax == null) {
				arguments.getSender().sendMessage(String.format("Unexpected sub command: %s", command));
				return null;
			}
			return commandSyntax.parse(arguments, parameterValues);
		}
		
		CommandArguments argumentsClone = arguments.clone();
		for (ParameterSyntax parameterSyntax : this._parameterSyntaxList) {
			if (!parameterSyntax.parse(argumentsClone, parameterValues)) return null;
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
			if (!parameterSyntax.parse(argumentsClone, null)) return null;
			if (argumentsClone.hasReachedEnd()) return null;
		}
		return null;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder(this.getName());
		sb.append(" ");
		for (CommandSyntax commandSyntax : this._subCommands.values()) {
			sb.append(commandSyntax.toString());
			sb.append(" ");
		}
		for (ParameterSyntax parameterSyntax : this._parameterSyntaxList) {
			sb.append(parameterSyntax.toString());
			sb.append(" ");			
		}
		return sb.toString().trim();
	}

}
