package main.java.models;

import main.java.managers.ConnectionManager;
import main.java.models.base.Attribute;
import main.java.models.base.AttributeRelationship;
import main.java.models.base.BaseObject;
import main.java.models.base.ObjectCollection;

import java.sql.PreparedStatement;
import java.util.*;

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

			new Attribute("TemporaryHousing", TemporaryHousing.class, "TemporaryHousing", false, MANY_TO_ONE ,
					Arrays.asList(new AttributeRelationship("TemporaryHousingId", "Id"))),
			new Attribute("Period", Period.class, "Period", false, MANY_TO_ONE,
					Arrays.asList(new AttributeRelationship("PeriodId", "Id")))
	);

	private static final Map<String, Attribute> AttributeMap;
	static {
		Map<String, Attribute> aMap = new HashMap<>();
		for (Attribute attr: Attributes) {
			aMap.put(attr.JavaFieldName, attr);
		}
		AttributeMap = Collections.unmodifiableMap(aMap);
	}
	public final static String TableName = "Available";

	@Override
	public List<Attribute> getAttributes() {
		return Attributes;
	}

	@Override
	public Map<String, Attribute> getAttributeMap() {
		return AttributeMap;
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
			TemporaryHousingQuery query = new TemporaryHousingQuery();
			query.filterByField("Id", getTemporaryHousingId());
			setTemporaryHousing(query.findOne());
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
			PeriodQuery query = new PeriodQuery();
			query.filterByField("Id", getPeriodId());
			setPeriod(query.findOne());
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
