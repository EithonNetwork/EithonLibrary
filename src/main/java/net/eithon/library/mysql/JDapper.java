package net.eithon.library.mysql;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import net.eithon.library.exceptions.FatalException;
import net.eithon.library.exceptions.ProgrammersErrorException;
import net.eithon.library.exceptions.TryAgainException;

public class JDapper<T> {
	private final Database database;
	private final Class<T> type;
	private final Field[] fields;
	private Constructor<T> constructor;

	public JDapper(Class<T> type, Database database) throws FatalException{
		this.type = type;
		this.fields = this.type.getFields();
		this.database = database;
		try {
			this.constructor = this.type.getConstructor();
		} catch (NoSuchMethodException | SecurityException e) {
			throw new ProgrammersErrorException(e);
		}
	}

	public List<T> readSome(String sql, Object ... objects) throws FatalException, TryAgainException {
		List<T> results = new ArrayList<T>();
		try (Connection connection = this.database.getConnection()) {
			try (PreparedStatement statement = connection.prepareStatement(sql)){
				setParameters(statement, objects);
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

	public T readFirst(String sql, Object ... objects) throws FatalException, TryAgainException {
		try (Connection connection = this.database.getConnection()) {
			try (PreparedStatement statement = connection.prepareStatement(sql)){
				setParameters(statement, objects);
				try (ResultSet resultSet = statement.executeQuery()) {
					if (!resultSet.next()) return null;
					return resultSetToObject(resultSet);
				}
			}
		} catch (SQLException e) {
			throw new FatalException(e);
		}
	}

	public T readTheOnlyOne(String sql, Object ... objects) throws FatalException, TryAgainException {
		try (Connection connection = this.database.getConnection()) {
			try (PreparedStatement statement = connection.prepareStatement(sql)){
				setParameters(statement, objects);
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

	public void update(String sql, Object ... objects) throws FatalException, TryAgainException {
		try (Connection connection = this.database.getConnection()) {
			try (PreparedStatement statement = connection.prepareStatement(sql, java.sql.Statement.RETURN_GENERATED_KEYS)){
				setParameters(statement, objects);

				int affectedRows = statement.executeUpdate();

				if (affectedRows == 0) {
					throw new ProgrammersErrorException("Saving review failed!");
				}else if (affectedRows > 1) {
					throw new ProgrammersErrorException("Unexpected result. Database returned multiple rows");
				}
			}
		} catch (SQLException e) {
			throw new FatalException(e);
		}
	}

	public void update(String tableName, T data, String where, Object... objects) throws FatalException, TryAgainException {
		StringBuilder sql = new StringBuilder();
		sql.append(String.format("UPDATE %s SET ", tableName));
		sql.append(String.join("= ?, ", getFieldNames()));
		sql.append(" WHERE ");
		sql.append(where);
		update(sql.toString(), getFieldValues(data), objects);
	}

	public int insert(String sql, Object ... objects) throws FatalException, TryAgainException {
		try (Connection connection = this.database.getConnection()) {
			try (PreparedStatement statement = connection.prepareStatement(sql, java.sql.Statement.RETURN_GENERATED_KEYS)){
				setParameters(statement, objects);

				int affectedRows = statement.executeUpdate();

				if (affectedRows == 0) {
					throw new ProgrammersErrorException("Saving review failed!");
				}else if (affectedRows > 1) {
					throw new ProgrammersErrorException("Unexpected result. Database returned multiple rows");
				}

				try (ResultSet resultSet = statement.getGeneratedKeys()){
					boolean found = resultSet.next();
					if (!found){
						throw new ProgrammersErrorException("Something is very wrong. Check to see if pigs are flying");
					}
					int newID = resultSet.getInt(1);
					return newID;
				}
			}
		} catch (SQLException e) {
			throw new FatalException(e);
		}
	}

	public int delete(String sql, Object ... objects) throws FatalException, TryAgainException {
		try (Connection connection = this.database.getConnection()) {
			try (PreparedStatement statement = connection.prepareStatement(sql)){
				setParameters(statement, objects);
				return statement.executeUpdate();
			}
		} catch (SQLException e) {
			throw new FatalException(e);
		}
	}

	private void setParameters(PreparedStatement statement, Object... objects) throws FatalException {
		int parameterIndex = 1;
		for (Object object : objects) {
			try {
				statement.setObject(parameterIndex++, object);
			} catch (SQLException e) {
				throw new FatalException(e);
			}
		}
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

	private String[] getFieldNames() {
		ArrayList<String> list = new ArrayList<String>();
		for (Field field : this.fields) {
			list.add(field.getName());
		}
		return (String[]) list.toArray();
	}

	private Object[] getFieldValues(T object) throws FatalException {
		ArrayList<Object> list = new ArrayList<Object>();
		try {
			for (Field field : this.fields) {

				list.add(field.get(object));
			}
		} catch (IllegalArgumentException | IllegalAccessException e) {
			throw new ProgrammersErrorException(e);
		}
		return (String[]) list.toArray();
	}
}
