package net.eithon.library.command;

import java.util.HashMap;

import net.eithon.library.command.syntax.CommandSyntax;
import net.eithon.library.command.syntax.CommandSyntax.CommandExecutor;
import net.eithon.library.extensions.EithonPlayer;
import net.eithon.library.plugin.GeneralMessage;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandParser {

	private CommandSender _sender;
	private CommandArguments _commandArguments;
	private ICommandHandler _commandHandler;
	private CommandSyntax _commandSyntax;

	public CommandParser(ICommandHandler commandHandler, CommandSender sender, Command cmd, String label, String[] args) {
		this._sender = sender;
		this._commandArguments = new CommandArguments(sender, args);
		this._commandHandler = commandHandler;
		this._commandSyntax = commandHandler.getCommandSyntax();
	}
	
	public boolean execute() {
		String command = this._commandArguments.getStringAsLowercase();
		if (!command.equalsIgnoreCase(this._commandSyntax.getName())) {
			this._sender.sendMessage(String.format("Expected command \"%s\", got \"%s\"", this._commandSyntax.getName(), command));
			return false;
		}
		CommandExecutor executor = this._commandSyntax.verifyAndGetExecutor(this._commandArguments);
		if (executor == null) return false;
		executor.execute(this);
		return true;
	}

	public CommandArguments getArguments() { return this._commandArguments; }

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

	public boolean hasPermission(String permission)
	{
		EithonPlayer eithonPlayer = getEithonPlayer();
		if (eithonPlayer == null) return true;
		return eithonPlayer.hasPermission(permission);
	}

	public boolean hasPermissionOrInformSender(String permission)
	{
		EithonPlayer eithonPlayer = getEithonPlayer();
		if (eithonPlayer == null) return true;
		return eithonPlayer.hasPermissionOrInformPlayer(permission);
	}
}
