package net.eithon.library.test.command;

import net.eithon.library.command.EithonCommand;
import net.eithon.library.command.syntax.CommandSyntax;
import net.eithon.library.command.syntax.CommandSyntaxException;
import net.eithon.library.command.syntax.ParameterSyntax;
import net.eithon.library.command.syntax.ParameterSyntax.ParameterType;

import org.junit.Assert;
import org.junit.Test;

public class EithonCommandSyntaxTest {
	@Test
	public void rootOnly() 
	{
		// Prepare
		CommandSyntax root = Support.createRoot("root");
		
		// Do
		root.setCommandExecutor(ec -> Assert.assertNotNull(ec));
		EithonCommand command = new EithonCommand(root, null, null, "alias", new String[] {"root"});
		
		// Verify
		Assert.assertTrue(command.execute());
	}

	@Test
	public void subCommand() 
	{
		// Prepare
		final String commandName = "sub";
		final String commandSyntax = "sub";
		final String command = "root sub";
		CommandSyntax root = new CommandSyntax("root");
		CommandSyntax sub = null;
		try {
			root.parseSyntax(commandSyntax);
		} catch (CommandSyntaxException e) {
			Assert.fail();
		}
		sub = root.getSubCommand(commandName);
		
		// Do
		sub.setCommandExecutor(ec -> Assert.assertNotNull(ec));
		EithonCommand ec = new EithonCommand(root, null, null, "alias", command.split(" "));
		
		// Verify
		Assert.assertTrue(ec.execute());
	}

	@Test
	public void parameter() 
	{
		// Prepare
		final String commandName = "sub";
		final String commandSyntax = "sub <parameter>";
		final String command = "root sub a";
		CommandSyntax root = new CommandSyntax("root");
		CommandSyntax sub = null;
		try {
			root.parseSyntax(commandSyntax);
		} catch (CommandSyntaxException e) {
			Assert.fail();
		}
		sub = root.getSubCommand(commandName);
		
		// Do
		sub.setCommandExecutor(ec -> {
			Assert.assertNotNull(ec);
			Assert.assertEquals("a", ec.getArgument("parameter").asString());
		});
		EithonCommand ec = new EithonCommand(root, null, null, "alias", command.split(" "));
		
		// Verify
		Assert.assertTrue(ec.execute());
	}

	@Test
	public void parameterInteger() 
	{
		// Prepare
		final String commandName = "sub";
		final String commandSyntax = "sub <parameter : INTEGER>";
		final String command = "root sub 37";
		CommandSyntax root = new CommandSyntax("root");
		CommandSyntax sub = null;
		try {
			root.parseSyntax(commandSyntax);
		} catch (CommandSyntaxException e) {
			Assert.fail();
		}
		sub = root.getSubCommand(commandName);
		
		// Do
		sub.setCommandExecutor(ec -> {
			Assert.assertNotNull(ec);
			Assert.assertEquals(37, ec.getArgument("parameter").asInteger());
		});
		EithonCommand ec = new EithonCommand(root, null, null, "alias", command.split(" "));
		
		// Verify
		Assert.assertTrue(ec.execute());
	}

	@Test
	public void parameterDefault() 
	{
		// Prepare
		final String commandName = "sub";
		final String commandSyntax = "sub <parameter : INTEGER {_42_,...}>";
		final String command = "root sub";
		CommandSyntax root = new CommandSyntax("root");
		CommandSyntax sub = null;
		try {
			root.parseSyntax(commandSyntax);
		} catch (CommandSyntaxException e) {
			Assert.fail();
		}
		sub = root.getSubCommand(commandName);
		
		// Do
		sub.setCommandExecutor(ec -> {
			Assert.assertNotNull(ec);
			Assert.assertEquals(42, ec.getArgument("parameter").asInteger());
		});
		EithonCommand ec = new EithonCommand(root, null, null, "alias", command.split(" "));
		
		// Verify
		Assert.assertTrue(ec.execute());
	}

	@Test
	public void parameterOneHelp() 
	{
		// Prepare
		final String commandName = "sub";
		final String commandSyntax = "sub <parameter {113,...}>";
		final String command = "root sub 113";
		CommandSyntax root = new CommandSyntax("root");
		CommandSyntax sub = null;
		try {
			root.parseSyntax(commandSyntax);
		} catch (CommandSyntaxException e) {
			Assert.fail();
		}
		sub = root.getSubCommand(commandName);
		
		// Do
		sub.setCommandExecutor(ec -> {
			Assert.assertNotNull(ec);
			Assert.assertEquals(113, ec.getArgument("parameter").asInteger());
		});
		EithonCommand ec = new EithonCommand(root, null, null, "alias", command.split(" "));
		
		// Verify
		Assert.assertTrue(ec.execute());
	}

	@Test
	public void parameterTwoHelp() 
	{
		// Prepare
		final String commandName = "sub";
		final String commandSyntax = "sub <parameter {113, 4477,...}>";
		final String command = "root sub 4477";
		CommandSyntax root = new CommandSyntax("root");
		CommandSyntax sub = null;
		try {
			root.parseSyntax(commandSyntax);
		} catch (CommandSyntaxException e) {
			Assert.fail();
		}
		sub = root.getSubCommand(commandName);
		
		// Do
		sub.setCommandExecutor(ec -> {
			Assert.assertNotNull(ec);
			Assert.assertEquals(4477, ec.getArgument("parameter").asInteger());
		});
		EithonCommand ec = new EithonCommand(root, null, null, "alias", command.split(" "));
		
		// Verify
		Assert.assertTrue(ec.execute());
	}

	@Test
	public void parameterTwoMandatoryOk() 
	{
		// Prepare
		final String commandName = "sub";
		final String commandSyntax = "sub <parameter {1,2}>";
		final String command = "root sub 2";
		CommandSyntax root = new CommandSyntax("root");
		CommandSyntax sub = null;
		try {
			root.parseSyntax(commandSyntax);
		} catch (CommandSyntaxException e) {
			Assert.fail();
		}
		sub = root.getSubCommand(commandName);
		
		// Do
		sub.setCommandExecutor(ec -> {
			Assert.assertNotNull(ec);
			Assert.assertEquals(2, ec.getArgument("parameter").asInteger());
		});
		EithonCommand ec = new EithonCommand(root, null, null, "alias", command.split(" "));
		
		// Verify
		Assert.assertTrue(ec.execute());
	}

	@Test
	public void parameterTwoMandatoryFail() 
	{
		// Prepare
		final String commandName = "sub";
		final String commandSyntax = "sub <parameter {1,2}>";
		final String command = "root sub 3";
		CommandSyntax root = new CommandSyntax("root");
		CommandSyntax sub = null;
		try {
			root.parseSyntax(commandSyntax);
		} catch (CommandSyntaxException e) {
			Assert.fail();
		}
		sub = root.getSubCommand(commandName);
		
		// Do
		sub.setCommandExecutor(ec -> {
			Assert.assertNotNull(ec);
			Assert.assertEquals(3, ec.getArgument("parameter").asInteger());
		});
		
		EithonCommand ec = new EithonCommand(root, null, null, "alias", command.split(" "));
		
		// Verify
		Assert.assertFalse(ec.execute());
	}

	@Test
	public void parameterTwoMandatoryWithDefault() 
	{
		// Prepare
		final String commandName = "sub";
		final String commandSyntax = "sub <parameter {1,_2_}>";
		final String command = "root sub";
		CommandSyntax root = new CommandSyntax("root");
		CommandSyntax sub = null;
		try {
			root.parseSyntax(commandSyntax);
		} catch (CommandSyntaxException e) {
			Assert.fail();
		}
		sub = root.getSubCommand(commandName);
		
		// Do
		sub.setCommandExecutor(ec -> {
			Assert.assertNotNull(ec);
			Assert.assertEquals(2, ec.getArgument("parameter").asInteger());
		});
		
		EithonCommand ec = new EithonCommand(root, null, null, "alias", command.split(" "));
		
		// Verify
		Assert.assertTrue(ec.execute());
	}
}
