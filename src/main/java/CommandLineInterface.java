package main.java;

import main.java.managers.UserManager;
import main.java.models.TemporaryHousing;
import main.java.models.User;
import main.java.managers.ConnectionManager;
import main.java.models.base.ObjectCollection;
import main.java.models.UserQuery;
import main.java.view.TemporaryHousingPage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.PreparedStatement;


public class CommandLineInterface {
	
	private static final int EXIT_CODE = 0;
	private static final int LOGIN_CODE = 1;
	private static final int REGISTER_CODE = 2;
	private static final int OWNED_HOUSING_CODE = 1;
	private static final int MAKE_RESERVATION_CODE = 2;
	private static final int RECORD_STAY_CODE = 3;
	private static final int MANAGE_FAVORITES_CODE = 4;
	private static final int GIVE_FEEDBACK_CODE = 5;
	private static final int MANAGE_TRUSTED_USERS_CODE = 6;
	private static final int BROWSE_FOR_HOUSING_CODE = 7;
	
	public static final String INVALID_USER_RESPONSE = "Invalid response...";

	public static final BufferedReader Input = new BufferedReader(new InputStreamReader(System.in));

	public final static void main(String[] argv) {
		printGreeting();
		try {
			System.out.println("Attempting to connect to database...");
			ConnectionManager.init("5530u60","jure0kku", "jdbc:mysql://georgia.eng.utah.edu", "5530db60");
			System.out.println("Connection with database established...");

			int code;

			while (!UserManager.isUserLoggedIn()) {
				printAccountSignInMenu();
				code = getUserInt();
				switch (code){
					case EXIT_CODE:
						ConnectionManager.closeConnection();
						return;

					case LOGIN_CODE:
						while (true){
							String enteredUsername;
							String enteredPassword;
							try {
								System.out.println("Please enter your username and password");
								System.out.print("Username:");
								enteredUsername = Input.readLine();
								System.out.print("Password:");
								enteredPassword = Input.readLine();
								boolean validUsername = enteredUsername != null && enteredUsername.length() > 0;
								boolean validPassword = enteredPassword != null && enteredPassword.length() > 0;
								if (!validUsername || !validPassword) {
									System.out.println(INVALID_USER_RESPONSE);
									continue;
								}
							}catch (Exception e){
								System.out.println(INVALID_USER_RESPONSE);
								continue;
							}

							String query = "SELECT idUser FROM Users WHERE login = ? AND password = ?;";
							try {
								PreparedStatement stmnt = ConnectionManager.prepareStatement(query);
								stmnt.setString(1, enteredUsername);
								stmnt.setString(2, enteredPassword);
								UserQuery userQuery = new UserQuery();
								ObjectCollection UserCollection = userQuery.getCollectionFromObjectResult(stmnt.executeQuery());
								if (UserCollection.size() == 0) {
									System.out.println("Account does not exist");
									break;
								}
								UserManager.setCurrentUser((User) UserCollection.get(0));
							}catch (Exception e) {
								System.out.println("Unexpected Error");
								e.printStackTrace();
								break;
							}

							break;
						}
						break;

					case REGISTER_CODE:
						while (true) {
							User newUser = new User();
							try {
								System.out.println("Please enter your User Info.");
								System.out.print("Desired Username:");
								newUser.setLogin(Input.readLine());
								System.out.print("Desired Password:");
								newUser.setPassword(Input.readLine());
								System.out.print("Desired Name:");
								newUser.setName(Input.readLine());
								System.out.print("Desired Address:");
								newUser.setAddress(Input.readLine());
								System.out.print("Desired Phone Number (Eg. \"+X (XXX) XXX-XXXX\"):");
								newUser.setPhoneNumber(Input.readLine());
								newUser.setIsAdmin(false);
							} catch (Exception e) {
								e.printStackTrace();
								System.out.println(INVALID_USER_RESPONSE);
								break;
							}

							try {
								ConnectionManager.startTransaction();
								newUser.save();
								ConnectionManager.commit();

								UserManager.setCurrentUser(newUser);
							} catch (Exception e) {
								ConnectionManager.rollback();
								System.out.println("Invalid user credentials (please make sure login is unique and all fields are included)...");
								System.out.println("Error:" + e.getMessage());
							}

							break;
						}
						break;


					default:
						System.out.println(INVALID_USER_RESPONSE);
					case -1:
						break;
				}
			}
			
			System.out.println("Login Successful");
			
			while (true) {
				printMenu();
				code = getUserInt();

				switch (code) {
					case EXIT_CODE:
						ConnectionManager.closeConnection();
						return;

					case OWNED_HOUSING_CODE:
						TemporaryHousingPage.indexAction();
						break;

					case MAKE_RESERVATION_CODE:
						// TODO
						printWIPNote();
						break;

					case RECORD_STAY_CODE:
						// TODO
						printWIPNote();
						break;

					case MANAGE_FAVORITES_CODE:
						// TODO
						printWIPNote();
						break;

					case GIVE_FEEDBACK_CODE:
						// TODO
						printWIPNote();
						break;

					case MANAGE_TRUSTED_USERS_CODE:
						// TODO
						printWIPNote();
						break;

					case BROWSE_FOR_HOUSING_CODE:
						// TODO
						printWIPNote();
						break;

					default:
						System.out.println(INVALID_USER_RESPONSE);
					case -1:
						break;

				}
			}

		} catch (Exception e) {
			System.out.println("An unexpected error occurred...");
			e.printStackTrace();
			ConnectionManager.closeConnection();
		}
	}

	public static int getUserInt(){
		try {
			String userResponse = Input.readLine();
			return Integer.parseInt(userResponse);
		} catch (IOException e) {
			System.out.println("Unable to read from input.");
		} catch (NumberFormatException e) {
			System.out.println("Please just enter a number.");
		}

		return -1;

	}

	public static boolean confirm(String confirmMessage){
		System.out.println(confirmMessage);
		String[] possibleConfirmations = {"Y", "YES", "OK", "CONTINUE"};
		try {
			String input = Input.readLine().toUpperCase();
			for(String possibleConf: possibleConfirmations){
				if(possibleConf.equals(input)){
					return true;
				}
			}
			return false;
		} catch (IOException e) {
			return false;
		}
	}
	
	private static void printGreeting() {
		System.out.println("Welcome to Uotel");
	}
	
	private static void printWIPNote() {
		System.out.println("This feature is not yet implemented...");
	}
	
	private static void printAccountSignInMenu() {
		System.out.println(LOGIN_CODE + ". Login");
		System.out.println(REGISTER_CODE + ". Register New Account");
		System.out.println(EXIT_CODE + ". Exit");
		System.out.print("Please enter your choice:");
	}
	
	private static void printMenu() {
		System.out.println(OWNED_HOUSING_CODE + ". Owned Housing (Create And Update Temp. Housing)");
		System.out.println(MAKE_RESERVATION_CODE + ". Make Reservation");
		System.out.println(RECORD_STAY_CODE + ". Record A Stay");
		System.out.println(MANAGE_FAVORITES_CODE + ". Manage Favorites");
		System.out.println(GIVE_FEEDBACK_CODE + ". Give Feedback");
		System.out.println(MANAGE_TRUSTED_USERS_CODE + ". Manage Trusted Users");
		System.out.println(BROWSE_FOR_HOUSING_CODE + ". Browse Housing");
		System.out.println(EXIT_CODE + ". Exit");
		System.out.print("Please enter your choice:");
	}
}
