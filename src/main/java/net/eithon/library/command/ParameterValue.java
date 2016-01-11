package net.eithon.library.command;

import org.bukkit.entity.Player;

import net.eithon.library.command.syntax.CommandSyntax;
import net.eithon.library.command.syntax.ParameterSyntax;
import net.eithon.library.command.syntax.Syntax;

public class ParameterValue {

	private ParameterSyntax _parameterSyntax;
	private CommandSyntax _commandSyntax;
	private String _value;

	public ParameterValue(CommandSyntax commandSyntax, String command) {
		this._commandSyntax = commandSyntax;
		this._value = command;
	}

	public ParameterValue(ParameterSyntax parameterSyntax, String argument) {
		this._parameterSyntax = parameterSyntax;
		this._value = argument;
	}

	public Player asPlayer() {
		// TODO Auto-generated method stub
		return null;
	}

	public float getFloat() {
		return Float.parseFloat(getString());
	}

	public double getDouble() {
		return Double.parseDouble(getString());
	}

	public int getInteger() {
		return Integer.parseInt(getString());
	}

	public long getLong() {
		return Long.parseLong(getString());
	}

	public boolean getBoolean() {
		return Boolean.parseBoolean(getString());
	}

	public String getString() {
		if (this._value != null) return this._value;
		if (this._parameterSyntax == null) return null;
		return this._parameterSyntax.getDefault();
	}

	public String getStringAsLowerCase() {
		String value = getString();
		if (value == null) return null;
		return value.toLowerCase();
	}

	public String getRaw() {
		return this._value;
	}

	public boolean hasValue() {
		return this._value != null;
	}

}
