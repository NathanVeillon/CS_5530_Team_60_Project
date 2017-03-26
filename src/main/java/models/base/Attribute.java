package main.java.models.base;

import java.sql.SQLType;

/**
 * Created by StudentNathan on 2/23/2017.
 */
public class Attribute {

	public enum ForeignRelationshipType {ONE_TO_ONE, ONE_TO_MANY, MANY_TO_ONE};

	public String JavaFieldName;
	public Class<?> JavaType;
	public String DatabaseName; // Column Name In Database Unless Attribute Is Entity Then It Is A Table Name
	public boolean IsPrimaryKey;
	public ForeignRelationshipType ForeignEntityType;


	public Attribute(String javaFieldName, Class<?> javaType,
					 String databaseName, boolean isPrimaryKey) {
		this(javaFieldName, javaType, databaseName, isPrimaryKey, null);
	}

	public Attribute(String javaFieldName, Class<?> javaType,
					 String databaseName, boolean isPrimaryKey,
					 ForeignRelationshipType foreignEntityType) {
		JavaFieldName =  javaFieldName;
		JavaType = javaType;
		DatabaseName =  databaseName;
		IsPrimaryKey =  isPrimaryKey;
		ForeignEntityType = foreignEntityType;
	}

	public boolean isForeignEntity(){
		return ForeignEntityType != null;
	}

}
