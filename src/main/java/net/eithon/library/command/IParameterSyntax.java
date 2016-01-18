package net.eithon.library.command;

import net.eithon.library.command.ParameterSyntax.DefaultGetter;
import net.eithon.library.command.ParameterSyntax.ValueGetter;

public interface IParameterSyntax {

	public enum ParameterType { STRING, REAL, INTEGER, Player, REST, BOOLEAN, TIME_SPAN };

	public abstract boolean getIsOptional();

	public abstract boolean getAcceptsAnyValue();

	public abstract String getDefault();

	public abstract ParameterType getType();

	public abstract IParameterSyntax setMandatoryValues(ValueGetter valueGetter);

	public abstract IParameterSyntax setExampleValues(ValueGetter valueGetter);

	public abstract void setDefaultValue(DefaultGetter defaultGetter);

	public abstract IAdvancedParameterSyntax getAdvancedMethods();

}