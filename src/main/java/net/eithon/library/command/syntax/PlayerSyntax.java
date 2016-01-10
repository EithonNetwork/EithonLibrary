package net.eithon.library.command.syntax;

import java.util.ArrayList;
import java.util.List;

import net.eithon.library.command.CommandArguments;

import org.bukkit.command.CommandSender;

class PlayerSyntax extends ArgumentSyntax {
	public PlayerSyntax(String name) {
		super(ArgumentType.Player, name);
	}

	@Override
	public boolean isOk(CommandArguments arguments) {
		String argument = arguments.getString();
		if ((argument != null) || getIsOptional()) return true;
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
