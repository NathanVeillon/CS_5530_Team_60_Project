package main.java.models.base;

import java.awt.*;
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
			this.set(i, baseObject);
			return false;
		}
	}

	public void save() throws Exception{
		for (BaseObject object: this){
			object.save();
		}
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
