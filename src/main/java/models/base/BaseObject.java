package main.java.models.base;

import java.sql.*;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class BaseObject {

	public abstract String getTableName();
	public abstract List<Attribute> getAttributes();

	public HashMap<String, Attribute> ModifiedAttributes = new HashMap<>();


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

	public Attribute getRelatedAttr(String fieldName){
		for (Attribute attribute: getAttributes()) {
			if(attribute.JavaFieldName == fieldName){
				return attribute;
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
					System.out.println(getField(attribute.JavaFieldName));
					pks.put(attribute.JavaFieldName, getField(attribute.JavaFieldName));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

		return pks;
	}
	
	public Attribute getNullPrimaryKey(){
		for (Attribute attr: getAttributes()) {
			if(attr.IsPrimaryKey && this.IsCreating && !this.ModifiedAttributes.containsKey(attr.JavaFieldName)){
				return getRelatedAttr(attr.JavaFieldName);
			}
		}

		return null;
	}

	public Object getField(String fieldName) throws NoSuchFieldException, IllegalAccessException {
		return this.getClass().getField(fieldName).get(this);
	}

	public void setField(String fieldName, Object value) throws NoSuchFieldException, IllegalAccessException {
		Attribute attr = getRelatedAttr(fieldName);
		if(attr == null){
			throw new NoSuchFieldException("No Field Named "+fieldName);
		}

		ModifiedAttributes.put(fieldName, attr);

		if(attr.IsPrimaryKey && value != getField(fieldName)){
			IsCreating = true;
		}

		this.getClass().getField(fieldName).set(this, value);
	}

	public void save() throws Exception {
		if(IsCreating){
			create();
		}else {
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
			if(attrNames.length() != 1){
				attrNames += ", ";
				value += ", ";
			}
			attrNames += attribute.DatabaseName;
			value += "?"; // Using Param To have safe inserts
		}

		attrNames += ")";
		value += ")";
		String str = "INSERT INTO "+getTableName()+" "+attrNames
				+" VALUES "
				+value+";";
		PreparedStatement stmnt = ConnectionManager.prepareStatement(str);

		int i = 1;
		for (Attribute attribute: attributes) {
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
			if(setStr.length() != 4){
				setStr += ", ";
			}
			setStr += attribute.DatabaseName + " =  ?";// Using Param To have safe updates

			if(attribute.IsPrimaryKey){
				if(whereStr.length() != 6){
					whereStr += ", ";
				}
				whereStr += attribute.DatabaseName + " = ?";// Using Param To have safe updates
			}
		}

		String str = "UPDATE "+getTableName()+" "+setStr+" "+whereStr+";";
		PreparedStatement stmnt = ConnectionManager.prepareStatement(str);

		int i = 1;
		int j = attributes.size()+1;
		for (Attribute attribute: attributes) {
			stmnt.setObject(i, getField(attribute.JavaFieldName));
			i++;
			if(attribute.IsPrimaryKey){
				stmnt.setObject(j, getField(attribute.JavaFieldName));
				j++;
			}
		}

		stmnt.execute();
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
			if(attribute.IsPrimaryKey){
				if(whereStr.length() != 6){
					whereStr += ", ";
				}

				whereStr += attribute.DatabaseName + " = ?";// Using Param To have safe updates
			}
		}

		String str = "DELETE FROM "+getTableName()+" "+whereStr+" LIMIT 1;";
		PreparedStatement stmnt = ConnectionManager.prepareStatement(str);

		int i = 1;
		for (Attribute attribute: attributes) {
			if(attribute.IsPrimaryKey){
				stmnt.setObject(i, getField(attribute.JavaFieldName));
				i++;
			}
		}

		stmnt.execute();
	}

}
