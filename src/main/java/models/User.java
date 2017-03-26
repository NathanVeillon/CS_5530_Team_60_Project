package main.java.models;

import main.java.models.base.Attribute;
import main.java.models.base.BaseObject;

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

			new Attribute("OwnedTemporaryHousing", TemporaryHousing.class, "TemporaryHousing", false, ONE_TO_MANY)
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


	public Integer getId() throws Exception {
		return (Integer) this.getField("Integer");
	}

	public String getLogin() throws Exception {
		return (String) this.getField("Login");
	}

	public void setLogin(String login) throws Exception {
		setField("Login", login);
	}

	public String getPassword() throws Exception {
		return "**********";
	}

	public void setPassword(String password) throws Exception {
		setField("Password", password);
	}

	public String getName() throws Exception {
		return (String) this.getField("Name");
	}

	public void setName(String name) throws Exception {
		setField("Name", name);
	}

	public String getAddress() throws Exception {
		return (String) this.getField("Address");
	}

	public void setAddress(String address) throws Exception {
		setField("Address", address);
	}

	public String getPhoneNumber() throws Exception {
		return (String) this.getField("PhoneNumber");
	}

	public void setPhoneNumber(String phoneNumber) throws Exception {
		setField("PhoneNumber", phoneNumber);
	}

	public boolean getIsAdmin() throws Exception {
		return (boolean) this.getField("IsAdmin");
	}

	public void setIsAdmin(Boolean isAdmin) throws Exception {
		int admin = (isAdmin) ? 1 : 0;
		setField("IsAdmin", admin);
	}

}
