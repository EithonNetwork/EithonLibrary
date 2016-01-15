package net.eithon.library.command.syntax;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.command.CommandSender;

import net.eithon.library.command.Argument;
import net.eithon.library.time.TimeMisc;

public class ParameterSyntax extends Syntax {
	private static String parameterName = "([^:{]+)";
	private static String type = "([^{]+)";
	private static String valueList = "([^}]+)";
	private static Pattern insideParameterPattern= Pattern.compile(parameterName + "( *: *" + type + ")?" + "( *\\{" + valueList + "\\})?");

	private ParameterType _type;
	private boolean _isNamed;
	private String _leftHandName;
	private ArrayList<String> _validValues;
	private ValueGetter _valueGetter;
	private boolean _isOptional;
	private String _defaultValue;
	private boolean _acceptsAnyValue;
	private DefaultGetter _defaultGetter;

	public enum ParameterType { STRING, REAL, INTEGER, Player, REST, BOOLEAN, TIME_SPAN };

	public interface ValueGetter {
		List<String> getValues(CommandSender sender);
	}
	
	public interface DefaultGetter {
		String getDefault(CommandSender sender);
	}

	public static List<String> fromArray(String[] array) {
		ArrayList<String> list = new ArrayList<String>();
		for (String string : array) {
			list.add(string);
		}
		return list;
	}

	public ParameterSyntax(String name, ParameterType type) {
		this(name, type, null);
	}

	ParameterSyntax(String parameterName, ParameterType type, String leftHandName) {
		super(parameterName);
		this._leftHandName = leftHandName;
		this._type = type;
		this._isNamed = leftHandName != null;
		this._isOptional = this._isNamed;
		this._valueGetter = null;
		this._acceptsAnyValue = true;
		this._validValues = new ArrayList<String>();
	}

	public boolean getIsOptional() { return this._isOptional; }
	public String getDefault() { return this._defaultValue; }

	public void setDefault(String defaultValue) {
		this._isOptional = true;
		this._defaultValue = defaultValue;
	}

	public ParameterSyntax setMandatoryValues(ValueGetter valueGetter) {
		this._valueGetter = valueGetter;
		this._acceptsAnyValue = false;
		return this;	}

	public ParameterSyntax setExampleValues(ValueGetter valueGetter) {
		this._valueGetter = valueGetter;
		this._acceptsAnyValue = true;
		return this;
	}

	public void setValues(String... args) {
		setValues(Arrays.asList(args));
	}

	public void setValues(List<String> values) {
		this._validValues = new ArrayList<String>();
		this._validValues.addAll(values);
	}

	public void parseArguments(String argument, HashMap<String, Argument> collectedArguments) throws CommandSyntaxException {
		Argument parameterValue = new Argument(this, argument);
		if (argument == null) {
			if (this._isOptional) {
				if (collectedArguments != null) collectedArguments.put(getName(), parameterValue);
				return;
			}	
			throw new CommandSyntaxException(String.format("Expected a value for argument %s", getName()));
		}
		verifyValueIsOkAccordingToType(argument);
		if (this._acceptsAnyValue) {
			if (collectedArguments != null) collectedArguments.put(getName(), parameterValue);
			return;
		}
		if (this._valueGetter != null) {
			this._validValues = new ArrayList<String>();
			// TODO: How to handle sender parameter?
			this._validValues.addAll(this._valueGetter.getValues(null));
		}
		if (this._validValues != null) {
			for (String validValue : this._validValues) {
				if (argument.equals(validValue)) {
					if (collectedArguments != null) collectedArguments.put(getName(), parameterValue);
					return;
				}
			}
		}
		throw new CommandSyntaxException(String.format("The value \"%s\" was not an accepted value for argument %s.",
				argument, getName()));
	}

	public List<String> getValidValues() {
		if (this._valueGetter != null) {
			this._validValues = new ArrayList<String>();
			// TODO: How to handle sender parameter?
			this._validValues.addAll(this._valueGetter.getValues(null));
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

	private boolean verifyValueIsOkAccordingToType(String argument) throws CommandSyntaxException {
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
			throw new CommandSyntaxException(String.format("\"%s\" is not of type %s", argument, this._type.toString()));
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

	public static ParameterSyntax parseSyntax(String leftSide, String parameter) throws CommandSyntaxException {
		Matcher matcher = insideParameterPattern.matcher(parameter);
		if (!matcher.matches()) {
			throw new CommandSyntaxException(String.format("Could not parse \"<%s>\". Format accepted: \"<name : TYPE {valuelist}>\", where \": TYPE\" and \"{valuelist}\" are optional.", parameter));
		}
		String parameterName = matcher.group(1).trim();
		ParameterType type = ParameterType.STRING;
		if (matcher.group(3) != null) {
			String typeAsString = matcher.group(3).trim();
			try {
				type = ParameterType.valueOf(typeAsString);
			} catch (Exception e) {
				String typesAsString = getParameterTypesAsString();
				throw new CommandSyntaxException(String.format("\"<%s>\" is not one of the know types (%s).",
						type, typesAsString));
			}
		}
		ParameterSyntax parameterSyntax;
		if ((leftSide == null) || leftSide.isEmpty()) parameterSyntax= new ParameterSyntax(parameterName, type);
		else parameterSyntax= new ParameterSyntax(parameterName, type, leftSide);
		if (matcher.group(5) == null) return parameterSyntax;
		String valueList = matcher.group(5).trim();
		if ((valueList != null) && !valueList.isEmpty()) {
			ValueListSyntax valueListParser = new ValueListSyntax(parameterName, valueList);
			parameterSyntax.setValues(valueListParser.getValues());
			parameterSyntax.setDefault(valueListParser.getDefault());
			parameterSyntax.setAcceptsAnyValue(valueListParser.acceptsAnyValue());
		}
		return parameterSyntax;
	}

	private static String getParameterTypesAsString() {
		List<String> typesAsList = new ArrayList<String>();
		for (ParameterType t : ParameterType.values()) {
			typesAsList.add(t.toString());
		}
		return String.join(", ", typesAsList);
	}

	public void setDefaultValue(DefaultGetter defaultGetter) {
		this._defaultGetter = defaultGetter;
	}
}
