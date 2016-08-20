package net.eithon.library.mysql;

import java.util.List;

import net.eithon.library.exceptions.FatalException;
import net.eithon.library.exceptions.TryAgainException;

public abstract class DbTable<T extends IRow> {
	protected JDapper<T> jDapper;	
	
	public DbTable(Class<T> type, final Database database) throws FatalException {
		this.jDapper = new JDapper<T>(type, database);
	}

	public T get(final long id) throws FatalException, TryAgainException {
		return this.jDapper.read(id);
	}

	public List<T> findAll() throws FatalException, TryAgainException {
		return this.jDapper.readSomeWhere("1=1");
	}
	
	public void update(T data) throws FatalException, TryAgainException {
		this.jDapper.updateWhere(data, "id = ?", data.getId());
	}

	public void delete(long id) throws FatalException, TryAgainException {
		this.jDapper.delete(id);
	}
}
