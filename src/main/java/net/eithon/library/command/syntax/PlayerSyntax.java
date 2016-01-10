package net.eithon.library.command.syntax;

import net.eithon.library.command.CommandArguments;

import org.bukkit.command.CommandSender;

class PlayerSyntax extends ArgumentSyntax {
	public PlayerSyntax(String name) {
		super(ArgumentType.Player, name);
	}

	@Override
	public boolean isOk(CommandSender sender, CommandArguments arguments) {
		String argument = arguments.getString();
		if ((argument != null) || getIsOptional()) return true;
		sender.sendMessage(String.format("Expected a value for argument %s", getName()));
		return false;
	}
}
