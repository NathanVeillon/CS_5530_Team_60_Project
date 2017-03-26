package main.java.models;

import main.java.models.base.BaseObjectQuery;

/**
 * Created by StudentNathan on 3/25/2017.
 */
public class UserQuery extends BaseObjectQuery<User> {
	public UserQuery() {
		super(User.class);
	}
}
