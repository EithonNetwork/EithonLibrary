package net.eithon.library.mysql;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


@SuppressWarnings("rawtypes")
public abstract class DbRecord<T extends DbRecord> implements IDbRecord<T> {
	private long id;
	private DbTable dbTable;
	private LocalDateTime updatedAt;

	protected DbRecord(Database database, String tableName, long id) {
		this.dbTable = DbTable.get(database, tableName, getUpdatedAtColumnName());
		this.id = id;
	}

	protected DbRecord(Database database, String tableName) {
		this(database, tableName, -1);
	}

	protected DbRecord() {
	}

	public long getDbId() { return this.id; }
	protected void setDbId(long dbId) { this.id = dbId; }
	public LocalDateTime getUpdatedAt() { return this.updatedAt; }
	
	public LocalDateTime getDatabaseNow() {
		Timestamp timestamp = null;
		try {
			timestamp = this.dbTable.getDataBaseNow();
		} catch (ClassNotFoundException | SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (timestamp == null) return null;
		return timestamp.toLocalDateTime();
	}

	protected T getById(long dbId)  {
		return getByWhere("id=?", dbId);
	}

	protected T getByWhere(String format, Object... arguments)  {
		List<T> list = findByWhere(format, arguments);
		if (list.size() == 0) return null;
		if (list.size() > 1) {
			throw new IllegalArgumentException("Get returned more than one row.");
		}
		return list.get(0);
	}

	protected List<T> findAll()  {
		return findByWhere("1=1");
	}

	protected List<T> findByWhere(String format, Object... arguments)  {
		List<T> list = new ArrayList<T>(); 
		ResultSet resultSet;
		try {
			resultSet = this.dbTable.select(format, arguments);
			while (resultSet.next()) {
				T data = factory(getDatabase(), resultSet.getLong("id"));
				data.fromDb(resultSet);
				list.add(data);
			}
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		} finally {
			closeConnection();
		}
		return list;
	}

	private void closeConnection() {
		try {
			this.dbTable.getDatabase().closeConnection();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void refresh()  {
		try {
			ResultSet resultSet = this.dbTable.select("id=?", getDbId());
			if ((resultSet == null) || !resultSet.next()) return;
			this.fromDb(resultSet);
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		} finally {
			closeConnection();
		}
	}

	protected void deleteByWhere(String format, Object... arguments)  {
		try {
			this.dbTable.delete(format, arguments);
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		} finally {
			closeConnection();
		}
	}

	protected void dbUpdate() {		
		try {
			this.dbTable.update(getColumnValues(), "id=?", this.id);
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		} finally {
			closeConnection();
		}
	}

	protected void dbCreate() {	
		try {
			this.id = this.dbTable.create(getColumnValues());
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		} finally {
			closeConnection();
		}
	}

	protected Database getDatabase() {
		// TODO Auto-generated method stub
		return this.dbTable.getDatabase();
	}

	protected String getTableName() {
		// TODO Auto-generated method stub
		return this.dbTable.getName();
	}

	public void delete() {
		try {
			this.dbTable.delete(this.id);
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings("unchecked")
	public T fromDb(ResultSet resultSet) throws SQLException {
		if (this.dbTable.hasUpdatedAtColumn()) {
			this.updatedAt = null;
			Timestamp timestamp = resultSet.getTimestamp("updated_at");
			if (timestamp != null) this.updatedAt = timestamp.toLocalDateTime();
		}
		return (T) this;
	}

	public abstract T factory(Database database, long id);
	public abstract HashMap<String, Object> getColumnValues();
	public String getUpdatedAtColumnName() { return null; }
}
