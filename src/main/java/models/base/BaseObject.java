package main.java.models.base;

import main.java.managers.ConnectionManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

public abstract class BaseObject implements Comparable{

	public abstract String getTableName();
	public abstract List<Attribute> getAttributes();

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
		for (Attribute attribute: getAttributes()) {
			if(attribute.JavaFieldName.equals(fieldName)){
				return attribute;
			}
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

	public List<Attribute> getRelatedForeignEnitityAttributes(){
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
		int j = attributes.size()+1 - getRelatedForeignEnitityAttributes().size();
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
