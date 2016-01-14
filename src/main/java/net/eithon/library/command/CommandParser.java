package net.eithon.library.command;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;

import net.eithon.library.command.syntax.CommandArgumentException;
import net.eithon.library.command.syntax.CommandSyntax;
import net.eithon.library.command.syntax.CommandSyntax.CommandExecutor;
import net.eithon.library.extensions.EithonPlayer;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandParser {

	private CommandSender _sender;
	private Queue<String> _commandQueue;
	private CommandSyntax _commandSyntax;
	private HashMap<String, Argument> _arguments;

	public CommandParser(CommandSyntax commandSyntax, CommandSender sender, Command cmd, String label, String[] args) {
		this._sender = sender;
		this._commandQueue = new LinkedList<String>();
		this._commandQueue.addAll(Arrays.asList(args));
		this._commandSyntax = commandSyntax;
	}

	public boolean execute() {
		String command = this._commandQueue.poll().toLowerCase();
		if (!command.equalsIgnoreCase(this._commandSyntax.getName())) {
			this._sender.sendMessage(String.format("Expected command \"%s\", got \"%s\"", this._commandSyntax.getName(), command));
			return false;
		}
		this._arguments = new HashMap<String, Argument>();
		try {
			CommandExecutor executor = this._commandSyntax.parse(this._commandQueue, this._arguments);
			if (executor == null) return false;
			executor.execute(this);
		} catch (CommandArgumentException e) {
			this._sender.sendMessage(e.getMessage());
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
}
