package net.eithon.library.mysql;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

public interface IDbRecord<T> {
	HashMap<String, Object> getColumnValues();
	T fromDb(ResultSet resultSet) throws SQLException;	
	T factory(Database database, long id);
	String getUpdatedAtColumnName();
}
