package main.java.models;

import main.java.models.base.Attribute;
import main.java.models.base.AttributeRelationship;
import main.java.models.base.BaseObject;

import java.util.*;

import static java.util.Arrays.asList;
import static main.java.models.base.Attribute.ForeignRelationshipType.MANY_TO_ONE;

/**
 * Created by Student Nathan on 4/13/2017.
 */
public class UserTrust extends BaseObject{

    public final static List<Attribute> Attributes = asList(
            new Attribute("SourceUserId", Integer.class, "idUser1", true),
            new Attribute("TargetUserId", Integer.class, "idUser2", true),
            new Attribute("SourceTrustsTarget", Integer.class, "isTrusted", false),

            new Attribute("SourceUser", User.class, "User", false, MANY_TO_ONE,
                    Arrays.asList(new AttributeRelationship("SourceUserId", "Id"))),
            new Attribute("TargetUser", User.class, "User", false, MANY_TO_ONE,
                    Arrays.asList(new AttributeRelationship("TargetUserId", "Id")))
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

    public Integer SourceUserId;
    public Integer TargetUserId;
    public Integer SourceTrustsTarget;

    public User SourceUser;
    public User TargetUser;

    public Integer getSourceUserId() throws Exception {
        return (Integer) this.getField("SourceUserId");
    }

    public UserTrust setSourceUserId(Integer sourceUserId) throws Exception {
        this.setField("SourceUserId", sourceUserId);
        return this;
    }

    public Integer getTargetUserId() throws Exception {
        return (Integer) this.getField("TargetUserId");
    }

    public UserTrust setTargetUserId(Integer targetUserId) throws Exception {
        this.setField("TargetUserId", targetUserId);
        return this;
    }

    public Integer getSourceTrustsTarget() throws Exception {
        return (Integer) this.getField("SourceTrustsTarget");
    }

    public UserTrust setSourceTrustsTarget(Integer sourceTrustsTarget) throws Exception {
        this.setField("SourceTrustsTarget", sourceTrustsTarget);
        return this;
    }

    public User getSourceUser() throws Exception {
        if(SourceUser == null && !IsCreating){
            UserQuery query = new UserQuery();
            query.filterByField("Id", getSourceUserId());
            setSourceUser(query.findOne());
        }

        return (User) this.getField("SourceUser");
    }

    public UserTrust setSourceUser(User sourceUser) throws Exception {
        this.setField("SourceUser", sourceUser);
        return this;
    }

    public User getTargetUser() throws Exception {
        if(TargetUser == null && !IsCreating){
            UserQuery query = new UserQuery();
            query.filterByField("Id", getTargetUserId());
            setTargetUser(query.findOne());
        }

        return (User) this.getField("TargetUser");
    }

    public UserTrust setTargetUser(User targetUser) throws Exception {
        this.setField("TargetUser", targetUser);
        return this;
    }
}
