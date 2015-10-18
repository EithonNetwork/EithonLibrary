package net.eithon.library.json;

import org.json.simple.JSONObject;

public interface IJsonObjectDelta<T> {
	JSONObject toJsonObjectDelta(boolean saveAll);
}
