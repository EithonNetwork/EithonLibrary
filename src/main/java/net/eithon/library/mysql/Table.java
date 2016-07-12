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

public abstract class Table<T extends ITableRow>
{
	private String _connectionString;
	private String _tableName;
	private String _orderBy;

	protected Table(String tableName, String connectionString)
	{
		_tableName = tableName;
		_connectionString = connectionString;
	}


	/// <summary>
	/// A list of all the unique column names, i.e. not the columns in "IMandatoryDatabaseColumns"/>.
	/// </summary>
	protected abstract List<String> getUniqueColumnNames();

	protected Connection getConnection() {
		Class.forName("com.mysql.jdbc.Driver");
		return DriverManager.getConnection(_connectionString,
				this.user, this.password);
	}

	/// <summary>
	/// Insert a new item into the database
	/// </summary>
	/// <param name="item">The item to insert</param>
	/// <returns>The created item.</returns>
	public T create(Map<String, Object> columnValues)
	{
		long id = 0;
		try (Connection c = getConnection()) {
			String sql = String.format("INSERT INTO %s (%s) VALUES (%s)",
					_tableName, joinColumnNames(columnValues), joinColumnValues(columnValues));

			try (PreparedStatement statement = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
				fillInBlanks(statement, stringColumnValues(columnValues));
				statement.executeUpdate();
				try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
					generatedKeys.next();
			     id = generatedKeys.getLong(1);
				}
			}
		}
		return read(id);
	}

	/// <summary>
	/// Makes a search for an item. If not found, <see cref="item"/> will be inserted and returned.
	/// </summary>
	/// <param name="item">The item to search for. If not found, it will be inserted.</param>
	/// <param name="where">The where statement to use for searching.</param>
	/// <returns>The found item or the inserted item.</returns>
	/// <remarks>The fields in the <see cref="item"/> will be used to fill in the parameters in <see cref="where"/>.</remarks>
	/// <remarks>If more than one item is found, an excepton is thrown.</remarks>
	protected T readOrCreate(Map<String, Object> columnValues, String where)
	{
		T found = SearchTheOnlyOne(columnValues, where);
		if (found != null) return found;
		return create(columnValues);
	}
	
	/// <summary>
	/// Get the item specified by the <paramref name="id"/>.
	/// </summary>
	/// <param name="id">The id for the item that should be fetched.</param>
	/// <returns>The found item or null.</returns>
	public T read(long id)
	{
		return SearchTheOnlyOne("Id = @Id");
	}

	#endregion

	#region UPDATE

	/// <summary>
	/// Update the data for a item
	/// </summary>
	/// <param name="item">The updated item</param>
	/// <returns>The updated item.</returns>
	public T Update(T item)
	{
		using (IDbConnection db = NewSqlConnection())
		{
			item.Etag = long.Newlong();
			var sqlQuery = $"UPDATE {TableName} SET {UpdateSets}, RowUpdatedAt=GETUTCDATE() WHERE Id = @Id";
			db.Execute(sqlQuery, item);
			return Read(item.Id);
		}
	}

	private IEnumerable<String> UpdateColumnNames
	{
		get
		{
			lock (_updateColumnNames)
			{
				if (_updateColumnNames.Count > 0) return _updateColumnNames;
				_updateColumnNames.AddRange(_mandatoryUpdateColumnNames);
				_updateColumnNames.AddRange(UniqueColumnNames);
				return _updateColumnNames;
			}
		}
	}

	private String UpdateSets
	{
		get
		{
			lock (_updateColumnNames)
			{
				if (_updateSets != null) return _updateSets;
				_updateSets = String.Join(
						", ",
						UpdateColumnNames
						.Select(columnName => $"{columnName} = @{GetFieldName(columnName)}"));
				return _updateSets;
			}
		}
	}

	#endregion

	#region DELETE

	/// <summary>
	/// Remove one item from the table.
	/// </summary>
	/// <param name="id">The id for the item that should be removed</param>
	/// <returns>The number of rows that were removed.</returns>
	public int Delete(long id)
	{
		using (IDbConnection db = NewSqlConnection())
		{
			String sqlQuery = $"DELETE FROM {TableName} WHERE Id = @Id";
			return db.Execute(sqlQuery, new { Id = id });
		}
	}

	#endregion

	#region SEARCH

	/// <summary>
	/// Fetches all rows for the current table.
	/// </summary>
	/// <param name="offset">The number of items that will be skipped in result.</param>
	/// <param name="limit">The maximum number of items to return.</param>
	/// <returns>The found items.</returns>
	/// <remarks>Will use ORDER BY as defined in <see cref="OrderBy"/>.</remarks>
	public PagingEnvelope Search(uint offset = 0, uint limit = Paging.DefaultLimit)
	{
		return Search(null, OrderBy, offset, limit);
	}

	/// <summary>
	/// Fetches all rows for the current table.
	/// </summary>
	/// <param name="orderBy">An expression for how to order the result.</param>
	/// <param name="offset">The number of items that will be skipped in result.</param>
	/// <param name="limit">The maximum number of items to return.</param>
	/// <returns>The found items.</returns>
	protected PagingEnvelope Search(String orderBy, uint offset = 0, uint limit = Paging.DefaultLimit)
	{
		return Search(null, orderBy, offset, limit);
	}

	/// <summary>
	/// Find the items specified by the <paramref name="where"/> clause.
	/// </summary>
	/// <param name="where">The selection condition.</param>
	/// <param name="orderBy">An expression for how to order the result.</param>
	/// <param name="offset">The number of items that will be skipped in result.</param>
	/// <param name="limit">The maximum number of items to return.</param>
	/// <returns>The found items.</returns>
	protected PagingEnvelope Search(String where, String orderBy = null, uint offset = 0, uint limit = Paging.DefaultLimit)
	{
		return Search(null, where, orderBy, offset, limit);
	}

	/// <summary>
	/// Find the items specified by the <paramref name="where"/> clause and using fields from the <paramref name="param"/>.
	/// </summary>
	/// <param name="param">The fields for the <seealso cref="where"/> expression.</param>
	/// <param name="where">The selection condition.</param>
	/// <param name="orderBy">An expression for how to order the result.</param>
	/// <param name="offset">The number of items that will be skipped in result.</param>
	/// <param name="limit">The maximum number of items to return.</param>
	/// <returns>The found items.</returns>
	protected PagingEnvelope Search(object param, String where = null, String orderBy = null, uint offset = 0, uint limit = Paging.DefaultLimit)
	{
		var total = CountItems(param, where);
		var data = SearchInternal(param, where, orderBy, offset, limit);
		return new PagingEnvelope
				{
			Data = JArray.FromObject(data),
					Paging = new Paging
					{

				Offset = offset,
						Limit = limit,
						Returned = (uint)data.Count,
						Total = total,
					}
				};
	}

	/// <summary>
	/// Find the item specified by the <paramref name="where"/> condition.
	/// </summary>
	/// <param name="param">The fields for the <seealso cref="where"/> condition.</param>
	/// <param name="where">The selection condition.</param>
	/// <returns>The found item or null.</returns>
	/// <remarks>If more than one item is found, an excepton is thrown.</remarks>
	/// <remarks>If you just want the first item of possibly many that matches the where condition, 
	/// please use <see cref="SearchFirst"/></remarks>
	protected T SearchTheOnlyOne(object param, String where = null)
	{
		if (where == null) where = "1=1";
		using (IDbConnection db = NewSqlConnection())
		{
			var sqlQuery = $"SELECT * FROM {TableName} WHERE {where}" +
					" ORDER BY 1 OFFSET 0 ROWS FETCH NEXT 2 ROWS ONLY";
			return db.Query<T>(sqlQuery, param).SingleOrDefault();
		}
	}

	/// <summary>
	/// Find the items specified by the <paramref name="where"/> clause and using fields from the <paramref name="param"/>.
	/// </summary>
	/// <param name="param">The fields for the <seealso cref="where"/> condition.</param>
	/// <param name="where">The selection condition.</param>
	/// <param name="orderBy">An expression for how to order the result.</param>
	/// <returns>The found items.</returns>
	protected T SearchFirst(object param, String where = null, String orderBy = null)
	{
		return SearchInternal(param, where, orderBy, 0, 1).SingleOrDefault();
	}

	/// <summary>
	/// Find the items specified by the <paramref name="where"/> clause.
	/// </summary>
	/// <param name="param">The fields for the <seealso cref="where"/> condition.</param>
	/// <param name="where">The selection condition.</param>
	/// <param name="orderBy">An expression for how to order the result.</param>
	/// <param name="offset">The number of items that will be skipped in result.</param>
	/// <param name="limit">The maximum number of items to return.</param>
	/// <returns>The found items.</returns>
	private List<T> SearchInternal(object param, String where = null, String orderBy = null, uint offset = 0, uint limit = Paging.DefaultLimit)
	{
		if (where == null) where = "1=1";
		if (orderBy == null) orderBy = "1";
		using (IDbConnection db = NewSqlConnection())
		{
			var sqlQuery = $"SELECT * FROM {TableName} WHERE {where}" +
					$" ORDER BY {orderBy}" +
					$" OFFSET {offset} ROWS FETCH NEXT {limit} ROWS ONLY";

			return db.Query<T>(sqlQuery, param)
					.ToList();
		}
	}

	/// <summary>
	/// Find the number of rows that fulfill the <paramref name="where"/> condition..
	/// </summary>
	/// <param name="param">The fields for the <seealso cref="where"/> expression.</param>
	/// <param name="where">The selection expression.</param>
	/// <returns>The number of rows that fulfill the where statement.</returns>
	private uint CountItems(object param, String where = null)
	{
		if (where == null) where = "1=1";
		using (IDbConnection db = NewSqlConnection())
		{
			var sqlQuery = $"SELECT COUNT(*) FROM {TableName} WHERE {where}";
			return db.Query<uint>(sqlQuery, param)
					.SingleOrDefault();
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
}
