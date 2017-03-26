package main.java.models;

import main.java.models.base.Attribute;
import main.java.models.base.BaseObject;

import java.time.Year;
import java.util.Calendar;
import java.sql.Date;
import java.util.List;

import static java.util.Arrays.asList;
import static main.java.models.base.Attribute.ForeignRelationshipType.*;

/**
 * Created by StudentNathan on 3/25/2017.
 */
public class TemporaryHousing extends BaseObject {

	public final static List<Attribute> Attributes = asList(
			new Attribute("Id", Integer.class, "idTH", true),
			new Attribute("Name", String.class, "name", false),
			new Attribute("Category", String.class, "category", false),
			new Attribute("Address", String.class, "address", false),
			new Attribute("URL", String.class, "url", false),
			new Attribute("PhoneNumber", String.class, "phoneNumber", false),
			new Attribute("YearBuilt", String.class, "yearBuilt", false),
			new Attribute("OwnerId", Integer.class, "idOwner", false),

			new Attribute("Owner", User.class, "Users", false, MANY_TO_ONE)
	);
	public final static String TableName = "TemporaryHousing";

	@Override
	public List<Attribute> getAttributes() {
		return Attributes;
	}

	@Override
	public String getTableName() {
		return TableName;
	}

	public Integer Id;
	public String Name;
	public String Category;
	public String Address;
	public String URL;
	public String PhoneNumber;
	public String YearBuilt;
	public Integer OwnerId;

	public User Owner;

	public Integer getId() throws Exception {
		return (Integer) this.getField("Id");
	}

	public TemporaryHousing setId(Integer id) throws Exception {
		setField("Id", id);
		return this;
	}

	public String getName() throws Exception {
		return (String) this.getField("Name");
	}

	public TemporaryHousing setName(String name) throws Exception {
		setField("Name", name);
		return this;
	}

	public String getCategory() throws Exception {
		return (String) this.getField("Category");
	}

	public TemporaryHousing setCategory(String category) throws Exception {
		setField("Category", category);
		return this;
	}

	public String getAddress() throws Exception {
		return (String) this.getField("Address");
	}

	public TemporaryHousing setAddress(String address) throws Exception {
		setField("Address", address);
		return this;
	}

	public String getURL() throws Exception {
		return (String) this.getField("URL");
	}

	public TemporaryHousing setURL(String URL) throws Exception {
		this.setField("URL", URL);
		return this;
	}

	public String getPhoneNumber() throws Exception {
		return (String) this.getField("PhoneNumber");
	}

	public TemporaryHousing setPhoneNumber(String phoneNumber) throws Exception {
		setField("PhoneNumber", phoneNumber);
		return this;
	}

	public String getYearBuilt() throws Exception {
		return (String) this.getField("YearBuilt");
	}

	public TemporaryHousing setYearBuilt(String yearBuilt) throws Exception {
		setField("YearBuilt", yearBuilt);
		return this;
	}

	public Integer getOwnerId() throws Exception {
		return (Integer) this.getField("OwnerId");
	}

	public TemporaryHousing setOwnerId(Integer ownerId) throws Exception {
		setField("OwnerId", ownerId);
		return this;
	}

	public User getOwner() throws Exception {
		return (User) this.getField("Owner");
	}

	public TemporaryHousing setOwner(User owner) throws Exception {
		setField("OwnerId", owner.getId());
		setField("Owner", owner);
		return this;
	}
}
