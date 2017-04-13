package main.java.models.base;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by StudentNathan on 3/30/2017.
 */
public class FilterCriteria {
	public enum Comparison {EQUAL, NOT_EQUAL, GREATER_THAN, LESS_THAN, GREATER_EQUAL, LESS_EQUAL}
	public enum GroupAdhesive {AND, OR}

	private String FilterDatabaseName;
	private Object FilterValue;
	private Comparison FilterComparison;

	List<FilterCriteria> SubCriteria = new ArrayList<>();
	GroupAdhesive SubCriteriaAdhesive;


	public FilterCriteria(String fieldName, Object value, Comparison comparison){
		FilterDatabaseName = fieldName;
		FilterValue = value;
		FilterComparison = comparison;
	}

	public FilterCriteria(GroupAdhesive subCriteriaAdhesive, FilterCriteria... theCriteria){
		SubCriteriaAdhesive = subCriteriaAdhesive;
		for (FilterCriteria aCriteria: theCriteria) {
			SubCriteria.add(aCriteria);
		}
	}

	public void addSubFilter(FilterCriteria aCriteria){
		if(SubCriteriaAdhesive == null){
			SubCriteriaAdhesive = GroupAdhesive.AND;
			SubCriteria.add(new FilterCriteria(FilterDatabaseName, FilterValue, FilterComparison));
			FilterDatabaseName = null;
			FilterValue = null;
			FilterComparison = null;
		}
		SubCriteria.add(aCriteria);
	}

	public void addSubFilter(String databaseName, Object value, Comparison comparison){
		addSubFilter(new FilterCriteria(databaseName, value, comparison));
	}

	public int getTotalFilterSize(){
		if(SubCriteriaAdhesive == null){
			return 1;
		}

		int size = 0;
		for (FilterCriteria subCriteria: SubCriteria) {
			size += subCriteria.getTotalFilterSize();
		}

		return size;
	}

	public Object getFilterValue(int criteriaIndex) throws IndexOutOfBoundsException{
		if(criteriaIndex >= getTotalFilterSize()){
			throw new IndexOutOfBoundsException();
		}

		if(SubCriteriaAdhesive == null && criteriaIndex == 0){
			return FilterValue;
		}

		for (FilterCriteria criteria: SubCriteria) {
			if(criteriaIndex >= criteria.getTotalFilterSize()){
				criteriaIndex -= criteria.getTotalFilterSize();
				continue;
			}

			return criteria.getFilterValue(criteriaIndex);
		}

		throw new IndexOutOfBoundsException();
	}

	public String getCriteriaString(){
		StringBuilder criteriaStringBuilder = new StringBuilder();
		if(SubCriteriaAdhesive == null){
			return FilterDatabaseName+" "+getComparisonOperator()+" ?";
		}

		if(SubCriteria.size() == 0){
			return "";
		}

		for (FilterCriteria subCriteria: SubCriteria) {
			if(criteriaStringBuilder.length() != 0){
				criteriaStringBuilder.append(" ");
				criteriaStringBuilder.append(SubCriteriaAdhesive);
				criteriaStringBuilder.append(" ");
			}

			criteriaStringBuilder.append(subCriteria.getCriteriaString());
		}

		return "("+criteriaStringBuilder.toString()+")";
	}

	private String getComparisonOperator(){
		switch (FilterComparison){
			case EQUAL:
				return "=";
			case NOT_EQUAL:
				return "=";
			case GREATER_THAN:
				return ">";
			case GREATER_EQUAL:
				return ">=";
			case LESS_THAN:
				return "<";
			case LESS_EQUAL:
				return "<=";
			default:
				return "";
		}
	}

}
