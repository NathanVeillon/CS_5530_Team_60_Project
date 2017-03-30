package main.java.models;

import main.java.managers.ConnectionManager;
import main.java.models.base.Attribute;
import main.java.models.base.BaseObject;
import main.java.models.base.ObjectCollection;

import java.sql.PreparedStatement;
import java.util.List;

import static java.util.Arrays.asList;
import static main.java.models.base.Attribute.ForeignRelationshipType.*;

/**
 * Created by StudentNathan on 3/25/2017.
 */
public class User extends BaseObject {

	public final static List<Attribute> Attributes = asList(
			new Attribute("Id", Integer.class, "idUser", true),
			new Attribute("Login", String.class, "login", false),
			new Attribute("Password", String.class, "password", false),
			new Attribute("Name", String.class, "name", false),
			new Attribute("Address", String.class, "address", false),
			new Attribute("PhoneNumber", String.class, "phoneNumber", false),
			new Attribute("IsAdmin", Integer.class, "isAdmin", false),

			new Attribute("OwnedTemporaryHousing", TemporaryHousing.class, "TemporaryHousing", false, ONE_TO_MANY),
			new Attribute("Reservations", Reservation.class, "Reservation", false, ONE_TO_MANY),
			new Attribute("Visits", Visit.class, "Visit", false,ONE_TO_MANY)
	);
	public final static String TableName = "Users";

	@Override
	public List<Attribute> getAttributes() {
		return Attributes;
	}

	@Override
	public String getTableName() {
		return TableName;
	}

	public Integer Id;
	public String Login;
	public String Password;
	public String Name;
	public String Address;
	public String PhoneNumber;
	public Integer IsAdmin;

	public ObjectCollection OwnedTemporaryHousing;
	public ObjectCollection Reservations;
	public ObjectCollection Visits;

	public Integer getId() throws Exception {
		return (Integer) this.getField("Id");
	}

	public User setId(Integer id) throws Exception {
		setField("Id", id);
		return this;
	}

	public String getLogin() throws Exception {
		return (String) this.getField("Login");
	}

	public User setLogin(String login) throws Exception {
		setField("Login", login);
		return this;
	}

	public String getPassword() throws Exception {
		return (String) this.getField("Password");
	}

	public User setPassword(String password) throws Exception {
		setField("Password", password);
		return this;
	}

	public String getName() throws Exception {
		return (String) this.getField("Name");
	}

	public User setName(String name) throws Exception {
		setField("Name", name);
		return this;
	}

	public String getAddress() throws Exception {
		return (String) this.getField("Address");
	}

	public User setAddress(String address) throws Exception {
		setField("Address", address);
		return this;
	}

	public String getPhoneNumber() throws Exception {
		return (String) this.getField("PhoneNumber");
	}

	public User setPhoneNumber(String phoneNumber) throws Exception {
		setField("PhoneNumber", phoneNumber);
		return this;
	}

	public boolean getIsAdmin() throws Exception {
		return ((Integer)this.getField("IsAdmin") != 0);
	}

	public User setIsAdmin(boolean isAdmin) throws Exception {
		int intAdmin = (isAdmin) ? 1 : 0;
		setField("IsAdmin", intAdmin);
		return this;
	}

	public ObjectCollection getReservations() throws Exception {
		if(this.Reservations == null && !IsCreating){
			String query = "SELECT * FROM "+ Reservation.TableName+" " +
					"JOIN ("+Period.TableName+", "+TemporaryHousing.TableName+") " +
					"ON ("+Reservation.TableName+".idPeriod = "+Period.TableName+".idPeriod AND "+Reservation.TableName+".idTH = "+TemporaryHousing.TableName+".idTH) " +
					"WHERE "+Reservation.TableName+".idUser = ?;";
			PreparedStatement statement = ConnectionManager.prepareStatement(query);
			statement.setInt(1, getId());
			ReservationQuery query1 = new ReservationQuery();
			ObjectCollection collection = query1.getCollectionFromObjectResult(statement.executeQuery());
			setReservations(collection);
		}
		return (ObjectCollection) this.getField("Reservations");
	}

	public User setVisits(ObjectCollection collection) throws Exception {
		if(collection != null && collection.size() > 0 && collection.get(0).getClass() != Reservation.class)
			throw new Exception("The Collection had An Invalid Class");
		setField("Reservations", collection);
		return this;
	}

	public ObjectCollection getVisits() throws Exception {
		if(this.Reservations == null && !IsCreating){
			String query = "SELECT * FROM "+ Reservation.TableName+" " +
					"JOIN ("+Period.TableName+", "+TemporaryHousing.TableName+") " +
					"ON ("+Reservation.TableName+".idPeriod = "+Period.TableName+".idPeriod AND "+Reservation.TableName+".idTH = "+TemporaryHousing.TableName+".idTH) " +
					"WHERE "+Reservation.TableName+".idUser = ?;";
			PreparedStatement statement = ConnectionManager.prepareStatement(query);
			statement.setInt(1, getId());
			ReservationQuery query1 = new ReservationQuery();
			ObjectCollection collection = query1.getCollectionFromObjectResult(statement.executeQuery());
			setReservations(collection);
		}
		return (ObjectCollection) this.getField("Reservations");
	}

	public User setReservations(ObjectCollection collection) throws Exception {
		if(collection != null && collection.size() > 0 && collection.get(0).getClass() != Reservation.class)
			throw new Exception("The Collection had An Invalid Class");
		setField("Reservations", collection);
		return this;
	}

}
