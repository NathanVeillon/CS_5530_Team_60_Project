package main.java.models.base;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
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

		ResultSetMetaData md = resultSet.getMetaData();
		int colMax = md.getColumnCount() + 1;
		while (resultSet.next()){
			DataObject newItem = DataObjectClass.newInstance();

			for(int i = 1; i < colMax; i++){
				if(md.getTableName(i).equals(newItem.getTableName())){
					Attribute attr = newItem.getRelatedAttrFromDbName(md.getColumnName(i));
					newItem.setField(attr.JavaFieldName, attr.JavaType.cast(resultSet.getObject(i)));
				}else {
					Attribute attr = newItem.getRelatedAttrFromDbName(md.getTableName(i));
					if(BaseObject.class.isAssignableFrom(attr.JavaType)){
						Class<? extends  BaseObject> objectClass = attr.JavaType.asSubclass(BaseObject.class);

						switch (attr.ForeignEntityType){
							case ONE_TO_MANY:
								ObjectCollection foreignCollection = (ObjectCollection) newItem.getField(attr.JavaFieldName);
								foreignCollection = (foreignCollection == null) ? new ObjectCollection() : foreignCollection;
								BaseObject aForiegnItem = (foreignCollection.isEmpty()) ? objectClass.newInstance() : foreignCollection.get(0);
								aForiegnItem.setField(aForiegnItem.getRelatedAttrFromDbName(md.getColumnName(i)).JavaFieldName, resultSet.getObject(i));
								aForiegnItem.IsCreating = false;
								foreignCollection.add(aForiegnItem);
								newItem.setField(attr.JavaFieldName, foreignCollection);
								break;

							case ONE_TO_ONE:
							case MANY_TO_ONE:
								BaseObject foreignEntity = (BaseObject) newItem.getField(attr.JavaFieldName);
								foreignEntity = (foreignEntity == null) ? objectClass.newInstance(): foreignEntity;
								foreignEntity.setField(foreignEntity.getRelatedAttrFromDbName(md.getColumnName(i)).JavaFieldName, resultSet.getObject(i));
								foreignEntity.IsCreating = false;
								newItem.setField(attr.JavaFieldName, foreignEntity);
								break;

							default:
								break;
						}
					}

				}
			}
			newItem.IsCreating = false;
			newItem.ModifiedAttributes.clear();

			DataObject item;
			if(collection.contains(newItem)) {
				item = (DataObject) collection.get(collection.indexOf(newItem));
			}else {
				item = newItem;
			}
			if(collection.contains(item)){
				for(Attribute attr : item.getRelatedForeignEnitityAttributes()){
					if(BaseObject.class.isAssignableFrom(attr.JavaType)){
						Class<? extends  BaseObject> objectClass = attr.JavaType.asSubclass(BaseObject.class);

						switch (attr.ForeignEntityType){
							case ONE_TO_MANY:
								ObjectCollection newForeignCollection = (ObjectCollection) newItem.getField(attr.JavaFieldName);
								ObjectCollection oldForeignCollection = (ObjectCollection) newItem.getField(attr.JavaFieldName);
								if(newForeignCollection == null)
									break;
								if(oldForeignCollection == null)
									oldForeignCollection = new ObjectCollection();
								for(BaseObject foreignObject: newForeignCollection){
									foreignObject.IsCreating = false;
									foreignObject.ModifiedAttributes.clear();
									oldForeignCollection.add(foreignObject);
								}
								item.setField(attr.JavaFieldName, oldForeignCollection);
								break;

							case MANY_TO_ONE:
							case ONE_TO_ONE:
								BaseObject newForeignItem = (BaseObject) newItem.getField(attr.JavaFieldName);
								if(newForeignItem == null)
									break;

								newForeignItem.IsCreating = false;
								newForeignItem.ModifiedAttributes.clear();
								item.setField(attr.JavaFieldName, newForeignItem);
								break;
							default:
								break;
						}
					}
				}
			}
			item.IsCreating = false;
			collection.add(item);
		}

		return collection;
	}
}
