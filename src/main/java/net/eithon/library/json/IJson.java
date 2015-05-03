package net.eithon.library.json;

import net.eithon.library.core.IFactory;

public interface IJson<T> extends IFactory<T> {
	Object toJson();
	T fromJson(Object json);
}
