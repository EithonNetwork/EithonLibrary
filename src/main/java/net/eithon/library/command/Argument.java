package net.eithon.library.command;

import net.eithon.library.extensions.EithonPlayer;
import net.eithon.library.time.TimeMisc;

import org.bukkit.entity.Player;

public class Argument extends ArgumentBase{

	public Argument(IParameterSyntax parameterSyntax, String argument) {
		super(parameterSyntax, argument);
		// TODO Auto-generated constructor stub
	}

	public long asSeconds() { return TimeMisc.stringToSeconds(asString()); }
	public long asTicks() { return TimeMisc.stringToTicks(asString()); }

	public Player asPlayer() {
		String playerName = asString();
		if (playerName == null) return null;
		EithonPlayer eithonPlayer = EithonPlayer.getFromString(playerName);
		if (eithonPlayer == null) return null;
		return eithonPlayer.getPlayer();
	}
}
