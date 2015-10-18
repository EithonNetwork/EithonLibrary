package net.eithon.library.json;

import org.json.simple.JSONObject;

public interface IJsonObject<T> {
	JSONObject toJsonObject();
	String toJsonString();
	T fromJsonObject(JSONObject json);
}
