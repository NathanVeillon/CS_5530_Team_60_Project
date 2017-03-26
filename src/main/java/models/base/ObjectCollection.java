package main.java.models.base;

import java.util.*;

/**
 * Created by StudentNathan on 3/21/2017.
 */
public class ObjectCollection extends ArrayList<BaseObject> {
	@Override
	public boolean add(BaseObject baseObject) {
		int i = this.indexOf(baseObject);
		if(i == -1) {
			return super.add(baseObject);
		}else {
			this.set(i, baseObject);
			return false;
		}
	}
}
