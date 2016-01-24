package net.eithon.library.command;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import net.eithon.library.file.FileMisc;

import org.bukkit.OfflinePlayer;

public class EithonCommandUtilities {
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
	
	public static List<String> getFileNames(File folder, String extension) {
		return Arrays.asList(FileMisc.getFileNames(folder, extension));
	}

	public static List<String> getRange(int start, int end) {
		ArrayList<String> list = new ArrayList<String>();
		for (int i = start; i < end+1; i++) {
			list.add(Integer.toString(i));
		}
		return list;
	}
}
