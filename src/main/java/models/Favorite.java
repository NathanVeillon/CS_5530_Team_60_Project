package main.java.models;

import main.java.models.base.Attribute;
import main.java.models.base.AttributeRelationship;
import main.java.models.base.BaseObject;
import main.java.models.base.ObjectCollection;

import java.sql.Date;
import java.util.*;

import static java.util.Arrays.asList;
import static main.java.models.base.Attribute.ForeignRelationshipType.MANY_TO_ONE;
import static main.java.models.base.Attribute.ForeignRelationshipType.ONE_TO_MANY;

/**
 * Created by Student Nathan on 4/13/2017.
 */
public class Favorite extends BaseObject{

    public final static List<Attribute> Attributes = asList(
            new Attribute("TemporaryHousingId", Integer.class, "idTH", true),
            new Attribute("UserId", Integer.class, "idUser", true),
            new Attribute("Date", Date.class, "date", false),

            new Attribute("User", User.class, "User", false, MANY_TO_ONE,
                    Arrays.asList(new AttributeRelationship("UserId", "Id"))),
            new Attribute("TemporaryHousing", TemporaryHousing.class, "TemporaryHousing", false, MANY_TO_ONE,
                    Arrays.asList(new AttributeRelationship("TemporaryHousingId", "Id")))
    );

    private static final Map<String, Attribute> AttributeMap;
    static {
        Map<String, Attribute> aMap = new HashMap<>();
        for (Attribute attr: Attributes) {
            aMap.put(attr.JavaFieldName, attr);
        }
        AttributeMap = Collections.unmodifiableMap(aMap);
    }

    public final static String TableName = "feedback";

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

    public Integer UserId;
    public Integer TemporaryHousingId;
    public Date Date;

    public User User;
    public TemporaryHousing TemporaryHousing;

    public Integer getUserId() throws Exception {
        return (Integer) this.getField("UserId");
    }

    public Favorite setUserId(Integer userId) throws Exception {
        this.setField("UserId", userId);
        return this;
    }

    public Integer getTemporaryHousingId() throws Exception {
        return (Integer) this.getField("TemporaryHousingId");
    }

    public Favorite setTemporaryHousingId(Integer temporaryHousingId) throws Exception {
        this.setField("TemporaryHousingId", temporaryHousingId);
        return this;
    }

    public java.sql.Date getDate() throws Exception {
        return (java.sql.Date) this.getField("Date");
    }

    public Favorite setDate(java.sql.Date date) throws Exception {
        this.setField("Date", date);
        return this;
    }

    public User getUser() throws Exception {
        if(User == null && !IsCreating){
            UserQuery query = new UserQuery();
            query.filterByField("Id", this.getUserId());
            setUser(query.findOne());
        }

        return (User) this.getField("User");
    }

    public Favorite setUser(main.java.models.User user) throws Exception {
        this.setField("User", user);
        return this;
    }

    public TemporaryHousing getTemporaryHousing() throws Exception {
        if(TemporaryHousing == null && !IsCreating){
            TemporaryHousingQuery query = new TemporaryHousingQuery();
            query.filterByField("Id", this.getTemporaryHousingId());
            setTemporaryHousing(query.findOne());
        }

        return (main.java.models.TemporaryHousing) this.getField("TemporaryHousing");
    }

    public Favorite setTemporaryHousing(main.java.models.TemporaryHousing temporaryHousing) throws Exception {
        this.setField("TemporaryHousing", temporaryHousing);
        return this;
    }
}
