package main.java.view;

import main.java.CommandLineInterface;
import main.java.managers.UserManager;
import main.java.models.base.ObjectCollection;

import java.io.BufferedReader;

/**
 * Created by StudentNathan on 3/27/2017.
 */
public class UserReservationsPage {

	private static final int EXIT_CODE = 0;
	private static final int CREATE_NEW_RESERVATIONS_CODE = 1;

	final static BufferedReader Input = CommandLineInterface.Input;
	private final static String[] TableLabels = {"Reserved From", "Reserved To", "Cost", "Housing Name", "Housing Address"};
	private final static String[] TableFields = {"Period.From", "Period.To", "Cost", "TemporaryHousing.Name", "TemporaryHousing.Address"};

	private static ObjectCollection ExistingReservationCollection;

	public static void indexAction(){
		while (true){
			try {
				UserManager.getCurrentUser().setReservations(null);
				ExistingReservationCollection = UserManager.getCurrentUser().getReservations();
			}catch (Exception e){
				System.out.println("Unexpected Error:");
				e.printStackTrace();
			}
			printMenu();
			int code = CommandLineInterface.getUserInt();

			switch (code){
				case CREATE_NEW_RESERVATIONS_CODE:
					NewUserReservationsPage.indexAction();
					break;

				case EXIT_CODE:
					return;

				default:
					System.out.println(CommandLineInterface.INVALID_USER_RESPONSE);
				case -1:
					continue;
			}
		}
	}

	private static void printMenu() {
		printOwnedHousing();

		System.out.println(CREATE_NEW_RESERVATIONS_CODE + ". Add New Reservations");
		System.out.println(EXIT_CODE + ". Back");
		System.out.print("Please enter your choice:");
	}

	private static void printOwnedHousing(){
		System.out.println("Your Reservations");
		ExistingReservationCollection.printTable(TableLabels, TableFields);
	}
}
