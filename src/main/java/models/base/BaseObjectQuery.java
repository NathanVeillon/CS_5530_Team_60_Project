package main.java.models.base;

import main.java.managers.ConnectionManager;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by StudentNathan on 3/19/2017.
 */
public abstract class BaseObjectQuery<DataObject extends BaseObject> {

	private Map<String, Attribute> AttributesToJoin = new HashMap<>();
	private FilterCriteria TheQueryCriteria = new FilterCriteria(FilterCriteria.GroupAdhesive.AND);
	private SorterCriteria TheSorterCriteria = new SorterCriteria();

	private int PerPage = 0;
	private int Page = 1;

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

	public BaseObjectQuery<DataObject> populateFromFlatJsonMap(Map<String, String[]> paramMap) throws Exception{
		return populateFromFlatJsonMap(paramMap, "populate");
	}

	public BaseObjectQuery<DataObject> populateFromFlatJsonMap(Map<String, String[]> paramMap, String keyPrepend) throws Exception{
		keyPrepend = (keyPrepend == null) ? "" : keyPrepend+"-";

		if(!paramMap.containsKey(keyPrepend+"length")){
			return this;
		}

		int length = Integer.parseInt(paramMap.get(keyPrepend+"length")[0]);

		for(int i = 0; i < length; i++){
			String populateParamKey = keyPrepend+i;
			String relationshipName = paramMap.get(populateParamKey)[0];

			populateRelation(relationshipName);
		}

		return this;
	}

	public BaseObjectQuery<DataObject> sortFromFlatJsonMap(Map<String, String[]> paramMap) throws Exception{
		return sortFromFlatJsonMap(paramMap, "sorter");
	}

	public BaseObjectQuery<DataObject> sortFromFlatJsonMap(Map<String, String[]> paramMap, String keyPrepend) throws Exception{
		keyPrepend = (keyPrepend == null) ? "" : keyPrepend+"-";

		if(!paramMap.containsKey(keyPrepend+"length")){
			return this;
		}

		int length = Integer.parseInt(paramMap.get(keyPrepend+"length")[0]);

		for(int i = 0; i < length; i++){
			String sortParamKey = keyPrepend+i;
			String fieldName = paramMap.get(sortParamKey+"-field")[0];
			SorterCriteria.Direction direction = SorterCriteria.Direction.ASC;
			if(paramMap.containsKey(sortParamKey+"-direction")) {
				direction = SorterCriteria.Direction.valueOf(paramMap.get(sortParamKey+"-direction")[0].toUpperCase());
			}
			sortByField(fieldName, direction);
		}

		return this;
	}

	public BaseObjectQuery<DataObject> filterFromFlatJsonMap(Map<String, String[]> paramMap) throws Exception{
		return filterFromFlatJsonMap(paramMap, "filter");
	}

	public BaseObjectQuery<DataObject> filterFromFlatJsonMap(Map<String, String[]> paramMap, String keyPrepend) throws Exception{
		keyPrepend = (keyPrepend == null) ? "" : keyPrepend+"-";

		if(!paramMap.containsKey(keyPrepend+"length")){
			return this;
		}

		int length = Integer.parseInt(paramMap.get(keyPrepend+"length")[0]);

		for(int i = 0; i < length; i++){
			String filterParamKey = keyPrepend+i;
			String fieldName = paramMap.get(filterParamKey+"-field")[0];
			String valueString = paramMap.get(filterParamKey+"-value")[0];
			FilterCriteria.Comparison comparison = FilterCriteria.Comparison.EQUAL;
			if(paramMap.containsKey(filterParamKey+"-type")) {
				comparison = FilterCriteria.Comparison.valueOf(paramMap.get(filterParamKey+"-type")[0].toUpperCase());
			}

			Attribute attr = DataObjectInstance.getRelatedAttr(fieldName);
			Object value = null;
			if (attr.JavaType.equals(Date.class)) {
				value = Date.valueOf(valueString);
			}

			if (attr.JavaType.equals(String.class)) {
				value = valueString;
			}

			if (attr.JavaType.equals(Boolean.class) ||
					attr.JavaType.equals(boolean.class)) {
				value = (valueString.equalsIgnoreCase("true") || valueString.equalsIgnoreCase("1"));
			}

			if (attr.JavaType.equals(int.class) ||
					attr.JavaType.equals(Integer.class)){
				value = Integer.parseInt(valueString);
			}

			if (attr.JavaType.equals(float.class) ||
					attr.JavaType.equals(Float.class)){
				value = Float.parseFloat(valueString);
			}

			if (attr.JavaType.equals(double.class) ||
					attr.JavaType.equals(Double.class)){
				value = Double.parseDouble(valueString);
			}

			if (attr.JavaType.equals(BigDecimal.class)){
				value = new BigDecimal(valueString);
			}

			filterByField(fieldName, value, comparison);
		}

		return this;
	}

	public BaseObjectQuery<DataObject> filterByField(String fieldName, Object value) throws Exception {
		return filterByField(fieldName, value, FilterCriteria.Comparison.EQUAL);
	}

	public BaseObjectQuery<DataObject> filterByField(String fieldName, Object value, FilterCriteria.Comparison comparision) throws Exception{
			TheQueryCriteria.addSubFilter(getAliasedDatabaseFeild(fieldName), value, comparision);

			return this;
	}

	public String getAliasedDatabaseFeild(String fieldName) throws Exception{
		String[] attributeFields = fieldName.split("\\.");
		BaseObject relatedObjectToSortOn = DataObjectInstance;

		Attribute relatedAttr = null;
		StringBuilder tableAliasBuilder = new StringBuilder();
		for(String attrFieldName: attributeFields){
			relatedAttr = relatedObjectToSortOn.getRelatedAttr(attrFieldName);
			if(relatedAttr.isForeignEntity()){
				if(tableAliasBuilder.length() > 0){
					tableAliasBuilder.append("@");
				}
				relatedObjectToSortOn = (BaseObject) relatedAttr.JavaType.newInstance();
				tableAliasBuilder.append(relatedAttr.JavaFieldName);
				AttributesToJoin.put(tableAliasBuilder.toString(), relatedAttr);
			}
		}

		String tableAlias = (tableAliasBuilder.length() == 0) ? DataObjectInstance.getTableName() : tableAliasBuilder.toString();

		return  "`"+tableAlias+"`.`"+relatedAttr.DatabaseName+"`";
	}

	public BaseObjectQuery<DataObject> sortByField(String fieldName) throws Exception {
		return sortByField(fieldName, SorterCriteria.Direction.ASC);
	}

	public BaseObjectQuery<DataObject> sortByField(String fieldName, SorterCriteria.Direction direction) throws Exception{
			TheSorterCriteria.addSubSorter(getAliasedDatabaseFeild(fieldName), direction);

			return this;
	}

	public BaseObjectQuery<DataObject> populateRelation(String relationshipName) throws Exception{
		getAliasedDatabaseFeild(relationshipName);
		return this;
	}

	public BaseObjectQuery<DataObject> paginate(int page, int perPage){
		PerPage = perPage;
		Page = page;
		return this;
	}

	private BaseObject getNewDatabaseInstanceFromAliasedTableName(String aliasedTableName) throws Exception{
		String[] relationNames = aliasedTableName.split("@");
		BaseObject theInstance = DataObjectInstance;


		for(String relationName: relationNames){
			Attribute relatedAttr = theInstance.getRelatedAttr(relationName);
			theInstance = (BaseObject) relatedAttr.JavaType.newInstance();
		}

		return theInstance;
	}

	private String getJoinTypeFromAliasedTableName(String aliasedTableName) throws Exception{
		//ToDo:: Allow Specificity On Join Type When Populating Queries
		String[] relationNames = aliasedTableName.split("@");
		BaseObject objectInstance = DataObjectInstance;


		for(String relationName: relationNames){
			Attribute relatedAttr = objectInstance.getRelatedAttr(relationName);
			if(relatedAttr.ForeignEntityType == Attribute.ForeignRelationshipType.ONE_TO_MANY){
				return "LEFT JOIN";
			}
			objectInstance = (BaseObject) relatedAttr.JavaType.newInstance();
		}

		return "JOIN";
	}

	private String generateFromQuerySection() throws Exception{
		StringBuilder fromQueryStringBuilder = new StringBuilder();
		fromQueryStringBuilder.append("FROM ");
		fromQueryStringBuilder.append(DataObjectInstance.getTableName());

		if(AttributesToJoin.size() > 0){
			for (String foreignObjectAlias: AttributesToJoin.keySet()) {
				StringBuilder joinTablesBuilder = new StringBuilder();
				StringBuilder onAttributesBuilder = new StringBuilder();

				Attribute foreignAttribute = AttributesToJoin.get(foreignObjectAlias);
				String stringJoinType = getJoinTypeFromAliasedTableName(foreignObjectAlias);

				System.out.println(foreignObjectAlias);

				BaseObject foreignInstanceToFilterOn = (BaseObject) foreignAttribute.JavaType.newInstance();
				joinTablesBuilder.append(foreignInstanceToFilterOn.getTableName()).append(" `").append(foreignObjectAlias).append("`");

				int lastIndexOfRelationship = foreignObjectAlias.lastIndexOf("@");
				String localTableAlias = (lastIndexOfRelationship == -1) ? DataObjectInstance.getTableName() : foreignObjectAlias.substring(0, lastIndexOfRelationship);
				BaseObject localObjectToJoinOn = (lastIndexOfRelationship == -1) ? DataObjectInstance : getNewDatabaseInstanceFromAliasedTableName(localTableAlias);

				for (AttributeRelationship attrMap: foreignAttribute.ForeignEntityMap) {
					if(onAttributesBuilder.length() != 0){
						onAttributesBuilder.append(" AND ");
					}

					onAttributesBuilder.append("`");
					onAttributesBuilder.append(localTableAlias);
					onAttributesBuilder.append("`.`");
					onAttributesBuilder.append(localObjectToJoinOn.getRelatedAttr(attrMap.getLocalObjectFieldName()).DatabaseName);
					onAttributesBuilder.append("`");

					onAttributesBuilder.append(" = ");

					onAttributesBuilder.append("`");
					onAttributesBuilder.append(foreignObjectAlias);
					onAttributesBuilder.append("`.`");
					onAttributesBuilder.append(foreignInstanceToFilterOn.getRelatedAttr(attrMap.getForeignObjectFieldName()).DatabaseName);
					onAttributesBuilder.append("`");
				}

				fromQueryStringBuilder.append(" ");
				fromQueryStringBuilder.append(stringJoinType);
				fromQueryStringBuilder.append(" ");
				fromQueryStringBuilder.append(joinTablesBuilder.toString());
				fromQueryStringBuilder.append(" ON (");
				fromQueryStringBuilder.append(onAttributesBuilder.toString());
				fromQueryStringBuilder.append(")");
			}
		}

		return fromQueryStringBuilder.toString();
	}

	private String getAliasedColumnsToSelect() throws Exception{
		StringBuilder columnsToQueryStringBuilder = new StringBuilder();

		for(Attribute attr: DataObjectInstance.getAttributes()){
			if(attr.isForeignEntity()){
				continue;
			}

			if(columnsToQueryStringBuilder.length() > 0){
				columnsToQueryStringBuilder.append(", ");
			}

			columnsToQueryStringBuilder.append("`")
					.append(DataObjectInstance.getTableName())
					.append("`.`")
					.append(attr.DatabaseName)
					.append("` as `@")
					.append(attr.JavaFieldName)
					.append("`");
		}

		for (String foreignObjectAlias: AttributesToJoin.keySet()) {
			Attribute foreignAttribute = AttributesToJoin.get(foreignObjectAlias);
			BaseObject foreignInstance = (BaseObject) foreignAttribute.JavaType.newInstance();

			for(Attribute attr: foreignInstance.getAttributes()){
				if(attr.isForeignEntity()){
					continue;
				}

				if(columnsToQueryStringBuilder.length() > 0){
					columnsToQueryStringBuilder.append(", ");
				}

				columnsToQueryStringBuilder.append("`")
						.append(foreignObjectAlias)
						.append("`.`")
						.append(attr.DatabaseName)
						.append("` as `")
						.append(foreignObjectAlias)
						.append("@")
						.append(attr.JavaFieldName)
						.append("`");
			}

		}




		return columnsToQueryStringBuilder.toString();
	}

	public ObjectCollection find() throws Exception{
		String queryString = "SELECT "+getAliasedColumnsToSelect()+" "+generateFromQuerySection();
		if(TheQueryCriteria.getTotalFilterSize() > 0){
			queryString += " WHERE "+TheQueryCriteria.getCriteriaString();
		}
		if(TheSorterCriteria.getCriteriaSize() > 0){
			queryString += " ORDER BY "+TheSorterCriteria.getCriteriaString();
		}

		if(PerPage > 0){
			int lastItemOnOtherPage = PerPage*(Page - 1);
			queryString +=" LIMIT "+lastItemOnOtherPage+", "+PerPage;
		}
		queryString += ";";

		System.out.print(queryString);

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
		String queryString = "SELECT "+getAliasedColumnsToSelect()+" "+generateFromQuerySection();
		if(TheQueryCriteria.getTotalFilterSize() > 0){
			queryString += " WHERE "+TheQueryCriteria.getCriteriaString();
		}

		if(TheSorterCriteria.getCriteriaSize() > 0){
			queryString += " ORDER BY "+TheSorterCriteria.getCriteriaString();
		}

		boolean multipleRowsPerObject = false;
		for(Attribute attr : AttributesToJoin.values()){
			if(attr.ForeignEntityType == Attribute.ForeignRelationshipType.ONE_TO_MANY){
				multipleRowsPerObject = true;
				break;
			}
		}
		if(!multipleRowsPerObject){
			queryString +=" LIMIT 1";
		}
		queryString += ";";

		System.out.println(queryString);

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

	public int count() throws Exception{

		StringBuilder mainPksBuilder = new StringBuilder();
		for(Attribute attr : DataObjectInstance.getAttributes()){
			if(!attr.IsPrimaryKey){
				continue;
			}

			if(mainPksBuilder.length() != 0){
				mainPksBuilder.append(", ");
			}

			mainPksBuilder.append("`");
			mainPksBuilder.append(DataObjectInstance.getTableName());
			mainPksBuilder.append("`.`");
			mainPksBuilder.append(attr.DatabaseName);
			mainPksBuilder.append("`");

		}

		String subQuery = "SELECT "+mainPksBuilder.toString()+" "+generateFromQuerySection();

		if(TheQueryCriteria.getTotalFilterSize() > 0){
			subQuery += " WHERE "+TheQueryCriteria.getCriteriaString();
		}


		subQuery += " GROUP BY ";
		subQuery += mainPksBuilder.toString();

		String queryString = "SELECT COUNT(*) FROM ("+subQuery+")as rowCount;";

		System.out.println(queryString);

		PreparedStatement statement = ConnectionManager.prepareStatement(queryString);

		int filterCriteriaSize = TheQueryCriteria.getTotalFilterSize();
		for (int i = 0; i < filterCriteriaSize; i++){
			statement.setObject(i + 1, TheQueryCriteria.getFilterValue(i));
		}

		statement.execute();

		ResultSet results = statement.getResultSet();
		results.first();
		int count = results.getInt(1);

		statement.close();

		return count;
	}

	private ObjectCollection populateCollectionFromObjectResult(ResultSet resultSet) throws Exception {
		ObjectCollection collection = new ObjectCollection();

		ResultSetMetaData md = resultSet.getMetaData();
		int colMax = md.getColumnCount() + 1;
		while (resultSet.next()){
			HashMap<String, BaseObject> rowObjects = new HashMap<>(AttributesToJoin.size()+1);

			for(int i = 1; i < colMax; i++){
				String colLabel = md.getColumnLabel(i);
				int lastAtSymbol = colLabel.lastIndexOf("@");
				boolean isFromBaseTable = (lastAtSymbol == 0) || (md.getTableName(i).equals(DataObjectInstance.getTableName()) && lastAtSymbol == -1);
				String rowObjectKey = colLabel.substring(0, lastAtSymbol);
				String rowObjectField = colLabel.substring(lastAtSymbol+1);

				if(resultSet.getObject(i) == null){
					continue;
				}

				BaseObject aRowObject = (rowObjects.containsKey(rowObjectKey)) ?
						rowObjects.get(rowObjectKey) :
						(isFromBaseTable) ?
							DataObjectClass.newInstance() :
							(BaseObject) AttributesToJoin.get(rowObjectKey).JavaType.newInstance();

				Attribute fieldAttribute = aRowObject.getRelatedAttr(rowObjectField);
				aRowObject.setField(rowObjectField, fieldAttribute.JavaType.cast(resultSet.getObject(i)));

				rowObjects.put(rowObjectKey, aRowObject);
			}

			for(String rowObjectKey: AttributesToJoin.keySet()){
				BaseObject aForeignRowObject = rowObjects.get(rowObjectKey);
				if(aForeignRowObject == null){
					continue;
				}

				aForeignRowObject.IsCreating = false;
				aForeignRowObject.ModifiedAttributes.clear();

				int endOfParentKey = (rowObjectKey.lastIndexOf("@") == -1) ? 0 : rowObjectKey.lastIndexOf("@");
				BaseObject localObject = rowObjects.get(rowObjectKey.substring(0, endOfParentKey));
				Attribute localToForeignAttr = AttributesToJoin.get(rowObjectKey);
				String foreignRelationName = rowObjectKey.substring(rowObjectKey.lastIndexOf("@") + 1);

				switch (localToForeignAttr.ForeignEntityType){
					case ONE_TO_ONE:
					case MANY_TO_ONE:
						localObject.setField(foreignRelationName, aForeignRowObject);
						break;
					case ONE_TO_MANY:
						ObjectCollection collectionForeignEntries  = (ObjectCollection) localObject.getField(foreignRelationName);
						collectionForeignEntries = (collectionForeignEntries == null) ? new ObjectCollection() : collectionForeignEntries;
						collectionForeignEntries.add(aForeignRowObject);
						localObject.setField(foreignRelationName, collectionForeignEntries);
						break;
				}
			}

			BaseObject item = rowObjects.get("");

			item.IsCreating = false;
			item.ModifiedAttributes.clear();


			collection.add(item);
		}

		return collection;
	}
}
