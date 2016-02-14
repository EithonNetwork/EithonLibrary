package net.eithon.library.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import net.eithon.library.command.ICommandSyntax.CommandExecutor;
import net.eithon.library.extensions.EithonPlayer;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class EithonCommand {

	private CommandSender _sender;
	private Queue<String> _commandQueue;
	private CommandSyntax _commandSyntax;
	private HashMap<String, EithonArgument> _arguments;

	public EithonCommand(ICommandSyntax commandSyntax, CommandSender sender, Command cmd, String alias, String[] args) {
		if (!(commandSyntax instanceof CommandSyntax)) {
			throw new IllegalArgumentException("The argument commandSyntax could not be casted to CommandSyntax");
		}
		this._sender = sender;
		this._commandQueue = new LinkedList<String>();
		this._commandQueue.addAll(Arrays.asList(args));
		this._commandSyntax = (CommandSyntax) commandSyntax;
		this._arguments = new HashMap<String, EithonArgument>();
	}

	public static ICommandSyntax createRootCommand(String commandName) {
		return new CommandSyntax(commandName);
	}

	public boolean execute() {
		CommandExecutor executor = null;
		try {
			executor = this._commandSyntax.parseArguments(this, this._commandQueue, this._arguments, "syntax: ");
		} catch (CommandParseException e) {
			sendMessage(e.getSyntaxDocumentation());
			String message = e.getMessage();
			if (message != null) sendMessage(message);
		}
		if (executor != null) executor.execute(this);
		return true;
	}

	public CommandSender getSender() { return this._sender; }

	public Player getPlayer() {
		if (this._sender == null) return null;
		if (!(this._sender instanceof Player)) return null;
		return (Player) this._sender;
	}

	public Player getPlayerOrInformSender() {
		Player player = getPlayer();
		if (player != null) return player;
		//GeneralMessage.expectedToBePlayer.sendMessage(this._sender, this._sender.getName());
		return null;
	}

	public EithonPlayer getEithonPlayer() {
		Player player = getPlayer();
		if (player == null) return null;
		return new EithonPlayer(player);
	}

	public EithonPlayer getEithonPlayerOrInformSender() {
		Player player = getPlayerOrInformSender();
		if (player == null) return null;
		return new EithonPlayer(player);
	}

	public EithonArgument getArgument(String name) {
		EithonArgument argument = this._arguments.get(name);
		if (argument == null) {
			IParameterSyntax parameterSyntax = this._commandSyntax.getParameterSyntax(name);
			if (parameterSyntax == null) {
				throw new IllegalArgumentException(String.format("No such parameter: <%s>", name));
			}
			throw new IllegalArgumentException(String.format("Could not find a value for parameter <%s>.", name));
		}
		return argument;
	}

	private void sendMessage(String message) {
		if (this._sender != null) this._sender.sendMessage(message);
		else System.out.println(message);
	}

	public List<String> tabComplete() {
		Queue<String> argumentQueue = this._commandQueue;
		return tabComplete(this._commandSyntax, argumentQueue);
	}

	private List<String> tabComplete(CommandSyntax commandSyntax, Queue<String> argumentQueue) {
		if (argumentQueue.isEmpty()) throw new IllegalArgumentException("argumentQueue unexpectedly was empty");
		List<String> suggestions = new ArrayList<String>();
		String argument = argumentQueue.poll();
		if (commandSyntax.hasSubCommands()) {
			if (!argumentQueue.isEmpty()) {
				CommandSyntax subCommandSyntax = commandSyntax.getSubCommand(argument);
				if (subCommandSyntax != null) return tabComplete(subCommandSyntax, argumentQueue);
				if (!commandSyntax.hasParameters()) {
					sendMessage(String.format("Unexpected key word: %s", argument));
					return new ArrayList<String>();
				}
			} else if (argument.isEmpty()) {
				suggestions = commandSyntax.getKeyWordList();
			} else {
				suggestions = findPartialMatches(argument, commandSyntax.getKeyWordList());
			}
		}

		return tabCompleteParameter(commandSyntax, argumentQueue, argument, suggestions);
	}

	private List<String> tabCompleteParameter(
			CommandSyntax commandSyntax,
			Queue<String> argumentQueue,
			String argument,
			List<String> suggestions) {
		for (ParameterSyntax parameterSyntax : commandSyntax.getParameterSyntaxList()) {
			boolean hintGiven = false;
			String hint = parameterSyntax.getHint();
			if ((hint != null) && hint.contains(argument)) {
				hintGiven = true;
				argument = argumentQueue.poll();
				if (argument == null) {
					suggestions.addAll(getHintAsList(parameterSyntax));
					return suggestions;
				}
			}
			if (argument.isEmpty()) {
				if (parameterSyntax.getDisplayHint() && !hintGiven) suggestions.addAll(getHintAsList(parameterSyntax));
				else suggestions.addAll(parameterSyntax.getValidValues(this));
				return suggestions;
			}
			if (argumentQueue.isEmpty()) {
				suggestions.addAll(findPartialMatches(argument, parameterSyntax.getValidValues(this)));
				return suggestions;			
			}
			argument = argumentQueue.poll();
		}
		return suggestions;
	}

	private List<String> getHintAsList(ParameterSyntax parameterSyntax) {
		String hintPrefix = parameterSyntax.getHint() + " ";
		List<String> hint = new ArrayList<String>();
		List<String> validValues = parameterSyntax.getValidValues(this);
		if (validValues.isEmpty()) {
			hint.add(hintPrefix);
		} else {
			for (String value : validValues) {
				hint.add(hintPrefix + value);	
			}
		}
		return hint;
	}

	private static List<String> findPartialMatches(String partial, List<String> valueList) {
		List<String> found = new ArrayList<String>();
		for (String value : valueList) {
			if (value.startsWith(partial)) found.add(value);
		}
		return found;
	}
}
