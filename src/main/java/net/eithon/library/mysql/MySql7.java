package net.eithon.library.mysql;

//This code was copied from https://github.com/Huskehhh/MySQL

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


/**
 * Connects to and uses a MySQL database
 * 
 * @author -_Husky_-
 * @author tips48
 */
public class MySql7 extends Database7 {
	private final String user;
	private final String database;
	private final String password;
	private final String port;
	private final String hostname;

	/**
	 * Creates a new MySQL instance
	 *
	 * @param hostname
	 *            Name of the host
	 * @param port
	 *            Port number
	 * @param username
	 *            Username
	 * @param password
	 *            Password
	 */
	public MySql7(String hostname, String port, String username,
			String password) {
		this(hostname, port, null, username, password);
	}

	/**
	 * Creates a new MySQL instance for a specific database
	 *
	 * @param hostname
	 *            Name of the host
	 * @param port
	 *            Port number
	 * @param database
	 *            Database name
	 * @param username
	 *            Username
	 * @param password
	 *            Password
	 */
	public MySql(String hostname, String port, String database,
			String username, String password) {
		this.hostname = hostname;
		this.port = port;
		this.database = database;
		this.user = username;
		this.password = password;
	}

	@Override
	public Connection openConnection() throws SQLException,
			ClassNotFoundException {
		if (checkConnection()) {
			return this.connection;
		}
		
		String connectionURL = "jdbc:mysql://"
				+ this.hostname + ":" + this.port;
		if (this.database != null) {
			connectionURL = connectionURL + "/" + this.database;
		}
		
		Class.forName("com.mysql.jdbc.Driver");
		this.connection = DriverManager.getConnection(connectionURL,
				this.user, this.password);
		return this.connection;
	}
}