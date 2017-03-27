package main.java.view;

import main.java.CommandLineInterface;
import main.java.managers.ConnectionManager;
import main.java.managers.UserManager;
import main.java.models.AvailablePeriod;
import main.java.models.Period;
import main.java.models.TemporaryHousing;
import main.java.models.TemporaryHousingQuery;
import main.java.models.base.BaseObject;
import main.java.models.base.ObjectCollection;

import java.io.BufferedReader;
import java.sql.Date;
import java.sql.PreparedStatement;

/**
 * Created by StudentNathan on 3/26/2017.
 */
public class UpdateOwnedTemporaryHousingPage {


	private static final int EXIT_CODE = 0;
	private static final int UPDATE_TH_CODE = 1;
	private static final int ADD_AN_AVAILABLE_PERIOD_CODE = 2;
	private static final int UPDATE_AN_AVAILABLE_PERIOD_CODE = 3;
	private static final int DELETE_AN_AVAILABLE_PERIOD_CODE = 4;

	final static BufferedReader Input = CommandLineInterface.Input;
	private final static String[] TableLabels = {"Id", "Name", "Category", "Address", "URL", "Phone-Number", "Built"};
	private final static String[] TableFields = {"Id", "Name", "Category", "Address", "URL", "PhoneNumber", "YearBuilt"};
	private final static String[] AvailableDatesTableLabels = {"Id", "Period.From", "Period.To", "Price Per Night"};
	private final static String[] AvailableDatesTableFields = {"Period.Id", "Period.From", "Period.To", "PricePerNight"};

	private static TemporaryHousing HousingToUpdate;
	private static ObjectCollection AvailablePeriods;


	public static void indexAction(TemporaryHousing housingToUpdate){
		HousingToUpdate = housingToUpdate;
		while (true) {
			try {
				HousingToUpdate = getTheTemporaryHousing();
				AvailablePeriods = HousingToUpdate.getAvailablePeriods();
			} catch (Exception e) {
				System.out.println("Unexpected Error:");
				e.printStackTrace();
				return;
			}

			printMenu();
			int code = CommandLineInterface.getUserInt();

			switch (code) {
				case UPDATE_TH_CODE:
					handleUpdateTHCode();
					break;

				case ADD_AN_AVAILABLE_PERIOD_CODE:
					handleAddAvailablePeriod();
					break;

				case UPDATE_AN_AVAILABLE_PERIOD_CODE:
					handleEditAvailablePeriod();
					break;

				case DELETE_AN_AVAILABLE_PERIOD_CODE:
					handleDeleteAvailablePeriod();
					break;

				case EXIT_CODE:
					return;

				default:
					System.out.println(CommandLineInterface.INVALID_USER_RESPONSE);
				case -1:
					break;
			}
		}

	}

	public static void handleUpdateTHCode(){
		while (true) {
			try {
				System.out.println("Please Enter In The Updated Temp. Housing Info.");
				System.out.print("Name:");
				HousingToUpdate.setName(Input.readLine());
				System.out.print("Category:");
				HousingToUpdate.setCategory(Input.readLine());
				System.out.print("Address:");
				HousingToUpdate.setAddress(Input.readLine());
				System.out.print("URL:");
				HousingToUpdate.setURL(Input.readLine());
				System.out.print("Phone Number (Ex. \"+1 (555) 555-5555\"):");
				HousingToUpdate.setPhoneNumber(Input.readLine());
				System.out.print("Year Built (Ex. 2007):");
				HousingToUpdate.setYearBuilt(Input.readLine());
				HousingToUpdate.setOwner(UserManager.getCurrentUser());
			} catch (Exception e) {
				System.out.println(CommandLineInterface.INVALID_USER_RESPONSE);
				System.out.println("Error: " + e.getMessage());
				continue;
			}

			if (CommandLineInterface.confirm("Are You Happy With Your Changes? (Y/N)")){
				try {
					ConnectionManager.startTransaction();
					System.out.println("Updating Temporary Housing");
					HousingToUpdate.save();
					ConnectionManager.commit();
					System.out.println("Temporary Housing Updated");
					return;
				}catch (Exception e){
					System.out.println("Unexpected Error:");
					e.printStackTrace();
				}
			}
		}
	}

	private static void handleAddAvailablePeriod(){
		AvailablePeriod newAvailablePeriod = populateAvailablePeriod(new AvailablePeriod());

		ObjectCollection printOut = new ObjectCollection();
		printOut.add(newAvailablePeriod);
		System.out.println("New Available Period To Make");
		printOut.printTable(AvailableDatesTableLabels, AvailableDatesTableFields);

		if(!CommandLineInterface.confirm("Are You Sure You Want To Create This Available Period"))
			return;

		try {
			ConnectionManager.startTransaction();
			Period newPeriod = newAvailablePeriod.getPeriod();
			newPeriod.save();
			System.out.println("Creating New Available Period");
			newAvailablePeriod.setPeriod(newPeriod).save();
			ConnectionManager.commit();
			System.out.println("New Available Period Added");
		}catch (Exception e){
			System.out.println("Unexpected Error:");
			e.printStackTrace();
		}
	}

	private static void handleEditAvailablePeriod(){
		AvailablePeriod periodToUpdate = selectAvailablePeriodToUpdate();
		periodToUpdate = populateAvailablePeriod(periodToUpdate);

		ObjectCollection printOut = new ObjectCollection();
		printOut.add(periodToUpdate);
		System.out.println("Available Period To Update");
		printOut.printTable(AvailableDatesTableLabels, AvailableDatesTableFields);

		if(!CommandLineInterface.confirm("Are You Sure You Want To Update This Available Period"))
			return;

		try {
			ConnectionManager.startTransaction();
			System.out.println("Saving Available Period");
			Period newPeriod = periodToUpdate.getPeriod();
			newPeriod.save();
			periodToUpdate.setPeriod(newPeriod).save();
			ConnectionManager.commit();
			System.out.println("Available Period Saved");
		}catch (Exception e){
			System.out.println("Unexpected Error:");
			e.printStackTrace();
		}
	}

	private static void handleDeleteAvailablePeriod(){
		AvailablePeriod availablePeriod = selectAvailablePeriodToUpdate();

		ObjectCollection printOut = new ObjectCollection();
		printOut.add(availablePeriod);
		System.out.println("Available Period To Update");
		printOut.printTable(AvailableDatesTableLabels, AvailableDatesTableFields);

		if(!CommandLineInterface.confirm("Are You Sure You Want To Delete This Available Period"))
			return;

		try {
			System.out.println("Deleting Available Period");
			ConnectionManager.startTransaction();
			Period period = availablePeriod.getPeriod();
			availablePeriod.delete();
			period.delete();
			ConnectionManager.commit();
			System.out.println("Available Period Deleted");
		}catch (Exception e){
			System.out.println("Unexpected Error:");
			e.printStackTrace();
		}
	}

	private static AvailablePeriod populateAvailablePeriod(AvailablePeriod selectedAvailablePeriod){
		Period relatedPeriod;
		ObjectCollection periodsRelatedToTheTempHousing;
		try{
			relatedPeriod = selectedAvailablePeriod.getPeriod();
			periodsRelatedToTheTempHousing = HousingToUpdate.getRelatedPeriods();
		}catch (Exception e){
			System.out.println("Unexpected Error:");
			e.printStackTrace();
			return null;
		}

		if(relatedPeriod == null)
			relatedPeriod = new Period();

		while (true){
			try {
				System.out.println("Please Enter In The Available Period Info.");
				System.out.print("From (Ex. 2017-3-14):");
				relatedPeriod.setFrom(Date.valueOf(Input.readLine()));
				System.out.print("To (Ex. 2017-3-24):");
				relatedPeriod.setTo(Date.valueOf(Input.readLine()));
				System.out.print("Price Per Night (Ex. 123):");
				selectedAvailablePeriod.setPricePerNight(Integer.parseInt(Input.readLine()));

				String errorMessage = relatedPeriod.validateAvailablePeriod(periodsRelatedToTheTempHousing);
				if(errorMessage != null){
					System.out.println("Period Given Is Not Valid");
					System.out.println(errorMessage);
					continue;
				}

				selectedAvailablePeriod.setTemporaryHousing(HousingToUpdate);
				selectedAvailablePeriod.setPeriod(relatedPeriod);
				return  selectedAvailablePeriod;
			} catch (Exception e) {
				System.out.println(CommandLineInterface.INVALID_USER_RESPONSE);
				System.out.println("Error: " + e.getMessage());
			}
		}
	}

	private static AvailablePeriod selectAvailablePeriodToUpdate(){
		printSelectedTemporaryHousing();
		System.out.print("Please Enter The Id of The Period:");
		int id = CommandLineInterface.getUserInt();

		try {

			for(BaseObject object: AvailablePeriods){
				AvailablePeriod availablePeriod = (AvailablePeriod) object;
				if(availablePeriod.getPeriodId() == id) {
					ObjectCollection printOut = new ObjectCollection();
					printOut.add(availablePeriod);
					System.out.println("Selected Date");
					printOut.printTable(AvailableDatesTableLabels, AvailableDatesTableFields);
					if(CommandLineInterface.confirm("Do You Want To Select This Avail. Period (Y/N)"))
						return availablePeriod;
					return null;
				}
			}

			System.out.println("No Available Period with that Id found.");
		}catch (Exception e){
			System.out.println("Unexpected Error:");
			e.printStackTrace();
		}

		return null;
	}

	private static TemporaryHousing getTheTemporaryHousing() throws Exception {
		String stringQuery = "SELECT * FROM " +TemporaryHousing.TableName + " WHERE idTH = ? ;";
		PreparedStatement statement = ConnectionManager.prepareStatement(stringQuery);
		statement.setInt(1, HousingToUpdate.getId());
		TemporaryHousingQuery query = new TemporaryHousingQuery();
		ObjectCollection collection = query.getCollectionFromObjectResult(statement.executeQuery());
		statement.close();
		return (TemporaryHousing) collection.get(0);
	}

	private static void printMenu() {
		printSelectedTemporaryHousing();

		System.out.println(UPDATE_TH_CODE + ". Update Main Housing Info");
		System.out.println(ADD_AN_AVAILABLE_PERIOD_CODE + ". Add an Available Date");
		System.out.println(UPDATE_AN_AVAILABLE_PERIOD_CODE + ". Edit an Available Date");
		System.out.println(DELETE_AN_AVAILABLE_PERIOD_CODE + ". Delete an Available Date");
		System.out.println(EXIT_CODE + ". Back");
		System.out.print("Please enter your choice:");
	}

	private static void printSelectedTemporaryHousing(){
		ObjectCollection printOut = new ObjectCollection();
		printOut.add(HousingToUpdate);
		System.out.println("Selected Housing");
		printOut.printTable(TableLabels, TableFields);
		System.out.println("The Available Dates Housing");
		AvailablePeriods.printTable(AvailableDatesTableLabels, AvailableDatesTableFields);
	}
}
