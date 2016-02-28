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
		String where = String.format("id=%d", dbId);
		return getByWhere(where);
	}
	
	protected T getByWhere(String where)  {
		List<T> list = findByWhere(where);
		if (list.size() == 0) return null;
		if (list.size() > 1) {
			throw new IllegalArgumentException(String.format("WHERE %s returned more than one row.", where));
		}
		return list.get(0);
	}
	
	public List<T> findAll()  {
		return findByWhere("1=1");
	}
	
	protected List<T> findByWhere(String where)  {
		List<T> list = new ArrayList<T>(); 
		ResultSet resultSet;
		try {
			resultSet = this.dbTable.select(where);
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
		String where = String.format("id=%d", this.id);
		try {
			this.dbTable.update(getColumnValues(), where);
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
