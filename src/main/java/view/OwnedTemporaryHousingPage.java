package main.java.view;

import main.java.CommandLineInterface;
import main.java.managers.ConnectionManager;
import main.java.managers.UserManager;
import main.java.models.TemporaryHousing;
import main.java.models.TemporaryHousingQuery;
import main.java.models.base.BaseObject;
import main.java.models.base.ObjectCollection;

import java.io.BufferedReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.PreparedStatement;

/**
 * Created by StudentNathan on 3/26/2017.
 */
public class OwnedTemporaryHousingPage {


	private static final int EXIT_CODE = 0;
	private static final int SUBMIT_NEW_TH_CODE = 1;
	private static final int UPDATE_TH_CODE = 2;

	final static BufferedReader Input = CommandLineInterface.Input;
	private final static String[] TableLabels = {"Id", "Name", "Category", "Address", "URL", "Phone-Number", "Built"};
	private final static String[] TableFields = {"Id", "Name", "Category", "Address", "URL", "PhoneNumber", "YearBuilt"};

	private static ObjectCollection RelatedHousingCollection;


	public static void indexAction(){
		while (true){
			try {
				RelatedHousingCollection = getRelatedTemporaryHousing();
			} catch (Exception e) {
				System.out.println("Unexpected Error:");
				e.printStackTrace();
				return;
			}
			printMenu();
			int code = CommandLineInterface.getUserInt();

			switch (code){
				case SUBMIT_NEW_TH_CODE:
					handleCreateCode();
					break;
				case UPDATE_TH_CODE:
					handleUpdateCode();
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

	public static void handleCreateCode(){
		TemporaryHousing newTemporaryHousing = new TemporaryHousing();
		while (true){
			try {
				System.out.println("Please Enter In The Temp. Housing Info.");
				System.out.print("Name:");
				newTemporaryHousing.setName(Input.readLine());
				System.out.print("Category:");
				newTemporaryHousing.setCategory(Input.readLine());
				System.out.print("Address:");
				newTemporaryHousing.setAddress(Input.readLine());
				System.out.print("URL:");
				newTemporaryHousing.setURL(Input.readLine());
				System.out.print("Phone Number (Ex. \"+1 (555) 555-5555\"):");
				newTemporaryHousing.setPhoneNumber(Input.readLine());
				System.out.print("Year Built (Ex. 2007):");
				newTemporaryHousing.setYearBuilt(Input.readLine());
				System.out.print("Expected Price (Ex. 3640.05):");
				String response = Input.readLine();
				BigDecimal amount = new BigDecimal(response);
				newTemporaryHousing.setExpectedPrice(amount);
				newTemporaryHousing.setOwner(UserManager.getCurrentUser());
			}catch (Exception e){
				System.out.println(CommandLineInterface.INVALID_USER_RESPONSE);
				System.out.println("Error: "+e.getMessage());
				continue;
			}

			if(!CommandLineInterface.confirm("Are You Sure Want To Create This Housing? (Y/N)"))
				return;

			try {
				ConnectionManager.startTransaction();
				System.out.println("Creating Temporary Housing");
				newTemporaryHousing.save();
				ConnectionManager.commit();
				System.out.println("Temporary Housing Created");
			}catch (Exception e){
				System.out.println("Unexpected Error:");
				e.printStackTrace();
			}

			return;
		}


	}

	public static void handleUpdateCode(){
		TemporaryHousing temporaryHousingToUpdate = null;
		while (temporaryHousingToUpdate == null){
			temporaryHousingToUpdate = selectTempHousingToUpdate();
			if(temporaryHousingToUpdate == null)
				if(CommandLineInterface.confirm("Do You Want To Go Back? (Y/N)"))
					return;
		}

		UpdateOwnedTemporaryHousingPage.indexAction(temporaryHousingToUpdate);
	}

	private static TemporaryHousing selectTempHousingToUpdate(){
		printOwnedHousing();
		System.out.print("Please Enter The Id of The House That You Want To Update:");
		int id = CommandLineInterface.getUserInt();

		try {

			for(BaseObject object: RelatedHousingCollection){
				TemporaryHousing housing = (TemporaryHousing) object;
				if(housing.getId() == id) {
					ObjectCollection printOut = new ObjectCollection();
					printOut.add(housing);
					System.out.println("Selected Housing");
					printOut.printTable(TableLabels, TableFields);
					if(CommandLineInterface.confirm("Do You Want To Update This Housing (Y/N)"))
						return housing;
					return null;
				}
			}

			System.out.println("No housing with that Id found.");
		}catch (Exception e){
			System.out.println("Unexpected Error:");
			e.printStackTrace();
		}

		return null;
	}

	private static ObjectCollection getRelatedTemporaryHousing() throws Exception {
		TemporaryHousingQuery query = new TemporaryHousingQuery();
		query.filterByField("OwnerId", UserManager.getCurrentUser().getId());
		return query.find();
	}

	private static void printMenu() {
		printOwnedHousing();

		System.out.println(SUBMIT_NEW_TH_CODE + ". Create New Housing");
		System.out.println(UPDATE_TH_CODE + ". Update Housing Info");
		System.out.println(EXIT_CODE + ". Exit");
		System.out.print("Please enter your choice:");
	}

	private static void printOwnedHousing(){
		System.out.println("Your Owned Temporary Housing");
		RelatedHousingCollection.printTable(TableLabels, TableFields);
	}
}
