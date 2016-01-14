package net.eithon.library.command.syntax;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import net.eithon.library.command.CommandArguments;
import net.eithon.library.command.ParameterValue;
import net.eithon.library.time.TimeMisc;

import org.bukkit.command.CommandSender;

public class ParameterSyntax extends Syntax {
	private ParameterType _type;
	private boolean _isNamed;
	private String _leftHandName;
	private ArrayList<String> _validValues;
	private ValueGetter _valueGetter;
	private boolean _isOptional;
	private String _defaultValue;
	private boolean _acceptsAnyValue;

	public enum ParameterType { STRING, REAL, INTEGER, Player, REST, BOOLEAN, TIME_SPAN };

	public interface ValueGetter {
		List<String> getValues();
	}

	public ParameterSyntax(ParameterType type, String name) {
		this(type, name, null);
	}

	public static List<String> fromArray(String[] array) {
		ArrayList<String> list = new ArrayList<String>();
		for (String string : array) {
			list.add(string);
		}
		return list;
	}

	ParameterSyntax(ParameterType type, String parameterName, String leftHandName) {
		super(parameterName);
		this._leftHandName = leftHandName;
		this._type = type;
		this._isNamed = leftHandName != null;
		this._isOptional = this._isNamed;
		this._valueGetter = null;
		this._validValues = new ArrayList<String>();
	}

	public boolean getIsOptional() { return this._isOptional; }
	public String getDefault() { return this._defaultValue; }

	public void setDefault(String defaultValue) {
		this._isOptional = true;
		this._defaultValue = defaultValue;
	}

	public void SetValueGetter(ValueGetter valueGetter, boolean acceptsAnyValue) {
		this._valueGetter = valueGetter;
		this._acceptsAnyValue = acceptsAnyValue;
	}

	public void setValues(String... args) {
		setValues(Arrays.asList(args));
	}

	public void setValues(List<String> values) {
		this._validValues = new ArrayList<String>();
		this._validValues.addAll(values);
	}

	public boolean parse(CommandArguments arguments, HashMap<String, ParameterValue> parameterValues) {
		String argument = arguments.getString();
		ParameterValue parameterValue = new ParameterValue(this, argument);
		if (argument == null) {
			if (this._isOptional) {
				if (parameterValues != null) parameterValues.put(getName(), parameterValue);
				return true;
			}
			arguments.getSender().sendMessage(String.format("Expected a value for argument %s", getName()));
			return false;
		}
		if (!verifyValueIsOkAccordingToType(arguments.getSender(), argument)) return false;
		if (this._acceptsAnyValue) {
			if (parameterValues != null) parameterValues.put(getName(), parameterValue);
			return true;
		}
		if (this._valueGetter != null) {
			this._validValues = new ArrayList<String>();
			this._validValues.addAll(this._valueGetter.getValues());
		}
		if (this._validValues != null) {
			for (String validValue : this._validValues) {
				if (argument.equals(validValue)) {
					if (parameterValues != null) parameterValues.put(getName(), parameterValue);
					return true;
				}
			}
		}
		arguments.getSender().sendMessage(String.format("The value \"%s\" was not an accepted value for argument %s.",
				argument, getName()));
		return false;
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
		if ((this._defaultValue != null) && !this._validValues.contains(this._defaultValue)) {
			this._validValues.add(0, this._defaultValue);
		}
		return this._validValues;
	}

	private boolean verifyValueIsOkAccordingToType(CommandSender sender, String argument) {
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
			case TIME_SPAN:
				TimeMisc.stringToSeconds(argument);
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

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("");
		if (!this._isNamed) sb.append(String.format("<%s", this.getName()));
		else sb.append(String.format("%s=<%s", this._leftHandName, this.getName()));
		if (this._type != ParameterType.STRING) sb.append(String.format(" : %s", this._type.toString()));
		if (this._validValues.size()>0) sb.append(String.format(" {%s}", validValuesAsString(true)));
		sb.append(">");
		return sb.toString();
	}

	private String validValuesAsString(boolean markDefault) {
		String validValues;
		if (this._defaultValue == null) validValues = String.join(", ", this._validValues);
		else {
			List<String> values = new ArrayList<String>();
			for (String value : this._validValues) {
				if (value.equals(this._defaultValue)) value = String.format("_%s_", value);
				values.add(value);
			}
			validValues = String.join(", ", values);
		}
		if (this._acceptsAnyValue) validValues = validValues + ", ...";
		return validValues;
	}

	public void setAcceptsAnyValue(boolean acceptsAnyValue) { this._acceptsAnyValue = acceptsAnyValue; }
}
