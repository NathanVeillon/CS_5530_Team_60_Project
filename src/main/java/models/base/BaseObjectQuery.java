package main.java.models.base;

import java.sql.ResultSet;
import java.util.List;

/**
 * Created by StudentNathan on 3/19/2017.
 */
public abstract class BaseObjectQuery<DataObject extends BaseObject> {

	private Class<DataObject> DataObjectClass;
	protected BaseObjectQuery(Class<DataObject> dataObjectClass){
		DataObjectClass = dataObjectClass;
	}

	public ObjectCollection getCollectionFromObjectResult(ResultSet resultSet) throws Exception {
		ObjectCollection collection = new ObjectCollection();

//		ResultSetMetaData metaData = resultSet.getMetaData();
//		int lastIndex = metaData.getColumnCount();
//		for (int i = 1; i <= lastIndex; i++){
//			System.out.println("Colummn: "+metaData.getTableName(i)+"."+metaData.getColumnName(i)+" | Label: "+metaData.getColumnLabel(i));
//		}

		while (resultSet.next()){
			DataObject newItem = DataObjectClass.newInstance();
			for (Attribute attr : newItem.getAttributes()){
				newItem.setField(attr.JavaFieldName, attr.JavaType.cast(resultSet.getObject(attr.DatabaseName)));
			}
			newItem.IsCreating = false;
			collection.add(newItem);
		}

		return collection;
	}
}
