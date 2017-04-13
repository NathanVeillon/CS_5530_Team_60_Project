package main.java.models.base;

/**
 * Created by Student Nathan on 4/12/2017.
 */
public class AttributeRelationship {

    private String LocalObjectFieldName;
    private String ForeignObjectFieldName;

    public AttributeRelationship(String localObjectFieldName, String foreignObjectFieldName) {
        LocalObjectFieldName = localObjectFieldName;
        ForeignObjectFieldName = foreignObjectFieldName;
    }

    public String getLocalObjectFieldName() {
        return LocalObjectFieldName;
    }

    public String getForeignObjectFieldName() {
        return ForeignObjectFieldName;
    }


}
