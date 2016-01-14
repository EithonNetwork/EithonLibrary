package net.eithon.library.command;

import net.eithon.library.command.syntax.ParameterSyntax;
import net.eithon.library.time.TimeMisc;

import org.bukkit.entity.Player;

public class Argument extends net.eithon.library.command.syntax.Argument{

	public Argument(ParameterSyntax parameterSyntax, String argument) {
		super(parameterSyntax, argument);
		// TODO Auto-generated constructor stub
	}

	public long asSeconds() {
		return TimeMisc.stringToSeconds(asString());
	}

	public long asTicks() {
		return TimeMisc.stringToTicks(asString());
	}

	public Player asPlayer() {
		// TODO Auto-generated method stub
		return null;
	}

	public Player asPlayer(Player defaultPlayer) {
		// TODO Auto-generated method stub
		return null;
	}
}
