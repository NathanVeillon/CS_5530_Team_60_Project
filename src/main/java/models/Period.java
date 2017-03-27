package main.java.models;

import main.java.models.base.Attribute;
import main.java.models.base.BaseObject;
import main.java.models.base.ObjectCollection;

import java.sql.Date;
import java.util.List;

import static java.util.Arrays.asList;
import static main.java.models.base.Attribute.ForeignRelationshipType.*;

/**
 * Created by StudentNathan on 3/26/2017.
 */
public class Period extends BaseObject {

	public final static List<Attribute> Attributes = asList(
			new Attribute("Id", Integer.class, "idPeriod", true),
			new Attribute("To", Date.class, "to", false),
			new Attribute("From", Date.class, "from", false),

			new Attribute("AvailablePeriods", User.class, "Available", false, ONE_TO_MANY)
	);
	public final static String TableName = "Period";

	@Override
	public List<Attribute> getAttributes() {
		return Attributes;
	}

	@Override
	public String getTableName() {
		return TableName;
	}

	public Integer Id;
	public Date To;
	public Date From;

	public ObjectCollection AvailablePeriods;

	public Integer getId() throws Exception {
		return (Integer) this.getField("Id");
	}

	public Period setId(Integer id) throws Exception {
		setField("Id", id);
		return this;
	}

	public Date getTo() throws Exception {
		return (Date) this.getField("To");
	}

	public Period setTo(Date to) throws Exception {
		setField("To", to);
		return this;
	}

	public Date getFrom() throws Exception {
		return (Date) this.getField("From");
	}

	public Period setFrom(Date from) throws Exception {
		setField("From", from);
		return this;
	}

	public String validatePeriod(){
		try {
			if(this.getFrom().after(this.getTo())){
				return "The Period Must Have The From Date Come Before The To Date";
			}
		}catch (Exception e){
			return e.getMessage();
		}

		return null;
	}

	public String validatePeriod(ObjectCollection relatedPeriods){
		if(relatedPeriods.size() == 0)
			return validatePeriod();

		if(relatedPeriods.get(0).getClass() != Period.class)
			return "Collection Of Related Periods Does Not Contain Period Objects";

		try {
			for (BaseObject object: relatedPeriods){
				Period period = (Period)object;

				if(this.Id != null && period.getId() == this.getId())
					continue;

				boolean intersectionExists = this.getFrom().before(period.getTo()) && !(this.getTo().before(period.getFrom()));
				if(intersectionExists)
					return "There Exists An Related Period That Contains Some Of The Same Days As This Period";
			}
		}catch (Exception e){
			return "Error: "+e.getMessage();
		}


		return validatePeriod();
	}
}
