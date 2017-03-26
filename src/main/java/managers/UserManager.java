package main.java.managers;

import main.java.models.User;

/**
 * Created by StudentNathan on 3/26/2017.
 */
public class UserManager {
	private static User CurrentUser = null;

	public static void setCurrentUser(User user){
		CurrentUser = user;
	}

	public static User getCurrentUser() throws Exception{
		if(CurrentUser == null)
			throw new Exception("No User Is Logged In.");
		return CurrentUser;
	}

	public static boolean isUserLoggedIn(){
		return (CurrentUser != null);
	}
}
