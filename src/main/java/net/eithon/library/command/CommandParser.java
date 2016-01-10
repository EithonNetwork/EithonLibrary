package net.eithon.library.command;

import java.util.HashMap;

import net.eithon.library.command.CommandSyntax.CommandExecutor;
import net.eithon.library.extensions.EithonPlayer;
import net.eithon.library.plugin.GeneralMessage;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandParser {

	private CommandSender _sender;
	private CommandArguments _arguments;
	private ICommandHandler _commandHandler;
	private String _currentCommand;
	private CommandSyntax _rootCommand;

	public CommandParser(ICommandHandler commandHandler, CommandSender sender, Command cmd, String label, String[] args) {
		this._sender = sender;
		this._arguments = new CommandArguments(sender, args);
		this._commandHandler = commandHandler;
		this._currentCommand.execute(this._arguments);
	}
	
	public CommandArguments getArguments() { return this._arguments; }

	public CommandSender getSender() { return this._sender; }

	public String getCurrentCommand() { return this._currentCommand; }

	public Player getPlayer() {
		if (this._sender == null) return null;
		if (!(this._sender instanceof Player)) return null;
		return (Player) this._sender;
	}

	public Player getPlayerOrInformSender() {
		Player player = getPlayer();
		if (player != null) return player;
		GeneralMessage.expectedToBePlayer.sendMessage(this._sender, this._sender.getName());
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

	public void showCommandSyntax() {
		if (this._currentCommand == null) {
			this._sender.sendMessage("Unknown command.");
			return;
		}
	}

	public CommandSyntax setRootCommand(String commandName) {
		CommandSyntax commandSyntax = new CommandSyntax(commandName);
		this._rootCommand = commandSyntax;
		return commandSyntax;
	}

	public CommandSyntax setRootCommand(String commandName, CommandExecutor commandExecutor) {
		CommandSyntax commandSyntax = setRootCommand(commandName);
		commandSyntax.setExecutor(commandExecutor);
		return commandSyntax;
	}
}
