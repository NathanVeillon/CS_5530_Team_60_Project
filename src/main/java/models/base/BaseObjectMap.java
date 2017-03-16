package main.java.models.base;

import java.util.HashMap;

/**
 * Created by StudentNathan on 2/23/2017.
 */
public abstract class BaseObjectMap {
	public BaseObjectMap(){}

	public String DatabaseTable;
	public final HashMap<String, Attribute> ObjectMappings = createMap();
	public abstract HashMap<String, Attribute> createMap();

//
//	public static String getDatabaseTable() {
//		return DatabaseTable;
//	}
//
//	public static HashMap<String, Attribute> getObjectMappings() {
//		return ObjectMappings;
//	}
}
