package project;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Interface {
	
	private static final int EXIT_CODE = 0;
	private static final int LOGIN_CODE = 1;
	private static final int REGISTER_CODE = 2;
	
	private static final String INVALID_USER_RESPONSE = "Invalid response...";
	
	public final static void main(String[] argv) {
		printGreeting();
		Connector connector = null;
		try {
			System.out.println("Attempting to connect to database...");
			connector = new Connector();
			System.out.println("Connection with database established...");
			
			BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
			String userResponse;
			String[] parsedResponse;
			int code;
			
			String idUser;
			while (true) {
				printAccountSignInMenu();
				userResponse = input.readLine();
				if (userResponse != null && userResponse.length() > 0) {
					try {
						code = Integer.parseInt(userResponse);
					} catch (NumberFormatException e) {
						System.out.println(INVALID_USER_RESPONSE);
						continue;
					}
					if (code == EXIT_CODE) {
						connector.kill();
						return;
					} else if (code == LOGIN_CODE) {
						boolean success = false;
						while (true) {
							System.out.println("Please enter your login and password separated by a comma");
							System.out.println("For example: JohnDoe,password");
							userResponse = input.readLine();
							if (userResponse != null && userResponse.length() > 0 && userResponse.indexOf(',') != -1) {
								parsedResponse = userResponse.split(",");
								String query = "SELECT idUser FROM Users WHERE login='" + parsedResponse[0] + "' AND password='" + parsedResponse[1] + "'";
								ResultSet rs = connector.statement.executeQuery(query);
								if (!rs.isBeforeFirst()) {
									System.out.println("Account does not exist");
									break;
								}
								while (rs.next()) {
									idUser = rs.getString("idUser");
								}
								success = true;
								break;
							} else {
								System.out.println(INVALID_USER_RESPONSE);
								continue;
							}
						}
						if (!success) {
							continue;
						} else {
							break;
						}
					} else if (code == REGISTER_CODE) {
						boolean success = false;
						while (true) {
							System.out.println("Please enter your login, name, password, address, and phone number separated by commas");
							System.out.println("For example: JohnDoe,John Doe,password,123 fake street,123-123-1234");
							userResponse = input.readLine();
							if (userResponse != null && userResponse.length() > 0 && userResponse.indexOf(',') != -1) {
								parsedResponse = userResponse.split(",");
								String query = "INSERT INTO Users (login, name, password, address, phoneNumber, isAdmin) VALUES ('" + parsedResponse[0] + "', '" + parsedResponse[1] + "', '" + parsedResponse[2] + "', '" + parsedResponse[3] + "', '" + parsedResponse[4] + "', 'N')";
								connector.statement.executeUpdate(query);
								query = "SELECT idUser FROM Users WHERE login='" + parsedResponse[0] + "' AND password='" + parsedResponse[2] + "'";
								ResultSet rs = connector.statement.executeQuery(query);
								if (!rs.isBeforeFirst()) {
									System.out.println("Account does not exist");
									break;
								}
								while (rs.next()) {
									idUser = rs.getString("idUser");
								}
								success = true;
								break;
							} else {
								System.out.println(INVALID_USER_RESPONSE);
								continue;
							}
						}
						if (!success) {
							continue;
						} else {
							break;
						}
					} else {
						System.out.println(INVALID_USER_RESPONSE);
						continue;
					}
				} else {
					System.out.println(INVALID_USER_RESPONSE);
					continue;
				}
			}
			
			System.out.println("Login Successful");
			
			connector.kill();
		} catch (Exception e) {
			System.out.println("An unexpected error occurred...");
			e.printStackTrace();
		} finally {
			try {
				if (connector != null) {
					connector.kill();
				}
			} catch (SQLException e) {
			}
		}
	}
	
	private static void printGreeting() {
		System.out.println("Welcome to Uotel");
	}
	
	private static void printAccountSignInMenu() {
		System.out.println(LOGIN_CODE + ". Login");
		System.out.println(REGISTER_CODE + ". Register New Account");
		System.out.println(EXIT_CODE + ". Exit");
		System.out.println("Please enter your choice:");
	}
}
