package net.eithon.library.mysql;

public class Table implements ITable {
	public long id;
	private String tableName;
	
	public Table(String tableName){
		this.tableName = tableName;
	}
	@Override
	public long getId() {
		// TODO Auto-generated method stub
		return this.id;
	}

	@Override
	public String getTableName() {
		// TODO Auto-generated method stub
		return this.tableName;
	}

}
