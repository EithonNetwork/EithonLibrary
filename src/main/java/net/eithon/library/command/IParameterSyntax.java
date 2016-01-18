package net.eithon.library.command;

import java.util.List;

public interface IParameterSyntax {

	public enum ParameterType { STRING, REAL, INTEGER, Player, REST, BOOLEAN, TIME_SPAN };

	public interface ValueGetter {
		List<String> getValues(EithonCommand command);
	}

	public interface DefaultGetter {
		String getDefault(EithonCommand command);
	}

	public void setDefaultValue(DefaultGetter defaultGetter);

	public IParameterSyntax setExampleValues(ValueGetter valueGetter);

	public IParameterSyntax setMandatoryValues(ValueGetter valueGetter);

	public boolean getIsOptional();

	public boolean getAcceptsAnyValue();

	public String getDefault();

	public ParameterType getType();

	public IParameterSyntaxAdvanced getAdvancedMethods();

}