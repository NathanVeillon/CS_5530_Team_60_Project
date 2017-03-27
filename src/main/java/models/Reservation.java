package main.java.models;

import main.java.models.base.Attribute;
import main.java.models.base.BaseObject;

import java.util.List;

import static java.util.Arrays.asList;
import static main.java.models.base.Attribute.ForeignRelationshipType.*;

/**
 * Created by StudentNathan on 2/27/2017.
 */
public class Reservation extends BaseObject {

	public final static List<Attribute> Attributes = asList(
			new Attribute("UserId", Integer.class, "idUser", true),
			new Attribute("TemporaryHousingId", Integer.class, "idTH", true),
			new Attribute("PeriodId", Integer.class, "idPeriod", true),
			new Attribute("Cost", Integer.class, "cost", false),

			new Attribute("User", User.class, "User", false, MANY_TO_ONE),
			new Attribute("TemporaryHousing", TemporaryHousing.class, "TemporaryHousing", false, MANY_TO_ONE),
			new Attribute("Period", Period.class, "Period", false, MANY_TO_ONE)
	);
	public final static String TableName = "Reserve";

	@Override
	public List<Attribute> getAttributes() {
		return Attributes;
	}

	@Override
	public String getTableName() {
		return TableName;
	}

	public Integer getUserId() throws Exception {
		return (Integer) this.getField("UserId");
	}

	public Reservation setUserId(Integer userId) throws Exception {
		setField("UserId", userId);
		return this;
	}

	public Integer getTemporaryHousingId() throws Exception {
		return (Integer) this.getField("TemporaryHousingId");
	}

	public Reservation setTemporaryHousingId(Integer temporaryHousingId) throws Exception {
		setField("TemporaryHousingId", temporaryHousingId);
		return this;
	}

	public Integer getPeriodId() throws Exception {
		return (Integer) this.getField("PeriodId");
	}

	public Reservation setPeriodId(Integer periodId) throws Exception {
		setField("PeriodId", periodId);
		return this;
	}

	public Integer getCost() throws Exception {
		return (Integer) this.getField("Cost");
	}

	public Reservation setCost(Integer cost) throws Exception {
		setField("Cost", cost);
		return this;
	}

	public main.java.models.User getUser() throws Exception {
		return (main.java.models.User) this.getField("User");
	}

	public Reservation setUser(main.java.models.User user) throws Exception {
		setUserId(user.getId());
		setField("User", user);
		return this;
	}

	public main.java.models.TemporaryHousing getTemporaryHousing() throws Exception {
		return (main.java.models.TemporaryHousing) this.getField("TemporaryHousing");
	}

	public Reservation setTemporaryHousing(main.java.models.TemporaryHousing temporaryHousing) throws Exception {
		if(temporaryHousing != null && !temporaryHousing.IsCreating && !temporaryHousing.IsDeleted)
			setTemporaryHousingId(temporaryHousing.getId());
		setField("TemporaryHousing", temporaryHousing);
		return this;
	}

	public main.java.models.Period getPeriod() throws Exception {
		return (main.java.models.Period) this.getField("Period");
	}

	public Reservation setPeriod(main.java.models.Period period) throws Exception {
		if(period != null && !period.IsCreating && !period.IsDeleted)
			setPeriodId(period.getId());
		setField("Period", period);
		return this;
	}

	public Integer UserId;
	public Integer TemporaryHousingId;
	public Integer PeriodId;
	public Integer Cost;

	public User User;
	public TemporaryHousing TemporaryHousing;
	public Period Period;
}
