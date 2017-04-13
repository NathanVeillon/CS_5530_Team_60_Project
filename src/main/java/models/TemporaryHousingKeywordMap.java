package main.java.models;

import main.java.models.base.Attribute;
import main.java.models.base.AttributeRelationship;
import main.java.models.base.BaseObject;
import main.java.models.base.ObjectCollection;

import java.util.*;

import static java.util.Arrays.asList;
import static main.java.models.base.Attribute.ForeignRelationshipType.MANY_TO_ONE;
import static main.java.models.base.Attribute.ForeignRelationshipType.ONE_TO_MANY;

/**
 * Created by Student Nathan on 4/13/2017.
 */
public class TemporaryHousingKeywordMap extends BaseObject{

    public final static List<Attribute> Attributes = asList(
            new Attribute("TemporaryHousingId", Integer.class, "idTH", true),
            new Attribute("KeywordId", Integer.class, "idKeywords", true),

            new Attribute("TemporaryHousing", TemporaryHousing.class, "TemporaryHousing", false, MANY_TO_ONE,
                    Arrays.asList(new AttributeRelationship("TemporaryHousingId", "Id"))),
            new Attribute("Keyword", Keyword.class, "Keywords", false, MANY_TO_ONE,
                          Arrays.asList(new AttributeRelationship("KeywordId", "Id")))
    );

    private static final Map<String, Attribute> AttributeMap;
    static {
        Map<String, Attribute> aMap = new HashMap<>();
        for (Attribute attr: Attributes) {
            aMap.put(attr.JavaFieldName, attr);
        }
        AttributeMap = Collections.unmodifiableMap(aMap);
    }

    public final static String TableName = "HasKeywords";

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
    public Integer KeywordId;

    public TemporaryHousing TemporaryHousing;
    public Keyword Keyword;

    public Integer getTemporaryHousingId() throws Exception {
        return (Integer) this.getField("TemporaryHousingId");
    }

    public TemporaryHousingKeywordMap setTemporaryHousingId(Integer temporaryHousingId) throws Exception {
        this.setField("TemporaryHousingId", temporaryHousingId);
        return this;
    }

    public Integer getKeywordId() throws Exception {
        return (Integer) this.getField("KeywordId");
    }

    public TemporaryHousingKeywordMap setKeywordId(Integer keywordId) throws Exception {
        this.setField("KeywordId", keywordId);
        return this;
    }

    public main.java.models.TemporaryHousing getTemporaryHousing() throws Exception {
        return (main.java.models.TemporaryHousing) this.getField("TemporaryHousing");
    }

    public TemporaryHousingKeywordMap setTemporaryHousing(main.java.models.TemporaryHousing temporaryHousing) throws Exception {
        this.setField("TemporaryHousing", temporaryHousing);
        return this;
    }

    public Keyword getKeyword() throws Exception {
        return (main.java.models.Keyword) this.getField("Keyword");
    }

    public TemporaryHousingKeywordMap setKeyword(main.java.models.Keyword keyword) throws Exception {
        this.setField("Keyword", keyword);
        return this;
    }
}
