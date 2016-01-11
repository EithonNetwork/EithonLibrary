package net.eithon.library.command.syntax;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.eithon.library.command.CommandArguments;
import net.eithon.library.command.ParameterValue;

import org.bukkit.command.CommandSender;

class PlayerParameterSyntax extends ParameterSyntax {
	public PlayerParameterSyntax(String name) {
		super(ParameterType.Player, name);
	}

	@Override
	public boolean parse(CommandArguments arguments, HashMap<String, ParameterValue> parameterValues) {
		String argument = arguments.getString();
		if ((argument != null) || getIsOptional()) {
			if (parameterValues != null) parameterValues.put(getName(), new ParameterValue(this, argument));
			return true;
		}
		arguments.getSender().sendMessage(String.format("Expected a value for argument %s", getName()));
		return false;
	}
	
	@Override
	public List<String> getValidValues() {
		List<String> list = new ArrayList<String>();
		list.add("kalle");
		list.add("lars");
		list.add("nisse");
		return list;
	}
}
