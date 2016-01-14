package net.eithon.library.command.syntax;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.eithon.library.command.syntax.ParameterSyntax.ParameterType;

class ParameterSyntaxParser {
	private static String parameterName = "([^:{]+)";
	private static String type = "([^{]+)";
	private static String valueList = "([^}]+)";
	private static Pattern insideParameterPattern= Pattern.compile(parameterName + "( *: *" + type + ")?" + "( *\\{" + valueList + "\\})?");
	private String _leftSide;
	private String _parameterName;
	private ParameterType _type;
	private ValueListParser _valueListParser;

	public ParameterSyntaxParser(String leftSide, String parameter) {
		this._leftSide = leftSide;
		this._type = ParameterType.STRING;
		this._valueListParser = null;
		Matcher matcher = insideParameterPattern.matcher(parameter);
		if (!matcher.matches()) {
			throw new IllegalArgumentException(String.format("Could not parse \"<%s>\". Format accepted: \"<name : TYPE {valuelist}>\", where \": TYPE\" and \"{valuelist}\" are optional.", parameter));
		}
		this._parameterName = matcher.group(1).trim();
		if (matcher.group(3) != null) {
			String type = matcher.group(3).trim();
			try {
				this._type = ParameterType.valueOf(type);
			} catch (Exception e) {
				String typesAsString = getParameterTypesAsString();
				throw new IllegalArgumentException(String.format("\"<%s>\" is not one of the know types (%s).",
						type, typesAsString));
			}
		}
		if (matcher.group(5) != null) {
			String valueList = matcher.group(5).trim();
			if ((valueList != null) && !valueList.isEmpty()) {
				this._valueListParser = new ValueListParser(this._parameterName, valueList);
			}
		}
	}

	private String getParameterTypesAsString() {
		List<String> typesAsList = new ArrayList<String>();
		for (ParameterType t : ParameterType.values()) {
			typesAsList.add(t.toString());
		}
		return String.join(", ", typesAsList);
	}

	public ParameterSyntax addParameterToCommand(CommandSyntax command) {
		ParameterSyntax parameter = null;
		if ((this._leftSide != null) && !this._leftSide.isEmpty()) {
			parameter = command.addNamedParameter(this._type, this._parameterName, this._leftSide);
		} else {
			parameter = command.addParameter(this._type, this._parameterName);
		}
		if (this._valueListParser == null) return parameter;

		String defaultValue = this._valueListParser.getDefault();
		if (defaultValue != null) parameter.setDefault(defaultValue);
		Boolean acceptsAnyValue = this._valueListParser.acceptsAnyValue();
		if (acceptsAnyValue) parameter.setAcceptsAnyValue(true);
		parameter.setValues(this._valueListParser.getValues());
		return parameter;
	}
}
