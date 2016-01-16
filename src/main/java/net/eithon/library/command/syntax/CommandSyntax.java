package net.eithon.library.command.syntax;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Queue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import net.eithon.library.command.Argument;
import net.eithon.library.command.EithonCommand;
import net.eithon.library.command.syntax.ParameterSyntax.ParameterType;

import org.apache.commons.lang.NotImplementedException;

public class CommandSyntax extends Syntax {	
	private static String leftHand = "([^<= ]+) *= *";
	private static String parameter = "([^>]+)";
	private static String rest = "(.*)";
	private static Pattern namedParameterPattern= Pattern.compile("^(" + leftHand + ")?<" + parameter + ">" + rest);
	private static String command = "([^ ]+)";
	private static Pattern commandPattern = Pattern.compile("^" + command + rest);

	public interface CommandExecutor {
		public void execute(EithonCommand command);
	}

	private HashMap<String, ParameterSyntax> _parameterSyntaxMap;
	private HashMap<String, CommandSyntax> _subCommands;
	private CommandExecutor _commandExecutor;
	private String _permission;

	public CommandSyntax(String name) {
		super(name);
		this._permission = null;
		this._parameterSyntaxMap =new HashMap<String, ParameterSyntax>();
		this._subCommands = new HashMap<String, CommandSyntax>();
	}

	public CommandSyntax getSubCommand(String command) { return this._subCommands.get(command); }
	public ParameterSyntax getParameterSyntax(String parameterName) { return this._parameterSyntaxMap.get(parameterName); }
	public boolean hasSubCommands() { return this._subCommands.size() > 0; }
	public boolean hasParameters() { return this._parameterSyntaxMap.size() > 0; }
	public List<ParameterSyntax> getParameterSyntaxList() { return this._parameterSyntaxMap.values().stream().collect(Collectors.toList());	}

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
	
	public CommandSyntax addCommand(String name) {
		CommandSyntax commandSyntax = new CommandSyntax(name);
		this._subCommands.put(name, commandSyntax);
		return commandSyntax;
	}

	public ParameterSyntax addParameter(String name) {
		return addParameter(name, ParameterType.STRING);
	}

	public ParameterSyntax addParameter(String name, ParameterType type) {
		ParameterSyntax parameterSyntax = new ParameterSyntax(name, type);
		return addParameter(parameterSyntax);
	}

	public ParameterSyntax addNamedParameter(String name, String leftSide) {
		ParameterSyntax parameterSyntax = new ParameterSyntax(name, ParameterType.STRING, leftSide);
		return addParameter(parameterSyntax);
	}

	public ParameterSyntax addNamedParameter(String name, ParameterType type, String leftSide) {
		ParameterSyntax parameterSyntax = new ParameterSyntax(name, type, leftSide);
		return addParameter(parameterSyntax);
	}

	public ParameterSyntax addParameter(ParameterSyntax parameterSyntax) {
		this._parameterSyntaxMap.put(parameterSyntax.getName(), parameterSyntax);
		return parameterSyntax;
	}

	public CommandSyntax setCommandExecutor(CommandExecutor commandExecutor) {
		this._commandExecutor = commandExecutor;
		return this;
	}

	public void setPermission(String permission) {
		this._permission = permission;
	}

	public CommandExecutor parseArguments(Queue<String> argumentQueue, HashMap<String, Argument> collectedArguments) throws CommandSyntaxException {
		if (this._subCommands.size() > 0) {
			String command = argumentQueue.poll();
			CommandSyntax commandSyntax = this._subCommands.get(command);
			if (commandSyntax == null) {
				throw new CommandSyntaxException(String.format("Unexpected sub command: %s", command));
			}
			return commandSyntax.parseArguments(argumentQueue, collectedArguments);
		}

		for (ParameterSyntax parameterSyntax : this._parameterSyntaxMap.values()) {
			parameterSyntax.parseArguments(argumentQueue.poll(), collectedArguments);
		}
		return this._commandExecutor;
	}

	public void parseSyntax(String commandLine) throws CommandSyntaxException {
		String remainingPart = commandLine.trim();
		if (remainingPart.isEmpty()) return;

		Matcher matcher = namedParameterPattern.matcher(remainingPart);
		if (matcher.matches()) {
			String leftSide = matcher.group(2);
			String parameter = matcher.group(3);
			ParameterSyntax parameterSyntax = ParameterSyntax.parseSyntax(leftSide, parameter);
			addParameter(parameterSyntax);
			parseSyntax(matcher.group(4));
		} else {
			matcher = commandPattern.matcher(remainingPart);
			if (!matcher.matches()) {
				throw new CommandSyntaxException(String.format("Expected to find a command token here: \"%s\"", remainingPart));
			}
			if (hasParameters()) {
				throw new NotImplementedException("Sub commands after parameters is not yet supported.");
			}
			String name = matcher.group(1);
			addCommand(name).parseSyntax(matcher.group(2));
		}
	}

	public void setPermissionsAutomatically() {
		throw new NotImplementedException();
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
			for (ParameterSyntax parameterSyntax : this._parameterSyntaxMap.values()) {
				soFar.append(" ");			
				soFar.append(parameterSyntax.toString());
			}
			commandLineList.add(soFar.toString().trim());
		}
	}
}