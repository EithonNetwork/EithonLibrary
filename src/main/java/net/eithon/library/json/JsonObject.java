package net.eithon.library.json;

import org.json.simple.JSONObject;

public abstract class JsonObject<T> implements IJson<T>, IJsonObject<T> {
	@Override
	public JSONObject toJsonObject() {
		Object json = toJson();
		return (JSONObject) json;
	}

	@Override
	public String toJsonString() {
		return toJsonObject().toJSONString();
	}
	
	@Override
	public T fromJsonObject(JSONObject jsonObject) {
		return fromJson(jsonObject);
	}
}
