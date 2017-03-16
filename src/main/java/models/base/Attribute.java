package main.java.models.base;

import java.sql.SQLType;

/**
 * Created by StudentNathan on 2/23/2017.
 */
public class Attribute {

	public String JavaFieldName;
	public Class<?> JavaType;
	public String DatabaseName;
	public SQLType DatabaseType;
	public boolean IsPrimaryKey;

	public Attribute(String javaFieldName, Class<?> javaType, String databaseName, SQLType databaseType, boolean isPrimaryKey) {
		JavaFieldName =  javaFieldName;
		JavaType = javaType;
		DatabaseName =  databaseName;
		DatabaseType =  databaseType;
		IsPrimaryKey =  isPrimaryKey;
	}

}
