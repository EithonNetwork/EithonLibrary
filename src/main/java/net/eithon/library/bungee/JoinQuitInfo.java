package net.eithon.library.bungee;

import java.util.UUID;

import net.eithon.library.json.IJson;

import org.json.simple.JSONObject;

public class JoinQuitInfo  implements IJson<JoinQuitInfo>{
	private String _mainGroup;
	private String _serverName;
	private UUID _playerId;
	
	public JoinQuitInfo() {
		this._mainGroup = null;
		this._serverName = null;
		this._playerId = null;
	}
	
	public JoinQuitInfo(String serverName, UUID playerId, String mainGroup) {
		this._serverName = serverName;
		this._playerId = playerId;
		this._mainGroup = mainGroup;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Object toJson() {
		JSONObject json = new JSONObject();
		json.put("serverName", this._serverName);
		json.put("mainGroup", this._mainGroup);
		json.put("playerId", this._playerId == null ? null : this._playerId.toString());
		return json;
	}

	@Override
	public JoinQuitInfo fromJson(Object json) {
		JSONObject jsonObject = (JSONObject) json;
		if (jsonObject == null) return null;
		this._serverName = (String) jsonObject.get("serverName");
		this._mainGroup = (String) jsonObject.get("mainGroup");
		this._playerId = null;
		String uuid = (String) jsonObject.get("playerId");
		if (uuid != null) {
			this._playerId = UUID.fromString(uuid);
		}
		return this;
	}

	@Override
	public JoinQuitInfo factory() {
		return new JoinQuitInfo();
	}

	public static JoinQuitInfo getFromJson(Object json) {
		JoinQuitInfo info = new JoinQuitInfo();
		return info.fromJson(json);
	}
}
