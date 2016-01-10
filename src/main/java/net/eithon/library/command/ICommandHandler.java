package net.eithon.library.command;

import net.eithon.library.command.syntax.CommandSyntax;


public interface ICommandHandler {
	CommandSyntax getCommandSyntax();
}
