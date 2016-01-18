package net.eithon.library.test.command;

import net.eithon.library.command.CommandSyntaxException;
import net.eithon.library.command.ICommandSyntax;
import net.eithon.library.command.IParameterSyntax;
import net.eithon.library.command.IParameterSyntax.ParameterType;

import org.junit.Assert;
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
		ICommandSyntax root = Support.createRoot("root");

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
		final ICommandSyntax root = Support.createRoot("root");
		final ICommandSyntax sub = Support.createSubCommand(root, subName, subName);

		// Do and verify
		Support.createParameter(sub, null, parameterName, null, null, true);
	}

	@Test
	public void parameterInteger() 
	{

		// Prepare
		final String subName = "sub";
		final String parameterName = "parameter";
		final ICommandSyntax root = Support.createRoot("root");
		final ICommandSyntax sub = Support.createSubCommand(root, subName, subName);

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
		final ICommandSyntax root = Support.createRoot("root");
		final ICommandSyntax sub = Support.createSubCommand(root, subName, subName);

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
		final ICommandSyntax root = Support.createRoot("root");
		final ICommandSyntax sub = Support.createSubCommand(root, subName, subName);

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
		final ICommandSyntax root = Support.createRoot("root");
		final ICommandSyntax sub = Support.createSubCommand(root, subName, subName);

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
		final ICommandSyntax root = Support.createRoot("root");
		final ICommandSyntax sub = Support.createSubCommand(root, subName, subName);

		Support.createParameter(sub, null, parameterName, null, helpValues, false);	
	}

	@Test
	public void parameterTwoMandatoryWithDefault() 
	{
		// Prepare
		final String subName = "sub";
		final String parameterName = "parameter";
		final String[] helpValues = new String[] { "113", "1477" };
		final ICommandSyntax root = Support.createRoot("root");
		final ICommandSyntax sub = Support.createSubCommand(root, subName, subName);

		Support.createParameter(sub, null, parameterName, helpValues[1], helpValues, false);
	}

	@Test
	public void automaticPermissionSimple() 
	{
		// Prepare
		ICommandSyntax root = Support.createRoot("root");
		root.setPermissionsAutomatically();

		// Do
		try {
			root.parseCommandSyntax("a");
		} catch (CommandSyntaxException e) {
			Assert.fail();
		}		
		ICommandSyntax a = root.getSubCommand("a");
		
		// Verify
		Assert.assertEquals("root.a", a.getRequiredPermission());
	}

	@Test
	public void automaticPermissionHierarchy() 
	{
		// Prepare
		ICommandSyntax root = Support.createRoot("root");
		root.setPermissionsAutomatically();

		// Do
		try {
			root.parseCommandSyntax("a");
			root.parseCommandSyntax("b c");
		} catch (CommandSyntaxException e) {
			Assert.fail();
		}		
		ICommandSyntax a = root.getSubCommand("a");
		ICommandSyntax b = root.getSubCommand("b");
		ICommandSyntax c = b.getSubCommand("c");
		
		// Verify
		Assert.assertEquals("root.a", a.getRequiredPermission());
		Assert.assertNull(b.getRequiredPermission());
		Assert.assertEquals("root.b.c", c.getRequiredPermission());
	}

	@Test
	public void eithonfixesBuyCommand() 
	{
		// Prepare
		final ICommandSyntax root = Support.createRoot("eithonfixes");
		try {
			root.parseCommandSyntax("buy <player> <item> <price : REAL> <amount : INTEGER {1, ...}>");
		} catch (CommandSyntaxException e) {
			Assert.fail();
		}
		
		root.getSubCommand("buy");
	}

	@Test
	public void eithonTwoCommands() 
	{
		// Prepare
		final ICommandSyntax root = Support.createRoot("eithonfixes");
		try {
			root.parseCommandSyntax("buy <player> <item> <price : REAL> <amount : INTEGER {1, ...}>");
			root.parseCommandSyntax("debug <plugin> <level : INTEGER {0, 1, 2, _3_}>");
		} catch (CommandSyntaxException e) {
			Assert.fail();
		}
		
		root.getSubCommand("buy");
	}
}
