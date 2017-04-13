package main.java.models;

import main.java.models.base.Attribute;
import main.java.models.base.AttributeRelationship;
import main.java.models.base.BaseObject;

import java.sql.Date;
import java.util.*;

import static java.util.Arrays.asList;
import static main.java.models.base.Attribute.ForeignRelationshipType.MANY_TO_ONE;
import static main.java.models.base.Attribute.ForeignRelationshipType.ONE_TO_MANY;
import static main.java.models.base.Attribute.ForeignRelationshipType.ONE_TO_ONE;

/**
 * Created by Student Nathan on 4/13/2017.
 */
public class FeedbackRating extends BaseObject{

    public final static List<Attribute> Attributes = asList(
            new Attribute("UserId", Integer.class, "idUser", true),
            new Attribute("FeedbackId", Integer.class, "idfeedback", true),
            new Attribute("Rating", Integer.class, "rating", false),

            new Attribute("User", User.class, "User", false, MANY_TO_ONE,
                    Arrays.asList(new AttributeRelationship("UserId", "Id"))),
            new Attribute("Feedback", Feedback.class, "feedback", false, MANY_TO_ONE,
                    Arrays.asList(new AttributeRelationship("FeedbackId", "Id")))
    );

    private static final Map<String, Attribute> AttributeMap;
    static {
        Map<String, Attribute> aMap = new HashMap<>();
        for (Attribute attr: Attributes) {
            aMap.put(attr.JavaFieldName, attr);
        }
        AttributeMap = Collections.unmodifiableMap(aMap);
    }

    public final static String TableName = "Rate";

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
    public Integer FeedbackId;
    public Integer Rating;

    public User User;
    public Feedback Feedback;

    public Integer getUserId() throws Exception {
        return (Integer) this.getField("UserId");
    }

    public FeedbackRating setUserId(Integer userId) throws Exception {
        this.setField("UserId", userId);
        return this;
    }

    public Integer getFeedbackId() throws Exception {
        return (Integer) this.getField("FeedbackId");
    }

    public FeedbackRating setFeedbackId(Integer feedbackId) throws Exception {
        this.setField("FeedbackId", feedbackId);
        return this;
    }

    public Integer getRating() throws Exception {
        return (Integer) this.getField("Rating");
    }

    public FeedbackRating setRating(Integer rating) throws Exception {
        this.setField("Rating", rating);
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

    public FeedbackRating setUser(main.java.models.User user) throws Exception {
        this.setField("User", user);
        return this;
    }

    public main.java.models.Feedback getFeedback() throws Exception {
        if(Feedback == null && !IsCreating){
            FeedbackQuery query = new FeedbackQuery();
            query.filterByField("Id", this.getFeedbackId());
            setFeedback(query.findOne());
        }

        return (main.java.models.Feedback) this.getField("Feedback");
    }

    public FeedbackRating setFeedback(main.java.models.Feedback feedback) throws Exception {
        this.setField("Feedback", feedback);
        return this;
    }
}
