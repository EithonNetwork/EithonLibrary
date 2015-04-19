package net.eithon.library.extensions;

import java.util.UUID;

import net.eithon.library.plugin.GeneralMessage;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class EithonPlayer {

	private Player _player;
	
	public EithonPlayer(Player player) { this._player = player; }
	
	public Player getPlayer() { return this._player; }	

	@SuppressWarnings("deprecation")
	public static EithonPlayer getFromString(String playerIdOrName) {
		Player player = null;
		try {
			UUID id = UUID.fromString(playerIdOrName);
			player = Bukkit.getPlayer(id);
		} catch (Exception e) { }
		if (player == null) try { player = Bukkit.getPlayer(playerIdOrName); } catch (Exception e) { }
		if (player == null) return null;
		return new EithonPlayer(player);
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
}
