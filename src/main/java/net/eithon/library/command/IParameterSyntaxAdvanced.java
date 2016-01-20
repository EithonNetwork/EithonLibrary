package net.eithon.library.command;

import java.util.List;

public interface IParameterSyntaxAdvanced extends IParameterSyntax {

	public void setDefault(String defaultValue);

	public void setValues(String... args);

	public void setValues(List<String> values);

	public List<String> getValidValues();

	public List<String> getValidValues(EithonCommand command);

	public void setAcceptsAnyValue(boolean acceptsAnyValue);

}