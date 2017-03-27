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
 * Created by StudentNathan on 3/26/2017.
 */
public class AvailablePeriod extends BaseObject {

	public final static List<Attribute> Attributes = asList(
			new Attribute("TemporaryHousingId", Integer.class, "idTH", true),
			new Attribute("PeriodId", Integer.class, "idPeriod", true),
			new Attribute("PricePerNight", Integer.class, "pricePerNight", false),

			new Attribute("TemporaryHousing", TemporaryHousing.class, "TemporaryHousing", false, MANY_TO_ONE),
			new Attribute("Period", Period.class, "Period", false, MANY_TO_ONE)
	);
	public final static String TableName = "Available";

	@Override
	public List<Attribute> getAttributes() {
		return Attributes;
	}

	@Override
	public String getTableName() {
		return TableName;
	}

	public Integer TemporaryHousingId;
	public Integer PeriodId;
	public Integer PricePerNight;

	public TemporaryHousing TemporaryHousing;
	public Period Period;

	public Integer getTemporaryHousingId() throws Exception {
		return (Integer) this.getField("TemporaryHousingId");
	}

	public AvailablePeriod setTemporaryHousingId(Integer temporaryHousingId) throws Exception {
		setField("TemporaryHousingId", temporaryHousingId);
		return this;
	}

	public Integer getPeriodId() throws Exception {
		return (Integer) this.getField("PeriodId");
	}

	public AvailablePeriod setPeriodId(Integer periodId) throws Exception {
		setField("PeriodId", periodId);
		return this;
	}

	public Integer getPricePerNight() throws Exception {
		return (Integer) this.getField("PricePerNight");
	}

	public AvailablePeriod setPricePerNight(Integer pricePerNight) throws Exception {
		setField("PricePerNight", pricePerNight);
		return this;
	}

	public main.java.models.TemporaryHousing getTemporaryHousing() throws Exception {
		if(this.TemporaryHousing == null && !IsCreating){
			String query = "SELECT * FROM "+main.java.models.TemporaryHousing.TableName+" WHERE idTH = ?;";
			PreparedStatement statement = ConnectionManager.prepareStatement(query);
			statement.setInt(1, getTemporaryHousingId());
			TemporaryHousingQuery query1 = new TemporaryHousingQuery();
			ObjectCollection collection = query1.getCollectionFromObjectResult(statement.executeQuery());
			setTemporaryHousing((TemporaryHousing) collection.get(0));
			statement.close();
		}
		return (main.java.models.TemporaryHousing) this.getField("TemporaryHousing");
	}

	public AvailablePeriod setTemporaryHousing(main.java.models.TemporaryHousing temporaryHousing) throws Exception {
		if(temporaryHousing != null && !temporaryHousing.IsCreating && !temporaryHousing.IsDeleted)
			setTemporaryHousingId(temporaryHousing.getId());
		setField("TemporaryHousing", temporaryHousing);
		return this;
	}

	public main.java.models.Period getPeriod() throws Exception {
		if(this.Period == null && !IsCreating){
			String query = "SELECT * FROM "+main.java.models.Period.TableName+" WHERE idPeriod = ?;";
			PreparedStatement statement = ConnectionManager.prepareStatement(query);
			statement.setInt(1, getPeriodId());
			PeriodQuery query1 = new PeriodQuery();
			ObjectCollection collection = query1.getCollectionFromObjectResult(statement.executeQuery());
			setPeriod((Period) collection.get(0));
			statement.close();
		}
		return (main.java.models.Period) this.getField("Period");
	}

	public AvailablePeriod setPeriod(main.java.models.Period period) throws Exception {
		if(period != null && !period.IsCreating && !period.IsDeleted)
			setPeriodId(period.getId());
		setField("Period", period);
		return this;
	}
}
