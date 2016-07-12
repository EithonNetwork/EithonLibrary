package net.eithon.library.mysql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

class DbTable<T extends DbRecord<T>> {
	private String name;
	private Database database;
	private String updatedAtColumnName;

	private static HashMap<String, DbTable<?>> knownTables = new HashMap<String, DbTable<?>>();

	@SuppressWarnings("rawtypes")
	public static DbTable<?> get(Database database, String name, String updatedAtColumnName) {
		String hashableString = getHashableString(database, name);
		DbTable<?> table = knownTables.get(hashableString);
		if (table == null) {
			table = (DbTable<?>) new DbTable(database, name, updatedAtColumnName);
			knownTables.put(hashableString, table);
		}
		return table;
	}

	@Override
	public int hashCode() {
		return getHashableString(this.database, this.name).hashCode();
	}

	private static String getHashableString(Database database, String name) {
		return database.toString() + ":" + name;
	}

	private DbTable(Database database, String name, String updatedAtColumnName) {
		this.database = database;
		this.name = name;
		this.updatedAtColumnName = updatedAtColumnName;
	}

	public Database getDatabase() {
		return this.database;
	}

	protected Connection getConnection() {
		Class.forName("com.mysql.jdbc.Driver");
		return DriverManager.getConnection(_connectionString,
				this.user, this.password);
	}


	public String getName() {
		return this.name;
	}

	public List<T> select(DbRecord<T> dbRecord, String whereFormat, Object... arguments) throws ClassNotFoundException, SQLException {
		try (Connection c = getConnection()) {
			String sql = String.format("SELECT * FROM %s WHERE %s", this.name, whereFormat);
			try (PreparedStatement statement = c.prepareStatement(sql)) {
				fillInBlanks(statement, toStringValueList(arguments));
				try (ResultSet resultSet = statement.executeQuery()) { 
					List<T> list = new ArrayList<T>(); 
					while (resultSet.next()) {
						T data = dbRecord.factory(this.database, resultSet.getLong("id"));
						data.fromDb(resultSet);
						list.add(data);
					}
					return list;
				} 
			}
		}
	}

	public boolean selectInto(DbRecord<T> dbRecord, String whereFormat, long l) throws ClassNotFoundException, SQLException {
		try (Connection c = getConnection()) {
			String sql = String.format("SELECT * FROM %s WHERE %s", this.name, whereFormat);
			try (PreparedStatement statement = c.prepareStatement(sql)) {
				fillInBlanks(statement, toStringValueList(l));
				try (ResultSet resultSet = statement.executeQuery()) { 
					if (!resultSet.next()) return false;
					dbRecord.fromDb(resultSet);
					if (!resultSet.next()) return true;
					throw new IllegalArgumentException(String.format("SELECT statement with WHERE \"%s\" unexpectedly returned more than one row."));
				}
			}
		}
	}

	public void delete(String whereFormat, Object... arguments) throws ClassNotFoundException, SQLException {
		String sql = String.format("DELETE FROM %s WHERE %s", this.name, whereFormat);
		PreparedStatement statement = null;
		synchronized (this.database) {
			try {
				statement = getOrOpenConnection().prepareStatement(sql);
				fillInBlanks(statement, toStringValueList(arguments));
				statement.executeUpdate();
			} finally {
				closeQuitely(statement);
				closeQuitely();
			}
		}
	}

	public void update(HashMap<String, Object> columnValues, String whereFormat, Object... arguments) throws ClassNotFoundException, SQLException {
		String sql = String.format("UPDATE %s SET %s WHERE %s", getName(), joinAssignments(columnValues), whereFormat);
		PreparedStatement statement = null;
		synchronized (this.database) {
			try {
				statement = getOrOpenConnection().prepareStatement(sql);
				List<String> list = stringColumnValues(columnValues);
				list.addAll(toStringValueList(arguments));
				fillInBlanks(statement, list);
				statement.executeUpdate();	
			} finally {
				closeQuitely(statement);
				closeQuitely();
			}
		}
	}

	public long create(Map<String, Object> columnValues) throws SQLException, ClassNotFoundException {
		String sql = String.format("INSERT INTO %s (%s) VALUES (%s)",
				getName(), joinColumnNames(columnValues), joinColumnValues(columnValues));
		PreparedStatement statement = null;
		synchronized (this.database) {
			try {
				statement = getOrOpenConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
				fillInBlanks(statement, stringColumnValues(columnValues));
				statement.executeUpdate();
				ResultSet generatedKeys = statement.getGeneratedKeys();
				generatedKeys.next();
				return generatedKeys.getLong(1);
			} finally {
				closeQuitely(statement);
				closeQuitely();
			}
		}
	}

	public void delete(long id) throws SQLException, ClassNotFoundException {
		String sql = String.format("DELETE FROM %s WHERE id=%d", getName(), id);
		Statement statement = null;
		synchronized (this.database) {
			try {
				statement = getOrOpenConnection().createStatement();
				statement.executeUpdate(sql);
			} finally {
				closeQuitely(statement);
				closeQuitely();
			}
		}
	}

	private List<String> toStringValueList(Object... arguments) {
		List<String> result = new ArrayList<String>();
		for (int i = 0; i < arguments.length; i++) {
			String value = arguments[i] == null ? "NULL" : arguments[i].toString();
			result.add(value);
		}
		return result;
	}

	private void fillInBlanks(PreparedStatement statement, List<String> stringValues) throws SQLException {
		int i=1;
		for (String stringValue : stringValues) {
			statement.setString(i++, stringValue);
		}
	}

	private List<String> stringColumnValues(Map<String, Object> columnValues) {
		return 
				columnValues.values().stream()
				.filter(o -> o instanceof String)
				.map(o -> (String) o)
				.collect(Collectors.toList());
	}

	private String joinColumnNames(Map<String, Object> columnValues) {
		return String.join(",", columnValues.keySet());
	}


	private String joinColumnValues(Map<String, Object> columnValues) {
		List<String> valueList = 
				columnValues.values().stream()
				.map(v -> getValueAsSqlObject(v))
				.collect(Collectors.toList());
		return String.join(",", valueList);
	}

	private String joinAssignments(Map<String, Object> columnValues) {
		List<String> assignments = 
				columnValues.keySet().stream()
				.map(name -> String.format("%s=%s", name, getValueAsSqlObject(columnValues.get(name))))
				.collect(Collectors.toList());
		if (hasUpdatedAtColumn()) {
			assignments.add(String.format("%s=NOW()", this.updatedAtColumnName));
		}
		return String.join(",", assignments);
	}

	private void closeQuitely(Statement x) {
		try { x.close(); } catch (Exception e) { /* ignored */ }
	}

	private void closeQuitely(ResultSet x) {
		try { x.close(); } catch (Exception e) { /* ignored */ }
	}

	private void closeQuitely() {
		try { closeConnection(); } catch (Exception e) { /* ignored */ }
	}

	public boolean hasUpdatedAtColumn() {
		return this.updatedAtColumnName != null;
	}

	private String getValueAsSqlObject(Object columnValue) {
		if (columnValue == null) return "NULL";
		if (columnValue instanceof String) return "?";
		if (columnValue instanceof Boolean) return String.format("%d", ((boolean)columnValue)?1:0);
		return columnValue.toString();
	}

}

