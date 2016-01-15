package net.eithon.library.command;

import net.eithon.library.command.syntax.ParameterSyntax;

public class ArgumentBase {
	private ParameterSyntax _parameterSyntax;
	private String _value;


	public ArgumentBase(ParameterSyntax parameterSyntax, String argument) {
		this._parameterSyntax = parameterSyntax;
		this._value = argument;
		// TODO Auto-generated constructor stub
	}

	public float asFloat() {
		return Float.parseFloat(asString());
	}

	public double asDouble() {
		return Double.parseDouble(asString());
	}

	public int asInteger() {
		return Integer.parseInt(asString());
	}

	public long asLong() {
		return Long.parseLong(asString());
	}

	public boolean asBoolean() {
		return Boolean.parseBoolean(asString());
	}

	public String asString() {
		if (this._value != null) return this._value;
		if (this._parameterSyntax == null) return null;
		return this._parameterSyntax.getDefault();
	}

	public String asLowerCase() {
		String value = asString();
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
