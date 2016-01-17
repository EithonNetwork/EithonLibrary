package net.eithon.library.command;

import java.util.List;

import net.eithon.library.command.ParameterSyntax.DefaultGetter;
import net.eithon.library.command.ParameterSyntax.ParameterType;
import net.eithon.library.command.ParameterSyntax.ValueGetter;

public interface IAdvancedParameterSyntax extends IParameterSyntax {

	public abstract void setDefault(String defaultValue);

	public abstract void setValues(String... args);

	public abstract void setValues(List<String> values);

	public abstract List<String> getValidValues(EithonCommand command);

	public abstract void setAcceptsAnyValue(boolean acceptsAnyValue);

}