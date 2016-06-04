package net.eithon.library.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import net.eithon.library.time.TimeMisc;

class ParameterSyntax extends Syntax implements IParameterSyntaxAdvanced {
	private static String parameterName = "([^:{]+)";
	private static String type = "([^{]+)";
	private static String valueList = "([^}]+)";
	private static Pattern insideParameterPattern= Pattern.compile(parameterName + "( *: *" + type + ")?" + "( *\\{" + valueList + "\\})?");

	private ParameterType _type;
	private boolean _isNamed;
	private String _leftHandName;
	Map<String, String> _validValuesMap;
	List<String> _validValuesOrdered;
	private ValueGetter _valueGetter;
	private boolean _isOptional;
	private String _defaultValue;
	private boolean _acceptsAnyValue;
	private DefaultGetter _defaultGetter;
	private String _hint;
	private Object lock = new Object();

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
			parameterSyntax.setValues(valueListParser.getValues());
			parameterSyntax.setDefault(valueListParser.getDefault());
			parameterSyntax.setAcceptsAnyValue(valueListParser.acceptsAnyValue());
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
		this._validValuesMap = new TreeMap<String, String>(String.CASE_INSENSITIVE_ORDER);
		this._validValuesOrdered = new ArrayList<String>();
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

	public void setValues(String... args) {
		setValues(Arrays.asList(args));
	}

	public void setValues(final List<String> values) {
		setValues(null, values);
	}

	private void setValues(final String defaultValue, final List<String> values) {
		synchronized (this.lock ) {
			this._validValuesMap = new TreeMap<String, String>(String.CASE_INSENSITIVE_ORDER);
			this._validValuesOrdered = new ArrayList<String>();
			if (values == null) return;
			for (String string : values) {
				this._validValuesMap.put(string, string);
			}
			this._validValuesOrdered.addAll(values);
			if ((defaultValue != null) && !this._validValuesMap.containsKey(defaultValue)) {
				this._validValuesMap.put(this._defaultValue, this._defaultValue);
				this._validValuesOrdered.add(0, this._defaultValue);
			}
		}
	}

	private void orderValues(final String defaultValue) {
		synchronized (this.lock) {
			this._validValuesOrdered = this._validValuesOrdered
					.stream()
					.sorted(new Comparator<String>() {
						@Override
						public int compare(String o1, String o2) {
							if (defaultValue != null) {
								if (o1.equalsIgnoreCase(defaultValue)) return -1;
								if (o2.equalsIgnoreCase(defaultValue)) return 1;
							}
							return o1.compareTo(o2);
						}
					})
					.collect(Collectors.toList());
		}
	}

	public void parseArguments(EithonCommand command, String argument, HashMap<String, EithonArgument> collectedArguments) 
			throws ArgumentParseException {
		EithonArgument parameterValue = new EithonArgument(command, this, argument);
		if (argument == null) {
			if (this._isOptional) {
				if (collectedArguments != null) collectedArguments.put(getName(), parameterValue);
				return;
			}	
			throw new ArgumentParseException(String.format("Expected a value for argument <%s>", getName()));
		}
		verifyValueIsOkAccordingToType(argument);
		if (this._valueGetter != null) {
			setValues(this._valueGetter.getValues(command));
		}
		synchronized (this.lock) {
			if (this._validValuesMap != null) {
				String foundValue = this._validValuesMap.get(argument);
				if (foundValue != null) {
					parameterValue = new EithonArgument(command, this, foundValue);
					if (collectedArguments != null) collectedArguments.put(getName(), parameterValue);
					return;
				}
			}
		}
		if (this._acceptsAnyValue) {
			if (collectedArguments != null) collectedArguments.put(getName(), parameterValue);
			return;
		}
		throw new ArgumentParseException(String.format("The value \"%s\" was not an accepted value for argument <%s>.",
				argument, getName()));
	}

	public List<String> getValidValues() {
		return getValidValues(null);
	}

	public List<String> getValidValues(EithonCommand command) {
		synchronized (this.lock) {
			if ((command != null) && (this._valueGetter != null)) {
				final String defaultValue = getDefault(command);
				setValues(defaultValue, this._valueGetter.getValues(command));
				orderValues(defaultValue);
			}
			return this._validValuesOrdered;
		}
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
		synchronized (this.lock) {
			if (this._validValuesMap.size()>0) sb.append(String.format(" {%s}", validValuesAsString(true)));
		}
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

	private String validValuesAsString(boolean markDefault) {
		final String defaultValue = getDefault();
		List<String> values = null;
		synchronized (this.lock) {
			values = this._validValuesOrdered;
			if (defaultValue != null) {
				values = this._validValuesOrdered
						.stream()
						.map(s -> s.equalsIgnoreCase(defaultValue) ? String.format("_%s_", s) : s)
						.collect(Collectors.toList());
			}
		}
		String validValues = String.join(", ", values);
		if (this._acceptsAnyValue) validValues = validValues + ", ...";
		return validValues;
	}

	public void setAcceptsAnyValue(boolean acceptsAnyValue) { this._acceptsAnyValue = acceptsAnyValue; }

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