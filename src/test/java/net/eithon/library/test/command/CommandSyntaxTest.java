package net.eithon.library.test.command;

import net.eithon.library.command.syntax.CommandSyntax;
import net.eithon.library.command.syntax.ParameterSyntax;
import net.eithon.library.command.syntax.ParameterSyntax.ParameterType;

import org.junit.Test;

public class CommandSyntaxTest {
	@Test
	public void rootOnly() 
	{
		// Do and verify
		Support.createRoot("root");
	}

	@Test
	public void subCommand() 
	{
		// Prepare
		final String subName = "sub";
		final String subCommand = String.format("%s", subName);
		CommandSyntax root = Support.createRoot("root");

		// Do and verify
		Support.createSubCommand(root, subName, subCommand);		
		root.getSubCommand(subName);
	}

	@Test
	public void parameter() 
	{

		// Prepare
		final String subName = "sub";
		final String parameterName = "parameter";
		final CommandSyntax root = Support.createRoot("root");
		final CommandSyntax sub = Support.createSubCommand(root, subName, subName);

		// Do and verify
		Support.createParameter(sub, null, parameterName, null, null, true);
	}

	@Test
	public void parameterInteger() 
	{

		// Prepare
		final String subName = "sub";
		final String parameterName = "parameter";
		final CommandSyntax root = Support.createRoot("root");
		final CommandSyntax sub = Support.createSubCommand(root, subName, subName);

		// Do and verify
		Support.createParameter(sub, null, parameterName, ParameterType.INTEGER, null, null, true);
	}

	@Test
	public void parameterDefault() 
	{

		// Prepare
		final String subName = "sub";
		final String parameterName = "parameter";
		final String defaultValue = "42";
		final CommandSyntax root = Support.createRoot("root");
		final CommandSyntax sub = Support.createSubCommand(root, subName, subName);

		// Do and verify
		Support.createParameter(sub, null, parameterName, defaultValue, null, true);
	}

	@Test
	public void parameterOneHelp() 
	{

		// Prepare
		final String subName = "sub";
		final String parameterName = "parameter";
		final String[] helpValues = new String[] { "113" };
		final CommandSyntax root = Support.createRoot("root");
		final CommandSyntax sub = Support.createSubCommand(root, subName, subName);

		// Do and verify
		Support.createParameter(sub, null, parameterName, null, helpValues, true);
	}

	@Test
	public void parameterTwoHelp() 
	{

		// Prepare
		final String subName = "sub";
		final String parameterName = "parameter";	
		final String[] helpValues = new String[] { "113", "1477" };
		final CommandSyntax root = Support.createRoot("root");
		final CommandSyntax sub = Support.createSubCommand(root, subName, subName);

		// Do and verify
		Support.createParameter(sub, null, parameterName, null, helpValues, true);
	}

	@Test
	public void parameterTwoMandatory() 
	{

		// Prepare
		final String subName = "sub";
		final String parameterName = "parameter";
		final String[] helpValues = new String[] { "113", "1477" };
		final CommandSyntax root = Support.createRoot("root");
		final CommandSyntax sub = Support.createSubCommand(root, subName, subName);

		// Do and verify
		final ParameterSyntax parameterSyntax = Support.createParameter(sub, null, parameterName, null, helpValues, false);	
	}

	@Test
	public void parameterTwoMandatoryWithDefault() 
	{
		// Prepare
		final String subName = "sub";
		final String parameterName = "parameter";
		final String[] helpValues = new String[] { "113", "1477" };
		final CommandSyntax root = Support.createRoot("root");
		final CommandSyntax sub = Support.createSubCommand(root, subName, subName);

		// Do and verify
		final ParameterSyntax parameterSyntax = Support.createParameter(sub, null, parameterName, helpValues[1], helpValues, false);
	}
}
