package main.java.models;

import main.java.models.base.Attribute;
import main.java.models.base.AttributeRelationship;
import main.java.models.base.BaseObject;


import java.sql.Date;
import java.util.*;

import static java.util.Arrays.asList;
import static main.java.models.base.Attribute.ForeignRelationshipType.MANY_TO_ONE;

/**
 * Created by Student Nathan on 4/13/2017.
 */
public class Feedback extends BaseObject{

    public final static List<Attribute> Attributes = asList(
            new Attribute("Id", Integer.class, "idfeedback", true),
            new Attribute("Score", Integer.class, "score", false),
            new Attribute("Text", String.class, "text", false),
            new Attribute("Date", Date.class, "date", false),
            new Attribute("UserId", Integer.class, "idUser", true),
            new Attribute("TemporaryHousingId", Integer.class, "idTH", true),

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

    public final static String TableName = "Reserve";

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
    public Integer Score;
    public String Text;
    public Date Date;
    public Integer UserId;
    public Integer TemporaryHousingId;

    public User User;
    public TemporaryHousing TemporaryHousing;

    public Integer getId() throws Exception {
        return (Integer) this.getField("Id");
    }

    public Feedback setId(Integer id) throws Exception {
        this.setField("Id", id);
        return this;
    }

    public Integer getScore() throws Exception {
        return (Integer) this.getField("Score");
    }

    public Feedback setScore(Integer score) throws Exception {
        this.setField("Score", score);
        return this;
    }

    public String getText() throws Exception {
        return (String) this.getField("Text");
    }

    public Feedback setText(String text) throws Exception {
        this.setField("Text", text);
        return this;
    }

    public java.sql.Date getDate() throws Exception {
        return (java.sql.Date) this.getField("Date");
    }

    public Feedback setDate(java.sql.Date date) throws Exception {
        this.setField("Date", date);
        return this;
    }

    public Integer getUserId() throws Exception {
        return (Integer) this.getField("UserId");
    }

    public Feedback setUserId(Integer userId) throws Exception {
        this.setField("UserId", userId);
        return this;
    }

    public Integer getTemporaryHousingId() throws Exception {
        return (Integer) this.getField("TemporaryHousingId");
    }

    public Feedback setTemporaryHousingId(Integer temporaryHousingId) throws Exception {
        this.setField("TemporaryHousingId", temporaryHousingId);
        return this;
    }

    public User getUser() throws Exception {
        if(TemporaryHousing == null && !IsCreating){
            UserQuery query = new UserQuery();
            query.filterByField("Id", this.getUserId());
            setUser(query.findOne());
        }

        return (User) this.getField("User");
    }

    public Feedback setUser(main.java.models.User user) throws Exception {
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

    public Feedback setTemporaryHousing(main.java.models.TemporaryHousing temporaryHousing) throws Exception {
        this.setField("TemporaryHousing", temporaryHousing);
        return this;
    }
}
