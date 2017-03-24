package main.java.models;

import main.java.models.base.Attribute;
import main.java.models.base.BaseObjectQuery;

import java.util.List;

/**
 * Created by StudentNathan on 3/21/2017.
 */
public class ExampleObjectQuery extends BaseObjectQuery<ExampleObject> {

	public ExampleObjectQuery() {
		super(ExampleObject.class);
	}
}
