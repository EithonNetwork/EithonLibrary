package net.eithon.library.command;

import net.eithon.library.extensions.EithonPlayer;
import net.eithon.library.time.TimeMisc;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class EithonArgument {
	private ParameterSyntax _parameterSyntax;
	private String _value;
	private EithonCommand _eithonCommand;

	public EithonArgument(EithonCommand command, ParameterSyntax parameterSyntax, String argument) {
		this._eithonCommand = command;
		this._parameterSyntax = parameterSyntax;
		this._value = argument;
	}
	
	public boolean hasValue() { return this._value != null;	}
	public String getRaw() { return this._value; }
	
	public float asFloat() { return Float.parseFloat(asString()); }
	public double asDouble() { return Double.parseDouble(asString()); }
	public int asInteger() { return Integer.parseInt(asString()); }
	public long asLong() { return Long.parseLong(asString()); }
	public boolean asBoolean() { return Boolean.parseBoolean(asString()); }

	public String asString() {
		if (this._value != null) return this._value;
		if (this._parameterSyntax == null) return null;
		return this._parameterSyntax.getDefault(this._eithonCommand);
	}

	public String asLowerCase() {
		String value = asString();
		if (value == null) return null;
		return value.toLowerCase();
	}

	public long asSeconds() { return TimeMisc.stringToSeconds(asString()); }
	public long asTicks() { return TimeMisc.stringToTicks(asString()); }

	public EithonPlayer asEithonPlayer() {
		String playerName = asString();
		if (playerName == null) return null;
		return EithonPlayer.getFromString(playerName);
	}

	public Player asPlayer() {
		EithonPlayer eithonPlayer = asEithonPlayer();
		if (eithonPlayer == null) return null;
		return eithonPlayer.getPlayer();
	}

	public OfflinePlayer asOfflinePlayer() {
		EithonPlayer eithonPlayer = asEithonPlayer();
		if (eithonPlayer == null) return null;
		return eithonPlayer.getOfflinePlayer();
	}
}

