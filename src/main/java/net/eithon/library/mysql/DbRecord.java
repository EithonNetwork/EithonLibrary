package net.eithon.library.mysql;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang.NotImplementedException;


// TODO: Change to DbRecord<T extends IDbRecord<T>> implements IDbRecord<T>
// Probably need to rebuild all database Plugins after that.
public abstract class DbRecord<T extends DbRecord<T>> implements IDbRecord<T> {
	private long id;
	private DbTable<T> dbTable;
	private LocalDateTime updatedAt;

	@SuppressWarnings("unchecked")
	protected DbRecord(Database database, String tableName, long id) {
		this.dbTable = (DbTable<T>) DbTable.get(database, tableName, getUpdatedAtColumnName());
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
		try {
			return this.dbTable.select(this, format, arguments);
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
			return new ArrayList<T>() ;
		}
	}

	public void refresh()  {
		try {
			if (this.dbTable.selectInto(this, "id=?", getDbId())) return;
			throw new NotImplementedException(String.format("Could not refresh id %d in table %s.",
					getDbId(), this.dbTable.toString()));
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}
	}

	protected void deleteByWhere(String format, Object... arguments)  {
		try {
			this.dbTable.delete(format, arguments);
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}
	}

	protected void dbUpdate() {	
		try {
			this.dbTable.update(getColumnValues(), "id=?", this.id);
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}
	}

	protected void dbCreate() {	
		try {
			this.id = this.dbTable.create(getColumnValues());
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}
	}

	protected Database getDatabase() {
		return this.dbTable.getDatabase();
	}

	protected String getTableName() {
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
