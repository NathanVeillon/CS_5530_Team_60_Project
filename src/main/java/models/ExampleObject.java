package main.java.models;

import main.java.models.base.*;

import java.sql.JDBCType;
import static java.util.Arrays.asList;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by StudentNathan on 2/27/2017.
 */
public class ExampleObject extends BaseObject {

	public final static List<Attribute> Attributes = asList(
			new Attribute("Id", Integer.class, "id", true),
			new Attribute("Name", String.class, "name", false)
	);
	public final static String TableName = "example_table";

	@Override
	public List<Attribute> getAttributes() {
		return Attributes;
	}

	private static final Map<String, Attribute> AttributeMap;

	static {
		Map<String, Attribute> aMap = new HashMap<>();
		for (Attribute attr : Attributes) {
			aMap.put(attr.JavaFieldName, attr);
		}
		AttributeMap = Collections.unmodifiableMap(aMap);
	}

	@Override
	public Map<String, Attribute> getAttributeMap() {
		return AttributeMap;
	}

	@Override
	public String getTableName() {
		return TableName;
	}

	public Integer Id;
	public String Name;
}