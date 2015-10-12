package net.eithon.library.bungee;

import java.time.LocalDateTime;
import java.util.UUID;

import net.eithon.library.json.IJson;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

public class JoinQuitInfo  implements IJson<JoinQuitInfo>{
	private String _mainGroup;
	private String _serverName;
	private UUID _playerId;
	private LocalDateTime _sendTime;
	
	public JoinQuitInfo() {
		this._mainGroup = null;
		this._serverName = null;
		this._playerId = null;
		this._sendTime = null;
	}
	
	public JoinQuitInfo(String serverName, UUID playerId, String mainGroup) {
		this._serverName = serverName;
		this._playerId = playerId;
		this._mainGroup = mainGroup;
		this._sendTime = null;
	}
	
	public String getMainGroup() { return this._mainGroup; }
	public String getServerName() { return this._serverName; }
	public UUID getPlayerId() { return this._playerId; }
	public LocalDateTime getSendTime() { return this._sendTime; }

	public boolean isTooOld() {
		if (this._sendTime == null) return false;
		return LocalDateTime.now().minusSeconds(30).isAfter(this._sendTime);
	}
	
	public String toJSONString() {
		return ((JSONObject) toJson()).toJSONString();
	}
	
	public static JoinQuitInfo getFromJsonString(String jsonString) {
		JSONObject jsonObject = (JSONObject) JSONValue.parse(jsonString);
		return getFromJson(jsonObject);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Object toJson() {
		JSONObject json = new JSONObject();
		json.put("serverName", this._serverName);
		json.put("mainGroup", this._mainGroup);
		json.put("playerId", this._playerId == null ? null : this._playerId.toString());
		json.put("sendTime", LocalDateTime.now().toString());
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
		this._sendTime = null;
		String sendTime = (String) jsonObject.get("sendTime");
		if (sendTime != null) {
			this._sendTime = LocalDateTime.parse(sendTime);
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
