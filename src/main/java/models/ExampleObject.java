package main.java.models;

import main.java.models.base.*;

import java.sql.JDBCType;
import static java.util.Arrays.asList;
import java.util.List;

/**
 * Created by StudentNathan on 2/27/2017.
 */
public class ExampleObject extends BaseObject {

	public final static List<Attribute> Attributes = asList(
			new Attribute("Id", int.class, "id", JDBCType.INTEGER, true),
			new Attribute("Name", String.class, "name", JDBCType.VARCHAR, false)
	);
	public final static String TableName = "example_table";

	@Override
	public List<Attribute> getAttributes() {
		return Attributes;
	}

	@Override
	public String getTableName() {
		return TableName;
	}

	public int Id;
	public String Name;
}
