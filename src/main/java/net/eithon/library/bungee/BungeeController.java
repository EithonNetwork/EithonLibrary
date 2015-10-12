package net.eithon.library.bungee;

import net.eithon.library.core.CoreMisc;
import net.eithon.library.extensions.EithonPlugin;
import net.eithon.library.facades.ZPermissionsFacade;
import net.eithon.library.plugin.Logger.DebugPrintLevel;
import net.eithon.plugin.eithonlibrary.Config;

import org.bukkit.entity.Player;

public class BungeeController {

	private static BungeeListener bungeeListener;
	private BungeeSender _bungeeSender;
	private EithonPlugin _eithonPlugin;
	private String _serverName;

	public BungeeController(EithonPlugin eithonPlugin) {
		this._eithonPlugin = eithonPlugin;
		eithonPlugin.getServer().getMessenger().registerOutgoingPluginChannel(eithonPlugin, "BungeeCord");
		this._bungeeSender = new BungeeSender(eithonPlugin);
	}

	public void createBungeeListener() {
		
		bungeeListener = new BungeeListener(this._eithonPlugin, this);
		this._eithonPlugin.getServer().getMessenger().registerIncomingPluginChannel(this._eithonPlugin, "BungeeCord", bungeeListener);
		this._bungeeSender.getServer();
	}


	public String getServerName() { return this._serverName; }

	public void setServerName(String serverName) { this._serverName = serverName; }

	public boolean connectToServer(Player player, String serverName) { return this._bungeeSender.connect(player, serverName);}

	public void eithonBungeeJoinEvent(Player player) {
		eithonBungeeJoinQuitEvent(player, "EithonBungeeJoinEvent");
	}

	public void eithonBungeeQuitEvent(Player player) {
		eithonBungeeJoinQuitEvent(player, "EithonBungeeQuitEvent");
	}

	private void eithonBungeeJoinQuitEvent(Player player, String eventName) {
		verbose("eithonBungeeJoinQuitEvent", "Enter, player = %s", player == null ? "NULL" : player.getName());
		if (player == null) {
			verbose("eithonBungeeJoinQuitEvent", "Leave");
			return;
		}
		String mainGroup = getHighestGroup(player);
		verbose("eithonBungeeJoinQuitEvent", String.format("mainGroup=%s", mainGroup));
		String serverName = getServerName();
		verbose("eithonBungeeJoinQuitEvent", String.format("serverName=%s", serverName));
		JoinQuitInfo info = new JoinQuitInfo(serverName, player.getUniqueId(), mainGroup);
		boolean success = this._bungeeSender.forwardToAll(eventName, info.toJSONString());
		verbose("eithonBungeeJoinQuitEvent", String.format("success=%s", success ? "TRUE" : "FALSE"));
		verbose("eithonBungeeJoinQuitEvent", "Leave");
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
