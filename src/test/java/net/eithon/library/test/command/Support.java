package net.eithon.library.test.command;

import java.util.ArrayList;
import java.util.List;

import net.eithon.library.command.syntax.CommandSyntax;
import net.eithon.library.command.syntax.CommandSyntaxException;
import net.eithon.library.command.syntax.ParameterSyntax;
import net.eithon.library.command.syntax.ParameterSyntax.ParameterType;

import org.junit.Assert;

class Support {
	static CommandSyntax createRoot(String name) {
		final CommandSyntax root = new CommandSyntax(name); 
		Assert.assertNotNull(root);	
		Assert.assertEquals(name,root.getName());
		Assert.assertEquals(name,root.toString());
		return root;
	}

	static CommandSyntax createSubCommand(CommandSyntax root, String name, String command) {
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

	static ParameterSyntax createParameter(CommandSyntax command, String leftSide, String name, String defaultValue, String[] validValues, boolean acceptsAnyValue) { 
		return createParameter(command, leftSide, name, ParameterType.STRING, defaultValue, validValues, acceptsAnyValue);
	}

	static ParameterSyntax createParameter(CommandSyntax command, String leftSide, String name, ParameterType type, String defaultValue, String[] validValues, boolean acceptsAnyValue) {
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

	static String[] createValues(String defaultValue, String[] validValues,
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
