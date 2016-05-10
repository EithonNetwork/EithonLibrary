package net.eithon.library.test.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.eithon.library.command.CommandSyntaxException;
import net.eithon.library.command.EithonCommand;
import net.eithon.library.command.ICommandSyntax;
import net.eithon.library.command.ICommandSyntaxAdvanced;
import net.eithon.library.command.IParameterSyntax;
import net.eithon.library.command.IParameterSyntax.ParameterType;
import net.eithon.library.command.IParameterSyntaxAdvanced;

import org.junit.Assert;

class Support {
	static ICommandSyntax createRoot(String name) {
		final ICommandSyntax root = EithonCommand.createRootCommand(name); 
		Assert.assertNotNull(root);	
		Assert.assertEquals(name,root.getName());
		Assert.assertEquals(name,root.toString());
		return root;
	}

	static ICommandSyntax createSubCommand(ICommandSyntax root, String name, String command) {
		// Prepare
		final String completeCommand = String.format("%s %s", root.toString(), command);

		// Do
		try {
			root.parseCommandSyntax(command);
		} catch (CommandSyntaxException e) {
			Assert.fail();
		}	

		// Verify
		final ICommandSyntax sub = root.getSubCommand(name);
		Assert.assertNotNull(sub);	
		Assert.assertEquals(name,sub.getName());		
		Assert.assertEquals(command,sub.toString());
		Assert.assertEquals(completeCommand,root.toString());

		return sub;
	}

	static IParameterSyntax createParameter(ICommandSyntax command, String leftSide, String name, String defaultValue, String[] validValues, boolean acceptsAnyValue) { 
		return createParameter(command, leftSide, name, ParameterType.STRING, defaultValue, validValues, acceptsAnyValue);
	}

	static IParameterSyntax createParameter(ICommandSyntax command, String leftSide, String name, ParameterType type, String defaultValue, String[] validValues, boolean acceptsAnyValue) {
		// Prepare
		String parameter = "";
		parameter += name;
		if (type != ParameterType.STRING) {
			parameter += " : " + type.toString();
		}
		String[] values = createValues(defaultValue, validValues, acceptsAnyValue);
		if (values.length > 0) {
			parameter += " {" + String.join(", ", values) + "}";
		}

		// Do
		IParameterSyntaxAdvanced parameterSyntax = null;
		
		try {
			ICommandSyntaxAdvanced c = command.getAdvancedMethods();
			IParameterSyntax ps = c.parseParameterSyntax(leftSide, parameter);		
			parameterSyntax = ps.getAdvancedMethods();
		} catch (CommandSyntaxException e) {
			Assert.fail();
		}

		// Verify
		Assert.assertNotNull(parameterSyntax);
		Assert.assertNotNull(command.getParameterSyntax(name));
		Assert.assertEquals(defaultValue != null, parameterSyntax.getIsOptional());
		Assert.assertEquals(defaultValue, parameterSyntax.getDefault());
		final List<String> valueList = parameterSyntax.getValidValues();
		Assert.assertNotNull(valueList);
		Assert.assertEquals(values.length - (acceptsAnyValue ? 1 : 0), valueList.size());
		if (defaultValue != null) Assert.assertTrue(valueList.contains(defaultValue));
		if (validValues != null) {
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
	
	static String[] split(String command) {
		String[] array = command.split(" ");
		List<String> list = new ArrayList<String>();
		list.addAll(Arrays.asList(array));
		if (command.matches(".* $")) {
			list.add("");
		}
		return list.toArray(new String[0]);
	}

	static EithonCommand createEithonCommand(final ICommandSyntax root, final String command) {
		return new EithonCommand(root, null, null, "alias", split(command));
	}
}
