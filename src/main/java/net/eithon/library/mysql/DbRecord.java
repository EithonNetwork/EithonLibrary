package net.eithon.library.mysql;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


@SuppressWarnings("rawtypes")
public abstract class DbRecord<T extends DbRecord & IDbRecord<T>> {
	private long id;
	private DbTable dbTable;
	
	protected DbRecord(DbTable dbTable, long id) {
		this.dbTable = dbTable;
		this.id = id;
	}
	
	protected DbRecord(DbTable dbTable) {
		this(dbTable, -1);
	}

	public DbRecord() {
	}

	public long getDbId() { return this.id; }
	protected void setDbId(long dbId) { this.id = dbId; }
	
	protected T getById(long dbId)  {
		return getByWhere("id=", dbId);
	}
	
	protected T getByWhere(Object... whereParts)  {
		List<T> list = findByWhere(whereParts);
		if (list.size() == 0) return null;
		if (list.size() > 1) {
			throw new IllegalArgumentException(String.format("WHERE %s returned more than one row.", whereParts));
		}
		return list.get(0);
	}
	
	public List<T> findAll()  {
		return findByWhere("1=", 1);
	}
	
	protected List<T> findByWhere(Object... whereParts)  {
		List<T> list = new ArrayList<T>(); 
		ResultSet resultSet;
		try {
			resultSet = this.dbTable.select(whereParts);
			while (resultSet.next()) {
				T data = factory(this.dbTable, resultSet.getLong("id"));
				data.fromDb(resultSet);
				list.add(data);
			}
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}
		return list;
	}

	public void dbUpdate() {		
		try {
			this.dbTable.update(getColumnValues(), "id=", this.id);
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}
	}

	public void dbCreate() {	
		try {
			this.id = this.dbTable.create(getColumnValues());
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}
	}
	public abstract T factory(DbTable table, long id);
	public abstract HashMap<String, Object> getColumnValues();
}
