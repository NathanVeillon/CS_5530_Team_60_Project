package main.java.models.base;

import jdk.nashorn.internal.runtime.JSONFunctions;
import main.java.managers.ConnectionManager;

import java.math.BigDecimal;
import java.sql.*;
import java.sql.Date;
import java.util.*;

public abstract class BaseObject implements Comparable{

	public abstract String getTableName();
	public abstract List<Attribute> getAttributes();
	public abstract Map<String, Attribute> getAttributeMap();

	public HashMap<String, Attribute> ModifiedAttributes = new HashMap<>();
	public boolean isModified(){
		return !ModifiedAttributes.isEmpty();
	}

	public boolean IsCreating = true;
	public boolean IsDeleted = false;

	public BaseObject(){
	}

	public BaseObject(BaseObject o) {
		super();

	}

//	public boolean isValidField(String fieldName, Class type){
//		try {
//
//			HashMap<String, Attribute> attrbutes = getAttributes();
//
//			if(!attrbutes.containsKey(fieldName)){
//				return false;
//			}
//
//			Attribute attr = attrbutes.get(fieldName);
//
//			if(!attr.JavaType.isAssignableFrom(type)){
//				return false;
//			}
//
//
//			return true;
//		}catch (Exception e){
//			return false;
//		}
//	}

	public boolean equals(Object o){
		if(o == null)
			return false;
		if(o.getClass() != this.getClass())
			return false;

		BaseObject baseObject = (BaseObject) o;
		return getPkString().equals(baseObject.getPkString());
	}

	public Attribute getRelatedAttr(String fieldName){
		if(getAttributeMap().containsKey(fieldName)){
			return getAttributeMap().get(fieldName);
		}
		return null;
	}

	public Attribute getRelatedAttrFromDbName(String databaseName){
		for (Attribute attribute: getAttributes()) {
			if(attribute.DatabaseName.equals(databaseName)){
				return attribute;
			}
		}

		return null;
	}

	public List<Attribute> getRelatedForeignEntityAttributes(){
		List<Attribute> attributes = new ArrayList<>();
		for (Attribute attribute: getAttributes()) {
			if(attribute.isForeignEntity())
				attributes.add(attribute);
		}

		return attributes;
	}

	public Attribute getNullPrimaryKey(){
		for (Attribute attr: getAttributes()) {
			if(attr.IsPrimaryKey && this.IsCreating && !this.ModifiedAttributes.containsKey(attr.JavaFieldName)){
				return getRelatedAttr(attr.JavaFieldName);
			}
		}

		return null;
	}

	/**
	 * Returns The Primary Keys By An Hash Map
	 * Where The Key Is The Field Name, And The Value Is The Value OF The Field
	 * [
	 *  	PK_field1 => 12345
	 *  	PK_field2 => 'Hello World'
	 * ]
	 *
	 * @return PK String
	 */
	public HashMap<String, Object> getPrimaryKeys(){
		HashMap<String, Object> pks = new HashMap<String, Object>();

		for (Attribute attribute: getAttributes()) {
			if(attribute.IsPrimaryKey){
				try {
					pks.put(attribute.JavaFieldName, getField(attribute.JavaFieldName));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

		return pks;
	}

	public String getPkString(){
		String pkStr = "#";
		for (Attribute attribute: getAttributes()) {
			if(attribute.IsPrimaryKey){
				try {
					pkStr += getField(attribute.JavaFieldName).toString()+" || ";
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

		return pkStr;
	}

	public String toJson(){
		StringBuilder jsonBuilder = new StringBuilder();
		jsonBuilder.append("{");
		for (Attribute attr: getAttributes()){

			Object val;
			try {
				val = getField(attr.JavaFieldName);
			} catch (Exception e){
				continue;
			}

			if(val == null){
				continue;
			}

			if(jsonBuilder.length() > 1){
				jsonBuilder.append(", ");
			}

			jsonBuilder.append("\"");
			jsonBuilder.append(attr.JavaFieldName);
			jsonBuilder.append("\": ");
			if(attr.isForeignEntity()){
				BaseObject relatedObject = (BaseObject) val;
				jsonBuilder.append(relatedObject.toJson());
				continue;
			}

			if(attr.JavaType.equals(Time.class)){
				Time time = (Time) val;
				jsonBuilder.append(time.getTime());
				continue;
			}

			if(attr.JavaType.equals(Date.class)){
				Date date = (Date) val;
				jsonBuilder.append(date.getTime());
				continue;
			}

			if(attr.JavaType.equals(String.class)){
				String string = (String) val;
				jsonBuilder.append(quoteForJson(string));
				continue;
			}

			if(attr.JavaType.equals(Boolean.class)||
				attr.JavaType.equals(boolean.class)){
				boolean bool = (boolean) val;
				jsonBuilder.append((bool) ? "true" : "false");
				continue;
			}

			if(attr.JavaType.equals(int.class) ||
				attr.JavaType.equals(Integer.class) ||
				attr.JavaType.equals(float.class) ||
				attr.JavaType.equals(Float.class) ||
				attr.JavaType.equals(double.class) ||
				attr.JavaType.equals(Double.class) ||
				attr.JavaType.equals(BigDecimal.class)
			){
				jsonBuilder.append(val);
				continue;
			}
		}

		jsonBuilder.append("}");

		return jsonBuilder.toString();
	}

	public boolean isValidForJsonMap(Map<String, String[]> paramMap, String keyPrepend){
		keyPrepend = (keyPrepend == null) ? "" : keyPrepend+"-";

		for(Attribute attr: getAttributes()){
			String attributeParamKey = keyPrepend+attr.JavaFieldName;

			if(attr.isForeignEntity()){
				continue;
			}

			if(paramMap.containsKey(attributeParamKey)){
				return true;
			}
		}

		return false;
	}

	public void fromFlatJsonMap(Map<String, String[]> paramMap) throws Exception {
		fromFlatJsonMap(paramMap, null);
	}

	public void fromFlatJsonMap(Map<String, String[]> paramMap, String keyPrepend) throws Exception{
		keyPrepend = (keyPrepend == null) ? "" : keyPrepend+"-";

		for(Attribute attr: getAttributes()){
			String attributeParamKey = keyPrepend+attr.JavaFieldName;

			if(attr.isForeignEntity()){
				switch (attr.ForeignEntityType){
					case ONE_TO_ONE:
					case MANY_TO_ONE:
						BaseObject newInstance = (BaseObject) attr.JavaType.newInstance();
						if(!newInstance.isValidForJsonMap(paramMap, attributeParamKey)){
							break;
						}
						newInstance.fromFlatJsonMap(paramMap, attributeParamKey);
						setField(attr.JavaFieldName, newInstance);
						break;
					case ONE_TO_MANY:
						ObjectCollection newObjectCollection = new ObjectCollection();
						boolean validCollection = newObjectCollection.fromFlatJsonMap(paramMap, attributeParamKey, attr);
						if(!validCollection){
							break;
						}
						setField(attr.JavaFieldName, newObjectCollection);
						break;
					default:
						break;
				}
				continue;
			}

			if(!paramMap.containsKey(attributeParamKey)){
				continue;
			}

			String val = paramMap.get(attributeParamKey)[0];


			try {

				if (attr.JavaType.equals(Date.class)) {
					Date date = Date.valueOf(val);
					this.setField(attr.JavaFieldName, date);
					continue;
				}

				if (attr.JavaType.equals(String.class)) {
					this.setField(attr.JavaFieldName, val);
					continue;
				}

				if (attr.JavaType.equals(Boolean.class) ||
						attr.JavaType.equals(boolean.class)) {
					setField(attr.JavaFieldName, (val.equalsIgnoreCase("true") || val.equalsIgnoreCase("1")));
				}

				if (attr.JavaType.equals(int.class) ||
						attr.JavaType.equals(Integer.class)){
					setField(attr.JavaFieldName, Integer.parseInt(val));
					continue;
				}

				if (attr.JavaType.equals(float.class) ||
						attr.JavaType.equals(Float.class)){
					setField(attr.JavaFieldName, Float.parseFloat(val));
					continue;
				}

				if (attr.JavaType.equals(double.class) ||
						attr.JavaType.equals(double.class)){
					setField(attr.JavaFieldName, Double.parseDouble(val));
					continue;
				}

				if (attr.JavaType.equals(BigDecimal.class)){
					setField(attr.JavaFieldName, new BigDecimal(val));
					continue;
				}
			}catch (Exception e){
				throw new Exception("Invalid Value ("+val+") For The Field ("+attr.JavaFieldName+")");
			}
		}
	}

	private static String quoteForJson(String string) {
		if (string == null || string.length() == 0) {
			return "\"\"";
		}

		char c = 0;
		int len = string.length();
		StringBuilder sb = new StringBuilder(len + 4);
		String t;

		sb.append('"');
		for (int i = 0; i < len; i++) {
			c = string.charAt(i);
			switch (c) {
				case '\\':
				case '"':
					sb.append('\\');
					sb.append(c);
					break;
				case '/':
					//                if (b == '<') {
					sb.append('\\');
					//                }
					sb.append(c);
					break;
				case '\b':
					sb.append("\\b");
					break;
				case '\t':
					sb.append("\\t");
					break;
				case '\n':
					sb.append("\\n");
					break;
				case '\f':
					sb.append("\\f");
					break;
				case '\r':
					sb.append("\\r");
					break;
				default:
					if (c < ' ') {
						t = "000" + Integer.toHexString(c);
						sb.append("\\u" + t.substring(t.length() - 4));
					} else {
						sb.append(c);
					}
			}
		}
		sb.append('"');
		return sb.toString();
	}

	public Object getField(String fieldName) throws NoSuchFieldException, IllegalAccessException {
		if(fieldName.contains(".")){
			String dataObjectFieldName = fieldName.substring(0, fieldName.indexOf("."));
			String subFieldName = fieldName.substring(fieldName.indexOf(".")+1);
			BaseObject foreignObject = (BaseObject)this.getClass().getField(dataObjectFieldName).get(this);
			if(foreignObject == null)
				return null;
			return foreignObject.getField(subFieldName);
		}else {
			return this.getClass().getField(fieldName).get(this);
		}

	}
	public Object getField(int index) throws NoSuchFieldException, IllegalAccessException {
		return this.getClass().getField(getAttributes().get(index).JavaFieldName).get(this);
	}

	public void setField(String fieldName, Object value) throws NoSuchFieldException, IllegalAccessException {
		Attribute attr = getRelatedAttr(fieldName);
		if(attr == null){
			throw new NoSuchFieldException("No Field Named "+fieldName);
		}

		ModifiedAttributes.put(fieldName, attr);

		if(attr.IsPrimaryKey && !value.equals(getField(fieldName))){
			IsCreating = true;
		}

		this.getClass().getField(fieldName).set(this, value);
	}

	public void save() throws Exception {
		if(IsCreating){
			create();
		}else if(isModified()) {
			update();
		}
	}

	public void create() throws Exception {
		if(!ConnectionManager.inTransaction()){
			throw new Exception("Cannot Create Outside A Transaction");
		}

		String attrNames = "(";
		String value = "(";
		Collection<Attribute> attributes = getAttributes();
		for (Attribute attribute: attributes) {
			if(attribute.isForeignEntity())
				continue;

			if(attrNames.length() != 1){
				attrNames += ", ";
				value += ", ";
			}
			attrNames += "`"+attribute.DatabaseName+"`";
			value += "?"; // Using Param To have safe inserts
		}

		attrNames += ")";
		value += ")";
		String str = "INSERT INTO "+getTableName()+" "+attrNames
				+" VALUES "
				+value+";";

		System.out.println(str);
		PreparedStatement stmnt = ConnectionManager.prepareStatement(str);

		int i = 1;
		for (Attribute attribute: attributes) {
			if(attribute.isForeignEntity())
				continue;
			stmnt.setObject(i, getField(attribute.JavaFieldName));
			i++;
		}

		stmnt.executeUpdate();


		// To Deal With AutoIncrement Field.
		Attribute nullAttr = getNullPrimaryKey();
		if (nullAttr == null){
			return;
		}

		ResultSet resultSet = stmnt.getGeneratedKeys();
		if(resultSet == null){
			return;
		}

		resultSet.first();
		setField(nullAttr.JavaFieldName, Integer.parseInt(resultSet.getObject(1).toString()));
		IsCreating = false;
		ModifiedAttributes.clear();
		stmnt.close();
	}



	public void update() throws Exception {
		if(!ConnectionManager.inTransaction()){
			throw new Exception("Cannot Update Outside A Transaction");
		}

		if(IsDeleted){
			throw new Exception("Cannot Save Object As It Is Deleted");
		}

		String setStr = "SET ";
		String whereStr = "WHERE ";
		List<Attribute> attributes = getAttributes();
		for (Attribute attribute: attributes) {
			if(attribute.isForeignEntity())
				continue;
			if(setStr.length() != 4){
				setStr += ", ";
			}
			setStr += "`"+attribute.DatabaseName+"`" + " =  ?";// Using Param To have safe updates

			if(attribute.IsPrimaryKey){
				if(whereStr.length() != 6){
					whereStr += " AND ";
				}
				whereStr += "`"+attribute.DatabaseName+"`" + " = ?";// Using Param To have safe updates
			}
		}

		String str = "UPDATE "+getTableName()+" "+setStr+" "+whereStr+";";
		PreparedStatement stmnt = ConnectionManager.prepareStatement(str);

		int i = 1;
		int j = attributes.size()+1 - getRelatedForeignEntityAttributes().size();
		for (Attribute attribute: attributes) {
			if(attribute.isForeignEntity())
				continue;
			stmnt.setObject(i, getField(attribute.JavaFieldName));
			i++;
			if(attribute.IsPrimaryKey){
				stmnt.setObject(j, getField(attribute.JavaFieldName));
				j++;
			}
		}

		stmnt.execute();
		ModifiedAttributes.clear();
		stmnt.close();
	}

	public void delete() throws Exception {
		if(IsCreating){
			throw new Exception("Cannot Delete A Record That Doesn't Exist In Database");
		}

		if(!ConnectionManager.inTransaction()){
			throw new Exception("Cannot Delete Outside A Transaction");
		}

		String whereStr = "WHERE ";
		List<Attribute> attributes = getAttributes();
		for (Attribute attribute: attributes) {
			if(attribute.isForeignEntity())
				continue;

			if(attribute.IsPrimaryKey){
				if(whereStr.length() != 6){
					whereStr += " AND ";
				}

				whereStr += attribute.DatabaseName + " = ?";// Using Param To have safe updates
			}
		}

		String str = "DELETE FROM "+getTableName()+" "+whereStr+" LIMIT 1;";
		PreparedStatement stmnt = ConnectionManager.prepareStatement(str);

		int i = 1;
		for (Attribute attribute: attributes) {
			if(attribute.isForeignEntity())
				continue;

			if(attribute.IsPrimaryKey){
				stmnt.setObject(i, getField(attribute.JavaFieldName));
				i++;
			}
		}

		stmnt.execute();
		IsDeleted = true;
		stmnt.close();
	}

	@Override
	public int compareTo(Object o) {
		if(o == null)
			return 1;
		if(o.getClass() != this.getClass())
			return 1;
		return this.getPkString().compareTo(((BaseObject) o).getPkString());
	}
}
