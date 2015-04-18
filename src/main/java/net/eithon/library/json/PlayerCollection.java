package net.eithon.library.json;

import java.io.Serializable;
import java.util.HashMap;
import java.util.UUID;

import net.eithon.library.core.IFactory;
import net.eithon.library.core.IUuidAndName;
import net.eithon.library.plugin.Logger;

import org.json.simple.JSONArray;

public class PlayerCollection<T extends IJson<T> & IUuidAndName>
extends net.eithon.library.core.PlayerCollection<T> 
implements Iterable<T>, IJson<PlayerCollection<T>>, Serializable 
{
	private static final long serialVersionUID = 1L;
	private HashMap<UUID, T> playerInfo = null;
	private T _instance;

	public PlayerCollection(IFactory<T> factory) {
		this.playerInfo = new HashMap<UUID, T>();
		this._instance = factory.factory();
	}
	
	public PlayerCollection(T instance) {
		this.playerInfo = new HashMap<UUID, T>();
		this._instance = instance;
	}
	
	@SuppressWarnings("unchecked")
	public Object toJson() {
		JSONArray json = new JSONArray();
		for (T value : this.playerInfo.values()) {
			if (!(value instanceof IJson<?>)) {
				Logger.libraryError("%s must implement interface J", value.toString());
				return null;
			}
			IJson<T> info = (IJson<T>) value;
			json.add(info.toJson());
		}
		return json;
	}
	
	@Override
	public void fromJson(Object json) {
		JSONArray jsonArray = (JSONArray) json;
		HashMap<UUID, T> playerInfo = new HashMap<UUID, T>();
		for (Object o : jsonArray) {
			T info = this._instance.factory();
			info.fromJson(o);
			playerInfo.put(info.getUniqueId(), info);
		}
		this.playerInfo = playerInfo;
	}

	@Override
	public PlayerCollection<T> factory() {
		return new PlayerCollection<T>(this._instance);
	}
}