package net.eithon.library.command;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;

import net.eithon.library.command.CommandSyntax.CommandExecutor;
import net.eithon.library.extensions.EithonPlayer;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class EithonCommand {

	private CommandSender _sender;
	private Queue<String> _commandQueue;
	private CommandSyntax _commandSyntax;
	private HashMap<String, Argument> _arguments;

	public EithonCommand(CommandSyntax commandSyntax, CommandSender sender, Command cmd, String alias, String[] args) {
		this._sender = sender;
		this._commandQueue = new LinkedList<String>();
		this._commandQueue.addAll(Arrays.asList(args));
		this._commandSyntax = commandSyntax;
	}

	public boolean execute() {
		/*
		if (this._commandQueue.size() < 1) {
			sendMessage(String.format("Empty command. Expected it to start with \"%s\"", this._commandSyntax.getName()));
			return false;
		}
		String command = this._commandQueue.poll();
		if (!command.equals(this._commandSyntax.getName())) {
			sendMessage(String.format("Expected command \"%s\", got \"%s\"", this._commandSyntax.getName(), command));
			return false;
		}
		*/
		return execute(this._commandSyntax);
	}

	public boolean execute(CommandSyntax commandSyntax) {
		if (commandSyntax.hasSubCommands()) {
			if (this._commandQueue.size() < 1) {
				sendMessage(String.format("Too short command. Expected one of the following: %s", String.join(", ", this._commandSyntax.getSubCommands())));
				return false;
			}
			String commandName = this._commandQueue.poll();
			CommandSyntax subCommand = commandSyntax.getSubCommand(commandName);
			if (subCommand == null) {
				this._sender.sendMessage(String.format("Expected command \"%s\", got \"%s\"", this._commandSyntax.getName(), commandName));
				return false;
			}
			return execute(subCommand);
		}
		this._arguments = new HashMap<String, Argument>();
		try {
			CommandExecutor executor = commandSyntax.parseArguments(this, this._commandQueue, this._arguments);
			if (executor == null) return false;
			executor.execute(this);
		} catch (CommandSyntaxException e) {
			sendMessage(e.getMessage());
			return false;
		}
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
}
