package net.eithon.library.command;

import net.eithon.library.command.CommandSyntax.CommandExecutor;

public interface ICommandSyntax {
	
	public ICommandSyntaxAdvanced addKeyWord(String name);

	public ICommandSyntax parseCommandSyntax(String commandLine)
			throws CommandSyntaxException;
	
	public String getName();
	
	public ICommandSyntax getSubCommand(String keyWord);

	public IParameterSyntax getParameterSyntax(String parameterName);

	public void setPermissionsAutomatically();

	public ICommandSyntax setCommandExecutor(CommandExecutor commandExecutor);

	public void setPermission(String permission);

	public String getRequiredPermission();

	public ICommandSyntaxAdvanced getAdvancedMethods();

}