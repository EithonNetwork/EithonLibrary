package net.eithon.library.mysql;

import net.eithon.library.core.IFactory;

public interface IRowMapper<TModel, TRow> extends IFactory<TModel> {
	TRow toRow();
	TModel fromRow(TRow row);
}
