package net.eithon.library.plugin;

import net.eithon.library.extensions.EithonPlayer;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandParser {

	private CommandSender _sender;
	private String[] _args;
	private ICommandHandler _commandHandler;
	private String _currentCommand;

	public CommandParser(ICommandHandler commandHandler, CommandSender sender, Command cmd, String label, String[] args) {
		this._sender = sender;
		this._args = args;
		this._commandHandler = commandHandler;
		this._currentCommand = null;
	}

	public void setCurrentCommand(String command) {
		this._currentCommand = command;
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

	public boolean hasCorrectNumberOfArgumentsOrShowSyntax(int min) {
		return hasCorrectNumberOfArgumentsOrShowSyntax(min, Integer.MAX_VALUE);
	}

	public boolean hasCorrectNumberOfArgumentsOrShowSyntax(int min, int max) {
		if (hasCorrectNumberOfArguments(min, max)) return true;
		showCommandSyntax();
		return false;
	}

	public boolean hasCorrectNumberOfArguments(int min, int max) {
		int length = this._args.length;
		return ((length >= min)  && (length <= max));
	}

	public boolean hasCorrectNumberOfArguments(int min) {
		return hasCorrectNumberOfArguments(min, Integer.MAX_VALUE);
	}

	public String getArgumentStringAsLowercase(int index) {
		if (this._args.length <= index) return null;
		return this._args[index].toLowerCase();
	}

	public int getArgumentInteger(int index, int defaultValue) {
		if (this._args.length <= index) return defaultValue;
		try {
			return Integer.parseInt(this._args[index]);
		} catch (Exception ex) { 
			this._sender.sendMessage(String.format("Could not parse this as an integer: %s", this._args[index]));
			showCommandSyntax();
			return defaultValue; 
		}
	}

	public double getArgumentDouble(int index, double defaultValue) {
		if (this._args.length <= index) return defaultValue;
		try {
			return Double.parseDouble(this._args[index]);
		} catch (Exception ex) {
			this._sender.sendMessage(String.format("Could not parse this as a float number: %s", this._args[index]));
			showCommandSyntax();
			return defaultValue; 
		}
	}

	public EithonPlayer getArgumentEithonPlayer(int index, EithonPlayer defaultValue) {
		String playerIdOrName =  getArgumentStringAsLowercase(index);
		if (playerIdOrName == null) return defaultValue;
		EithonPlayer eithonPlayer = EithonPlayer.getFromString(playerIdOrName);
		return eithonPlayer;
	}

	public EithonPlayer getArgumentEithonPlayerOrInformSender(int index, EithonPlayer defaultValue) {
		EithonPlayer eithonPlayer = getArgumentEithonPlayer(index, defaultValue);
		if (eithonPlayer == null) {
			String playerIdOrName =  getArgumentStringAsLowercase(index);
			if (playerIdOrName != null) {
				this._sender.sendMessage(String.format("Unknown player: \"%s\".", playerIdOrName));
			}
			return null;
		}
		return eithonPlayer;
	}

	public Player getArgumentPlayer(int index, Player defaultValue) {
		EithonPlayer defaultEithonPlayer = null;
		if (defaultValue != null) defaultEithonPlayer = new EithonPlayer(defaultValue);
		EithonPlayer eithonPlayer = getArgumentEithonPlayer(index, defaultEithonPlayer);
		if (eithonPlayer == null) return defaultValue;
		return eithonPlayer.getPlayer();
	}

	public Player getArgumentPlayerOrInformSender(int index, Player defaultValue) {
		EithonPlayer defaultEithonPlayer = null;
		if (defaultValue != null) defaultEithonPlayer = new EithonPlayer(defaultValue);
		EithonPlayer eithonPlayer = getArgumentEithonPlayerOrInformSender(index, defaultEithonPlayer);
		if (eithonPlayer == null) return defaultValue;
		return eithonPlayer.getPlayer();
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
		this._commandHandler.showCommandSyntax(this._sender, this._currentCommand);
	}
	
	public boolean execute() {
		return this._commandHandler.onCommand(this);
	}
}