package net.eithon.library.command.syntax;

import java.util.ArrayList;

import net.eithon.library.command.CommandArguments;

import org.bukkit.command.CommandSender;
import org.yaml.snakeyaml.tokens.ValueToken;

public class ArgumentSyntax {
	private ArgumentType _type;
	private String _name;
	private boolean _isNamed;
	private ArrayList<String> _validValues;
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
		this._validValues = null;
	}

	public String getName() {return this._name; }

	public boolean getIsOptional() { return this._isOptional; }

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
		this._validValues = new ArrayList<String>();
		for (Integer value : args) {
			this._validValues.add(value.toString());
		}
	}

	public boolean isOk(CommandSender sender, CommandArguments arguments) {
		String argument = arguments.getString();
		if (argument == null) {
			if (this._isOptional) return true;
			sender.sendMessage(String.format("Expected a value for argument %s", this._name));
			return false;
		}
		if (!typeIsOk(sender, argument)) return false;
		if (!this._valuesAreMandatory) return true;
		if (this._valueGetter != null) {
			this._validValues = new ArrayList<String>();
			for (String value : this._valueGetter.getValues()) {
				this._validValues.add(value);
			}
		}
		if (this._validValues != null) {
			for (String validValue : this._validValues) {
				if (argument.equals(validValue)) return true;
			}
		}
		sender.sendMessage(String.format("The value \"%s\" was not an accepted value for argument %s.",
				argument, this._name));
		return false;
	}

	private boolean typeIsOk(CommandSender sender, String argument) {
		try {
			switch (this._type) {
			case BOOLEAN:
				Boolean.parseBoolean(argument);
				break;
			case INTEGER:
				Integer.parseInt(argument);
				break;
			case REAL:
				Float.parseFloat(argument);
				break;
			default:
				break;
			}
		} catch (final Exception e) {
			sender.sendMessage(String.format("\"%s\" is not of type %s", argument, this._type.toString()));
			return false;
		}
		return true;
	}
}
