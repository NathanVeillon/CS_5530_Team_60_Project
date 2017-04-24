package main.java.models.base;

import javax.smartcardio.ATR;
import java.util.*;

/**
 * Created by StudentNathan on 3/21/2017.
 */
public class ObjectCollection extends ArrayList<BaseObject> {
	@Override
	public boolean add(BaseObject baseObject) {
		if(baseObject.IsCreating)
			return super.add(baseObject);


		int i = this.indexOf(baseObject);
		if(i == -1) {
			return super.add(baseObject);
		}else {
			this.get(i).mergeIntoThis(baseObject);
			return false;
		}
	}

	public BaseObject getItemInCollection(BaseObject baseObject){
		int i = this.indexOf(baseObject);
		if(i == -1) {
			return null;
		}

		return this.get(i);
	}

	public void save() throws Exception{
		for (BaseObject object: this){
			object.save();
		}
	}

	public String toJson(){
		StringBuilder jsonBuilder = new StringBuilder();

		jsonBuilder.append("[");
		for(BaseObject object: this){
			if(jsonBuilder.length() > 1){
				jsonBuilder.append(", ");
			}
			jsonBuilder.append(object.toJson());
		}
		jsonBuilder.append("]");

		return jsonBuilder.toString();
	}


	public boolean fromFlatJsonMap(Map<String, String[]> paramMap, String keyPrepend, Class objectType) throws Exception{
		if(!BaseObject.class.isAssignableFrom(objectType)){
			return false;
		}

		System.out.println("ASDFASDFASDFASDAF");

		keyPrepend = (keyPrepend == null || keyPrepend.length() == 0) ? "" : keyPrepend+"-";

		if(!paramMap.containsKey(keyPrepend+"CollectionLength")){
			return false;
		}

		int length = Integer.parseInt(paramMap.get(keyPrepend+"CollectionLength")[0]);

		for(int i = 0; i < length; i++){
			BaseObject newObject = (BaseObject) objectType.newInstance();
			if(!newObject.isValidForJsonMap(paramMap, keyPrepend+i)){
				return false;
			}
			newObject.fromFlatJsonMap(paramMap, keyPrepend+i);
			this.add(newObject);
		}

		return true;
	}

	public void printTable(String[] labels, String[] fieldsToPrint){
		if(labels.length != fieldsToPrint.length) {
			System.out.println("ERROR: MISMATCH BETWEEN HEADER LENGTH AND DATA COLUMNS TO PRINT");
			return;
		}

		int[] maxWidth = new int[labels.length];
		for (int j = 0; j < labels.length; j++){
			try {
				maxWidth[j] = Math.max(maxWidth[j], labels[j].length());
			} catch (Exception e) {
				maxWidth[j] = Math.max(maxWidth[j], labels[j].length());
			}
		}
		for (int i = 0; i < this.size();  i++){
			for (int j = 0; j < fieldsToPrint.length; j++){
				try {
					Object value = this.get(i).getField(fieldsToPrint[j]);
					String string = (value == null) ? "-" : value.toString();
					maxWidth[j] = Math.max(maxWidth[j], string.length());
				} catch (Exception e) {
					maxWidth[j] = Math.max(maxWidth[j], ("ERROR: "+e.getMessage()).length());
				}
			}
		}

		ArrayList<String> thickHorizontalBorders = new ArrayList<>();
		ArrayList<String> thinHorizontalBorders = new ArrayList<>();
		for(int j = 0; j < fieldsToPrint.length; j++) {
			thickHorizontalBorders.add(String.join("", Collections.nCopies(maxWidth[j],"-")));
			thinHorizontalBorders.add(String.join("", Collections.nCopies(maxWidth[j],"-")));
		}
		System.out.println("╔"+String.join("╤", thickHorizontalBorders)+"╗");
		String labelsStr = "║";
		for (int j = 0; j < labels.length;  j++) {
			int padding = maxWidth[j] - labels[j].length();
			labelsStr += labels[j]+String.join("", Collections.nCopies(padding," "));
			if(j != fieldsToPrint.length - 1)
				labelsStr += "│";
		}
		System.out.println(labelsStr+"║");
		System.out.println("╟"+String.join("┼", thinHorizontalBorders)+"╢");

		for (int i = 0; i < this.size();  i++){
			String toPrint = "║";
			for (int j = 0; j < fieldsToPrint.length; j++){
				String text;
				if(fieldsToPrint[j].equals("")) {
					text = Integer.toString(i + 1);
				}else {
					try {
						Object value = this.get(i).getField(fieldsToPrint[j]);
						text = (value == null) ? "-" : value.toString();
					} catch (Exception e) {
						text = "ERROR: "+e.getMessage();
					}
				}
				int padding = maxWidth[j] - text.length();
				toPrint += text+String.join("", Collections.nCopies(padding," "));
				if(j != fieldsToPrint.length - 1)
					toPrint += "│";
			}
			System.out.println(toPrint+"║");
		}

		System.out.println("╚"+String.join("╧", thickHorizontalBorders)+"╝");

	}
}
