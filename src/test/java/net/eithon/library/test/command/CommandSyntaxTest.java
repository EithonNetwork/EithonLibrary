package net.eithon.library.test.command;

import java.util.List;

import net.eithon.library.command.syntax.CommandSyntax;
import net.eithon.library.command.syntax.CommandSyntaxException;
import net.eithon.library.command.syntax.ParameterSyntax;
import net.eithon.library.command.syntax.ParameterSyntax.ParameterType;

import org.junit.Assert;
import org.junit.Test;

public class CommandSyntaxTest {
	@Test
	public void rootOnly() 
	{
		// Do and verify
		createRoot("root");
	}
	
	@Test
	public void subCommand() 
	{
		// Prepare
		final String subName = "sub";
		final String subCommand = String.format("%s", subName);
		CommandSyntax root = createRoot("root");
		
		// Do and verify
		createSubCommand(root, subName, subCommand);		
	}
	
	@Test
	public void parameter() 
	{

		// Prepare
		final String subName = "sub";
		final String parameterName = "parameter";
		final String subCommand = String.format("%s <%s>", subName, parameterName);
		final CommandSyntax root = createRoot("root");
		
		// Do
		createSubCommand(root, subName, subCommand);		
		
		// Verify
		final CommandSyntax sub = root.getSubCommand(subName);
		final ParameterSyntax parameterSyntax = sub.getParameterSyntax(parameterName);
		Assert.assertNotNull(parameterSyntax);
		Assert.assertEquals(parameterName, parameterSyntax.getName());
		Assert.assertEquals(ParameterType.STRING, parameterSyntax.getType());
		Assert.assertNull(parameterSyntax.getDefault());
		final List<String> validValues = parameterSyntax.getValidValues();
		Assert.assertNotNull(validValues);
		Assert.assertEquals(0, validValues.size());
		Assert.assertFalse(parameterSyntax.getIsOptional());
		Assert.assertTrue(parameterSyntax.getAcceptsAnyValue());
	}
	
	@Test
	public void parameterInteger() 
	{

		// Prepare
		final String subName = "sub";
		final String parameterName = "parameter";
		final String subCommand = String.format("%s <%s : INTEGER>", subName, parameterName);
		final CommandSyntax root = createRoot("root");
		
		// Do
		createSubCommand(root, subName, subCommand);		
		
		// Verify
		final CommandSyntax sub = root.getSubCommand(subName);
		final ParameterSyntax parameterSyntax = sub.getParameterSyntax(parameterName);
		Assert.assertNotNull(parameterSyntax);
		Assert.assertEquals(parameterName, parameterSyntax.getName());
		Assert.assertEquals(ParameterType.INTEGER, parameterSyntax.getType());
		Assert.assertNull(parameterSyntax.getDefault());
		final List<String> validValues = parameterSyntax.getValidValues();
		Assert.assertNotNull(validValues);
		Assert.assertEquals(0, validValues.size());
		Assert.assertFalse(parameterSyntax.getIsOptional());
		Assert.assertTrue(parameterSyntax.getAcceptsAnyValue());
	}
	
	@Test
	public void parameterDefault() 
	{

		// Prepare
		final String subName = "sub";
		final String parameterName = "parameter";
		final String defaultValue = "42";
		final String subCommand = String.format("%s <%s {_%s_, ...}>", subName, parameterName, defaultValue);
		final CommandSyntax root = createRoot("root");
		
		// Do
		createSubCommand(root, subName, subCommand);		
		
		// Verify
		final CommandSyntax sub = root.getSubCommand(subName);
		final ParameterSyntax parameterSyntax = sub.getParameterSyntax(parameterName);
		Assert.assertNotNull(parameterSyntax);
		Assert.assertEquals(defaultValue, parameterSyntax.getDefault());
		final List<String> validValues = parameterSyntax.getValidValues();
		Assert.assertNotNull(validValues);
		Assert.assertEquals(1, validValues.size());
		Assert.assertEquals(defaultValue, validValues.get(0));
		Assert.assertTrue(parameterSyntax.getIsOptional());
		Assert.assertTrue(parameterSyntax.getAcceptsAnyValue());
	}
	
	@Test
	public void parameterOneHelp() 
	{

		// Prepare
		final String subName = "sub";
		final String parameterName = "parameter";
		final String helpValue1 = "113";
		final String subCommand = String.format("%s <%s {%s, ...}>", subName, parameterName, helpValue1);
		final CommandSyntax root = createRoot("root");
		
		// Do
		createSubCommand(root, subName, subCommand);		
		
		// Verify
		final CommandSyntax sub = root.getSubCommand(subName);
		final ParameterSyntax parameterSyntax = sub.getParameterSyntax(parameterName);
		Assert.assertNotNull(parameterSyntax);
		Assert.assertNull(parameterSyntax.getDefault());
		final List<String> validValues = parameterSyntax.getValidValues();
		Assert.assertNotNull(validValues);
		Assert.assertEquals(1, validValues.size());
		Assert.assertEquals(helpValue1, validValues.get(0));
		Assert.assertFalse(parameterSyntax.getIsOptional());
		Assert.assertTrue(parameterSyntax.getAcceptsAnyValue());
	}
	
	@Test
	public void parameterTwoHelp() 
	{

		// Prepare
		final String subName = "sub";
		final String parameterName = "parameter";
		final String helpValue1 = "113";
		final String helpValue2 = "1447";
		final String subCommand = String.format("%s <%s {%s, %s, ...}>", subName, parameterName, helpValue1, helpValue2);
		final CommandSyntax root = createRoot("root");
		
		// Do
		createSubCommand(root, subName, subCommand);		
		
		// Verify
		final CommandSyntax sub = root.getSubCommand(subName);
		final ParameterSyntax parameterSyntax = sub.getParameterSyntax(parameterName);
		Assert.assertNotNull(parameterSyntax);
		Assert.assertNull(parameterSyntax.getDefault());
		final List<String> validValues = parameterSyntax.getValidValues();
		Assert.assertNotNull(validValues);
		Assert.assertEquals(2, validValues.size());
		Assert.assertEquals(helpValue1, validValues.get(0));
		Assert.assertEquals(helpValue2, validValues.get(1));
		Assert.assertFalse(parameterSyntax.getIsOptional());
		Assert.assertTrue(parameterSyntax.getAcceptsAnyValue());
	}
	
	@Test
	public void parameterTwoMandatory() 
	{

		// Prepare
		final String subName = "sub";
		final String parameterName = "parameter";
		final String helpValue1 = "113";
		final String helpValue2 = "1447";
		final String subCommand = String.format("%s <%s {%s, %s}>", subName, parameterName, helpValue1, helpValue2);
		final CommandSyntax root = createRoot("root");
		
		// Do
		createSubCommand(root, subName, subCommand);		
		
		// Verify
		final CommandSyntax sub = root.getSubCommand(subName);
		final ParameterSyntax parameterSyntax = sub.getParameterSyntax(parameterName);
		Assert.assertNotNull(parameterSyntax);
		Assert.assertNull(parameterSyntax.getDefault());
		final List<String> validValues = parameterSyntax.getValidValues();
		Assert.assertNotNull(validValues);
		Assert.assertEquals(2, validValues.size());
		Assert.assertEquals(helpValue1, validValues.get(0));
		Assert.assertEquals(helpValue2, validValues.get(1));
		Assert.assertFalse(parameterSyntax.getIsOptional());
		Assert.assertFalse(parameterSyntax.getAcceptsAnyValue());
	}

	private CommandSyntax createRoot(String name) {
		final CommandSyntax root = new CommandSyntax(name); 
		Assert.assertNotNull(root);	
		Assert.assertEquals(name,root.getName());
		Assert.assertEquals(name,root.toString());
		return root;
	}

	private CommandSyntax createSubCommand(CommandSyntax root, String name, String command) {
		// Prepare
		final String completeCommand = String.format("%s %s", root.toString(), command);
		
		// Do
		try {
			root.parseSyntax(command);
		} catch (CommandSyntaxException e) {
			Assert.fail();
		}	
		
		// Verify
		final CommandSyntax sub = root.getSubCommand(name);
		Assert.assertNotNull(sub);	
		Assert.assertEquals(name,sub.getName());		
		Assert.assertEquals(command,sub.toString());
		Assert.assertEquals(completeCommand,root.toString());
		
		return root;
	}
}
