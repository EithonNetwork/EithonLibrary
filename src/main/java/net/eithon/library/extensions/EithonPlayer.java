package net.eithon.library.extensions;

import java.util.UUID;

import net.eithon.library.core.IUuidAndName;
import net.eithon.library.json.IJson;
import net.eithon.library.plugin.GeneralMessage;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.json.simple.JSONObject;

public class EithonPlayer implements IJson<EithonPlayer>, IUuidAndName{

	private Player _player = null;
	private UUID _id = null;
	private String _name = null;

	public EithonPlayer(Player player) { 
		this._player = player; 
		if (player != null) {
			this._id = player.getUniqueId();
			this._name = player.getName();
		}
	}

	EithonPlayer() {
	}

	@Override
	public UUID getUniqueId() { return this._id; }

	@Override
	public String getName() { return this._name; }

	@SuppressWarnings("deprecation")
	public Player getPlayer() { 
		if (this._player != null) return this._player;
		this._player = Bukkit.getPlayer(this._id);
		if (this._player != null) return this._player;
		this._player = Bukkit.getPlayer(this._name);			
		return this._player;
	}	

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
	public void fromJson(Object json) {
		JSONObject jsonObject = (JSONObject) json;
		this._id = UUID.fromString((String) jsonObject.get("id"));
		this._name = (String) jsonObject.get("name");
	}
}
