package net.eithon.library.command;

import net.eithon.library.extensions.EithonPlayer;
import net.eithon.library.time.TimeMisc;

import org.bukkit.entity.Player;

public class Argument {
	private String _argument;
	private int _position;

	public Argument(String argument, int position) {
		this._argument = argument;
		this._position = position;
	}
	
	public int getPosition() {
		return this._position;
	}

	public String getString(String defaultValue) {
		if (this._argument == null) return null;
		return this._argument;
	}

	public String getStringAsLowercase(String defaultValue) {
		String argument = this._argument;
		if (argument == null) argument = defaultValue;
		if (argument == null) return null;
		return argument.toLowerCase();
	}

	public long getLong(long defaultValue) {
		if (this._argument == null) return defaultValue;
		return Long.parseLong(this._argument);
	}

	public int getInteger(int defaultValue) {
		if (this._argument == null) return defaultValue;
		return Integer.parseInt(this._argument);
	}

	public double getDouble(double defaultValue) {
		if (this._argument == null) return defaultValue;
		return Double.parseDouble(this._argument);
	}

	public boolean getBoolean(boolean defaultValue) {
		String argument = getStringAsLowercase(null);
		if (argument == null) return defaultValue;
		if (argument.matches("^(yes|true|1)$")) return true;
		if (argument.matches("^(no|false|0)$")) return false;
		return Boolean.parseBoolean(argument);
	}

	public long getTimeSpanAsSeconds(long defaultValue) {
		String argument = getStringAsLowercase(null);
		if (argument == null) return defaultValue;
		return TimeMisc.stringToSeconds(this._argument);
	}

	public EithonPlayer getEithonPlayer(EithonPlayer defaultValue) {
		String playerIdOrName =  getStringAsLowercase(null);
		if (playerIdOrName == null) return defaultValue;
		return EithonPlayer.getFromString(playerIdOrName);
	}

	public Player getPlayer(Player defaultValue) {
		EithonPlayer defaultEithonPlayer = null;
		if (defaultValue != null) defaultEithonPlayer = new EithonPlayer(defaultValue);
		EithonPlayer eithonPlayer = getEithonPlayer(defaultEithonPlayer);
		if (eithonPlayer == null) return defaultValue;
		return eithonPlayer.getPlayer();
	}
}
