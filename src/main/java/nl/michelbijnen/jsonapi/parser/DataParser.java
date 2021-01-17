package nl.michelbijnen.jsonapi.parser;

import nl.michelbijnen.jsonapi.annotation.JsonApiId;
import nl.michelbijnen.jsonapi.annotation.JsonApiObject;
import nl.michelbijnen.jsonapi.exception.JsonApiException;
import org.json.JSONObject;

import java.lang.reflect.Field;

class DataParser {

    private AttributesParser attributesParser;
    private RelationshipParser relationshipParser;

    DataParser() {
        this.attributesParser = new AttributesParser();
        this.relationshipParser = new RelationshipParser();
    }

    /**
     * This method should return a jsonobject with the following properties:
     * type
     * id
     * attributes
     * relationships
     * <p>
     * As a relation, this should only return type and id
     *
     * @param object The object to be converted to data
     * @return a json object with the object's data
     */
    JSONObject parse(Object object) {
        return this.parse(object, false);
    }

    JSONObject parse(Object object, boolean asRelation) {
        JSONObject data = new JSONObject();

        data.put("type", this.getType(object));
        data.put("id", this.getId(object));

        if (asRelation) {
            return data;
        }

        data.put("attributes", this.attributesParser.parse(object));
        data.put("relationships", this.relationshipParser.parse(object));

        return data;
    }

    private String getType(Object object) {
        if (object.getClass().getAnnotation(JsonApiObject.class) != null) {
            return object.getClass().getAnnotation(JsonApiObject.class).value();
        }
        throw new JsonApiException("@JsonApiObject(\"<classname>\") missing");
    }

    private String getId(Object object) {
        for (Field field : object.getClass().getDeclaredFields()) {
            if (field.isAnnotationPresent(JsonApiId.class)) {
                return field.getName();
            }
        }
        throw new JsonApiException("No field with @JsonApiId is found");
    }
}
