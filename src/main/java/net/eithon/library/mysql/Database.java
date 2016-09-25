package net.eithon.library.mysql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import net.eithon.library.exceptions.FatalException;
import net.eithon.library.exceptions.ProgrammersErrorException;
import net.eithon.library.exceptions.TryAgainException;
import net.eithon.library.plugin.Logger;

public class Database {
	private String connectionUrl;
	private String connectionUser;
	private String connectionPassword;
	private String driver = "com.mysql.jdbc.Driver";

	public Database(String connectionUrl, String userName, String password){
		this.connectionUrl = connectionUrl;
		this.connectionUser = userName;
		this.connectionPassword = password;
	}

	public Database(String hostName, String port, String databaseName, String userName, String password){
		this("jdbc:mysql://" + hostName + ":" + port + "/" + databaseName, userName, password);
	}

	public Connection getConnection() throws TryAgainException, FatalException{
		try {
			Class.forName(this.driver).newInstance();
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
			throw new FatalException(e);
		}
		try {
			Connection connection = DriverManager.getConnection(this.connectionUrl, this.connectionUser, this.connectionPassword);
			return connection;
		} catch (SQLException e) {
			throw new TryAgainException(String.format(
					"Failed to connect to database %s@(%s)", this.connectionUser, this.connectionUrl),
					e);
		} catch (Exception e) {
			Logger.libraryError("Database.getConnection() %s", e.getMessage());
			e.printStackTrace();
			throw e;
		}
	}

	long executeInsert(String sql, Object ... objects) throws FatalException, TryAgainException {
		try (Connection connection = getConnection()) {
			try (PreparedStatement statement = connection.prepareStatement(sql, java.sql.Statement.RETURN_GENERATED_KEYS)){
				setParameters(statement, objects);

				int affectedRows = statement.executeUpdate();
				if (affectedRows == 0) {
					throw new ProgrammersErrorException("Could not create a database row!");
				}else if (affectedRows > 1) {
					throw new ProgrammersErrorException("Unexpected result. Database returned multiple rows");
				}

				try (ResultSet resultSet = statement.getGeneratedKeys()){
					boolean found = resultSet.next();
					if (!found){
						throw new ProgrammersErrorException("Something is very wrong. Check to see if pigs are flying");
					}
					long newID = resultSet.getLong(1);
					return newID;
				}
			}
		} catch (SQLException e) {
			Logger.libraryError("Database.executeInsert(\"%s\"): %s", sql, e.getMessage());
			e.printStackTrace();
			throw new FatalException(e);
		} catch (Exception e) {
			Logger.libraryError("Database.executeInsert(\"%s\"): %s", sql, e.getMessage());
			e.printStackTrace();
			throw e;
		}
	}

	public int executeUpdate(String sql, Object ... objects) throws FatalException, TryAgainException {
		try (Connection connection = getConnection()) {
			try (PreparedStatement statement = connection.prepareStatement(sql)){
				setParameters(statement, objects);
				return statement.executeUpdate();
			}
		} catch (SQLException e) {
			Logger.libraryError("Database.executeUpdate(\"%s\"): %s", sql, e.getMessage());
			e.printStackTrace();
			throw new FatalException(e);
		} catch (Exception e) {
			Logger.libraryError("Database.executeUpdate(\"%s\"): %s", sql, e.getMessage());
			throw e;
		}
	}

	static void setParameters(PreparedStatement statement, Object... objects) throws FatalException {
		int parameterIndex = 1;
		for (Object object : objects) {
			try {
				statement.setObject(parameterIndex++, object);
			} catch (SQLException e) {
				throw new ProgrammersErrorException(String.format("Argument %d failed: %s", parameterIndex, e.getMessage()), e);
			} catch (Exception e) {
				Logger.libraryError("Database.setParameters(\"%s\"): %s", statement.toString(), e.getMessage());
				e.printStackTrace();
				throw e;
			}
		}
	}
}
