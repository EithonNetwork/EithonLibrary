package net.eithon.library.test.command;

import java.util.Arrays;
import java.util.List;

import net.eithon.library.command.CommandSyntaxException;
import net.eithon.library.command.EithonArgument;
import net.eithon.library.command.EithonCommand;
import net.eithon.library.command.EithonCommandUtilities;
import net.eithon.library.command.ICommandSyntax;

import org.junit.Assert;
import org.junit.Test;

public class EithonCommandTest {
	private boolean _hasExecuted;
	private int _executeNumber;

	private boolean getHasExecuted() { return this._hasExecuted;	}
	private int getExecuteNumber() { return this._executeNumber;	}

	private void setHasExecuted(boolean hasExecuted) { this._hasExecuted = hasExecuted; }
	private void setExecuteNumber(int executeNumber) { this._executeNumber = executeNumber; }

	@Test
	public void rootOnly() 
	{
		// Prepare
		ICommandSyntax root = Support.createRoot("root");
		root.setCommandExecutor(ec -> {Assert.assertNotNull(ec);setHasExecuted(true);});

		// Do
		EithonCommand command = new EithonCommand(root, null, null, "alias", new String[] {"root"});

		// Verify
		setHasExecuted(false);
		Assert.assertTrue(command.execute());
		Assert.assertTrue(getHasExecuted());
	}

	@Test
	public void subCommand() 
	{
		// Prepare
		final String commandName = "sub";
		final String commandSyntax = "sub";
		final String command = "sub";
		ICommandSyntax root = EithonCommand.createRootCommand("root");
		ICommandSyntax sub = null;
		try {
			root.parseCommandSyntax(commandSyntax);
		} catch (CommandSyntaxException e) {
			Assert.fail();
		}
		sub = root.getSubCommand(commandName);

		// Do
		sub.setCommandExecutor(ec -> {Assert.assertNotNull(ec); setHasExecuted(true);});
		EithonCommand ec = Support.createEithonCommand(root, command);

		// Verify
		setHasExecuted(false);
		Assert.assertTrue(ec.execute());
		Assert.assertTrue(getHasExecuted());
	}

	@Test
	public void parameter() 
	{
		// Prepare
		final String commandName = "sub";
		final String commandSyntax = "sub <parameter>";
		final String command = "sub a";
		ICommandSyntax root = EithonCommand.createRootCommand("root");
		ICommandSyntax sub = null;
		try {
			root.parseCommandSyntax(commandSyntax);
		} catch (CommandSyntaxException e) {
			Assert.fail();
		}
		sub = root.getSubCommand(commandName);

		// Do
		sub.setCommandExecutor(ec -> {
			Assert.assertNotNull(ec);
			Assert.assertEquals("a", ec.getArgument("parameter").asString());
			setHasExecuted(true);
		});
		EithonCommand ec = Support.createEithonCommand(root, command);

		// Verify
		setHasExecuted(false);
		Assert.assertTrue(ec.execute());
		Assert.assertTrue(getHasExecuted());
	}

	@Test
	public void parameterInteger() 
	{
		// Prepare
		final String commandName = "sub";
		final String commandSyntax = "sub <parameter : INTEGER>";
		final String command = "sub 37";
		ICommandSyntax root = EithonCommand.createRootCommand("root");
		ICommandSyntax sub = null;
		try {
			root.parseCommandSyntax(commandSyntax);
		} catch (CommandSyntaxException e) {
			Assert.fail();
		}
		sub = root.getSubCommand(commandName);

		// Do
		sub.setCommandExecutor(ec -> {
			Assert.assertNotNull(ec);
			Assert.assertEquals(37, ec.getArgument("parameter").asInteger());
			setHasExecuted(true);
		});
		EithonCommand ec = Support.createEithonCommand(root, command);

		// Verify
		setHasExecuted(false);
		Assert.assertTrue(ec.execute());
		Assert.assertTrue(getHasExecuted());
	}

	@Test
	public void parameterDefault() 
	{
		// Prepare
		final String commandName = "sub";
		final String commandSyntax = "sub <parameter : INTEGER {_42_,...}>";
		final String command = "sub";
		ICommandSyntax root = EithonCommand.createRootCommand("root");
		ICommandSyntax sub = null;
		try {
			root.parseCommandSyntax(commandSyntax);
		} catch (CommandSyntaxException e) {
			Assert.fail();
		}
		sub = root.getSubCommand(commandName);

		// Do
		sub.setCommandExecutor(ec -> {
			Assert.assertNotNull(ec);
			Assert.assertEquals(42, ec.getArgument("parameter").asInteger());
			setHasExecuted(true);
		});
		EithonCommand ec = Support.createEithonCommand(root, command);

		// Verify
		setHasExecuted(false);
		Assert.assertTrue(ec.execute());
		Assert.assertTrue(getHasExecuted());
	}

	@Test
	public void parameterOneHelp() 
	{
		// Prepare
		final String commandName = "sub";
		final String commandSyntax = "sub <parameter {113,...}>";
		final String command = "sub 113";
		ICommandSyntax root = EithonCommand.createRootCommand("root");
		ICommandSyntax sub = null;
		try {
			root.parseCommandSyntax(commandSyntax);
		} catch (CommandSyntaxException e) {
			Assert.fail();
		}
		sub = root.getSubCommand(commandName);

		// Do
		sub.setCommandExecutor(ec -> {
			Assert.assertNotNull(ec);
			Assert.assertEquals(113, ec.getArgument("parameter").asInteger());
			setHasExecuted(true);
		});
		EithonCommand ec = Support.createEithonCommand(root, command);

		// Verify
		setHasExecuted(false);
		Assert.assertTrue(ec.execute());
		Assert.assertTrue(getHasExecuted());
	}

	@Test
	public void parameterTwoHelp() 
	{
		// Prepare
		final String commandName = "sub";
		final String commandSyntax = "sub <parameter {113, 4477,...}>";
		final String command = "sub 4477";
		ICommandSyntax root = EithonCommand.createRootCommand("root");
		ICommandSyntax sub = null;
		try {
			root.parseCommandSyntax(commandSyntax);
		} catch (CommandSyntaxException e) {
			Assert.fail();
		}
		sub = root.getSubCommand(commandName);

		// Do
		sub.setCommandExecutor(ec -> {
			Assert.assertNotNull(ec);
			Assert.assertEquals(4477, ec.getArgument("parameter").asInteger());
			setHasExecuted(true);
		});
		EithonCommand ec = Support.createEithonCommand(root, command);

		// Verify
		setHasExecuted(false);
		Assert.assertTrue(ec.execute());
		Assert.assertTrue(getHasExecuted());
	}

	@Test
	public void parameterTwoMandatoryOk() 
	{
		// Prepare
		final String commandName = "sub";
		final String commandSyntax = "sub <parameter {1,2}>";
		final String command = "sub 2";
		ICommandSyntax root = EithonCommand.createRootCommand("root");
		ICommandSyntax sub = null;
		try {
			root.parseCommandSyntax(commandSyntax);
		} catch (CommandSyntaxException e) {
			Assert.fail();
		}
		sub = root.getSubCommand(commandName);

		// Do
		sub.setCommandExecutor(ec -> {
			Assert.assertNotNull(ec);
			Assert.assertEquals(2, ec.getArgument("parameter").asInteger());
			setHasExecuted(true);
		});
		EithonCommand ec = Support.createEithonCommand(root, command);

		// Verify
		setHasExecuted(false);
		Assert.assertTrue(ec.execute());
		Assert.assertTrue(getHasExecuted());
	}

	@Test
	public void parameterTwoMandatoryFail() 
	{
		// Prepare
		final String commandName = "sub";
		final String commandSyntax = "sub <parameter {1,2}>";
		final String command = "sub 3";
		ICommandSyntax root = EithonCommand.createRootCommand("root");
		ICommandSyntax sub = null;
		try {
			root.parseCommandSyntax(commandSyntax);
		} catch (CommandSyntaxException e) {
			Assert.fail();
		}
		sub = root.getSubCommand(commandName);

		// Do
		sub.setCommandExecutor(ec -> setHasExecuted(true));

		EithonCommand ec = Support.createEithonCommand(root, command);

		// Verify
		setHasExecuted(false);
		Assert.assertTrue(ec.execute());
		Assert.assertFalse(this.getHasExecuted());
	}

	@Test
	public void parameterTwoMandatoryWithDefault() 
	{
		// Prepare
		final String commandName = "sub";
		final String commandSyntax = "sub <parameter {1,_2_}>";
		final String command = "sub";
		ICommandSyntax root = EithonCommand.createRootCommand("root");
		ICommandSyntax sub = null;
		try {
			root.parseCommandSyntax(commandSyntax);
		} catch (CommandSyntaxException e) {
			Assert.fail();
		}
		sub = root.getSubCommand(commandName);

		// Do
		sub.setCommandExecutor(ec -> {
			Assert.assertNotNull(ec);
			Assert.assertEquals(2, ec.getArgument("parameter").asInteger());
			setHasExecuted(true);
		});

		EithonCommand ec = Support.createEithonCommand(root, command);

		// Verify
		setHasExecuted(false);
		Assert.assertTrue(ec.execute());
		Assert.assertTrue(getHasExecuted());
	}

	@Test
	public void tabCompleter() 
	{
		// Prepare
		final String commandName = "sub";
		final String commandSyntax = "sub <parameter {113, 4477,...}>";
		final String command = "sub (parameter) ";
		ICommandSyntax root = EithonCommand.createRootCommand("root");
		ICommandSyntax sub = null;
		try {
			root.parseCommandSyntax(commandSyntax);
		} catch (CommandSyntaxException e) {
			Assert.fail();
		}
		sub = root.getSubCommand(commandName);

		// Do
		sub.setCommandExecutor(ec -> {
			Assert.assertNotNull(ec);
			Assert.assertEquals(4477, ec.getArgument("parameter").asInteger());
		});
		EithonCommand ec = Support.createEithonCommand(root, command);
		List<String> list = ec.tabComplete();

		// Verify
		Assert.assertEquals(2, list.size());
		Assert.assertEquals("113", list.get(0));
		Assert.assertEquals("4477", list.get(1));
	}

	@Test
	public void tabCompleteHint() 
	{
		// Prepare
		final String commandName = "sub";
		final String commandSyntax = "sub <parameter {113, 4477,...}>";
		final String command = "sub ";
		ICommandSyntax root = EithonCommand.createRootCommand("root");
		ICommandSyntax sub = null;
		try {
			root.parseCommandSyntax(commandSyntax);
		} catch (CommandSyntaxException e) {
			Assert.fail();
		}
		sub = root.getSubCommand(commandName);

		// Do
		sub.setCommandExecutor(ec -> {
			Assert.assertNotNull(ec);
			Assert.assertEquals(4477, ec.getArgument("parameter").asInteger());
		});
		EithonCommand ec = Support.createEithonCommand(root, command);
		List<String> list = ec.tabComplete();

		// Verify
		Assert.assertEquals(2, list.size());
		Assert.assertEquals("(parameter) 113", list.get(0));
	}

	@Test
	public void tabCompleteHintsForEithonFixesBuyCommandNoSpace() 
	{
		// Prepare
		final String commandName = "buy";
		final String commandSyntax = "buy <player> <item> <price : REAL> <amount : INTEGER {1, ...}>";
		ICommandSyntax root = EithonCommand.createRootCommand("eithonfixes");
		ICommandSyntax sub = null;
		try {
			root.parseCommandSyntax(commandSyntax);
		} catch (CommandSyntaxException e) {
			Assert.fail();
		}
		sub = root.getSubCommand(commandName);

		// Do
		EithonCommand ec = Support.createEithonCommand(root, "buy Eithon");
		List<String> list = ec.tabComplete();

		// Verify
		Assert.assertNotNull(list);
		Assert.assertEquals(0, list.size());
	}

	@Test
	public void tabCompleteHintsForEithonFixesBuyCommandWithSpace() 
	{
		// Prepare
		final String commandName = "buy";
		final String commandSyntax = "buy <player> <item> <price : REAL> <amount : INTEGER {1, ...}>";
		ICommandSyntax root = EithonCommand.createRootCommand("eithonfixes");
		ICommandSyntax sub = null;
		try {
			root.parseCommandSyntax(commandSyntax);
		} catch (CommandSyntaxException e) {
			Assert.fail();
		}
		sub = root.getSubCommand(commandName);

		// Do
		EithonCommand ec = Support.createEithonCommand(root, "buy Eithon ");
		List<String> list = ec.tabComplete();

		// Verify
		Assert.assertNotNull(list);
		Assert.assertEquals(1, list.size());
		Assert.assertEquals("(item) ", list.get(0));
	}

	@Test
	public void tabCompleteHintsForEithonFixesBuyCommand3() 
	{
		// Prepare
		final String commandName = "buy";
		final String commandSyntax = "buy <player> <item> <price : REAL> <amount : INTEGER {1, ...}>";
		ICommandSyntax root = EithonCommand.createRootCommand("eithonfixes");
		ICommandSyntax sub = null;
		try {
			root.parseCommandSyntax(commandSyntax);
		} catch (CommandSyntaxException e) {
			Assert.fail();
		}
		sub = root.getSubCommand(commandName);

		// Do
		EithonCommand ec = Support.createEithonCommand(root, "buy Eithon gold ");
		List<String> list = ec.tabComplete();

		// Verify
		Assert.assertNotNull(list);
		Assert.assertEquals(1, list.size());
		Assert.assertEquals("(price) ", list.get(0));
	}

	@Test
	public void executeWithHints() 
	{
		// Prepare
		final String commandName = "freeze";
		final String commandSyntax = "freeze <player>";
		ICommandSyntax root = EithonCommand.createRootCommand("eithonfixes");
		ICommandSyntax sub = null;
		try {
			root.parseCommandSyntax(commandSyntax);
		} catch (CommandSyntaxException e) {
			Assert.fail();
		}
		sub = root.getSubCommand(commandName);		
		sub.setCommandExecutor(ec -> Assert.assertNotNull(ec));

		// Do
		EithonCommand ec = Support.createEithonCommand(root, "freeze (player) Eithon");
		Assert.assertTrue(ec.execute());
	}

	@Test
	public void subpartOfHint() 
	{
		// Prepare
		final String commandName = "freeze";
		final String commandSyntax = "freeze <player>";
		ICommandSyntax root = EithonCommand.createRootCommand("eithonfixes");
		ICommandSyntax sub = null;
		try {
			root.parseCommandSyntax(commandSyntax);
		} catch (CommandSyntaxException e) {
			Assert.fail();
		}
		sub = root.getSubCommand(commandName);		
		sub.setCommandExecutor(ec -> Assert.assertNotNull(ec));

		// Do
		EithonCommand ec = Support.createEithonCommand(root, "freeze (play");
		List<String> list = ec.tabComplete();

		// Verify
		Assert.assertNotNull(list);
		Assert.assertEquals(1, list.size());
		Assert.assertEquals("(player) ", list.get(0));
	}

	@Test
	public void repeatedTabs() 
	{
		// Prepare
		final String commandName = "freeze";
		final String commandSyntax = "freeze <player>";
		ICommandSyntax root = EithonCommand.createRootCommand("eithonfixes");
		ICommandSyntax sub = null;
		try {
			root.parseCommandSyntax(commandSyntax);
		} catch (CommandSyntaxException e) {
			Assert.fail();
		}
		sub = root.getSubCommand(commandName);		
		sub.setCommandExecutor(ec -> Assert.assertNotNull(ec));

		// Do
		EithonCommand ec = Support.createEithonCommand(root, "freeze ");
		List<String> list = ec.tabComplete();

		// Verify
		Assert.assertNotNull(list);
		Assert.assertEquals(1, list.size());
		Assert.assertEquals("(player) ", list.get(0));

		// Do
		ec = Support.createEithonCommand(root, "freeze (player) ");
		list = ec.tabComplete();

		// Verify
		Assert.assertNotNull(list);
		Assert.assertEquals(0, list.size());
	}

	@Test
	public void manyWordsInRest() 
	{
		// Prepare
		ICommandSyntax root = EithonCommand.createRootCommand("eithoncop");

		// blacklist add
		try {
			root.parseCommandSyntax("blacklist add <profanity> <is-literal : BOOLEAN {_true_, false}> <synonyms:REST>");
		} catch (CommandSyntaxException e) {
			Assert.fail();
		}
		ICommandSyntax add = root.getSubCommand("blacklist").getSubCommand("add");		
		add.setCommandExecutor(ec -> {
			String rest = ec.getArgument("synonyms").asString();
			Assert.assertNotNull(rest);
			Assert.assertEquals("snurgel snargel", rest);
			setHasExecuted(true);
		});

		// Do & Verify
		EithonCommand ec = Support.createEithonCommand(root, "blacklist add snigel true snurgel snargel");
		Assert.assertNotNull(ec);
		setHasExecuted(false);
		Assert.assertTrue(ec.execute());
		Assert.assertTrue(getHasExecuted());
	}

	@Test
	public void tabAfterEnd() 
	{
		// Prepare
		ICommandSyntax root = EithonCommand.createRootCommand("root");

		// blacklist add
		try {
			root.parseCommandSyntax("buy <amount : INTEGER {0,1,2,3,4}>");
		} catch (CommandSyntaxException e) {
			Assert.fail();
		}
		root.getSubCommand("buy")
		.setCommandExecutor(ec -> {
			int amount = ec.getArgument("amount").asInteger();
			Assert.assertEquals(3, amount);
		});

		// Do & Verify
		EithonCommand ec = Support.createEithonCommand(root, "buy (amount) 3 ");
		Assert.assertNotNull(ec);
		List<String> list = ec.tabComplete();
		Assert.assertNotNull(list);
		Assert.assertTrue(list.isEmpty());
	}

	@Test
	public void tabAfterNonCommand() 
	{
		// Prepare
		ICommandSyntax root = EithonCommand.createRootCommand("root");

		// blacklist add
		try {
			root.parseCommandSyntax("buy <amount : INTEGER {0,1,2,3,4}>");
		} catch (CommandSyntaxException e) {
			Assert.fail();
		}
		root.getSubCommand("buy")
		.setCommandExecutor(ec -> {
			int amount = ec.getArgument("amount").asInteger();
			Assert.assertEquals(3, amount);
		});

		// Do & Verify
		EithonCommand ec = Support.createEithonCommand(root, "wrong ");
		Assert.assertNotNull(ec);
		List<String> list = ec.tabComplete();
		Assert.assertNotNull(list);
		Assert.assertTrue(list.isEmpty());
	}

	@Test
	public void ambigousCommand1() 
	{
		// Prepare
		ICommandSyntax root = EithonCommand.createRootCommand("eithonfixes");
		try {
			root.parseCommandSyntax("restart <timespan : TIME_SPAN>");
		} catch (CommandSyntaxException e) {
			Assert.fail();
		}
		try {
			root.parseCommandSyntax("restart cancel");
		} catch (CommandSyntaxException e) {
			Assert.fail();
		}
		ICommandSyntax one = root.getSubCommand("restart");		
		ICommandSyntax two = one.getSubCommand("cancel");		
		one.setCommandExecutor(ec -> {Assert.assertNotNull(ec); setExecuteNumber(1);});		
		two.setCommandExecutor(ec -> {Assert.assertNotNull(ec); setExecuteNumber(2);});

		// Do
		EithonCommand restart = Support.createEithonCommand(root, "restart 10m");
		Assert.assertNotNull(restart);

		// Verify
		setExecuteNumber(0);
		Assert.assertTrue(restart.execute());
		Assert.assertEquals(1, getExecuteNumber());

		// Do
		EithonCommand cancel = Support.createEithonCommand(root, "restart cancel");
		Assert.assertNotNull(cancel);

		// Verify
		setExecuteNumber(0);
		Assert.assertTrue(cancel.execute());
		Assert.assertEquals(2, getExecuteNumber());
	}

	@Test
	public void ambigousCommand2() 
	{
		// Prepare
		ICommandSyntax root = EithonCommand.createRootCommand("eithonfixes");
		try {
			root.parseCommandSyntax("restart <timespan : TIME_SPAN {10m, ...}>");
		} catch (CommandSyntaxException e) {
			Assert.fail();
		}
		try {
			root.parseCommandSyntax("restart cancel");
		} catch (CommandSyntaxException e) {
			Assert.fail();
		}
		ICommandSyntax one = root.getSubCommand("restart");		
		ICommandSyntax two = one.getSubCommand("cancel");		
		one.setCommandExecutor(ec -> {Assert.assertNotNull(ec); setExecuteNumber(1);});		
		two.setCommandExecutor(ec -> {Assert.assertNotNull(ec); setExecuteNumber(2);});
		EithonCommand restart = Support.createEithonCommand(root, "restart ");
		Assert.assertNotNull(restart);
		
		// Do
		List<String> tabComplete = restart.tabComplete();
		
		// Verify
		Assert.assertNotNull(tabComplete);
		Assert.assertEquals(2, tabComplete.size());
		Assert.assertEquals("cancel", tabComplete.get(0));
		Assert.assertEquals("(timespan) 10m", tabComplete.get(0));
	}

	@Test
	public void defaultByGetter() 
	{
		ICommandSyntax root = EithonCommand.createRootCommand("root");
		// Prepare

		ICommandSyntax balance = null;
		try {
			balance = root.parseCommandSyntax("balance <player {a,b}>")
					.setCommandExecutor(ec -> defaultByGetterCommandExecutor(ec));
		} catch (CommandSyntaxException e) {
			Assert.fail();
		}

		balance
		.getParameterSyntax("player")
		.setExampleValues(ec -> EithonCommandUtilities.getOnlinePlayerNames(ec))
		.setDefaultGetter(ec -> "c");

		// Do
		EithonCommand ec = Support.createEithonCommand(root, "balance");
		Assert.assertNotNull(ec);

		// Verify
		setHasExecuted(false);
		Assert.assertTrue(ec.execute());
		Assert.assertTrue(getHasExecuted());
	}
	
	private void defaultByGetterCommandExecutor(EithonCommand ec) {
		Assert.assertNotNull(ec); 
		setHasExecuted(true);
		EithonArgument argument = ec.getArgument("player");
		Assert.assertNotNull(argument);
		Assert.assertEquals("c", argument.asString());
	}

	@Test
	public void emptyRest() 
	{
		ICommandSyntax root = EithonCommand.createRootCommand("root");
		// Prepare

		ICommandSyntax tempmute = null;
		try {
			tempmute = root.parseCommandSyntax("tempmute <player> <time-span : TIME_SPAN> <reason : REST>")
					.setCommandExecutor(ec -> commandExecutorForEmptyRest(ec));
		} catch (CommandSyntaxException e) {
			Assert.fail();
		}

		tempmute
		.getParameterSyntax("player")
		.setMandatoryValues(ec -> Arrays.asList(new String[]{"a", "b"}));

		tempmute
		.getParameterSyntax("time-span")
		.setDefault("10m");

		// Do
		EithonCommand ec = Support.createEithonCommand(root, "tempmute a");
		Assert.assertNotNull(ec);

		// Verify
		setHasExecuted(false);
		Assert.assertTrue(ec.execute());
		Assert.assertTrue(getHasExecuted());
	}
	
	private void commandExecutorForEmptyRest(EithonCommand ec) {
		Assert.assertNotNull(ec); 
		setHasExecuted(true);
		EithonArgument argument = ec.getArgument("reason");
		Assert.assertNotNull(argument);
		Assert.assertNull(argument.asString());
	}
}
