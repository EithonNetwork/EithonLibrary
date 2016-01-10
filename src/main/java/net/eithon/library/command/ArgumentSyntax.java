package net.eithon.library.command;

import java.util.ArrayList;

import org.yaml.snakeyaml.tokens.ValueToken;

public class ArgumentSyntax {
	private ArgumentType _type;
	private String _name;
	private boolean _isNamed;
	private ArrayList<Object> _values;
	private ValueGetter _valueGetter;
	private boolean _valuesAreMandatory;
	private boolean _isOptional;
	
	public enum ArgumentType { STRING, REAL, INTEGER, Player, REST, BOOLEAN };

	public interface ValueGetter {
		String[] getValues();
	}
	
	public ArgumentSyntax(ArgumentType type, String name) {
		this(type, name, false);
	}

	ArgumentSyntax(ArgumentType type, String name, boolean isNamed) {
		this._type = type;
		this._name = name;
		this._isNamed = isNamed;
		this._isOptional = false;
		this._valueGetter = null;
		this._values = null;
	}

	public String getName() {return this._name; }

	public void SetValueGetter(ValueGetter valueGetter, boolean mandatory) {
		this._valueGetter = valueGetter;
		this._valuesAreMandatory = mandatory;
	}

	void setOptional() {
		this._isOptional = true;
	}

	public void setValues(Integer... args) {
		if (this._type != ArgumentType.INTEGER) {
			throw new IllegalArgumentException(String.format("Expected values of type %s", this._type.toString()));
		}
		this._values = new ArrayList<Object>();
		for (Integer value : args) {
			this._values.add(value);
		}
	}
}
