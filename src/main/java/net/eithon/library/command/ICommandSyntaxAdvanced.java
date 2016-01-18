package net.eithon.library.command;

import net.eithon.library.command.IParameterSyntax.ParameterType;

public interface ICommandSyntaxAdvanced extends ICommandSyntax {

	public IParameterSyntax addParameter(String name);

	public IParameterSyntax addParameter(String name, ParameterType type);

	public IParameterSyntax addNamedParameter(String name,
			String leftSide);

	public IParameterSyntax addNamedParameter(String name,
			ParameterType type, String leftSide);

	public IParameterSyntax addParameter(ParameterSyntax parameterSyntax);
	
	public IParameterSyntax parseParameterSyntax(String leftSide, String parameter)
			throws CommandSyntaxException;

}