package net.eithon.library.mysql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
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

	public ResultSet select(Object... whereParts) throws ClassNotFoundException, SQLException {
		String where = joinWhereParts(whereParts);
		String sql = String.format("SELECT * FROM %s WHERE %s", this.name, where);
		PreparedStatement statement = getOrOpenConnection().prepareStatement(sql);
		fillInBlanks(statement, stringValues(whereParts));
		return statement.executeQuery();
	}

	public void delete(Object... whereParts) throws ClassNotFoundException, SQLException {
		String where = joinWhereParts(whereParts);
		String sql = String.format("DELETE FROM %s WHERE %s", this.name, where);
		PreparedStatement statement = getOrOpenConnection().prepareStatement(sql);
		fillInBlanks(statement, stringValues(whereParts));
		statement.executeUpdate();
	}

	private String joinWhereParts(Object... whereParts) {
		StringBuilder result = new StringBuilder();
		boolean firstTime = true;
		for (int i = 0; i < whereParts.length; i++) {
			if (firstTime) firstTime = false;
			else result.append(" ");
			Object part = whereParts[i];
			if (!(part instanceof String)) {
				throw new IllegalArgumentException("String expected");
			}
			String leftSide = (String) part;
			result.append(leftSide);
			i++;
			if (i >= whereParts.length) {
				throw new IllegalArgumentException("Expected an even number of wereParts");
			}
			result.append(getValueAsSqlObject(whereParts[i]));
		}
		return result.toString();
	}

	private List<String> stringValues(Object[] whereParts) {
		List<String> result = new ArrayList<String>();
		for (int i = 0; i < whereParts.length; i++) {
			Object part = whereParts[i];
			if (!(part instanceof String)) {
				throw new IllegalArgumentException("String expected");
			}
			i++;
			if (i >= whereParts.length) {
				throw new IllegalArgumentException("Expected an even number of wereParts");
			}
			Object rightSide = whereParts[i];
			if (rightSide instanceof String) result.add((String) rightSide);
		}
		return result;
	}

	public void update(HashMap<String, Object> columnValues, Object... whereParts) throws ClassNotFoundException, SQLException {
		String where = joinWhereParts(whereParts);
		String sql = String.format("UPDATE %s SET %s WHERE %s", getName(), joinAssignments(columnValues), where);
		PreparedStatement statement = getOrOpenConnection().prepareStatement(sql);
		List<String> list = stringColumnValues(columnValues);
		list.addAll(stringValues(whereParts));
		fillInBlanks(statement, list);
		statement.executeUpdate();	
	}

	public long create(HashMap<String, Object> columnValues) throws SQLException, ClassNotFoundException {
		String sql = String.format("INSERT INTO %s (%s) VALUES (%s)",
				getName(), joinColumnNames(columnValues), joinColumnValues(columnValues));
		PreparedStatement statement = getOrOpenConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
		fillInBlanks(statement, stringColumnValues(columnValues));
		statement.executeUpdate();
		ResultSet generatedKeys = statement.getGeneratedKeys();
		generatedKeys.next();
		return generatedKeys.getLong(1);
	}

	public void delete(long id) throws SQLException, ClassNotFoundException {
		String sql = String.format("DELETE FROM %s WHERE id=%d", getName(), id);
		Statement statement = getOrOpenConnection().createStatement();
		statement.executeUpdate(sql);	
	}

	private void fillInBlanks(PreparedStatement statement, List<String> stringValues) throws SQLException {
		int i=1;
		for (String stringValue : stringValues) {
			statement.setString(i++, stringValue);
		}
	}

	private List<String> stringColumnValues(HashMap<String, Object> columnValues) {
		return 
				columnValues.values().stream()
				.filter(o -> o instanceof String)
				.map(o -> (String) o)
				.collect(Collectors.toList());
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
		if (columnValue instanceof String) return "?";
		if (columnValue instanceof Boolean) return String.format("%d", ((boolean)columnValue)?1:0);
		return columnValue.toString();
	}

}

