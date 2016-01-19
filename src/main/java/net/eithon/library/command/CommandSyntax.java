package net.eithon.library.command;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Queue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import net.eithon.library.command.IParameterSyntax.ParameterType;

import org.apache.commons.lang.NotImplementedException;

class CommandSyntax implements ICommandSyntaxAdvanced {	
	private static String leftHand = "([^<:= ]+) *(:|=) *";
	private static String parameter = "([^>]+)";
	private static String rest = "(.*)";
	private static Pattern namedParameterPattern = Pattern.compile("^(" + leftHand + ")?<" + parameter + ">" + rest);
	private static String keyWord = "([^ <>{}:]+)";
	private static Pattern keyWordPattern = Pattern.compile("^" + keyWord + rest);
	private static Pattern hintPattern = Pattern.compile("^\\([^)]*\\)");

	private ArrayList<ParameterSyntax> _parameterSyntaxMap;
	private HashMap<String, CommandSyntax> _subCommands;
	private CommandExecutor _commandExecutor;
	private String _permission;
	private boolean _automaticPermissions;
	private String _name;
	private boolean _displayHints;

	CommandSyntax(String name) {
		this._name = name;
		this._permission = null;
		this._displayHints = true;
		this._parameterSyntaxMap = new ArrayList<ParameterSyntax>();
		this._subCommands = new HashMap<String, CommandSyntax>();
	}

	public CommandSyntax getSubCommand(String commandName) { return this._subCommands.get(commandName); }
	public IParameterSyntax getParameterSyntax(String parameterName) { return this._parameterSyntaxMap.stream().filter(ps -> parameterName.equals(ps.getName())).findFirst().get(); }
	public boolean hasSubCommands() { return this._subCommands.size() > 0; }
	public String getName() { return this._name; }
	public boolean hasParameters() { return this._parameterSyntaxMap.size() > 0; }
	public List<ParameterSyntax> getParameterSyntaxList() { return this._parameterSyntaxMap.stream().collect(Collectors.toList());	}
	public boolean getDisplayHints() { return this._displayHints; }
	public void setPermissionsAutomatically() { this._automaticPermissions = true;}
	public String getRequiredPermission() { return this._permission; }

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

	public CommandSyntax addKeyWord(String keyWord) {
		CommandSyntax commandSyntax = new CommandSyntax(keyWord);
		this._subCommands.put(keyWord, commandSyntax);
		return commandSyntax;
	}

	/* (non-Javadoc)
	 * @see net.eithon.library.command.ICommandSyntax#addParameter(java.lang.String)
	 */
	@Override
	public IParameterSyntax addParameter(String name) {
		return addParameter(name, ParameterType.STRING);
	}

	/* (non-Javadoc)
	 * @see net.eithon.library.command.ICommandSyntax#addParameter(java.lang.String, net.eithon.library.command.ParameterSyntax.ParameterType)
	 */
	@Override
	public IParameterSyntax addParameter(String name, ParameterType type) {
		ParameterSyntax parameterSyntax = new ParameterSyntax(name, type);
		return addParameter(parameterSyntax);
	}

	/* (non-Javadoc)
	 * @see net.eithon.library.command.ICommandSyntax#addNamedParameter(java.lang.String, java.lang.String)
	 */
	@Override
	public IParameterSyntax addNamedParameter(String name, String leftSide) {
		ParameterSyntax parameterSyntax = new ParameterSyntax(name, ParameterType.STRING, leftSide);
		return addParameter(parameterSyntax);
	}

	/* (non-Javadoc)
	 * @see net.eithon.library.command.ICommandSyntax#addNamedParameter(java.lang.String, net.eithon.library.command.ParameterSyntax.ParameterType, java.lang.String)
	 */
	@Override
	public IParameterSyntax addNamedParameter(String name, ParameterType type, String leftSide) {
		ParameterSyntax parameterSyntax = new ParameterSyntax(name, type, leftSide);
		return addParameter(parameterSyntax);
	}

	/* (non-Javadoc)
	 * @see net.eithon.library.command.ICommandSyntax#addParameter(net.eithon.library.command.ParameterSyntax)
	 */
	@Override
	public IParameterSyntax addParameter(ParameterSyntax parameterSyntax) {
		this._parameterSyntaxMap.add(parameterSyntax);
		return parameterSyntax;
	}

	/* (non-Javadoc)
	 * @see net.eithon.library.command.ICommandSyntax#setCommandExecutor(net.eithon.library.command.CommandSyntax.CommandExecutor)
	 */
	@Override
	public ICommandSyntax setCommandExecutor(CommandExecutor commandExecutor) {
		this._commandExecutor = commandExecutor;
		return this;
	}

	/* (non-Javadoc)
	 * @see net.eithon.library.command.ICommandSyntax#setPermission(java.lang.String)
	 */
	@Override
	public void setPermission(String permission) {
		this._permission = permission;
	}

	public CommandExecutor parseArguments(EithonCommand command, Queue<String> argumentQueue, HashMap<String, Argument> collectedArguments) throws CommandSyntaxException {
		if (this._subCommands.size() > 0) {
			String keyWord = null;
			Matcher matcher = null;
			do {
				keyWord = argumentQueue.poll();
				matcher = hintPattern.matcher(keyWord);
			} while (matcher.matches());

			CommandSyntax commandSyntax = this._subCommands.get(keyWord);
			if (commandSyntax == null) {
				throw new CommandSyntaxException(String.format("Unexpected key word: %s", keyWord));
			}
			return commandSyntax.parseArguments(command, argumentQueue, collectedArguments);
		}

		for (ParameterSyntax parameterSyntax : this._parameterSyntaxMap) {
			String argument = null;
			if (!argumentQueue.isEmpty()) {
				argument = argumentQueue.poll();
			}
			parameterSyntax.parseArguments(command, argument, collectedArguments);
		}
		return this._commandExecutor;
	}

	/* (non-Javadoc)
	 * @see net.eithon.library.command.ICommandSyntax#parseSyntax(java.lang.String)
	 */
	@Override
	public ICommandSyntax parseCommandSyntax(String commandLine) throws CommandSyntaxException {
		if ((this._permission == null) && (this._automaticPermissions)) this._permission = getName();
		return parseCommandSyntax(commandLine, this._permission);
	}

	private CommandSyntax parseCommandSyntax(String commandLine, String permission) throws CommandSyntaxException {
		String remainingPart = commandLine.trim();
		if (remainingPart.isEmpty()) return this;

		Matcher matcher = namedParameterPattern.matcher(remainingPart);
		if (matcher.matches()) {
			// Parameter
			String leftSide = matcher.group(2);
			String parameter = matcher.group(4);
			ParameterSyntax parameterSyntax = ParameterSyntax.parseSyntax(leftSide, parameter);
			addParameter(parameterSyntax);
			parseCommandSyntax(matcher.group(5), permission);
			return this;
		} else {
			// Command
			matcher = keyWordPattern.matcher(remainingPart);
			if (!matcher.matches()) {
				throw new CommandSyntaxException(String.format("Expected to find a command token here: \"%s\"", remainingPart));
			}
			if (hasParameters()) {
				throw new NotImplementedException("Sub commands after parameters is not yet supported.");
			}
			String name = matcher.group(1);
			String commandPermssion = permission + "." + name;
			CommandSyntax subCommand = addKeyWord(name);
			subCommand.parseCommandSyntax(matcher.group(2), commandPermssion);
			if (!subCommand.hasSubCommands() && (permission != null)) subCommand.setPermission(commandPermssion);
			return subCommand;
		}
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
			for (IParameterSyntax parameterSyntax : this._parameterSyntaxMap) {
				soFar.append(" ");			
				soFar.append(parameterSyntax.toString());
			}
			commandLineList.add(soFar.toString().trim());
		}
	}

	@Override
	public IParameterSyntax parseParameterSyntax(String leftSide, String parameter) throws CommandSyntaxException {
		ParameterSyntax parameterSyntax = ParameterSyntax.parseSyntax(leftSide, parameter);
		return addParameter(parameterSyntax);
	}

	@Override
	public ICommandSyntaxAdvanced getAdvancedMethods() { return this; }
}