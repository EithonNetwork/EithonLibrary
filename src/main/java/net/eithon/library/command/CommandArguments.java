package net.eithon.library.command;

import net.eithon.library.extensions.EithonPlayer;
import net.eithon.library.plugin.Logger;
import net.eithon.library.time.TimeMisc;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandArguments {
	private String[] _args;
	private int _nextArgument;
	private CommandSender _sender;

	public CommandArguments(CommandSender sender, String[] args) {
		this._args = args;
		this._nextArgument = 0;
		this._sender = sender;
	}
	
	@Override
	public CommandArguments clone() {
		CommandArguments clone = new CommandArguments(this._sender, this._args);
		clone._nextArgument = this._nextArgument;
		return clone;
	}

	public void setNextArgument(int nextArgument) { this._nextArgument = nextArgument; }

	private Argument getNextArgument() {
		int position = this._nextArgument++;
		String argument = this._args.length > position ? this._args[position] : null;
		return new Argument(argument, position);
	}

	boolean hasCorrectNumberOfArgumentsOrShowSyntax(int min) {
		return hasCorrectNumberOfArgumentsOrShowSyntax(min, Integer.MAX_VALUE);
	}

	boolean hasCorrectNumberOfArgumentsOrShowSyntax(int min, int max) {
		if (hasCorrectNumberOfArguments(min, max)) return true;
		Logger.libraryInfo("Expected %d to %d arguments, had %d arguments", min, max, this._args.length);
		return false;
	}

	public boolean hasCorrectNumberOfArguments(int min, int max) {
		int length = this._args.length;
		return ((length >= min)  && (length <= max));
	}

	public boolean hasCorrectNumberOfArguments(int min) {
		return hasCorrectNumberOfArguments(min, Integer.MAX_VALUE);
	}

	public String getString() {
		return getString(null);
	}

	public String getString(String defaultValue) {
		Argument argument = getNextArgument();
		return argument.getString(defaultValue);
	}

	public String getStringAsLowercase() {
		return getStringAsLowercase(null);
	}

	public String getStringAsLowercase(String defaultValue) {
		Argument argument = getNextArgument();
		return argument.getStringAsLowercase(defaultValue);
	}

	public String getRest() {
		return getRest(null);
	}

	public String getRest(String defaultValue) {
		if (this._args.length <= this._nextArgument) return defaultValue;
		String result = "";
		String value;
		while ((value = getString(null)) != null) {
			if (result.length() > 0) result += " ";
			result += value;
		}
		return result;
	}

	public int getInteger(int defaultValue) {
		Argument argument = getNextArgument();
		try {
			return argument.getInteger(defaultValue);
		} catch (Exception ex) { 
			this._sender.sendMessage(String.format("Could not parse this as an integer: %s, will default to %d", 
					argument.getString(null), defaultValue));
			return defaultValue;
		}
	}

	public long getLong(long defaultValue) {
		Argument argument = getNextArgument();
		try {
			return argument.getLong(defaultValue);
		} catch (Exception ex) { 
			this._sender.sendMessage(String.format("Could not parse this as a long integer: %s, will default to %d", 
					argument.getString(null), defaultValue));
			return defaultValue;
		}
	}

	public boolean getBoolean(boolean defaultValue) {
		Argument argument = getNextArgument();
		return argument.getBoolean(defaultValue);
	}

	public long getTimeSpanAsSeconds(long defaultValue) {
		Argument argument = getNextArgument();
		try {
			return argument.getTimeSpanAsSeconds(defaultValue);
		} catch (Exception ex) {
			this._sender.sendMessage(String.format("Could not parse %s this as a time span, will default to %s.",
					argument.getString(null), TimeMisc.secondsToString(defaultValue)));
			return defaultValue; 
		}
	}

	public double getDouble(double defaultValue) {
		Argument argument = getNextArgument();
		try {
			return argument.getDouble(defaultValue);
		} catch (Exception ex) {
			this._sender.sendMessage(String.format("Could not parse this as a double float value: %s, will default to %.2f", 
					argument.getString(null), defaultValue));
			return defaultValue; 
		}
	}

	public EithonPlayer getArgumentEithonPlayer(Player defaultValue) {
		return getEithonPlayer(defaultValue == null ? null : new EithonPlayer(defaultValue));
	}

	public EithonPlayer getEithonPlayer(EithonPlayer defaultValue) {
		Argument argument = getNextArgument();
		return argument.getEithonPlayer(defaultValue);
	}

	public EithonPlayer getEithonPlayerOrInformSender(EithonPlayer defaultValue) {
		Argument argument = getNextArgument();
		EithonPlayer eithonPlayer = argument.getEithonPlayer(defaultValue);
		if (eithonPlayer != null) return eithonPlayer;

		String playerIdOrName =  argument.getStringAsLowercase(null);
		if (playerIdOrName != null) {
			this._sender.sendMessage(String.format("Unknown player: \"%s\".", playerIdOrName));
		}
		return null;
	}

	public Player getPlayer(Player defaultValue) {
		Argument argument = getNextArgument();
		return argument.getPlayer(defaultValue);
	}

	public Player getPlayerOrInformSender(Player defaultValue) {
		EithonPlayer defaultEithonPlayer = null;
		if (defaultValue != null) defaultEithonPlayer = new EithonPlayer(defaultValue);
		EithonPlayer eithonPlayer = getEithonPlayerOrInformSender(defaultEithonPlayer);
		if (eithonPlayer == null) return defaultValue;
		return eithonPlayer.getPlayer();
	}
}
