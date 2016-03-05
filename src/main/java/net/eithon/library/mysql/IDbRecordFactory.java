package net.eithon.library.mysql;

public interface IDbRecordFactory<T> {
	T factory(DbTable table, long id);
}
