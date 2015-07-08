package net.eithon.library.extensions;

import java.util.List;
import java.util.UUID;

import net.eithon.library.core.CoreMisc;
import net.eithon.library.core.IUuidAndName;
import net.eithon.library.json.IJson;
import net.eithon.library.plugin.GeneralMessage;
import net.eithon.plugin.eithonlibrary.Config;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.json.simple.JSONObject;

public class EithonPlayer implements IJson<EithonPlayer>, IUuidAndName{

	private Player _player = null;
	private OfflinePlayer _offlinePlayer = null;
	private UUID _id = null;
	private String _name = null;
	public EithonPlayer(Player player) { 
		this._player = player;
		if (player != null) {
			this._id = player.getUniqueId();
			this._name = player.getName();
			verifyPlayerIsOnline();
		}
	}

	public EithonPlayer(OfflinePlayer player) { 
		this._offlinePlayer = player;
		if (player != null) {
			this._id = player.getUniqueId();
			this._name = player.getName();
			verifyPlayerIsOnline();
		}
	}

	public EithonPlayer(UUID id, String name) { 
		this._id = id;
		this._name = name;
	}

	EithonPlayer() {
	}

	@Override
	public UUID getUniqueId() { return this._id; }

	@Override
	public String getName() { return this._name; }
	
	public boolean isOnline() { return verifyPlayerIsOnline(); }

	public Player getPlayer() {
		verifyPlayerIsOnline();
		return this._player;
	}
	
	public OfflinePlayer getOfflinePlayer() {
		verifyPlayerIsOnline();
		return this._offlinePlayer;
	}

	private boolean verifyPlayerIsOnline() {
		if (this._player == null) {
			// Check if the player is online now
			this._player = Bukkit.getPlayer(this._id);
			if (this._player != null) {
				this._offlinePlayer = null;
				return true;
			}
		} else {
			if (this._player.isOnline()) return true;
			this._player = null;
		}

		if (this._offlinePlayer != null) return false;
		this._offlinePlayer = Bukkit.getOfflinePlayer(this._id);
		return false;
	}	

	@SuppressWarnings("deprecation")
	public static EithonPlayer getFromString(String playerIdOrName) {
		Player player = null;
		try {
			UUID id = UUID.fromString(playerIdOrName);
			player = Bukkit.getPlayer(id);
		} catch (Exception e) { }
		if (player == null) try { player = Bukkit.getPlayer(playerIdOrName); } catch (Exception e) { }
		if (player != null) return new EithonPlayer(player);
		
		OfflinePlayer offlinePlayer = null;
		try {
			UUID id = UUID.fromString(playerIdOrName);
			offlinePlayer = Bukkit.getOfflinePlayer(id);
		} catch (Exception e) { }
		if (offlinePlayer == null) try { offlinePlayer = Bukkit.getOfflinePlayer(playerIdOrName); } catch (Exception e) { }
		if (offlinePlayer != null) return new EithonPlayer(offlinePlayer);
		return null;
	}

	public boolean hasPermissionOrInformPlayer(String permission)
	{
		if (hasPermission(permission)) return true;
		GeneralMessage.requiredPermission.sendMessage(this._player, permission);
		return false;
	}

	public boolean hasPermission(String permission) {
		return this._player.hasPermission(permission);
	}

	public boolean isInAcceptableWorldOrInformPlayer(List<String> acceptableWorlds) {
		String worldName = getPlayer().getWorld().getName();
		for (String acceptableWorldName : acceptableWorlds) {
			if (worldName.equalsIgnoreCase(acceptableWorldName)) return true;
		}
		Config.M.expectedWorlds.sendMessage(getPlayer(), worldName,
				CoreMisc.arrayToString((String[]) acceptableWorlds.toArray()));
		return false;
	}

	@Override
	public EithonPlayer factory() {
		return new EithonPlayer();
	}

	@SuppressWarnings("unchecked")
	@Override
	public Object toJson() {
		JSONObject json = new JSONObject();
		json.put("id", getUniqueId().toString());
		json.put("name", getName());
		return json;
	}

	@Override
	public EithonPlayer fromJson(Object json) {
		JSONObject jsonObject = (JSONObject) json;
		this._id = UUID.fromString((String) jsonObject.get("id"));
		this._name = (String) jsonObject.get("name");
		return this;
	}

	public static EithonPlayer getFromJSon(Object json) {
		return new EithonPlayer().fromJson(json);
	}
}
