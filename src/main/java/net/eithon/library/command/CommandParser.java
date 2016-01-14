package net.eithon.library.command;

import java.util.HashMap;

import net.eithon.library.command.syntax.CommandSyntax;
import net.eithon.library.command.syntax.CommandSyntax.CommandExecutor;
import net.eithon.library.extensions.EithonPlayer;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandParser {

	private CommandSender _sender;
	private CommandArguments _commandArguments;
	private CommandSyntax _commandSyntax;
	private HashMap<String, ParameterValue> _parameterValues;

	public CommandParser(CommandSyntax commandSyntax, CommandSender sender, Command cmd, String label, String[] args) {
		this._sender = sender;
		this._commandArguments = new CommandArguments(sender, args);
		this._commandSyntax = commandSyntax;
	}
	
	public boolean execute() {
		String command = this._commandArguments.getStringAsLowercase();
		if (!command.equalsIgnoreCase(this._commandSyntax.getName())) {
			this._sender.sendMessage(String.format("Expected command \"%s\", got \"%s\"", this._commandSyntax.getName(), command));
			return false;
		}
		this._parameterValues = new HashMap<String, ParameterValue>();
		CommandExecutor executor = this._commandSyntax.parse(this._commandArguments, this._parameterValues);
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

	public ParameterValue getArgument(String name) {
		return this._parameterValues.get(name);
	}
}
