package net.eithon.library.bungee;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import net.eithon.library.core.CoreMisc;
import net.eithon.library.extensions.EithonPlugin;
import net.eithon.library.facades.ZPermissionsFacade;
import net.eithon.library.plugin.Logger.DebugPrintLevel;
import net.eithon.plugin.eithonlibrary.Config;

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
		verbose("eithonBungeeJoinEvent", "Enter, player = %s", player == null ? "NULL" : player.getName());
		if (player == null) {
			verbose("eithonBungeeJoinEvent", "Leave");
			return;
		}
		String mainGroup = getHighestGroup(player);
		if (mainGroup == null) mainGroup = "";
		ByteArrayOutputStream msgbytes = new ByteArrayOutputStream();
		DataOutputStream msgout = new DataOutputStream(msgbytes);
		String serverName = getServerName();
		if (serverName == null) serverName = "";
		try {
			msgout.writeUTF(serverName);
			msgout.writeUTF(player.getUniqueId().toString());
			msgout.writeUTF(mainGroup);
		} catch (IOException e) {
			verbose("eithonBungeeJoinEvent", "Leave");
			e.printStackTrace();
			return;
		}
		
		// Local event
		if (mainGroup.isEmpty()) mainGroup = null;
		if (serverName.isEmpty()) serverName = null;
		EithonBungeeJoinEvent e = new EithonBungeeJoinEvent(serverName, player, mainGroup);
		Bukkit.getServer().getPluginManager().callEvent(e);
		verbose("eithonBungeeJoinEvent", "Published local event");

		// Forward event to all other servers
		boolean success = this._bungeeSender.forwardToAll("EithonBungeeJoinEvent", msgbytes);
		verbose("eithonBungeeJoinEvent", String.format("sucess = %s", success ? "TRUE" : "FALSE"));
		verbose("eithonBungeeJoinEvent", "Leave");
	}

	private String getHighestGroup(Player player) {
		verbose("getHighestGroup", "Enter, Player = %s", player.getName());
		String[] currentGroups = ZPermissionsFacade.getPlayerPermissionGroups(player);
		for (String priorityGroup : Config.V.groupPriorities) {
			for (String playerGroup : currentGroups) {
				if (playerGroup.equalsIgnoreCase(priorityGroup)) {
					verbose("getHighestGroup", "Leave, priorityGroup = %s", priorityGroup);
					return priorityGroup;
				}
			}
		}
		verbose("getHighestGroup", "Leave, priorityGroup = null");
		return null;
	}

	private void verbose(String method, String format, Object... args) {
		String message = CoreMisc.safeFormat(format, args);
		this._eithonPlugin.getEithonLogger().debug(DebugPrintLevel.VERBOSE, "BungeeController.%s: %s", method, message);
	}
}
