package net.eithon.library.command;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.OfflinePlayer;

public class BukkitValueLists {
	public static List<String> getOnlinePlayerNames(EithonCommand command) {
		return command
				.getSender()
				.getServer()
				.getOnlinePlayers()
				.stream()
				.map(p -> p.getName())
				.collect(Collectors.toList());
	}
	
	public static List<String> getOfflinePlayerNames(EithonCommand command) {
		OfflinePlayer[] offlinePlayers = command
				.getSender()
				.getServer()
				.getOfflinePlayers();
		return Arrays.asList(offlinePlayers)
				.stream()
				.map(p -> p.getName())
				.collect(Collectors.toList());
	}
	
	public static List<String> getWorldNames(EithonCommand command) {
		return command
				.getSender()
				.getServer()
				.getWorlds()
				.stream()
				.map(p -> p.getName())
				.collect(Collectors.toList());
	}
	
	public static List<String> getOperatorNames(EithonCommand command) {
		return command
				.getSender()
				.getServer()
				.getOperators()
				.stream()
				.map(p -> p.getName())
				.collect(Collectors.toList());
	}
}
