package net.eithon.library.extensions;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import net.eithon.library.core.CoreMisc;
import net.eithon.library.core.IUuidAndName;
import net.eithon.library.json.IJson;
import net.eithon.library.plugin.GeneralMessage;
import net.eithon.plugin.eithonlibrary.Config;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.plugin.Plugin;
import org.json.simple.JSONObject;

public class EithonPlayer implements CommandSender, IJson<EithonPlayer>, IUuidAndName{

	private Player _player = null;
	private OfflinePlayer _offlinePlayer = null;
	private UUID _id = null;
	public EithonPlayer(Player player) { 
		this._player = player;
		this._offlinePlayer = player;
		if (player != null) {
			this._id = player.getUniqueId();
			verifyPlayerIsOnline();
		}
	}

	public EithonPlayer(OfflinePlayer player) { 
		this._player = null;
		this._offlinePlayer = player;
		if (player != null) {
			this._id = player.getUniqueId();
			verifyPlayerIsOnline();
		}
	}

	@Deprecated
	public EithonPlayer(UUID id, String name) { 
		this._id = id;
		verifyPlayerIsOnline();
	}

	public EithonPlayer(UUID id) { 
		this._id = id;
		verifyPlayerIsOnline();
	}

	EithonPlayer() {
	}

	@Override
	public UUID getUniqueId() { return this._id; }

	@Override
	public String getName() { return this._offlinePlayer.getName(); }
	
	public boolean isOnline() { return verifyPlayerIsOnline(); }

	public Player getPlayer() {
		verifyPlayerIsOnline();
		return this._player;
	}
	
	public OfflinePlayer getOfflinePlayer() {
		return this._offlinePlayer;
	}

	private boolean verifyPlayerIsOnline() {
		if (this._player == null) {
			// Check if the player is online now
			this._player = Bukkit.getPlayer(this._id);
			if (this._player != null) {
				this._offlinePlayer = this._player;
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
			return new EithonPlayer(id);
		} catch (Exception e) { }
		try { player = Bukkit.getPlayer(playerIdOrName); } catch (Exception e) { }
		if (player != null) return new EithonPlayer(player);
		
		OfflinePlayer offlinePlayer = null;
		try { offlinePlayer = Bukkit.getOfflinePlayer(playerIdOrName); } catch (Exception e) { }
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
		if (isInAcceptableWorld(acceptableWorlds)) return true;
		Config.M.expectedWorlds.sendMessage(getPlayer(), getPlayer().getWorld().getName(),
				CoreMisc.arrayToString(acceptableWorlds.toArray(new String[0])));
		return false;
	}

	public boolean isInAcceptableWorld(List<String> acceptableWorlds) {
		String worldName = getPlayer().getWorld().getName();
		for (String acceptableWorldName : acceptableWorlds) {
			if (worldName.equalsIgnoreCase(acceptableWorldName)) return true;
		}
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
		this.verifyPlayerIsOnline();
		return this;
	}

	@Deprecated // Misspelled; Use getFromJson
	public static EithonPlayer getFromJSon(Object json) {
		return new EithonPlayer().fromJson(json);
	}

	public static EithonPlayer getFromJson(Object json) {
		return new EithonPlayer().fromJson(json);
	}

	@Override
	public PermissionAttachment addAttachment(Plugin arg0) {
		Player player = getPlayer();
		if (player == null) return null;
		return player.addAttachment(arg0);
	}

	@Override
	public PermissionAttachment addAttachment(Plugin arg0, int arg1) {
		Player player = getPlayer();
		if (player == null) return null;
		return player.addAttachment(arg0, arg1);
	}

	@Override
	public PermissionAttachment addAttachment(Plugin arg0, String arg1,
			boolean arg2) {
		Player player = getPlayer();
		if (player == null) return null;
		return player.addAttachment(arg0, arg1, arg2);
	}

	@Override
	public PermissionAttachment addAttachment(Plugin arg0, String arg1,
			boolean arg2, int arg3) {
		Player player = getPlayer();
		if (player == null) return null;
		return player.addAttachment(arg0, arg1, arg2, arg3);
	}

	@Override
	public Set<PermissionAttachmentInfo> getEffectivePermissions() {
		Player player = getPlayer();
		if (player == null) return null;
		return player.getEffectivePermissions();
	}

	@Override
	public boolean hasPermission(Permission arg0) {
		Player player = getPlayer();
		if (player == null) return false;
		return player.hasPermission(arg0);
	}

	@Override
	public boolean isPermissionSet(String arg0) {
		Player player = getPlayer();
		if (player == null) return false;
		return player.isPermissionSet(arg0);
	}

	@Override
	public boolean isPermissionSet(Permission arg0) {
		Player player = getPlayer();
		if (player == null) return false;
		return player.isPermissionSet(arg0);
	}

	@Override
	public void recalculatePermissions() {
		Player player = getPlayer();
		if (player == null) return;
		player.recalculatePermissions();
	}

	@Override
	public void removeAttachment(PermissionAttachment arg0) {
		Player player = getPlayer();
		if (player == null) return;
		player.removeAttachment(arg0);
		
	}

	@Override
	public boolean isOp() {
		Player player = getPlayer();
		if (player == null) return false;
		return player.isOp();
	}

	@Override
	public void setOp(boolean arg0) {
		Player player = getPlayer();
		if (player == null) return;
		player.setOp(arg0);		
	}

	@Override
	public Server getServer() {
		Player player = getPlayer();
		if (player == null) return null;
		return player.getServer();
	}

	@Override
	public void sendMessage(String arg0) {
		Player player = getPlayer();
		if (player == null) return;
		player.sendMessage(arg0);
	}

	@Override
	public void sendMessage(String[] arg0) {
		Player player = getPlayer();
		if (player == null) return;
		player.sendMessage(arg0);
	}
}
