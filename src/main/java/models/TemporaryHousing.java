package main.java.models;

import main.java.managers.ConnectionManager;
import main.java.managers.UserManager;
import main.java.models.base.Attribute;
import main.java.models.base.AttributeRelationship;
import main.java.models.base.BaseObject;
import main.java.models.base.ObjectCollection;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.util.*;

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
			new Attribute("ExpectedPrice", BigDecimal.class, "expectedPrice", false),
			new Attribute("OwnerId", Integer.class, "idOwner", false),

			new Attribute("Owner", User.class, "Users", false, MANY_TO_ONE,
					Arrays.asList(new AttributeRelationship("OwnerId", "Id"))),
			new Attribute("AvailablePeriods", AvailablePeriod.class, "Available", false, ONE_TO_MANY,
					Arrays.asList(new AttributeRelationship("Id", "TemporaryHousingId"))),
			new Attribute("Reservations", Reservation.class, "Reservation", false, ONE_TO_MANY,
					Arrays.asList(new AttributeRelationship("Id", "TemporaryHousingId"))),
			new Attribute("Feedback", Feedback.class, "feedback", false,ONE_TO_MANY,
					Arrays.asList(new AttributeRelationship("Id", "TemporaryHousingId"))),
			new Attribute("TemporaryHousingKeywordMaps", TemporaryHousingKeywordMap.class, "HasKeywords", false, ONE_TO_MANY,
					Arrays.asList(new AttributeRelationship("Id", "TemporaryHousingId"))),
			new Attribute("Favorites", Favorite.class, "Favorites", false, ONE_TO_MANY,
					Arrays.asList(new AttributeRelationship("Id", "TemporaryHousingId")))
	);

	private static final Map<String, Attribute> AttributeMap;
	static {
		Map<String, Attribute> aMap = new HashMap<>();
		for (Attribute attr: Attributes) {
			aMap.put(attr.JavaFieldName, attr);
		}
		AttributeMap = Collections.unmodifiableMap(aMap);
	}

	public final static String TableName = "TemporaryHousing";

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

	public Integer Id;
	public String Name;
	public String Category;
	public String Address;
	public String URL;
	public String PhoneNumber;
	public BigDecimal ExpectedPrice;

	public String YearBuilt;
	public Integer OwnerId;

	public User Owner;
	public ObjectCollection AvailablePeriods;
	public ObjectCollection Reservations;
	public ObjectCollection Feedback;
	public ObjectCollection TemporaryHousingKeywordMaps;
	public ObjectCollection Favorites;

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
	
	public BigDecimal getExpectedPrice() throws Exception {
		return (BigDecimal) this.getField("ExpectedPrice");
	}

	public TemporaryHousing setExpectedPrice(BigDecimal expectedPrice) throws Exception {
		setField("ExpectedPrice", expectedPrice);
		return this;
	}

	public Integer getOwnerId() throws Exception {
		return (Integer) this.getField("OwnerId");
	}

	public TemporaryHousing setOwnerId(Integer ownerId) throws Exception {
		setField("OwnerId", ownerId);
		return this;
	}

	public ObjectCollection getRelatedPeriods() throws Exception{
		ObjectCollection AvailablePeriods = getAvailablePeriods();
		ObjectCollection relatedPeriods = new ObjectCollection();
		for(BaseObject object: AvailablePeriods){
			AvailablePeriod availablePeriod = (AvailablePeriod)object;
			relatedPeriods.add(availablePeriod.getPeriod());
		}

		return relatedPeriods;
	}

	public ObjectCollection getReservations() throws Exception {
		if(this.Reservations == null && !IsCreating){
			String query = "SELECT * FROM "+ Reservation.TableName+" " +
					"JOIN ("+Period.TableName+", "+User.TableName+") " +
					"ON ("+Reservation.TableName+".idPeriod = "+Period.TableName+".idPeriod AND "+Reservation.TableName+".idUser = "+User.TableName+".idUser) " +
					"WHERE "+Reservation.TableName+".idTH = ?;";
			PreparedStatement statement = ConnectionManager.prepareStatement(query);
			statement.setInt(1, getId());
			ReservationQuery query1 = new ReservationQuery();
			ObjectCollection collection = query1.getCollectionFromObjectResult(statement.executeQuery());
			setReservations(collection);
		}
		return (ObjectCollection) this.getField("AvailablePeriods");
	}

	public ObjectCollection getCurrentUserReservations() throws Exception {
		if(this.Reservations == null && !IsCreating){
			String query = "SELECT * FROM "+ Reservation.TableName+" " +
					"JOIN ("+Period.TableName+") " +
					"ON ("+Reservation.TableName+".idPeriod = "+Period.TableName+".idPeriod) " +
					"WHERE "+Reservation.TableName+".idTH = ? AND "+Reservation.TableName+".idUser = ?;";
			PreparedStatement statement = ConnectionManager.prepareStatement(query);
			statement.setInt(1, getId());
			statement.setInt(2, UserManager.getCurrentUser().getId());
			ReservationQuery query1 = new ReservationQuery();
			ObjectCollection collection = query1.getCollectionFromObjectResult(statement.executeQuery());
			setReservations(collection);
		}
		return (ObjectCollection) this.getField("AvailablePeriods");
	}

	public TemporaryHousing setReservations(ObjectCollection collection) throws Exception {
		if(collection.size() > 0 && collection.get(0).getClass() != Reservation.class)
				throw new Exception("The Collection had An Invalid Class");
		setField("Reservations", collection);
		return this;
	}

	public ObjectCollection getAvailablePeriods() throws Exception {
		if(this.AvailablePeriods == null && !IsCreating){
			String query = "SELECT * FROM "+ AvailablePeriod.TableName+" JOIN ("+Period.TableName+") ON ("+AvailablePeriod.TableName+".idPeriod = "+Period.TableName+".idPeriod)  WHERE "+AvailablePeriod.TableName+".idTH = ? ORDER BY "+Period.TableName+".`from` ASC;";
			PreparedStatement statement = ConnectionManager.prepareStatement(query);
			statement.setInt(1, getId());
			AvailablePeriodQuery query1 = new AvailablePeriodQuery();
			ObjectCollection collection = query1.getCollectionFromObjectResult(statement.executeQuery());
			setAvailablePeriods(collection);
		}
		return (ObjectCollection) this.getField("AvailablePeriods");
	}


	public TemporaryHousing setAvailablePeriods(ObjectCollection collection) throws Exception {
		if(collection.size() > 0 && collection.get(0).getClass() != AvailablePeriod.class)
				throw new Exception("The Collection had An Invalid Class");
		setField("AvailablePeriods", collection);
		return this;
	}

	public User getOwner() throws Exception {
		if(this.Owner == null && !IsCreating){
			String query = "SELECT * FROM "+ Period.TableName+" WHERE idUser = ?;";
			PreparedStatement statement = ConnectionManager.prepareStatement(query);
			statement.setInt(1, getOwnerId());
			UserQuery query1 = new UserQuery();
			ObjectCollection collection = query1.getCollectionFromObjectResult(statement.executeQuery());
			setOwner((User) collection.get(0));
			statement.close();
		}
		return (User) this.getField("Owner");
	}

	public TemporaryHousing setOwner(User owner) throws Exception {
		setField("OwnerId", owner.getId());
		setField("Owner", owner);
		return this;
	}

	public ObjectCollection getFeedback() throws Exception {
		if(this.Feedback == null && !IsCreating) {
			FeedbackQuery query = new FeedbackQuery();
			query.populateRelation("TemporaryHousing").filterByField("UserId", this.getId());
			setFeedback(query.find());
		}

		return (ObjectCollection) this.getField("Visits");
	}

	public TemporaryHousing setFeedback(ObjectCollection collection) throws Exception {
		if(collection != null && collection.size() > 0 && collection.get(0).getClass() != Feedback.class)
			throw new Exception("The Collection had An Invalid Class");
		setField("Feedback", collection);
		return this;
	}

	public ObjectCollection getTemporaryHousingKeywordMaps() throws Exception {
		if(TemporaryHousingKeywordMaps == null && !IsCreating){
			TemporaryHousingKeywordMapQuery query = new TemporaryHousingKeywordMapQuery();
			query.populateRelation("Keyword").filterByField("TemporaryHousingId", this.getId());
			setTemporaryHousingKeywordMaps(query.find());
		}

		return (ObjectCollection) this.getField("TemporaryHousingKeywordMaps");
	}

	public TemporaryHousing setTemporaryHousingKeywordMaps(ObjectCollection collection) throws Exception {
		if(collection != null && collection.size() > 0 && collection.get(0).getClass() != TemporaryHousingKeywordMap.class)
			throw new Exception("The Collection had An Invalid Class");
		this.setField("TemporaryHousingKeywordMaps", collection);
		return this;
	}

	public ObjectCollection getKeywords() throws Exception{
		ObjectCollection keywordMaps = getTemporaryHousingKeywordMaps();
		ObjectCollection relatedKeywords = new ObjectCollection();

		for (BaseObject object: keywordMaps) {
			TemporaryHousingKeywordMap keywordMap = (TemporaryHousingKeywordMap) object;

			relatedKeywords.add(keywordMap.getKeyword());
		}

		return relatedKeywords;
	}

	public ObjectCollection getFavorites() throws Exception {
		if(Favorites == null && !IsCreating){
			FavoriteQuery query = new FavoriteQuery();
			query.populateRelation("User").filterByField("TemporaryHousingId", this.getId());
			setTemporaryHousingKeywordMaps(query.find());
		}

		return (ObjectCollection) this.getField("Favorites");
	}

	public TemporaryHousing setFavorites(ObjectCollection collection) throws Exception {
		if(collection != null && collection.size() > 0 && collection.get(0).getClass() != Favorite.class)
			throw new Exception("The Collection had An Invalid Class");
		this.setField("Favorites", collection);
		return this;
	}
}
