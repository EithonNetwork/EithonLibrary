package net.eithon.library.command;

import net.eithon.library.command.CommandSyntax.CommandExecutor;
import net.eithon.library.command.IParameterSyntax.ParameterType;

public interface ICommandSyntax {
	public abstract String getName();
	
	public abstract ICommandSyntax getSubCommand(String command);

	public abstract IParameterSyntax getParameterSyntax(String parameterName);

	public abstract void setPermissionsAutomatically();

	public abstract String getRequiredPermission();

	public abstract ICommandSyntax addCommand(String name);
	
	public abstract IParameterSyntax parseParameterSyntax(String leftSide, String parameter) throws CommandSyntaxException;

	public abstract IParameterSyntax addParameter(String name);

	public abstract IParameterSyntax addParameter(String name, ParameterType type);

	public abstract IParameterSyntax addNamedParameter(String name,
			String leftSide);

	public abstract IParameterSyntax addNamedParameter(String name,
			ParameterType type, String leftSide);

	public abstract IParameterSyntax addParameter(ParameterSyntax parameterSyntax);

	public abstract ICommandSyntax setCommandExecutor(
			CommandExecutor commandExecutor);

	public abstract void setPermission(String permission);

	public abstract ICommandSyntax parseSyntax(String commandLine)
			throws CommandSyntaxException;

}