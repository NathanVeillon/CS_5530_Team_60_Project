package main.java.models.base;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by StudentNathan on 3/30/2017.
 */
public class SorterCriteria {
	public enum Direction {ASC, DESC}

	private String SorterDatabaseName;
	private Direction SorterDirection;

	List<SorterCriteria> SubCriteria = new ArrayList<>();


	public SorterCriteria(String fieldName){
		SorterDatabaseName = fieldName;
		SorterDirection = Direction.ASC;
	}
	public SorterCriteria(String fieldName, Direction comparison){
		SorterDatabaseName = fieldName;
		SorterDirection = comparison;
	}

	public SorterCriteria(){
	}

	public SorterCriteria(SorterCriteria... theCriteria){
		SubCriteria.addAll(Arrays.asList(theCriteria));
	}

	public void addSubSorter(SorterCriteria aCriteria){
		if(SorterDatabaseName != null){
			SubCriteria.add(new SorterCriteria(SorterDatabaseName, SorterDirection));
			SorterDatabaseName = null;
			SorterDirection = null;
		}
		SubCriteria.add(aCriteria);
	}

	public void addSubSorter(String databaseName, Direction direction){
		addSubSorter(new SorterCriteria(databaseName, direction));
	}

	public String getCriteriaString(){
		StringBuilder criteriaStringBuilder = new StringBuilder();
		if(SorterDatabaseName != null){
			return SorterDatabaseName +" "+SorterDirection.name();
		}

		if(SubCriteria.size() == 0){
			return "";
		}

		for (SorterCriteria subCriteria: SubCriteria) {
			if(criteriaStringBuilder.length() != 0 && subCriteria.SorterDatabaseName != null){
				criteriaStringBuilder.append(", ");
			}

			criteriaStringBuilder.append(subCriteria.getCriteriaString());
		}

		return criteriaStringBuilder.toString();
	}

	public int getCriteriaSize(){
		if(SorterDatabaseName != null){
			return 1;
		}

		int size = 0;
		for (SorterCriteria subCriteria: SubCriteria) {
			size += subCriteria.getCriteriaSize();
		}

		return size;
	}

}
