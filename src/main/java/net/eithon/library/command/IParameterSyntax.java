package net.eithon.library.command;

import net.eithon.library.command.ParameterSyntax.DefaultGetter;
import net.eithon.library.command.ParameterSyntax.ParameterType;
import net.eithon.library.command.ParameterSyntax.ValueGetter;

public interface IParameterSyntax {

	public abstract boolean getIsOptional();

	public abstract boolean getAcceptsAnyValue();

	public abstract String getDefault();

	public abstract ParameterType getType();

	public abstract IParameterSyntax setMandatoryValues(ValueGetter valueGetter);

	public abstract IParameterSyntax setExampleValues(ValueGetter valueGetter);

	public abstract void setDefaultValue(DefaultGetter defaultGetter);

}