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
	private HashMap<String, Argument> _arguments;

	public EithonCommand(ICommandSyntax commandSyntax, CommandSender sender, Command cmd, String alias, String[] args) {
		if (!(commandSyntax instanceof CommandSyntax)) {
			throw new IllegalArgumentException("The argument commandSyntax could not be casted to CommandSyntax");
		}
		this._sender = sender;
		this._commandQueue = new LinkedList<String>();
		this._commandQueue.addAll(Arrays.asList(args));
		this._commandSyntax = (CommandSyntax) commandSyntax;
		this._arguments = new HashMap<String, Argument>();
	}

	public static ICommandSyntax createRootCommand(String commandName) {
		return new CommandSyntax(commandName);
	}

	public boolean execute() {
		CommandExecutor executor = null;
		try {
			executor = this._commandSyntax.parseArguments(this, this._commandQueue, this._arguments);
		} catch (CommandSyntaxException e) {
			sendMessage(e.getMessage());
		}
		if (executor == null) return false;
		executor.execute(this);
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

	public Argument getArgument(String name) {
		return this._arguments.get(name);
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
		if (commandSyntax.hasSubCommands()) {
			String command = argumentQueue.poll();
			if (argumentQueue.isEmpty()) {
				List<String> found = findPartialMatches(command, commandSyntax.getKeyWordList());
				return found;
			}
			CommandSyntax subCommandSyntax = commandSyntax.getSubCommand(command);
			if (subCommandSyntax != null) return tabComplete(subCommandSyntax, argumentQueue);
			sendMessage(String.format("Unexpected sub command: %s", command));
			return null;
		}

		for (ParameterSyntax parameterSyntax : commandSyntax.getParameterSyntaxList()) {
			String argument = argumentQueue.poll();
			boolean hintGiven = false;
			String hint = parameterSyntax.getHint();
			if ((hint != null) && hint.contains(argument)) {
				hintGiven = true;
				argument = argumentQueue.poll();
				if (argument == null) return getHintAsList(parameterSyntax);
			}
			if (argument.isEmpty()) {
				if (parameterSyntax.getDisplayHint() && !hintGiven) return getHintAsList(parameterSyntax);
				else return parameterSyntax.getValidValues(this);
			}
			if (argumentQueue.isEmpty()) {
				List<String> found = findPartialMatches(argument, parameterSyntax.getValidValues(this));
				return found;			
			}
		}
		return null;
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
