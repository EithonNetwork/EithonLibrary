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

	boolean send(String subChannel) {
		return send(subChannel, (MessageOut) null, (String[]) null);
	}

	boolean send(String subChannel, String... arguments) {
		return send(subChannel, (MessageOut) null, arguments);
	}

	boolean send(String subChannel, MessageOut msgOut, String... arguments) {
		verbose("send", "Enter: subChannel=%s", subChannel);
		Player player = getPlayer(subChannel, msgOut, arguments);
		if (player == null) {
			this._eithonPlugin.getEithonLogger().warning("No player found, will try later.");
			return true;
		}
		boolean success = send(player, subChannel, msgOut, arguments);
		verbose("send", "Leave, success = %s", success ? "TRUE" : "FALSE");
		return success;
	}

	boolean send(Player player, String subChannel, String... arguments) {
		return send(player, subChannel, (MessageOut) null, arguments);
	}

	private boolean send(Player player, String subChannel, MessageOut message, String... arguments) {
		verbose("send", "Enter: Player=%s, subChannel=%s", 
				player == null? "NULL" : player.getName(), subChannel);
		if (player == null) {
			verbose("send", "Player was null, will try another send method");
			boolean success = send(subChannel, message, arguments);
			verbose("send", "Leave, success = %s", success ? "TRUE" : "FALSE");
			return success;
		}
		MessageOut messageOut = new MessageOut();
		messageOut.add(subChannel);
		messageOut.add(arguments);
		if (message != null) messageOut.add(message.toByteArray());
		//simulateBungee(player, messageOut);
		player.sendPluginMessage(this._eithonPlugin, "BungeeCord", messageOut.toByteArray());
		
		verbose("send", "Leave TRUE");
		return true;
	}

	/*
	private void simulateBungee(Player player, MessageOut messageOut) {
		verbose("simulateBungee", "Enter");
		MessageIn messageIn = new MessageIn(messageOut.toByteArray());
		String subchannel = messageIn.readString();
		verbose("simulateBungee", "subchannel=%s", subchannel);
		if (subchannel.equals("Forward")) {
			verbose("simulateBungee", "Repack Forward message");
			String destinationServer = messageIn.readString();
			verbose("simulateBungee", "destinationServer=%s", destinationServer);
			String pluginChannel = messageIn.readString();
			verbose("simulateBungee", "pluginChannel=%s", pluginChannel);
			byte[] body = messageIn.readByteArray();
			messageOut = new MessageOut()
			.add(pluginChannel)
			.add(body);
		}
		BungeeController.bungeeListener.onPluginMessageReceived("BungeeCord", player, messageOut.toByteArray());
	}
	*/

	private Player getPlayer(String subChannel, MessageOut msgOut, String... arguments) {
		Player player = Iterables.getFirst(Bukkit.getOnlinePlayers(), null);
		if (player == null) {
			BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
			scheduler.scheduleSyncDelayedTask(this._eithonPlugin, new Runnable() {
				public void run() {
					send(subChannel, msgOut, arguments);
				}
			}, TimeMisc.secondsToTicks(1));
			return null;
		}
		return player;
	}

	private void verbose(String method, String format, Object... args) {
		String message = CoreMisc.safeFormat(format, args);
		this._eithonPlugin.getEithonLogger().debug(DebugPrintLevel.VERBOSE, "MessageChannel.%s: %s", method, message);
	}
}
