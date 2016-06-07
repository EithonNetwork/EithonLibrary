package net.eithon.library.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.eithon.library.plugin.Logger;
import net.eithon.library.time.TimeMisc;

class ParameterSyntax extends Syntax implements IParameterSyntaxAdvanced {
	private static String parameterName = "([^:{]+)";
	private static String type = "([^{]+)";
	private static String valueList = "([^}]+)";
	private static Pattern insideParameterPattern= Pattern.compile(parameterName + "( *: *" + type + ")?" + "( *\\{" + valueList + "\\})?");

	private ParameterType _type;
	private boolean _isNamed;
	private String _leftHandName;
	private ValueGetter _valueGetter;
	private boolean _isOptional;
	private String _defaultValue;
	private boolean _acceptsAnyValue;
	private DefaultGetter _defaultGetter;
	private String _hint;
	private ValidValues _predefinedValidValues;

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
						typeAsString, typesAsString));
			}
		}
		ParameterSyntax parameterSyntax;
		if ((leftSide == null) || leftSide.isEmpty()) parameterSyntax= new ParameterSyntax(parameterName, type);
		else parameterSyntax= new ParameterSyntax(parameterName, type, leftSide);
		if (matcher.group(5) == null) return parameterSyntax;
		String valueList = matcher.group(5).trim();
		if ((valueList != null) && !valueList.isEmpty()) {
			ValueListSyntax valueListParser = new ValueListSyntax(parameterName, valueList);
			parameterSyntax.setDefault(valueListParser.getDefault());
			parameterSyntax.setAcceptsAnyValue(valueListParser.acceptsAnyValue());
			parameterSyntax.setValues(valueListParser.getValues());
		}
		return parameterSyntax;
	}

	public ParameterSyntax(String name, ParameterType type) {
		this(name, type, null);
	}

	ParameterSyntax(String parameterName, ParameterType type, String leftHandName) {
		super(parameterName);
		this._hint = getName();
		this._leftHandName = leftHandName;
		this._type = type;
		this._isNamed = leftHandName != null;
		this._isOptional = this._isNamed || (type == ParameterType.REST);
		this._valueGetter = null;
		this._acceptsAnyValue = true;
		this._predefinedValidValues = new ValidValues(getDefault(), getAcceptsAnyValue(), null);
	}

	public boolean getIsOptional() { return this._isOptional; }
	public boolean getAcceptsAnyValue() { return this._acceptsAnyValue; }
	public String getDefault() { return getDefault(null); }
	public ParameterType getType() { return this._type; }
	public String getHint() { return String.format("(%s)", this._hint); } 
	public ParameterSyntax setHint(String hint) { this._hint = hint; return this;}
	@Override
	public ParameterSyntax setDisplayHint(boolean displayHint) { return (ParameterSyntax) super.setDisplayHint(displayHint); }

	public IParameterSyntax setDefaultGetter(DefaultGetter defaultGetter) {
		this._isOptional = defaultGetter != null;
		this._defaultGetter = defaultGetter;
		return this;
	} 

	public String getDefault(EithonCommand command) {
		if (this._defaultGetter == null) return this._defaultValue; 
		return this._defaultGetter.getDefault(command);
	} 


	public ParameterSyntax setDefault(String defaultValue) {
		this._isOptional = defaultValue != null;
		this._defaultValue = defaultValue;
		this._predefinedValidValues.setDefault(defaultValue);
		return this;
	}

	public ParameterSyntax setDefault(long defaultValue) { return setDefault(Long.toString(defaultValue)); }
	public ParameterSyntax setDefault(double defaultValue) { return setDefault(Double.toString(defaultValue)); }

	public IParameterSyntax setMandatoryValues(ValueGetter valueGetter) {
		this._valueGetter = valueGetter;
		this._acceptsAnyValue = false;
		return this;	}

	public IParameterSyntax setExampleValues(ValueGetter valueGetter) {
		this._valueGetter = valueGetter;
		this._acceptsAnyValue = true;
		return this;
	}

	@Override
	public void setValues(List<String> values) {
		this._predefinedValidValues = new ValidValues(getDefault(), getAcceptsAnyValue(), values);
	}

	public void setValues(String... args) {
		setValues(Arrays.asList(args));
	}

	public void parseArguments(EithonCommand command, String argument, HashMap<String, EithonArgument> collectedArguments) 
			throws ArgumentParseException {
		if (argument == null) {
			if (this._isOptional) {
				EithonArgument parameterValue = new EithonArgument(command, this, argument);
				if (collectedArguments != null) collectedArguments.put(getName(), parameterValue);
				return;
			}	
			throw new ArgumentParseException(String.format("Expected a value for argument <%s>", getName()));
		}
		verifyValueIsOkAccordingToType(argument);
		String foundValue = getMatch(command, argument);
		EithonArgument parameterValue = new EithonArgument(command, this, foundValue);
		if (collectedArguments != null) collectedArguments.put(getName(), parameterValue);
	}

	public String getMatch(EithonCommand command, String argument) throws ArgumentParseException {
		ValidValues validValues = internalGetValidValues(command);
		final String validValue = validValues.getValidValue(argument);
		if (validValue != null) return validValue;
		if (getAcceptsAnyValue()) return argument;
		List<String> foundList = validValues.findPartialMatches(argument);
		final int size = foundList.size();
		if (size == 1) return foundList.get(0);
		String message = "";
		if (size == 0) {
			message = String.format("The value \"%s\" did not match any of the expected values.",
					argument);
		} else if (size > 4) {
			message = String.format("The value \"%s\" was ambigous (%d matches).",
					argument, size);
		} else {
			message = String.format("The value \"%s\" was ambigous, pick one of %s.", 
					argument, String.join(", ", foundList));
		}
		Logger.libraryWarning("ParameterSyntax.getMatch: %s", message);
		throw new ArgumentParseException(message);
	}

	public List<String> getValidValues() {
		return internalGetValidValues(null).getOrdered();
	}

	public  List<String> getValidValues(EithonCommand command) {
		return internalGetValidValues(command).getOrdered();
	}

	private ValidValues internalGetValidValues(EithonCommand command) {
		if ((command == null) || (this._valueGetter == null)) return this._predefinedValidValues;
		final ValidValues validValues = new ValidValues(
				getDefault(command), 
				getAcceptsAnyValue(),
				this._valueGetter.getValues(command));
		return validValues;
	}

	private boolean verifyValueIsOkAccordingToType(String argument) throws ArgumentParseException {
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
			throw new ArgumentParseException(String.format("\"%s\" is not of type %s", argument, this._type.toString()));
		}
		return true;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("");
		if (this._isNamed) sb.append(this._leftHandName + ":");
		sb.append(String.format("<%s", this.getName())); 
		if (this._type != ParameterType.STRING) sb.append(String.format(" : %s", this._type.toString()));
		String validValues = this._predefinedValidValues.toString();
		if (!validValues.isEmpty()) sb.append(String.format(" {%s}", validValues));
		sb.append(">");
		return sb.toString();
	}

	public String getSyntaxString() {
		String syntaxString = getSyntaxString2();
		if (this._isNamed || !this._isOptional) return syntaxString;
		return String.format("[%s]", syntaxString);
	}

	private String getSyntaxString2() {
		StringBuilder sb = new StringBuilder("");
		if (this._isNamed) sb.append(this._leftHandName + ":"); 
		sb.append(String.format("<%s>", this.getName()));
		return sb.toString();
	}

	public void setAcceptsAnyValue(boolean acceptsAnyValue) { 
		this._predefinedValidValues.setAcceptsAnyValue(acceptsAnyValue);
		this._acceptsAnyValue = acceptsAnyValue; 
	}

	private static String getParameterTypesAsString() {
		List<String> typesAsList = new ArrayList<String>();
		for (ParameterType t : ParameterType.values()) {
			typesAsList.add(t.toString());
		}
		return String.join(", ", typesAsList);
	}

	public IParameterSyntaxAdvanced getAdvancedMethods() { return this; }
}

class ValueListSyntax {
	private static Pattern defaultValuePattern = Pattern.compile("_(.*)_");
	private String _defaultValue;
	private List<String> _valueList;
	private boolean _acceptsAnyValue;

	public ValueListSyntax(String parameterName, String valueList) {
		this._valueList = new ArrayList<String>();
		parse(parameterName, valueList);
	}

	public void parse(String parameterName, String valueList) {
		String[] values = valueList.split(",");
		for (String value : values) {
			if (this._acceptsAnyValue) {
				throw new IllegalArgumentException(String.format("Parameter <%s>: The \"...\" must be last in the value list (%s)",
						parameterName, valueList));				
			}
			value = value.trim();
			if (value.isEmpty()) {
				throw new IllegalArgumentException(String.format("Parameter <%s>: The value list \"%s\" contained an empty element", 
						parameterName, valueList));
			}
			if (value.equals("...")) {
				this._acceptsAnyValue = true;
				continue;
			}
			Matcher matcher = defaultValuePattern.matcher(value);
			if (matcher.matches()) {
				value = matcher.group(1).trim();
				this._defaultValue = value;
			}
			this._valueList.add(value);
		}
		if (!this._acceptsAnyValue && (this._valueList.size() == 1)) {
			throw new IllegalArgumentException(String.format("Parameter <%s>: A value list with only one element does not make any sense. Did you mean {%s, ...}?", 
					parameterName, valueList));
		}
	}

	public List<String> getValues() { return this._valueList; }
	public String getDefault() {return this._defaultValue; }
	public boolean acceptsAnyValue() {return this._acceptsAnyValue; }
}