package main.java.view;

import main.java.CommandLineInterface;
import main.java.managers.*;
import main.java.models.*;
import main.java.models.base.*;

import java.io.BufferedReader;
import java.sql.Date;
import java.sql.PreparedStatement;

/**
 * Created by StudentNathan on 3/27/2017.
 */
public class NewUserReservationsPage {

	private static final int EXIT_CODE = 0;
	private static final int ADD_NEW_RESERVATION_CODE = 1;
	private static final int REMOVE_NEW_RESERVATIONS_CODE = 2;
	private static final int CONFIRM_RESERVATIONS_CODE = 3;

	final static BufferedReader Input = CommandLineInterface.Input;
	private final static String[] TableLabels = {"Row #", "Reserved From", "Reserved To", "Cost", "Housing Name", "Housing Address"};
	private final static String[] TableFields = {"", "Period.From", "Period.To", "Cost", "TemporaryHousing.Name", "TemporaryHousing.Address"};


	private final static String[] HousingTableLabels = {"Id", "Name", "Category", "Address", "URL", "Phone-Number", "Built"};
	private final static String[] HousingTableFields = {"Id", "Name", "Category", "Address", "URL", "PhoneNumber", "YearBuilt"};


	private final static String[] AvailableDatesTableLabels = {"Period.From", "Period.To", "Price Per Night"};
	private final static String[] AvailableDatesTableFields = {"Period.From", "Period.To", "PricePerNight"};

	private static ObjectCollection NewReservationCollection;

	public static void indexAction(){
		NewReservationCollection = new ObjectCollection();
		while (true){
			printMenu();
			int code = CommandLineInterface.getUserInt();

			switch (code){
				case ADD_NEW_RESERVATION_CODE:
					handleAddReservation();
					break;

				case REMOVE_NEW_RESERVATIONS_CODE:
					handleRemoveReservation();
					break;

				case CONFIRM_RESERVATIONS_CODE:
					handleSaveReservations();
					return;

				case EXIT_CODE:
					return;

				default:
					System.out.println(CommandLineInterface.INVALID_USER_RESPONSE);
				case -1:
					continue;
			}



		}
	}

	private static void handleAddReservation(){
		Reservation newReservation = new Reservation();
		TemporaryHousing selectedTemporaryHousing = getSelectedRelatedTemporaryHousing();

		try {
			newReservation.setUser(UserManager.getCurrentUser());
			newReservation.setTemporaryHousing(selectedTemporaryHousing);

			if(populateNewReservation(newReservation) == null)
				return;

			NewReservationCollection.add(newReservation);
		}catch (Exception e){
			System.out.println("Unexpected Error:");
			e.printStackTrace();
		}
	}

	private static void handleRemoveReservation(){
		int reservationToRemoveIndex = getReservationToRemoveIndex();
		NewReservationCollection.remove(reservationToRemoveIndex);
	}

	private static void handleSaveReservations(){
		System.out.println("Reservations To Create");
		NewReservationCollection.printTable(TableLabels, TableFields);
		if(!CommandLineInterface.confirm("Are You Sure That Want To Create These Reservations? (Y/N)\n" +
				"The Reservations Can't Be Changed Once They Are Confirmed"))
			return;
		try {
			System.out.println("Creating Reservations");
			ConnectionManager.startTransaction();
			for (BaseObject object: NewReservationCollection) {
				Reservation reservation = (Reservation) object;
				Period period = reservation.getPeriod();
				period.save();
				reservation.setPeriod(period).save();
			}

			ConnectionManager.commit();
			System.out.println("Reservations Created Successfully");
		}catch (Exception e){
			System.out.println("Unexpected Error:");
			e.printStackTrace();
		}
	}

	private static TemporaryHousing getSelectedRelatedTemporaryHousing(){
		while (true){
			ObjectCollection allTemporaryHousing;
			try {
				allTemporaryHousing = getAllTemporaryHousing();
			}catch (Exception e){
				System.out.println("Unexpected Error:");
				e.printStackTrace();
				return null;
			}


			System.out.println("Temporary Housing");
			allTemporaryHousing.printTable(HousingTableLabels, HousingTableFields);
			System.out.print("Select The Id of The Housing You Want To Make Reservations For:");
			int id = CommandLineInterface.getUserInt();

			try {
				for(BaseObject object :allTemporaryHousing){
					TemporaryHousing temporaryHousing = (TemporaryHousing) object;
					if(temporaryHousing.getId() == id){
						System.out.println("Periods You Can Register In");
						temporaryHousing.getAvailablePeriods().printTable(AvailableDatesTableLabels, AvailableDatesTableFields);
						if(CommandLineInterface.confirm("Are You Sure Want To Create A Reservation Here? (Y/N)"))
							return temporaryHousing;
						break;
					}
				}

				System.out.println("No Temp Housing Selected");
				continue;
			}catch (Exception e){
				System.out.println("Unexpected Error:");
				e.printStackTrace();
				return null;
			}
		}
	}

	private static Integer getReservationToRemoveIndex(){
		while (true){
			System.out.println("Reservations in Transaction");
			NewReservationCollection.printTable(TableLabels, TableFields);
			System.out.print("Select The Row Number of The Reservation You Want To Remove:");
			int rowNumber = CommandLineInterface.getUserInt();

			if(rowNumber == -1)
				continue;

			if(rowNumber > 0 && rowNumber <= NewReservationCollection.size())
				return rowNumber - 1;

			System.out.println(CommandLineInterface.INVALID_USER_RESPONSE);
		}
	}

	private static Reservation populateNewReservation(Reservation newReservation){
		Period relatedPeriod = new Period();

		while (true){
			try {
				System.out.println("Please Enter In The Available Period Info.");
				System.out.print("From (Ex. 2017-3-14):");
				relatedPeriod.setFrom(Date.valueOf(Input.readLine()));
				System.out.print("To (Ex. 2017-03-24):");
				relatedPeriod.setTo(Date.valueOf(Input.readLine()));
				System.out.print("Cost (Ex. 123):");
				newReservation.setCost(Integer.parseInt(Input.readLine()));

				String errorMessage = relatedPeriod.validateReservationPeriod(newReservation.getTemporaryHousing());
				if(errorMessage != null){
					System.out.println(CommandLineInterface.INVALID_USER_RESPONSE);
					System.out.println(errorMessage);

					if(CommandLineInterface.confirm("Do You Want To Stop Creating This Reservation? (Y/N)"))
						return null;
					continue;
				}

				newReservation.setPeriod(relatedPeriod);
				return newReservation;
			} catch (Exception e) {
				System.out.println(CommandLineInterface.INVALID_USER_RESPONSE);
				System.out.println("Error: " + e.getMessage());
			}
		}
	}

	private static ObjectCollection getAllTemporaryHousing()throws Exception{
		String stringQuery = "SELECT * FROM "+TemporaryHousing.TableName+";";
		PreparedStatement statement = ConnectionManager.prepareStatement(stringQuery);
		TemporaryHousingQuery query = new TemporaryHousingQuery();
		return query.getCollectionFromObjectResult(statement.executeQuery());
	}

	private static void printMenu() {
		printOwnedHousing();

		System.out.println(ADD_NEW_RESERVATION_CODE + ". Add A Reservation");
		System.out.println(REMOVE_NEW_RESERVATIONS_CODE + ". Remove A Reservation");
		System.out.println(CONFIRM_RESERVATIONS_CODE + ". Confirm These Reservations");
		System.out.println(EXIT_CODE + ". Exit");
		System.out.print("Please enter your choice:");
	}

	private static void printOwnedHousing(){
		System.out.println("New Reservations To Create");
		NewReservationCollection.printTable(TableLabels, TableFields);
	}
}
