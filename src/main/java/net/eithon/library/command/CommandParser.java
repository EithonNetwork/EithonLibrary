package net.eithon.library.command;

import net.eithon.library.extensions.EithonPlayer;
import net.eithon.library.plugin.GeneralMessage;
import net.eithon.library.plugin.Logger;
import net.eithon.library.time.TimeMisc;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandParser {

	private CommandSender _sender;
	private Arguments _arguments;
	private ICommandHandler _commandHandler;
	private String _currentCommand;

	public CommandParser(ICommandHandler commandHandler, CommandSender sender, Command cmd, String label, String[] args) {
		this._sender = sender;
		this._arguments = new Arguments(sender, args);
		this._commandHandler = commandHandler;
		this._currentCommand = null;
	}
	
	public Arguments getArguments() { return this._arguments; }

	public void setCurrentCommand(String command) { this._currentCommand = command;	}

	public CommandSender getSender() { return this._sender; }

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

	public String getArgumentCommand() {
		String command = this._arguments.getStringAsLowercase();
		setCurrentCommand(command);
		return command;
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
		this._commandHandler.showCommandSyntax(this._sender, this._currentCommand);
	}

	public boolean execute() {
		return this._commandHandler.onCommand(this);
	}
}
