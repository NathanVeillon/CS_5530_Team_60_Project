package main.java.models;

import main.java.models.base.Attribute;
import main.java.models.base.AttributeRelationship;
import main.java.models.base.BaseObject;
import main.java.models.base.ObjectCollection;

import java.util.*;

import static java.util.Arrays.asList;
import static main.java.models.base.Attribute.ForeignRelationshipType.*;

/**
 * Created by Student Nathan on 4/13/2017.
 */
public class Keyword extends BaseObject{

    public final static List<Attribute> Attributes = asList(
            new Attribute("Id", Integer.class, "idKeywords", true),
            new Attribute("Word", String.class, "word", false),
            new Attribute("Language", String.class, "language", false),

            new Attribute("TemporaryHousingKeywordMaps", User.class, "HasKeywords", false, ONE_TO_MANY,
                    Arrays.asList(new AttributeRelationship("Id", "KeywordId")))
    );

    private static final Map<String, Attribute> AttributeMap;
    static {
        Map<String, Attribute> aMap = new HashMap<>();
        for (Attribute attr: Attributes) {
            aMap.put(attr.JavaFieldName, attr);
        }
        AttributeMap = Collections.unmodifiableMap(aMap);
    }

    public final static String TableName = "Keywords";

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
    public String Word;
    public String Language;

    public ObjectCollection TemporaryHousingKeywordMaps;

    public Integer getId() throws Exception {
        return (Integer) this.getField("Id");
    }

    public Keyword setId(Integer id) throws Exception {
        this.setField("Id", id);
        return this;
    }

    public String getWord() throws Exception {
        return (String) this.getField("Word");
    }

    public Keyword setWord(String word) throws Exception {
        this.setField("Word", word);
        return this;
    }

    public String getLanguage() throws Exception {
        return (String) this.getField("Language");
    }

    public Keyword setLanguage(String language) throws Exception {
        this.setField("Language", language);
        return this;
    }

    public ObjectCollection getTemporaryHousingKeywordMaps() throws Exception {
        if(TemporaryHousingKeywordMaps == null && !IsCreating){
            TemporaryHousingKeywordMapQuery query = new TemporaryHousingKeywordMapQuery();
            query.populateRelation("TemporaryHousing").filterByField("KeywordId", this.getId());
            setTemporaryHousingKeywordMaps(query.find());
        }

        return (ObjectCollection) this.getField("TemporaryHousingKeywordMaps");
    }

    public Keyword setTemporaryHousingKeywordMaps(ObjectCollection collection) throws Exception {
        if(collection != null && collection.size() > 0 && collection.get(0).getClass() != TemporaryHousingKeywordMap.class)
            throw new Exception("The Collection had An Invalid Class");
        this.setField("TemporaryHousingKeywordMaps", collection);
        return this;
    }

    public ObjectCollection getTemporaryHousing() throws Exception{
        ObjectCollection keywordMaps = getTemporaryHousingKeywordMaps();
        ObjectCollection relatedHousing = new ObjectCollection();

        for (BaseObject object: keywordMaps) {
            TemporaryHousingKeywordMap keywordMap = (TemporaryHousingKeywordMap) object;

            relatedHousing.add(keywordMap.getTemporaryHousing());
        }

        return relatedHousing;
    }
}
