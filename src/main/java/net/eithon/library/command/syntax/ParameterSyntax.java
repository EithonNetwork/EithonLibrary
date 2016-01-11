package net.eithon.library.command.syntax;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import net.eithon.library.command.CommandArguments;

import org.bukkit.command.CommandSender;

public class ParameterSyntax {
	private ParameterType _type;
	private String _name;
	private boolean _isNamed;
	private ArrayList<String> _validValues;
	private ValueGetter _valueGetter;
	private boolean _valuesAreMandatory;
	private boolean _isOptional;

	public enum ParameterType { STRING, REAL, INTEGER, Player, REST, BOOLEAN };

	public interface ValueGetter {
		List<String> getValues();
	}

	public ParameterSyntax(ParameterType type, String name) {
		this(type, name, false);
	}
	
	public static List<String> fromArray(String[] array) {
		ArrayList<String> list = new ArrayList<String>();
		for (String string : array) {
			list.add(string);
		}
		return list;
	}

	ParameterSyntax(ParameterType type, String name, boolean isNamed) {
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
		if (this._type != ParameterType.INTEGER) {
			throw new IllegalArgumentException(String.format("Expected values of type %s", this._type.toString()));
		}
		this._validValues = new ArrayList<String>();
		for (Integer value : args) {
			this._validValues.add(value.toString());
		}
	}

	public boolean isOk(CommandArguments arguments) {
		String argument = arguments.getString();
		if (argument == null) {
			if (this._isOptional) return true;
			arguments.getSender().sendMessage(String.format("Expected a value for argument %s", this._name));
			return false;
		}
		if (!typeIsOk(arguments.getSender(), argument)) return false;
		if (!this._valuesAreMandatory) return true;
		if (this._valueGetter != null) {
			this._validValues = new ArrayList<String>();
			this._validValues.addAll(this._valueGetter.getValues());
		}
		if (this._validValues != null) {
			for (String validValue : this._validValues) {
				if (argument.equals(validValue)) return true;
			}
		}
		arguments.getSender().sendMessage(String.format("The value \"%s\" was not an accepted value for argument %s.",
				argument, this._name));
		return false;
	}

	public List<String> tabComplete(CommandArguments arguments) {
		String argument = arguments.getString();
		List<String> validValues = getValidValues();
		if (argument == null)  {
			if (this._isOptional && (validValues != null)) return validValues;
			arguments.getSender().sendMessage(String.format("Expected a value for argument %s", this._name));
			return null;
		}
		if (!typeIsOk(arguments.getSender(), argument)) return null;
		if (!this._valuesAreMandatory) return null;
		if (validValues != null) {
			for (String validValue : this._validValues) {
				if (argument.equals(validValue)) return null;
			}
		}
		arguments.getSender().sendMessage(String.format("The value \"%s\" was not an accepted value for argument %s.",
				argument, this._name));
		return null;
	}


	public List<String> getValidValues() {
		if (this._valueGetter != null) {
			this._validValues = new ArrayList<String>();
			this._validValues.addAll(this._valueGetter.getValues());
			this._validValues.sort(new Comparator<String>() {
				@Override
				public int compare(String o1, String o2) {
					return o1.compareTo(o2);
				}
			});
		}
		return this._validValues;
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
