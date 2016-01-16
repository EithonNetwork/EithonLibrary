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
		final CommandSyntax sub = root.getSubCommand(subName);
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
		final CommandSyntax sub = createSubCommand(root, subName, subCommand);

		// Do and verify
		final ParameterSyntax parameterSyntax = createParameter(sub, null, parameterName, ParameterType.INTEGER, null, null, false);


		// Verify
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
		final CommandSyntax sub = createSubCommand(root, subName, subCommand);

		// Do and verify
		final ParameterSyntax parameterSyntax = createParameter(sub, null, parameterName, defaultValue, null, false);

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
		final String[] helpValues = new String[] { "113" };
		final String subCommand = String.format("%s <%s {%s, ...}>", subName, parameterName, helpValues[0]);
		final CommandSyntax root = createRoot("root");
		final CommandSyntax sub = createSubCommand(root, subName, subCommand);

		// Do and verify
		final ParameterSyntax parameterSyntax = createParameter(sub, null, parameterName, null, helpValues, true);

		// Verify
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
		final String[] helpValues = new String[] { "113", "1477" };
		final String helpValue1 = "113";
		final String helpValue2 = "1447";
		final String subCommand = String.format("%s <%s {%s, %s, ...}>", subName, parameterName, helpValue1, helpValue2);
		final CommandSyntax root = createRoot("root");
		final CommandSyntax sub = createSubCommand(root, subName, subCommand);

		// Do and verify
		final ParameterSyntax parameterSyntax = createParameter(sub, null, parameterName, null, helpValues, true);
		
		// Verify
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
		final String[] helpValues = new String[] { "113", "1477" };
		final String helpValue1 = "113";
		final String helpValue2 = "1447";
		final String subCommand = String.format("%s <%s {%s, %s}>", subName, parameterName, helpValue1, helpValue2);
		final CommandSyntax root = createRoot("root");
		final CommandSyntax sub = createSubCommand(root, subName, subCommand);

		// Do and verify
		final ParameterSyntax parameterSyntax = createParameter(sub, null, parameterName, null, helpValues, false);	

		// Verify
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

	@Test
	public void parameterTwoMandatoryWithDefault() 
	{
		// Prepare
		final String subName = "sub";
		final String parameterName = "parameter";
		final String[] helpValues = new String[] { "113", "1477" };
		final String helpValue1 = "113";
		final String helpValue2 = "1447";
		final String subCommand = String.format("%s <%s {%s, _%s_}>", subName, parameterName, helpValue1, helpValue2);
		final CommandSyntax root = createRoot("root");
		final CommandSyntax sub = createSubCommand(root, subName, subCommand);

		// Do and verify
		final ParameterSyntax parameterSyntax = createParameter(sub, null, parameterName, helpValues[1], helpValues, false);	

		// Verify
		Assert.assertNotNull(parameterSyntax);
		Assert.assertNotNull(parameterSyntax.getDefault());
		Assert.assertEquals(helpValue2, parameterSyntax.getDefault());
		final List<String> validValues = parameterSyntax.getValidValues();
		Assert.assertNotNull(validValues);
		Assert.assertEquals(2, validValues.size());
		Assert.assertEquals(helpValue1, validValues.get(0));
		Assert.assertEquals(helpValue2, validValues.get(1));
		Assert.assertTrue(parameterSyntax.getIsOptional());
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

		return sub;
	}

	private ParameterSyntax createParameter(CommandSyntax command, String leftSide, String name, String defaultValue, String[] validValues, boolean acceptsAnyValue) { 
		return createParameter(command, leftSide, name, ParameterType.STRING, defaultValue, validValues, acceptsAnyValue);
	}

	private ParameterSyntax createParameter(CommandSyntax command, String leftSide, String name, ParameterType type, String defaultValue, String[] validValues, boolean acceptsAnyValue) {
		// Prepare
		String parameter = "";
		if (leftSide != null) parameter += leftSide+"=";
		parameter+="<" + name;
		if (type != ParameterType.STRING) {
			parameter += " " + type.toString();
		}
		String[] values = createValues(defaultValue, validValues, acceptsAnyValue);
		if (values.length > 0) {
			parameter += " {" + String.join(", ", values) + "}";
		}
		parameter += ">";

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
		Assert.assertEquals(validValues == null ? 0 : validValues.length, valueList.size());
		if (valueList.size() > 0) {
			int i = 0;
			for (String value : valueList) {
				Assert.assertEquals(validValues[i], value);
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
				if (!value.equals(defaultValue)) {
					list.add("_" + value + "_");
					found = true;
				}
				else list.add(value); 
			}
		}
		if (!found && (defaultValue != null)) list.add(0, defaultValue);
		if (acceptsAnyValue) list.add("...");
		return list.toArray(new String[0]);
	}
}
