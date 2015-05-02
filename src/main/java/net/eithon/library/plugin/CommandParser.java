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
	private int _nextArgument;

	public CommandParser(ICommandHandler commandHandler, CommandSender sender, Command cmd, String label, String[] args) {
		this._sender = sender;
		this._args = args;
		this._nextArgument = 0;
		this._commandHandler = commandHandler;
		this._currentCommand = null;
	}

	public void setCurrentCommand(String command) { this._currentCommand = command;	}

	public void setNextArgument(int nextArgument) { this._nextArgument = nextArgument; }

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

	public String getArgumentStringAsLowercase() {
		return getArgumentStringAsLowercase(this._nextArgument++, null);
	}

	public String getArgumentStringAsLowercase(String defaultValue) {
		return getArgumentStringAsLowercase(this._nextArgument++, defaultValue);
	}

	public String getArgumentStringAsLowercase(int index) {
		return getArgumentStringAsLowercase(index, null);
	}

	public String getArgumentStringAsLowercase(int index, String defaultValue) {
		String result = getArgumentString(index, defaultValue);
		if (result == null) return null;
		return result.toLowerCase();
	}

	public String getArgumentString() {
		return getArgumentString(this._nextArgument++, null);
	}

	public String getArgumentString(String defaultValue) {
		return getArgumentString(this._nextArgument++, defaultValue);
	}

	public String getArgumentString(int index) {
		return getArgumentString(index, null);
	}

	public String getArgumentString(int index, String defaultValue) {
		if (this._args.length <= index) return defaultValue;
		return this._args[index];
	}

	public String getArgumentRest() {
		return getArgumentRest(this._nextArgument++, null);
	}

	public String getArgumentRest(String defaultValue) {
		return getArgumentRest(this._nextArgument++, defaultValue);
	}

	public String getArgumentRest(int index) {
		return getArgumentRest(index, null);
	}

	private String getArgumentRest(int index, String defaultValue) {
		if (this._args.length <= index) return defaultValue;
		String result = "";
		String value;
		while ((value = getArgumentString(index++, null)) != null) {
			if (result.length() > 0) result += " ";
			result += value;
		}
		return result;
	}

	public int getArgumentInteger(int defaultValue) {
		return getArgumentInteger(this._nextArgument++, defaultValue);
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

	public double getArgumentDouble(double defaultValue) {
		return getArgumentDouble(this._nextArgument++,defaultValue);
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

	public String getArgumentCommand() {
		return getArgumentCommand(this._nextArgument++);
	}

	public String getArgumentCommand(int index) {
		String command =  getArgumentStringAsLowercase(index);
		setCurrentCommand(command);
		return command;
	}

	public EithonPlayer getArgumentEithonPlayer(EithonPlayer defaultValue) {
		return getArgumentEithonPlayer(this._nextArgument++, defaultValue);
	}

	public EithonPlayer getArgumentEithonPlayer(int index, EithonPlayer defaultValue) {
		String playerIdOrName =  getArgumentStringAsLowercase(index);
		if (playerIdOrName == null) return defaultValue;
		EithonPlayer eithonPlayer = EithonPlayer.getFromString(playerIdOrName);
		return eithonPlayer;
	}

	public EithonPlayer getArgumentEithonPlayerOrInformSender(EithonPlayer defaultValue) {
		return getArgumentEithonPlayerOrInformSender(this._nextArgument++, defaultValue);
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

	public Player getArgumentPlayer(Player defaultValue) {
		return getArgumentPlayer(this._nextArgument++, defaultValue);
	}

	public Player getArgumentPlayer(int index, Player defaultValue) {
		EithonPlayer defaultEithonPlayer = null;
		if (defaultValue != null) defaultEithonPlayer = new EithonPlayer(defaultValue);
		EithonPlayer eithonPlayer = getArgumentEithonPlayer(index, defaultEithonPlayer);
		if (eithonPlayer == null) return defaultValue;
		return eithonPlayer.getPlayer();
	}

	public Player getArgumentPlayerOrInformSender(Player defaultValue) {
		return getArgumentPlayerOrInformSender(this._nextArgument++, defaultValue);
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
