package main.java.models.base;

import main.java.managers.ConnectionManager;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by StudentNathan on 3/19/2017.
 */
public abstract class BaseObjectQuery<DataObject extends BaseObject> {

	private Map<Attribute, Attribute> AttributesToJoin = new HashMap<>();
	private FilterCriteria TheQueryCriteria = new FilterCriteria(FilterCriteria.GroupAdhesive.AND);

	private Class<DataObject> DataObjectClass;
	private DataObject DataObjectInstance; // Used For Object Methods Like GetAttributes And GetTableName

	protected BaseObjectQuery(Class<DataObject> dataObjectClass){
		DataObjectClass = dataObjectClass;
		try {
			DataObjectInstance = DataObjectClass.newInstance();
		}catch (Exception e){
			e.printStackTrace();
		}
	}

	public BaseObjectQuery<DataObject> filterByField(String fieldName, Object value) throws Exception {
		return filterByField(fieldName, value, FilterCriteria.Comparison.EQUAL);
	}

	public BaseObjectQuery<DataObject> filterByField(String fieldName, Object value, FilterCriteria.Comparison comparision) throws Exception{
			String[] attributeFields = fieldName.split("\\.");
			BaseObject relatedObjectToFilterOn = DataObjectInstance;

			String tableAlias = relatedObjectToFilterOn.getTableName();
			Attribute relatedAttr = null;
			for(String attrFieldName: attributeFields){
				Attribute previousAttribute = relatedAttr;
				relatedAttr = relatedObjectToFilterOn.getRelatedAttr(attrFieldName);
				if(relatedAttr.isForeignEntity()){
					AttributesToJoin.put(relatedAttr, previousAttribute);
					relatedObjectToFilterOn = (BaseObject) relatedAttr.JavaType.newInstance();
					tableAlias = relatedAttr.JavaFieldName;
				}
			}

			String databaseFieldName = "`"+tableAlias+"`.`"+relatedAttr.DatabaseName+"`";
			TheQueryCriteria.addSubFilter(databaseFieldName, value, comparision);

			return this;
		}

	public BaseObjectQuery<DataObject> populateRelation(String relationshipName) throws Exception{
		String[] attributeFields = relationshipName.split("\\.");
		BaseObject relatedObjectToFilterOn = DataObjectInstance;

		Attribute relatedAttr = null;
		for(String attrFieldName: attributeFields){
			Attribute previousAttr = relatedAttr;
			relatedAttr = relatedObjectToFilterOn.getRelatedAttr(attrFieldName);
			if(!relatedAttr.isForeignEntity()) {
				return this;
			}

			AttributesToJoin.put(relatedAttr, previousAttr);
			relatedObjectToFilterOn = (BaseObject) relatedAttr.JavaType.newInstance();
		}

		return this;
	}

	private String getQueryString() throws Exception{

		String queryString = "SELECT * FROM "+DataObjectInstance.getTableName();

		if(AttributesToJoin.size() > 0){
			StringBuilder joinTablesBuilder = new StringBuilder();
			StringBuilder onAttributesBuilder = new StringBuilder();

			for (Attribute foreignObjectAttrToJoin: AttributesToJoin.keySet()) {
				if(joinTablesBuilder.length() != 0){
					joinTablesBuilder.append(", ");
				}

				BaseObject foreignInstanceToFilterOn = (BaseObject) foreignObjectAttrToJoin.JavaType.newInstance();
				joinTablesBuilder.append(foreignInstanceToFilterOn.getTableName()).append(" ").append(foreignObjectAttrToJoin.JavaFieldName);

				for (AttributeRelationship attrMap: foreignObjectAttrToJoin.ForeignEntityMap) {
					if(onAttributesBuilder.length() != 0){
						onAttributesBuilder.append(" AND ");
					}

					Attribute localObjectAttrToJoin = AttributesToJoin.get(foreignObjectAttrToJoin);
					BaseObject localObjectToJoinOn = (localObjectAttrToJoin == null) ? DataObjectInstance : (BaseObject) localObjectAttrToJoin.JavaType.newInstance();
					String tableAlias = (localObjectAttrToJoin == null) ? DataObjectInstance.getTableName() : localObjectAttrToJoin.JavaFieldName;

					onAttributesBuilder.append("`");
					onAttributesBuilder.append(tableAlias);
					onAttributesBuilder.append("`.`");
					onAttributesBuilder.append(localObjectToJoinOn.getRelatedAttr(attrMap.getLocalObjectFieldName()).DatabaseName);
					onAttributesBuilder.append("`");

					onAttributesBuilder.append(" = ");

					onAttributesBuilder.append("`");
					onAttributesBuilder.append(foreignObjectAttrToJoin.JavaFieldName);
					onAttributesBuilder.append("`.`");
					onAttributesBuilder.append(foreignInstanceToFilterOn.getRelatedAttr(attrMap.getForeignObjectFieldName()).DatabaseName);
					onAttributesBuilder.append("`");
				}
			}


			queryString += " JOIN ("+joinTablesBuilder.toString()+") ON ("+onAttributesBuilder.toString()+")";
		}

		if(TheQueryCriteria.getTotalFilterSize() > 0){
			queryString += " WHERE "+TheQueryCriteria.getCriteriaString()+" ";
		}

		return queryString;
	}

	public ObjectCollection find() throws Exception{
		String queryString = getQueryString()+";";

		PreparedStatement statement = ConnectionManager.prepareStatement(queryString);

		int filterCriteriaSize = TheQueryCriteria.getTotalFilterSize();
		for (int i = 0; i < filterCriteriaSize; i++){
			statement.setObject(i + 1, TheQueryCriteria.getFilterValue(i));
		}
		statement.execute();

		ObjectCollection collection = populateCollectionFromObjectResult(statement.getResultSet());
		statement.close();
		return collection;
	}



	public DataObject findOne() throws Exception{
		String queryString = getQueryString()+" LIMIT 1;";

		PreparedStatement statement = ConnectionManager.prepareStatement(queryString);

		int filterCriteriaSize = TheQueryCriteria.getTotalFilterSize();
		for (int i = 0; i < filterCriteriaSize; i++){
			statement.setObject(i + 1, TheQueryCriteria.getFilterValue(i));
		}

		statement.execute();

		ObjectCollection collection = populateCollectionFromObjectResult(statement.getResultSet());
		statement.close();

		return (collection.size() == 0) ? null: (DataObject) collection.get(0);
	}

	/**
	 *
	 * @param resultSet
	 * @return
	 * @throws Exception
	 *
	 * @deprecated
	 */
	public ObjectCollection getCollectionFromObjectResult(ResultSet resultSet) throws Exception {
		return populateCollectionFromObjectResult(resultSet);
	}

	private ObjectCollection populateCollectionFromObjectResult(ResultSet resultSet) throws Exception {
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
					Attribute attr = newItem.getRelatedAttr(md.getTableName(i));
					attr  = (attr == null) ? newItem.getRelatedAttrFromDbName(md.getTableName(i)) : attr;
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
				for(Attribute attr : item.getRelatedForeignEntityAttributes()){
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
