package net.eithon.library.command.syntax;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
				throw new IllegalArgumentException(String.format("Parameter \"%s\": The \"...\" must be last in the value list (%s)",
						parameterName, valueList));				
			}
			value = value.trim();
			if (value.isEmpty()) {
				throw new IllegalArgumentException(String.format("Parameter \"%s\": The value list \"%s\" contained an empty element", 
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
			throw new IllegalArgumentException(String.format("Parameter \"%s\": A value list with only one element does not make any sense. Did you mean {%s, ...}?", 
					parameterName, valueList));
		}
	}
	
	public List<String> getValues() { return this._valueList; }
	public String getDefault() {return this._defaultValue; }
	public boolean acceptsAnyValue() {return this._acceptsAnyValue; }
}
