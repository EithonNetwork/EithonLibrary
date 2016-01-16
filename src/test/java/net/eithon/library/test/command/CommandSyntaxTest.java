package net.eithon.library.test.command;

import net.eithon.library.command.syntax.CommandSyntax;
import net.eithon.library.command.syntax.CommandSyntaxException;
import net.eithon.library.command.syntax.ParameterSyntax;

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
