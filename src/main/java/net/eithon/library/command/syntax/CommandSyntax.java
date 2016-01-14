package net.eithon.library.command.syntax;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.NotImplementedException;

import net.eithon.library.command.CommandArguments;
import net.eithon.library.command.CommandParser;
import net.eithon.library.command.ParameterValue;
import net.eithon.library.command.syntax.ParameterSyntax.ParameterType;

public class CommandSyntax extends Syntax {	
	private static String leftHand = "([^<= ]+) *= *";
	private static String parameter = "([^>]+)";
	private static String rest = "(.*)";
	private static Pattern namedParameterPattern= Pattern.compile("^(" + leftHand + ")?<" + parameter + ">" + rest);
	private static String command = "([^ ]+)";
	private static Pattern commandPattern = Pattern.compile("^" + command + rest);

	public interface CommandExecutor {
		public void execute(CommandParser commandParser);
	}

	private ArrayList<ParameterSyntax> _parameterSyntaxList;
	private HashMap<String, CommandSyntax> _subCommands;
	private CommandExecutor _commandExecutor;
	private String _permission;

	public CommandSyntax(String name, String permission) {
		super(name);
		this._permission = permission;
		this._parameterSyntaxList = new ArrayList<ParameterSyntax>();
		this._subCommands = new HashMap<String, CommandSyntax>();
	}

	public CommandSyntax(String name) {
		this(name, null);
	}

	public List<ParameterSyntax> getParameterSyntaxList() { return this._parameterSyntaxList; }
	public CommandSyntax getSubCommand(String command) { return this._subCommands.get(command); }
	public boolean hasSubCommands() { return this._subCommands.size() > 0; }
	public boolean hasParameters() { return this._parameterSyntaxList.size() > 0; }

	public ParameterSyntax addParameter(String name) {
		return addParameter(ParameterType.STRING, name);
	}

	public ParameterSyntax addParameter(ParameterType type, String name) {
		ParameterSyntax parameterSyntax = new ParameterSyntax(type, name);
		return addParameter(parameterSyntax);
	}

	public ParameterSyntax addNamedParameter(String leftSide, String parameterName) {
		return addNamedParameter(ParameterType.STRING, leftSide, parameterName);
	}

	public ParameterSyntax addNamedParameter(ParameterType type, String leftSide, String parameterName) {
		ParameterSyntax parameterSyntax = new ParameterSyntax(type, leftSide, parameterName);
		return addParameter(parameterSyntax);
	}

	public ParameterSyntax addParameter(ParameterSyntax parameterSyntax) {
		this._parameterSyntaxList.add(parameterSyntax);
		return parameterSyntax;
	}

	public CommandSyntax addCommand(String name) {
		CommandSyntax commandSyntax = new CommandSyntax(name);
		this._subCommands.put(name, commandSyntax);
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

	public List<String> getSubCommands() {
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

	@Override
	public String toString() {
		ArrayList<String> commandLineList = new ArrayList<String>();
		toString(commandLineList, "");
		return String.join("\n", commandLineList);
	}

	private void toString(List<String> commandLineList, String beginning) {
		StringBuilder soFar = new StringBuilder(beginning);
		soFar.append(" ");
		soFar.append(this.getName());
		if (!this._subCommands.isEmpty()) {
			for (CommandSyntax commandSyntax : this._subCommands.values()) {
				commandSyntax.toString(commandLineList, soFar.toString());
			}
		} else {
			for (ParameterSyntax parameterSyntax : this._parameterSyntaxList) {
				soFar.append(" ");			
				soFar.append(parameterSyntax.toString());
			}
			commandLineList.add(soFar.toString().trim());
		}
	}

	public void parseSyntax(String commandLine) {
		parseSyntax(this, commandLine);
	}

	private static void parseSyntax(CommandSyntax currentCommand, String commandLine) {
		String remainingPart = commandLine.trim();
		if (remainingPart.isEmpty()) return;

		Matcher matcher = namedParameterPattern.matcher(remainingPart);
		if (matcher.matches()) {
			String leftSide = matcher.group(2);
			String parameter = matcher.group(3);
			ParameterSyntax parameterSyntax = ParameterSyntax.parseSyntax(leftSide, parameter);
			currentCommand.addParameter(parameterSyntax);
			parseSyntax(currentCommand, matcher.group(4));
		} else {
			matcher = commandPattern.matcher(remainingPart);
			if (!matcher.matches()) {
				throw new IllegalArgumentException(String.format("Expected to find a command token here: \"%s\"", remainingPart));
			}
			if (currentCommand.hasParameters()) {
				throw new NotImplementedException("Sub commands after parameters is not yet supported.");
			}
			String name = matcher.group(1);
			currentCommand = currentCommand.addCommand(name);
			parseSyntax(currentCommand, matcher.group(2));
		}
	}

}
