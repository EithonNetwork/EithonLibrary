package net.eithon.library.mysql;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class DbTable {
	private String name;
	private Database database;

	public DbTable(Database database, String name) {
		this.database = database;
		this.name = name;
	}

	public Database getDatabase() {
		return this.database;
	}

	public Connection getOrOpenConnection() throws ClassNotFoundException, SQLException {
		return this.database.getOrOpenConnection();
	}

	public String getName() {
		return this.name;
	}

	public ResultSet select(String where) throws ClassNotFoundException, SQLException {
		String sql = String.format("SELECT * FROM %s WHERE %s", getName(), where);
		Statement statement = getOrOpenConnection().createStatement();
		return statement.executeQuery(sql);
	}

	public void update(HashMap<String, Object> columnValues, String where) throws ClassNotFoundException, SQLException {
		String sql = String.format("UPDATE %s SET %s WHERE %s", getName(), joinAssignments(columnValues), where);
		Statement statement = getOrOpenConnection().createStatement();
		statement.executeUpdate(sql);	
	}

	public long create(HashMap<String, Object> columnValues) throws SQLException, ClassNotFoundException {
		String sql = String.format("INSERT INTO %s (%s) VALUES (%s)",
				getName(), joinColumnNames(columnValues), joinColumnValues(columnValues));
		Statement statement = getOrOpenConnection().createStatement();
		statement.executeUpdate(sql, Statement.RETURN_GENERATED_KEYS);
		ResultSet generatedKeys = statement.getGeneratedKeys();
		generatedKeys.next();
		return generatedKeys.getLong(1);

	}

	private String joinColumnNames(HashMap<String, Object> columnValues) {
		return String.join(",", columnValues.keySet());
	}


	private String joinColumnValues(HashMap<String, Object> columnValues) {
		List<String> valueList = 
				columnValues.values().stream()
				.map(v -> getValueAsSqlObject(v))
				.collect(Collectors.toList());
		return String.join(",", valueList);
	}
	
	private String joinAssignments(HashMap<String, Object> columnValues) {
		List<String> assignments = 
				columnValues.keySet().stream()
				.map(name -> String.format("%s=%s", name, getValueAsSqlObject(columnValues.get(name))))
				.collect(Collectors.toList());
		return String.join(",", assignments);
	}

	private String getValueAsSqlObject(Object columnValue) {
		if (columnValue == null) return "NULL";
		if (columnValue instanceof String) return String.format("'%s'", columnValue);
		if (columnValue instanceof Boolean) return String.format("%d", ((boolean)columnValue)?1:0);
		return columnValue.toString();
	}

}

