package net.eithon.library.test.command;

import java.util.ArrayList;
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
		root.getSubCommand(subName);
	}

	@Test
	public void parameter() 
	{

		// Prepare
		final String subName = "sub";
		final String parameterName = "parameter";
		final CommandSyntax root = createRoot("root");
		final CommandSyntax sub = createSubCommand(root, subName, subName);

		// Do and verify
		createParameter(sub, null, parameterName, null, null, true);
	}

	@Test
	public void parameterInteger() 
	{

		// Prepare
		final String subName = "sub";
		final String parameterName = "parameter";
		final CommandSyntax root = createRoot("root");
		final CommandSyntax sub = createSubCommand(root, subName, subName);

		// Do and verify
		createParameter(sub, null, parameterName, ParameterType.INTEGER, null, null, true);
	}

	@Test
	public void parameterDefault() 
	{

		// Prepare
		final String subName = "sub";
		final String parameterName = "parameter";
		final String defaultValue = "42";
		final CommandSyntax root = createRoot("root");
		final CommandSyntax sub = createSubCommand(root, subName, subName);

		// Do and verify
		createParameter(sub, null, parameterName, defaultValue, null, true);
	}

	@Test
	public void parameterOneHelp() 
	{

		// Prepare
		final String subName = "sub";
		final String parameterName = "parameter";
		final String[] helpValues = new String[] { "113" };
		final CommandSyntax root = createRoot("root");
		final CommandSyntax sub = createSubCommand(root, subName, subName);

		// Do and verify
		createParameter(sub, null, parameterName, null, helpValues, true);
	}

	@Test
	public void parameterTwoHelp() 
	{

		// Prepare
		final String subName = "sub";
		final String parameterName = "parameter";	
		final String[] helpValues = new String[] { "113", "1477" };
		final CommandSyntax root = createRoot("root");
		final CommandSyntax sub = createSubCommand(root, subName, subName);

		// Do and verify
		createParameter(sub, null, parameterName, null, helpValues, true);
	}

	@Test
	public void parameterTwoMandatory() 
	{

		// Prepare
		final String subName = "sub";
		final String parameterName = "parameter";
		final String[] helpValues = new String[] { "113", "1477" };
		final CommandSyntax root = createRoot("root");
		final CommandSyntax sub = createSubCommand(root, subName, subName);

		// Do and verify
		final ParameterSyntax parameterSyntax = createParameter(sub, null, parameterName, null, helpValues, false);	
	}

	@Test
	public void parameterTwoMandatoryWithDefault() 
	{
		// Prepare
		final String subName = "sub";
		final String parameterName = "parameter";
		final String[] helpValues = new String[] { "113", "1477" };
		final CommandSyntax root = createRoot("root");
		final CommandSyntax sub = createSubCommand(root, subName, subName);

		// Do and verify
		final ParameterSyntax parameterSyntax = createParameter(sub, null, parameterName, helpValues[1], helpValues, false);
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

		return sub;
	}

	private ParameterSyntax createParameter(CommandSyntax command, String leftSide, String name, String defaultValue, String[] validValues, boolean acceptsAnyValue) { 
		return createParameter(command, leftSide, name, ParameterType.STRING, defaultValue, validValues, acceptsAnyValue);
	}

	private ParameterSyntax createParameter(CommandSyntax command, String leftSide, String name, ParameterType type, String defaultValue, String[] validValues, boolean acceptsAnyValue) {
		// Prepare
		String parameter = "";
		if (leftSide != null) parameter += leftSide+"=";
		parameter += name;
		if (type != ParameterType.STRING) {
			parameter += " : " + type.toString();
		}
		String[] values = createValues(defaultValue, validValues, acceptsAnyValue);
		if (values.length > 0) {
			parameter += " {" + String.join(", ", values) + "}";
		}

		// Do
		ParameterSyntax parameterSyntax = null;
		try {
			parameterSyntax = ParameterSyntax.parseSyntax(null, parameter);
		} catch (CommandSyntaxException e) {
			Assert.fail();
		}

		// Verify
		Assert.assertNotNull(parameterSyntax);
		Assert.assertEquals(defaultValue != null, parameterSyntax.getIsOptional());
		Assert.assertEquals(defaultValue, parameterSyntax.getDefault());
		final List<String> valueList = parameterSyntax.getValidValues();
		Assert.assertNotNull(valueList);
		Assert.assertEquals(values.length - (acceptsAnyValue ? 1 : 0), valueList.size());
		if (defaultValue != null) Assert.assertTrue(valueList.contains(defaultValue));
		if (validValues != null) {
			int i = 0;
			for (String value : validValues) {
				Assert.assertTrue(valueList.contains(value));
			}
		}
		Assert.assertEquals(acceptsAnyValue, parameterSyntax.getAcceptsAnyValue());

		return parameterSyntax;
	}

	private String[] createValues(String defaultValue, String[] validValues,
			boolean acceptsAnyValue) {
		boolean found = false;
		List<String> list = new ArrayList<String>();
		if (validValues != null) {
			for (String value : validValues) {
				if (value.equals(defaultValue)) {
					list.add("_" + value + "_");
					found = true;
				}
				else list.add(value); 
			}
		}
		if (!found && (defaultValue != null)) list.add(0, "_" + defaultValue + "_");
		if (acceptsAnyValue) list.add("...");
		return list.toArray(new String[0]);
	}
}
