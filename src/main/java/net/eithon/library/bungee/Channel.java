package net.eithon.library.bungee;

import net.eithon.library.core.CoreMisc;
import net.eithon.library.extensions.EithonPlugin;
import net.eithon.library.plugin.Logger.DebugPrintLevel;
import net.eithon.library.time.TimeMisc;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitScheduler;

import com.google.common.collect.Iterables;

class Channel {
	private EithonPlugin _eithonPlugin;

	public Channel(EithonPlugin eithonPlugin) {
		this._eithonPlugin = eithonPlugin;
	}

	boolean send(String subChannel, byte[] message, String... arguments) {
		verbose("send", "Enter: subChannel=%s, message=%s", 
				subChannel, message == null ? "NULL" : message.toString());
		Player player = getPlayer(subChannel, message, arguments);
		if (player == null) {
			verbose("send", "No player found, will try later. Return TRUE.");
			return true;
		}
		boolean success = send(player, subChannel, message, arguments);
		verbose("send", "Leave, success = %s", success ? "TRUE" : "FALSE");
		return success;
	}

	private Player getPlayer(String subChannel, byte[] message, String... arguments) {
		Player player = Iterables.getFirst(Bukkit.getOnlinePlayers(), null);
		if (player == null) {
			verbose("send", "No player, will try again in one second.");
			tryAgain(subChannel, message, arguments);
			verbose("send", "Leave, TRUE");
			return null;
		}
		return player;
	}

	private void tryAgain(String subChannel, byte[] message, String... arguments) {
		BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
		scheduler.scheduleSyncDelayedTask(this._eithonPlugin, new Runnable() {
			public void run() {
				send(subChannel, message, arguments);
			}
		}, TimeMisc.secondsToTicks(1));
	}

	boolean send(Player player, String subChannel, String... arguments) {
		return send(player, subChannel, null, arguments);
	}

	boolean send(String subChannel, String... arguments) {
		return send(subChannel, null, arguments);
	}

	private boolean send(Player player, String subChannel, byte[] message, String... arguments) {
		verbose("send", String.format("Enter: Player=%s, subChannel=%s, message=%s", 
				player == null? "NULL" : player.getName(), subChannel, message == null ? "NULL" : message.toString()));

		if (player == null) {
			verbose("send", "Player was null");
			verbose("send", "Leave FALSE");
			return false;
		}
		
		MessageOut messageOut = new MessageOut();
		messageOut.add(subChannel);
		messageOut.add(arguments);
		messageOut.add(message);

		player.sendPluginMessage(this._eithonPlugin, "BungeeCord", messageOut.toByteArray());
		verbose("send", "Leave TRUE");
		return true;
	}

	private void verbose(String method, String format, Object... args) {
		String message = CoreMisc.safeFormat(format, args);
		this._eithonPlugin.getEithonLogger().debug(DebugPrintLevel.VERBOSE, "MessageChannel.%s: %s", method, message);
	}
}
