package main.java;

import main.java.managers.UserManager;
import main.java.models.User;
import main.java.managers.ConnectionManager;
import main.java.models.base.ObjectCollection;
import main.java.models.UserQuery;
import main.java.view.OwnedTemporaryHousingPage;
import main.java.view.UserReservationsPage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;


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
								stmnt.close();
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
						OwnedTemporaryHousingPage.indexAction();
						break;

					case MAKE_RESERVATION_CODE:
						// TODO
						UserReservationsPage.indexAction();
						break;

					case RECORD_STAY_CODE:
						// TODO
						printWIPNote();
						break;

					case MANAGE_FAVORITES_CODE:
						handleFavorites();
						break;

					case GIVE_FEEDBACK_CODE:
						handleGiveFeedback();
						break;

					case MANAGE_TRUSTED_USERS_CODE:
						// TODO
						printWIPNote();
						break;

					case BROWSE_FOR_HOUSING_CODE:
						handleBrowse();
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
	
	public static void handleBrowse() throws IOException, SQLException {
		System.out.println("Browsing temporary housing...");
		while (true) {
			System.out.println("Please provide one of the following:");
			System.out.println("Please enter price range (for example \"500-2000\" or \"N/A\":");
			String priceRange = Input.readLine();
			System.out.println("Please enter address (for example \"Wasatch Road\" or \"SLC\") or \"N/A\":");
			String address = Input.readLine();
			System.out.println("Please enter keywords separated by commas (for example \"New\" or \"Studio,DogFriendly,SchoolNearby\") or \"N/A\":");
			String keywords = Input.readLine();
			System.out.println("Please enter category (for example \"House\") or \"N/A\":");
			String category = Input.readLine();
			if ((priceRange == null || priceRange.equalsIgnoreCase("N/A")) && (address == null || address.equalsIgnoreCase("N/A")) && (keywords == null || keywords.equalsIgnoreCase("N/A")) && (category == null || category.equalsIgnoreCase("N/A"))) {
				System.out.println(INVALID_USER_RESPONSE);
				return;
			}
			
			StringBuilder query = new StringBuilder();
			query.append("SELECT DISTINCT th.idTH, th.category, th.name, th.address, th.expectedPrice, th.url, th.phoneNumber, th.yearBuilt, u.name FROM TemporaryHousing AS th JOIN Users AS u ON th.idOwner = u.idUser LEFT JOIN HasKeywords AS hk ON th.idTH=hk.idTH LEFT JOIN Keywords AS k ON hk.idKeywords=k.idKeywords WHERE ");
			boolean addAnd = false;
			if (priceRange != null && !priceRange.equalsIgnoreCase("N/A")) {
				String[] rangeSplit = priceRange.split("-");
				query.append(" th.expectedPrice >= ");
				query.append(rangeSplit[0]);
				query.append(" AND ");
				query.append(" th.expectedPrice <= ");
				query.append(rangeSplit[1]);
				query.append(" ");
				addAnd = true;
			}
			if (address != null && !address.equalsIgnoreCase("N/A")) {
				if (addAnd) query.append(" AND ");
				query.append(" th.address LIKE '%");
				query.append(address);
				query.append("%' ");
				addAnd = true;
			}
			if (category != null && !category.equalsIgnoreCase("N/A")) {
				if (addAnd) query.append(" AND ");
				query.append(" th.category LIKE '%");
				query.append(category);
				query.append("%' ");
				addAnd = true;
			}
			if (keywords != null && !keywords.equalsIgnoreCase("N/A")) {
				if (addAnd) query.append(" AND ");
				query.append("(");
				String[] keywordsSplit = keywords.split(",");
				boolean addOr = false;
				for (String key : keywordsSplit) {
					if (addOr) query.append(" OR ");
					query.append(" k.word LIKE '%");
					query.append(key);
					query.append("%' ");
					addOr = true;
				}
				query.append(")");
			}
			PreparedStatement statement = ConnectionManager.prepareStatement(query.toString());
			ResultSet rs = statement.executeQuery();
			if (!rs.isBeforeFirst()) {
				System.out.println("No Matches...");
				return;
			}
			boolean space = false;
			while (rs.next()) {
				if (space) System.out.println();
				System.out.println("ID: " + rs.getString("th.idTH"));
				System.out.println("Category: " + rs.getString("th.category"));
				System.out.println("Name: " + rs.getString("th.name"));
				System.out.println("Address: " + rs.getString("th.address"));
				System.out.println("Price: " + rs.getString("th.expectedPrice"));
				System.out.println("URL: " + rs.getString("th.url"));
				System.out.println("Phone Number: " + rs.getString("th.phoneNumber"));
				System.out.println("Year Built: " + rs.getString("th.yearBuilt"));
				System.out.println("Owner: " + rs.getString("u.name"));
				space = true;
			}
			return;
		}
	}
	
	public static void handleFavorites() {
		try {
			while (true) {
				String query = "SELECT DISTINCT th.idTH, th.category, th.name, th.address, th.expectedPrice, th.url, th.phoneNumber, th.yearBuilt FROM TemporaryHousing AS th JOIN Favorites AS f ON th.idTH = f.idTH WHERE f.idUser=" + UserManager.getCurrentUser().getId();
				PreparedStatement statement = ConnectionManager.prepareStatement(query);
				ResultSet rs = statement.executeQuery();
				Set<String> alreadyFavorite = new HashSet<String>();
				if (!rs.isBeforeFirst()) {
					System.out.println("You currently have no favorites...");
				} else {
					System.out.println("Here are your current favorites:");
					boolean space = false;
					while (rs.next()) {
						if (space) System.out.println();
						alreadyFavorite.add(rs.getString("th.idTH"));
						System.out.println("ID: " + rs.getString("th.idTH"));
						System.out.println("Category: " + rs.getString("th.category"));
						System.out.println("Name: " + rs.getString("th.name"));
						System.out.println("Address: " + rs.getString("th.address"));
						System.out.println("Price: " + rs.getString("th.expectedPrice"));
						System.out.println("URL: " + rs.getString("th.url"));
						System.out.println("Phone Number: " + rs.getString("th.phoneNumber"));
						System.out.println("Year Built: " + rs.getString("th.yearBuilt"));
						space = true;
					}
				}
				
				
				System.out.println("Please enter ID of housing you would like to give favorite:");
				String id = Input.readLine();
				System.out.println("Please wait...");
				if (id == null || id.length() < 0) {
					System.out.println(INVALID_USER_RESPONSE);
					return;
				}
				if (alreadyFavorite.contains(id)) {
					System.out.println("That housing is already in your favorites");
					return;
				}
				query = "INSERT INTO Favorites (idUser, idTH, date) VALUES (" + UserManager.getCurrentUser().getId() + ", " + id + ", CURDATE())";
				statement = ConnectionManager.prepareStatement(query);
				ConnectionManager.startTransaction();
				statement.executeUpdate();
				ConnectionManager.commitAll();
				System.out.println("Housing is now in your favorites");
				return;
			}
		} catch (Exception e) {
			System.out.println("Error adding favorites, please make sure ID is valid");
			e.printStackTrace();
			return;
		}
	}
	
	public static void handleGiveFeedback() {
		try {
			while (true) {
				System.out.println("Please enter ID of housing you would like to give feedback:");
				String id = Input.readLine();
				System.out.println("Please wait...");
				if (id == null || id.length() < 0) {
					System.out.println(INVALID_USER_RESPONSE);
					return;
				}
				String query = "SELECT idTH FROM TemporaryHousing WHERE idTH=" + id;
				PreparedStatement statement = ConnectionManager.prepareStatement(query);
				ResultSet rs = statement.executeQuery();
				if (!rs.isBeforeFirst()) {
					System.out.println("No Matches...");
					return;
				}
				query = "SELECT idfeedback FROM feedback WHERE idTH=" + id + " AND idUser=" + UserManager.getCurrentUser().getId();
				statement = ConnectionManager.prepareStatement(query);
				rs = statement.executeQuery();
				if (rs.isBeforeFirst()) {
					System.out.println("You have already left feedback for this housing...");
					return;
				}
				
				System.out.println("Please enter score (0-10):");
				String score = Input.readLine();
				int scoreParsed = -1;
				try {
					scoreParsed = Integer.parseInt(score);
				} catch (NumberFormatException e) {
					System.out.println(INVALID_USER_RESPONSE);
					continue;
				}
				if (scoreParsed < 0 || scoreParsed > 10) {
					System.out.println(INVALID_USER_RESPONSE);
					continue;
				}
				System.out.println("Please enter text (optional):");
				String text = Input.readLine();
				if (text == null) {
					text = "";
				}
				query = "INSERT INTO feedback (score, text, date, idUser, idTH) VALUES (" + scoreParsed + ", '" + text + "', CURDATE(), " + UserManager.getCurrentUser().getId() + ", " + id + ")";
				statement = ConnectionManager.prepareStatement(query);
				ConnectionManager.startTransaction();
				statement.executeUpdate();
				ConnectionManager.commitAll();
				System.out.println("Thank you for leaving feedback");
				return;
			}
		} catch (Exception e) {
			System.out.println("Error giving feedback, please make sure ID is valid");
			e.printStackTrace();
			return;
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
		System.out.println(RECORD_STAY_CODE + ". Record A Stay (NOT FUNCTIONAL)");
		System.out.println(MANAGE_FAVORITES_CODE + ". Manage Favorites");
		System.out.println(GIVE_FEEDBACK_CODE + ". Give Feedback");
		System.out.println(MANAGE_TRUSTED_USERS_CODE + ". Manage Trusted Users (NOT FUNCTIONAL)");
		System.out.println(BROWSE_FOR_HOUSING_CODE + ". Browse Housing");
		System.out.println(EXIT_CODE + ". Exit");
		System.out.print("Please enter your choice:");
	}
}
