package net.eithon.library.json;

import org.json.simple.JSONObject;

public abstract class JsonObjectDelta<T> extends JsonObject<T> implements IJsonDelta<T>, IJsonObjectDelta<T> {
	@Override
	public JSONObject toJsonObjectDelta(boolean saveAll) {
		Object json = toJsonDelta(saveAll);
		return (JSONObject) json;
	}
}
