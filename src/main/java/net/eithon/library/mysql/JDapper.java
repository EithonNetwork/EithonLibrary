package net.eithon.library.mysql;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import net.eithon.library.exceptions.FatalException;
import net.eithon.library.exceptions.ProgrammersErrorException;
import net.eithon.library.exceptions.TryAgainException;

public class JDapper<T extends IRow> {
	private final Database database;
	private final Class<T> type;
	private final Field[] fields;
	private Constructor<T> constructor;
	private final T example;

	public JDapper(Class<T> type, Database database) throws FatalException{
		this.type = type;
		this.fields = this.type.getFields();
		this.database = database;
		try {
			this.constructor = this.type.getConstructor();
		} catch (NoSuchMethodException | SecurityException e) {
			throw new ProgrammersErrorException(e);
		}
		try {
			this.example = this.constructor.newInstance();
		} catch (InstantiationException | IllegalAccessException
				| IllegalArgumentException | InvocationTargetException e) {
			throw new ProgrammersErrorException("Could not instantiate the JDapper<> parameter class.", e);
		}
	}

	public long createOne(T data) throws FatalException, TryAgainException {
		StringBuilder sql = new StringBuilder();
		sql.append(String.format("INSERT INTO %s (", this.example.getTableName()));
		final String[] notNullFieldNames = getNotNullFieldName(data, false);
		sql.append(String.join(", ", notNullFieldNames));
		sql.append(") VALUES (");
		sql.append(String.join(", ", Stream.of(notNullFieldNames)
				.map(n -> "?")
				.collect(Collectors.toList())));
		sql.append(")");
		return this.database.executeInsert(sql.toString(), getNotNullFieldValues(data, false));
	}

	public List<T> readSomeWhere(String where, Object ... objects) throws FatalException, TryAgainException {
		String sql = String.format("SELECT * FROM %s WHERE %s", this.example.getTableName(), where);
		return readSome2(sql, objects);
	}

	public List<T> readSome2(String sql, Object ... objects) throws FatalException, TryAgainException {
		List<T> results = new ArrayList<T>();
		try (Connection connection = this.database.getConnection()) {
			try (PreparedStatement statement = connection.prepareStatement(sql)){
				Database.setParameters(statement, objects);
				try (ResultSet resultSet = statement.executeQuery()) {
					while (resultSet.next()) {
						results.add(resultSetToObject(resultSet));
					}
					return results;
				}
			}
		} catch (SQLException e) {
			throw new FatalException(e);
		}
	}

	public T readFirstWhere(String where, Object ... objects) throws FatalException, TryAgainException {
		String sql = String.format("SELECT * FROM %s WHERE %s", this.example.getTableName(), where);
		return readFirst(sql, objects);
	}

	public T readFirst(String sql, Object ... objects) throws FatalException, TryAgainException {
		try (Connection connection = this.database.getConnection()) {
			try (PreparedStatement statement = connection.prepareStatement(sql)){
				Database.setParameters(statement, objects);
				try (ResultSet resultSet = statement.executeQuery()) {
					if (!resultSet.next()) return null;
					return resultSetToObject(resultSet);
				}
			}
		} catch (SQLException e) {
			throw new FatalException(e);
		}
	}

	public T read(long id) throws FatalException, TryAgainException {
		return readTheOnlyOneWhere("id=?", id);
	}

	public T readTheOnlyOneWhere(String where, Object ... objects) throws FatalException, TryAgainException {
		String sql = String.format("SELECT * FROM %s WHERE %s", this.example.getTableName(), where);
		return readTheOnlyOne(sql, objects);
	}

	public T readTheOnlyOne(String sql, Object ... objects) throws FatalException, TryAgainException {
		try (Connection connection = this.database.getConnection()) {
			try (PreparedStatement statement = connection.prepareStatement(sql)){
				Database.setParameters(statement, objects);
				try (ResultSet resultSet = statement.executeQuery()) {
					if (!resultSet.next()) return null;
					T object = resultSetToObject(resultSet);
					if (resultSet.next()) {
						throw new ProgrammersErrorException(
								String.format("More than one row was returned for \"%s\".", sql));
					}
					return object;
				}
			}
		} catch (SQLException e) {
			throw new FatalException(e);
		}
	}

	public int updateWhere(T data, String where, Object... objects) throws FatalException, TryAgainException {
		String sql = String.format(
				"UPDATE %s SET %s WHERE %s", 
				this.example.getTableName(), getUpdateSet(data), where);
		ArrayList<Object> list = new ArrayList<Object>();
		list.addAll(Arrays.asList(getNotNullFieldValues(data, false)));
		list.addAll(Arrays.asList(objects));
		return this.database.executeUpdate(sql, list.toArray(new Object[]{}));
	}

	private String getUpdateSet(T data) {
		ArrayList<String> list = new ArrayList<String>();
		for (Field field : this.fields) {
			final String fieldName = field.getName();
			if (fieldName.equals("id")) continue;
			try {
				final Object value = field.get(data);
				String setColumn = null;
				if (value == null) {
					setColumn = fieldName + " = NULL";
				} else {
					setColumn = fieldName + " = ?";
				}
				list.add(setColumn);
			} catch (Exception e) {
				// Ignore fields with problems
			}
		}
		return String.join(", ", list);
	}

	public int delete(long id) throws FatalException, TryAgainException {
		return deleteWhere("id = ?", id);
	}

	public int deleteWhere(String where, Object ... objects) throws FatalException, TryAgainException {
		String sql = String.format("DELETE FROM %s WHERE %s", this.example.getTableName(), where);
		return this.database.executeUpdate(sql, objects);
	}

	private T resultSetToObject(ResultSet resultSet) throws FatalException, SQLException {
		T object;
		try {
			object = this.constructor.newInstance();
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
				| InvocationTargetException e1) {
			throw new ProgrammersErrorException(e1);
		}

		for (Field field : this.fields) {
			String fieldName = field.getName();
			int columnNumber ;
			try {
				columnNumber = resultSet.findColumn(fieldName);
			} catch (SQLException e) {
				throw new ProgrammersErrorException(String.format("Expected to find database column name %s.", fieldName), e);
			}
			Object data = resultSet.getObject(columnNumber);
			try {
				field.set(object, data);
			} catch (IllegalArgumentException | IllegalAccessException e) {
				throw new ProgrammersErrorException(e);
			}
		}
		return object;
	}

	private String[] getFieldNames(boolean includeId) {
		ArrayList<String> list = new ArrayList<String>();
		for (Field field : this.fields) {
			final String fieldName = field.getName();
			if (!includeId && fieldName.equals("id")) continue;
			list.add(fieldName);
		}
		return list.toArray(new String[] {});
	}

	private String[] getNotNullFieldName(Object object, boolean includeId) {
		ArrayList<String> list = new ArrayList<String>();
		for (Field field : this.fields) {
			try {
				if (field.get(object) != null) {
					final String fieldName = field.getName();
					if (!includeId && fieldName.equals("id")) continue;
					list.add(fieldName);
				}
			} catch (Exception e) {
				// We will ignore fields with errors.
			}
		}
		return list.toArray(new String[] {});
	}

	private Object[] getFieldValues(T object, boolean includeId) throws FatalException {
		ArrayList<Object> list = new ArrayList<Object>();
		try {
			for (Field field : this.fields) {
				final String fieldName = field.getName();
				if (!includeId && fieldName.equals("id")) continue;
				list.add(field.get(object));
			}
		} catch (IllegalArgumentException | IllegalAccessException e) {
			throw new ProgrammersErrorException(e);
		}
		return list.toArray(new Object[] {});
	}

	private Object[] getNotNullFieldValues(T object, boolean includeId) throws FatalException {
		ArrayList<Object> list = new ArrayList<Object>();
		try {
			for (Field field : this.fields) {
				try {
					final String fieldName = field.getName();
					if (!includeId && fieldName.equals("id")) continue;
					final Object fieldValue = field.get(object);
					if (fieldValue != null) list.add(fieldValue);
				} catch (Exception e) {
					// We will ignore fields with errors.
				}
			}
		} catch (IllegalArgumentException e) {
			throw new ProgrammersErrorException(e);
		}
		return list.toArray(new Object[] {});
	}
}
