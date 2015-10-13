package net.eithon.library.bungee;

import java.util.UUID;

import net.eithon.library.core.CoreMisc;
import net.eithon.library.extensions.EithonPlugin;
import net.eithon.library.facades.ZPermissionsFacade;
import net.eithon.library.plugin.Logger.DebugPrintLevel;
import net.eithon.plugin.eithonlibrary.Config;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class BungeeController {

	private static BungeeListener bungeeListener;
	private BungeeSender _bungeeSender;
	private EithonPlugin _eithonPlugin;
	private String _serverName;

	public BungeeController(EithonPlugin eithonPlugin) {
		this._eithonPlugin = eithonPlugin;
		createBungeeSender(eithonPlugin);
		createBungeeListener(eithonPlugin);
	}

	public void createBungeeSender(EithonPlugin eithonPlugin) {
		eithonPlugin.getServer().getMessenger().registerOutgoingPluginChannel(eithonPlugin, "BungeeCord");
		this._bungeeSender = new BungeeSender(eithonPlugin);
	}

	private void createBungeeListener(EithonPlugin eithonPlugin) {
		bungeeListener = new BungeeListener(eithonPlugin, this);
		eithonPlugin.getServer().getMessenger().registerIncomingPluginChannel(eithonPlugin, "BungeeCord", bungeeListener);
		eithonPlugin.getServer();
	}


	public String getServerName() { return this._serverName; }

	public void setServerName(String serverName) { this._serverName = serverName; }

	public boolean connectToServer(Player player, String serverName) { return this._bungeeSender.connect(player, serverName);}

	public void joinEvent(UUID playerId) {
		Player player = Bukkit.getPlayer(playerId);
		if (player == null) return;
		eithonBungeeJoinQuitEvent(player, "JoinEvent");
	}

	public void quitEvent(Player player) {
		eithonBungeeJoinQuitEvent(player, "QuitEvent");
	}

	public boolean broadcastMessage(String message, boolean useTitle) {
		verbose("broadcastMessage", "Enter, message = %s", message);
		MessageInfo info = new MessageInfo(message, useTitle);
		boolean success = this._bungeeSender.forwardToAll("BroadcastMessage", info.toJSONString(), true);
		verbose("broadcastMessage", String.format("success=%s", success ? "TRUE" : "FALSE"));
		verbose("broadcastMessage", "Leave");
		return success;
	}

	private boolean eithonBungeeJoinQuitEvent(Player player, String eventName) {
		verbose("eithonBungeeJoinQuitEvent", "Enter, player = %s", player == null ? "NULL" : player.getName());
		if (player == null) {
			verbose("eithonBungeeJoinQuitEvent", "Player NULL, Leave");
			return false;
		}
		String mainGroup = getHighestGroup(player);
		verbose("eithonBungeeJoinQuitEvent", String.format("mainGroup=%s", mainGroup));
		String serverName = getServerName();
		verbose("eithonBungeeJoinQuitEvent", String.format("serverName=%s", serverName));
		JoinQuitInfo info = new JoinQuitInfo(serverName, player.getUniqueId(), player.getName(), mainGroup);
		boolean success = this._bungeeSender.forwardToAll(eventName, info.toJSONString(), true);
		verbose("eithonBungeeJoinQuitEvent", String.format("success=%s", success ? "TRUE" : "FALSE"));
		verbose("eithonBungeeJoinQuitEvent", "Leave");
		return success;
	}

	public static String getHighestGroup(Player player) {
		String[] currentGroups = ZPermissionsFacade.getPlayerPermissionGroups(player);
		for (String priorityGroup : Config.V.groupPriorities) {
			for (String playerGroup : currentGroups) {
				if (playerGroup.equalsIgnoreCase(priorityGroup)) {
					return priorityGroup;
				}
			}
		}
		return null;
	}

	private void verbose(String method, String format, Object... args) {
		String message = CoreMisc.safeFormat(format, args);
		this._eithonPlugin.getEithonLogger().debug(DebugPrintLevel.VERBOSE, "BungeeController.%s: %s", method, message);
	}
}
