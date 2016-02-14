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

class CommandSyntax extends Syntax implements ICommandSyntaxAdvanced {	
	private static String leftHand = "([^<:= ]+) *(:|=) *";
	private static String parameter = "([^>]+)";
	private static String rest = "(.*)";
	private static Pattern namedParameterPattern = Pattern.compile("^(" + leftHand + ")?<" + parameter + ">" + rest);
	private static String keyWord = "([^ <>{}:]+)";
	private static Pattern keyWordPattern = Pattern.compile("^" + keyWord + rest);

	private ArrayList<ParameterSyntax> _parameterSyntaxList;
	private HashMap<String, CommandSyntax> _subCommandMap;
	private CommandExecutor _commandExecutor;
	private String _permission;
	private boolean _automaticPermissions;
	private boolean _displayHints;

	CommandSyntax(String name) {
		super(name);
		this._permission = null;
		this._displayHints = true;
		this._parameterSyntaxList = new ArrayList<ParameterSyntax>();
		this._subCommandMap = new HashMap<String, CommandSyntax>();
	}

	public CommandSyntax getSubCommand(String commandName) { return this._subCommandMap.get(commandName); }
	public boolean hasSubCommands() { return this._subCommandMap.size() > 0; }
	public boolean hasParameters() { return this._parameterSyntaxList.size() > 0; }
	public List<ParameterSyntax> getParameterSyntaxList() { return this._parameterSyntaxList.stream().collect(Collectors.toList());	}
	public boolean getDisplayHints() { return this._displayHints; }
	public void setPermissionsAutomatically() { this._automaticPermissions = true;}
	public String getRequiredPermission() { return this._permission; }

	public List<String> getKeyWordList() {
		ArrayList<String> subCommands = new ArrayList<String>();
		for (String name : this._subCommandMap.keySet()) {
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

	public IParameterSyntax getParameterSyntax(String parameterName) {
		if (this._parameterSyntaxList.isEmpty()) return null;
		return this._parameterSyntaxList.stream().filter(ps -> parameterName.equals(ps.getName())).findFirst().get(); 
	}

	public ICommandSyntax addKeyWords(String... keyWords) {
		ICommandSyntax commandSyntax = this;
		for (String keyWord : keyWords) {
			commandSyntax = commandSyntax.addKeyWord(keyWord);
		}
		return commandSyntax;
	}


	public CommandSyntax addKeyWord(String keyWord) {
		CommandSyntax commandSyntax = new CommandSyntax(keyWord);
		this._subCommandMap.put(keyWord, commandSyntax);
		commandSyntax.inherit(this);
		return commandSyntax;
	}

	public IParameterSyntax addParameter(String name) {
		return addParameter(name, ParameterType.STRING);
	}

	public IParameterSyntax addParameter(String name, ParameterType type) {
		ParameterSyntax parameterSyntax = new ParameterSyntax(name, type);
		return addParameter(parameterSyntax);
	}

	public IParameterSyntax addParameter(ParameterSyntax parameterSyntax) {
		this._parameterSyntaxList.add(parameterSyntax);
		parameterSyntax.inherit(this);
		return parameterSyntax;
	}

	public IParameterSyntax addNamedParameter(String name, String leftSide) {
		ParameterSyntax parameterSyntax = new ParameterSyntax(name, ParameterType.STRING, leftSide);
		return addParameter(parameterSyntax);
	}

	public IParameterSyntax addNamedParameter(String name, ParameterType type, String leftSide) {
		ParameterSyntax parameterSyntax = new ParameterSyntax(name, type, leftSide);
		return addParameter(parameterSyntax);
	}

	public ICommandSyntax setCommandExecutor(CommandExecutor commandExecutor) {
		this._commandExecutor = commandExecutor;
		return this;
	}

	public void setPermission(String permission) {
		this._permission = permission;
	}

	public CommandExecutor parseArguments(EithonCommand command, Queue<String> argumentQueue, HashMap<String, EithonArgument> collectedArguments, String commandLineSofar) 
			throws CommandParseException {

		if (hasSubCommands()) {
			String keyWord = argumentQueue.peek();
			CommandSyntax commandSyntax = getSubCommand(keyWord);
			if (commandSyntax != null) {
				argumentQueue.poll();
				commandLineSofar = commandLineSofar + " " + this.getName();
				return commandSyntax.parseArguments(command, argumentQueue, collectedArguments, commandLineSofar);
			}
			if (!hasParameters()) {
				throw new CommandParseException(getSyntaxString(commandLineSofar),
						keyWord == null ? null : String.format("Unexpected key word: %s", keyWord));
			}
		}

		for (ParameterSyntax parameterSyntax : this._parameterSyntaxList) {	
			String argument = null;
			if (!argumentQueue.isEmpty()) {
				if (parameterSyntax.getType() == ParameterType.REST) {
					argument = argumentQueue.stream().reduce((a, b) -> String.format("%s %s", a, b)).get();
				} else {
					argument = argumentQueue.poll();
					argument = skipHint(argumentQueue, parameterSyntax, argument);
				}
			}
			try {
				parameterSyntax.parseArguments(command, argument, collectedArguments);
			} catch (ArgumentParseException e) {
				throw new CommandParseException(getSyntaxString(commandLineSofar), e.getMessage());
			}
		}
		return this._commandExecutor;
	}

	public String skipHint(Queue<String> argumentQueue, ParameterSyntax parameterSyntax, String argument) {
		if ((argument != null) 
				&& !argumentQueue.isEmpty()
				&& (argument.equals(parameterSyntax.getHint()))) argument = argumentQueue.poll();
		return argument;
	}

	public ICommandSyntax parseCommandSyntax(String commandLine) throws CommandSyntaxException {
		if ((this._permission == null) && (this._automaticPermissions)) this._permission = getName();
		return parseCommandSyntax(commandLine, this._permission);
	}

	private CommandSyntax parseCommandSyntax(String commandLine, String permission) throws CommandSyntaxException {
		String remainingPart = commandLine.trim();
		if (remainingPart.isEmpty()) return this;

		Matcher matcher = namedParameterPattern.matcher(remainingPart);
		if (matcher.matches()) {
			parseParameter(permission, matcher.group(2), matcher.group(4), matcher.group(5));
			return this;
		} else {
			return parseSubCommand(permission, remainingPart);
		}
	}

	private void parseParameter(String permission, String leftSide, String parameter, String rest)
			throws CommandSyntaxException {
		ParameterSyntax parameterSyntax = ParameterSyntax.parseSyntax(leftSide, parameter);
		addParameter(parameterSyntax);
		if ((parameterSyntax.getType() == ParameterType.REST) 
				&& (rest != null) && (rest.trim().length() > 0)) {
			throw new CommandSyntaxException(
					String.format("Parameter %s was of type REST, this means that it should be last, but after it came \"%s\".",
							parameterSyntax.getName(), rest));
		}
		parseCommandSyntax(rest, permission);
	}


	private CommandSyntax parseSubCommand(String permission, String remainingPart)
			throws CommandSyntaxException {
		Matcher matcher;
		// Command
		matcher = keyWordPattern.matcher(remainingPart);
		if (!matcher.matches()) {
			throw new CommandSyntaxException(String.format("Expected to find a command token here: \"%s\"", remainingPart));
		}
		String name = matcher.group(1);
		String commandPermssion = permission + "." + name;
		CommandSyntax subCommand = getSubCommand(name);
		if (subCommand == null) subCommand = addKeyWord(name);
		CommandSyntax commandSyntax = subCommand.parseCommandSyntax(matcher.group(2), commandPermssion);
		if (subCommand.lastKeyWordInCommand() && (permission != null)) subCommand.setPermission(commandPermssion);
		return commandSyntax;
	}

	public boolean lastKeyWordInCommand() {
		return hasParameters() || !hasSubCommands();
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
		if (!this._subCommandMap.isEmpty()) {
			for (CommandSyntax commandSyntax : this._subCommandMap.values()) {
				commandSyntax.toString(commandLineList, soFar.toString());
			}
		} else {
			for (IParameterSyntax parameterSyntax : this._parameterSyntaxList) {
				soFar.append(" ");			
				soFar.append(parameterSyntax.toString());
			}
			commandLineList.add(soFar.toString().trim());
		}
	}

	public String getSyntaxString(String beginning) {
		StringBuilder soFar = new StringBuilder(beginning);
		soFar.append(" ");
		soFar.append(this.getName());
		if (hasSubCommands()) {
			soFar.append(" ");
			String[] subCommands = this._subCommandMap
					.values()
					.stream()
					.map(sc -> sc.getName())
					.sorted()
					.collect(Collectors.toList())
					.toArray(new String[0]);
			soFar.append(String.join(" | ", subCommands));
		} else if (hasParameters()) {
			soFar.append(" ");
			String[] parameters = this._parameterSyntaxList
					.stream()
					.map(p -> p.getSyntaxString())
					.sorted()
					.collect(Collectors.toList())
					.toArray(new String[0]);
			soFar.append(String.join(" ", parameters));
		}
		return soFar.toString();
	}

	public IParameterSyntax parseParameterSyntax(String leftSide, String parameter) throws CommandSyntaxException {
		ParameterSyntax parameterSyntax = ParameterSyntax.parseSyntax(leftSide, parameter);
		return addParameter(parameterSyntax);
	}

	public ICommandSyntaxAdvanced getAdvancedMethods() { return this; }
}