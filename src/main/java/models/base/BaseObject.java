package main.java.models.base;

import java.sql.PreparedStatement;
import java.util.Collection;
import java.util.List;

public abstract class BaseObject {

	public static Class<? extends BaseObjectMap> ObjectMapClass;
	public abstract String getTableName();
	public abstract List<Attribute> getAttributes();

	public boolean IsCreating = true;

	public BaseObject(){

//		for (Attribute attr: getAttributes()) {
//			if(!isValidField(attr.JavaFieldName, attr.JavaType)){
//				throw new OrmException("The Object Does Not Have the Same Field "+attr.JavaFieldName+" With The Same Type ("+attr.JavaType+") As The Object Map.");
//			}
//		}
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

	public Object getField(String fieldName) throws NoSuchFieldException, IllegalAccessException {
		return this.getClass().getField(fieldName).get(this);
	}

	public void setField(String fieldName, Object value) throws NoSuchFieldException, IllegalAccessException {
		Attribute attr = getRelatedAttr(fieldName);
		if(attr == null){
			throw new NoSuchFieldException("No Field Named "+fieldName);
		}

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

		stmnt.execute();
		IsCreating = false;
	}

	public void update() throws Exception {
		if(!ConnectionManager.inTransaction()){
			throw new Exception("Cannot Update Outside A Transaction");
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
				whereStr += attribute.DatabaseName + " =  ?";// Using Param To have safe updates
			}
		}

		String str = "UPDATE "+getTableName()+" "+setStr+" "+whereStr+";";
		System.out.println(str);
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

				whereStr += attribute.DatabaseName + " =  ?";// Using Param To have safe updates
			}
		}

		String str = "DELETE FOR "+getTableName()+" "+whereStr+" LIMIT 1;";
		PreparedStatement stmnt = ConnectionManager.prepareStatement(str);

		int i = 1;
		for (Attribute attribute: attributes) {
			if(attribute.IsPrimaryKey){
				stmnt.setObject(i, getField(attribute.JavaFieldName), attribute.DatabaseType);
				i++;
			}
		}

		stmnt.execute();
	}

}
