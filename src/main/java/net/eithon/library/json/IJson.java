package net.eithon.library.json;

import net.eithon.library.core.IFactory;

public interface IJson<T> extends IFactory<T> {
	Object toJson();
	void fromJson(Object json);
}
