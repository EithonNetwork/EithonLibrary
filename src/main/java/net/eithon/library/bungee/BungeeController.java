package net.eithon.library.bungee;

import net.eithon.library.core.CoreMisc;
import net.eithon.library.extensions.EithonPlugin;
import net.eithon.library.facades.ZPermissionsFacade;
import net.eithon.library.json.IJsonObject;
import net.eithon.library.plugin.Logger.DebugPrintLevel;
import net.eithon.plugin.eithonlibrary.Config;

import org.bukkit.entity.Player;
import org.json.simple.JSONObject;

public class BungeeController {

	static BungeeListener bungeeListener;
	private BungeeSender _bungeeSender;
	private EithonPlugin _eithonPlugin;
	private String _serverName;

	public BungeeController(EithonPlugin eithonPlugin) {
		this._eithonPlugin = eithonPlugin;
		createBungeeSender(eithonPlugin);
		createBungeeListener(eithonPlugin);
		this._bungeeSender.getServer();
	}

	public void createBungeeSender(EithonPlugin eithonPlugin) {
		eithonPlugin.getServer().getMessenger().registerOutgoingPluginChannel(eithonPlugin, "BungeeCord");
		this._bungeeSender = new BungeeSender(eithonPlugin);
	}

	private void createBungeeListener(EithonPlugin eithonPlugin) {
		bungeeListener = new BungeeListener(eithonPlugin, this);
		eithonPlugin.getServer().getMessenger().registerIncomingPluginChannel(eithonPlugin, "BungeeCord", bungeeListener);
	}

	public String getServerName() { return this._serverName; }

	void setServerName(String serverName) { this._serverName = serverName; }

	public boolean connectToServer(Player player, String serverName) { return this._bungeeSender.connect(player, serverName);}

	public void joinEvent(Player player) {
		joinQuitEvent(player, "JoinEvent");
	}

	public void quitEvent(Player player) {
		joinQuitEvent(player, "QuitEvent");
	}

	public boolean broadcastMessage(String message, boolean useTitle) {
		verbose("broadcastMessage", "Enter, message = %s", message);
		MessageInfo info = new MessageInfo(message, useTitle);
		boolean success = this._bungeeSender.forwardToAll("BroadcastMessage", info, true);
		verbose("broadcastMessage", String.format("success=%s", success ? "TRUE" : "FALSE"));
		verbose("broadcastMessage", "Leave");
		return success;
	}

	public boolean sendEventToServer(String targetServerName, String name,
			IJsonObject<?> data, boolean rejectOld) {
		JSONObject jsonObject = data == null ? null : data.toJsonObject();
		String jsonString = jsonObject == null ? null : jsonObject.toJSONString();
		verbose("sendEventToServer", "Enter, targetServerName = %s, name= %s, json=%s",
				targetServerName, name, jsonString);
		if (targetServerName == null) {
			verbose("sendEventToServer", "targetServerName NULL, Leave");
			return false;
		}
		String thisServerName = this.getServerName();
		EithonBungeeEvent info = new EithonBungeeEvent(thisServerName, name, jsonObject);
		boolean success = this._bungeeSender.forward(targetServerName, "CallEvent", info, rejectOld);
		verbose("sendEventToServer", String.format("Leave, success=%s", success ? "TRUE" : "FALSE"));
		return success;
	}

	private boolean joinQuitEvent(Player player, String eventName) {
		verbose("joinQuitEvent", "Enter, player = %s", player == null ? "NULL" : player.getName());
		if (player == null) {
			verbose("joinQuitEvent", "Player NULL, Leave");
			return false;
		}
		String mainGroup = getHighestGroup(player);
		verbose("joinQuitEvent", String.format("mainGroup=%s", mainGroup));
		String serverName = getServerName();
		verbose("joinQuitEvent", String.format("serverName=%s", serverName));
		JoinQuitInfo info = new JoinQuitInfo(serverName, player.getUniqueId(), player.getName(), mainGroup);
		boolean success = this._bungeeSender.forwardToAll(eventName, info, true);
		verbose("joinQuitEvent", String.format("success=%s", success ? "TRUE" : "FALSE"));
		verbose("joinQuitEvent", "Leave");
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
