package net.eithon.library.json;


public interface IJsonDelta<T> extends IJson<T> {
	Object toJsonDelta(boolean saveAll);
}
