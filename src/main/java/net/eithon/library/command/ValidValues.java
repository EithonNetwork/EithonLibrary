package net.eithon.library.command;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

class ValidValues {
	private Map<String, String> _validValuesMap;
	private List<String> _validValuesOrdered;
	private String _defaultValue = null;
	private boolean _acceptsAnyValue;

	public ValidValues(final String defaultValue, final boolean acceptsAnyValue, final List<String> values) {
		this._defaultValue = defaultValue;
		this._acceptsAnyValue = acceptsAnyValue;
		this._validValuesMap = new TreeMap<String, String>(String.CASE_INSENSITIVE_ORDER);
		this._validValuesOrdered = new ArrayList<String>();
		if (values == null) return;
		for (String string : values) {
			this._validValuesMap.put(string, string);
		}
		this._validValuesOrdered.addAll(values);
		orderValues();
	}

	public List<String> getOrdered() { return this._validValuesOrdered; }
	public void setAcceptsAnyValue(boolean acceptsAnyValue) { this._acceptsAnyValue = acceptsAnyValue; }
	
	public void setDefault(String defaultValue) {
		if ((this._defaultValue != null) && !this._validValuesMap.containsKey(this._defaultValue)) {
			this._validValuesMap.remove(this._defaultValue);
			this._validValuesOrdered.remove(this._defaultValue);
		}
		if ((defaultValue != null) && !this._validValuesMap.containsKey(defaultValue)) {
			this._validValuesMap.put(defaultValue, defaultValue);
			this._validValuesOrdered.add(0, defaultValue);
		}
		this._defaultValue = defaultValue;
	}

	public String getValidValue(String value) {
		return this._validValuesMap.get(value);
	}

	public List<String> findPartialMatches(String partial) {
		List<String> found = new ArrayList<String>();
		for (String value : this._validValuesOrdered) {
			if (value.startsWith(partial)) found.add(value);
		}
		return found;
	}
	
	/*
	new Comparator<String>() {
		@Override
		public int compare(String o1, String o2) {
			if (defaultValue != null) {
				if (o1.equalsIgnoreCase(defaultValue)) return -1;
				if (o2.equalsIgnoreCase(defaultValue)) return 1;
			}
			return o1.compareTo(o2);
		}
	}
	*/
	private void orderValues() {
		this._validValuesOrdered = this._validValuesOrdered
				.stream()
				.sorted()
				.collect(Collectors.toList());
	}

	@Override
	public String toString() {
		List<String> values = null;
		values = this._validValuesOrdered;
		if (this._defaultValue != null) {
			values = this._validValuesOrdered
					.stream()
					.map(s -> s.equalsIgnoreCase(this._defaultValue) ? String.format("_%s_", s) : s)
					.collect(Collectors.toList());
		}
		String validValues = String.join(", ", values);
		if (validValues.isEmpty()) return validValues;
		if (this._acceptsAnyValue) validValues += ", ...";
		return validValues;
	}
}
