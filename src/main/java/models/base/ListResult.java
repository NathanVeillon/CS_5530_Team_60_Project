package main.java.models.base;

/**
 * Created by Student Nathan on 4/21/2017.
 */
public class ListResult {
	public Integer Count;
	public Integer Page;
	public Integer PerPage;
	public ObjectCollection Results;

	public String toJson(){
		String jsonString = "{";
		jsonString += "\"count\": "+Count+",";
		jsonString += "\"page\": "+Page+",";
		jsonString += "\"perPage\": "+PerPage+",";
		jsonString += "\"results\": "+Results.toJson();
		jsonString += "}";

		return jsonString;
	}
}
