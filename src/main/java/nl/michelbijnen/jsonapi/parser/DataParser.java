package nl.michelbijnen.jsonapi.parser;

import nl.michelbijnen.jsonapi.annotation.JsonApiId;
import nl.michelbijnen.jsonapi.annotation.JsonApiObject;
import nl.michelbijnen.jsonapi.exception.JsonApiException;
import nl.michelbijnen.jsonapi.helper.GetterAndSetter;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.stream.Stream;

class DataParser {

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

        AttributesParser attributesParser = new AttributesParser();
        data.put("attributes", attributesParser.parse(object));

        JSONObject parsedRelationships = new RelationshipParser().parse(object);
        if (!parsedRelationships.isEmpty())
            data.put("relationships", parsedRelationships);

        return data;
    }

    private String getType(Object object) {
        if (object.getClass().getAnnotation(JsonApiObject.class) != null) {
            return object.getClass().getAnnotation(JsonApiObject.class).value();
        }
        throw new JsonApiException("@JsonApiObject(\"<classname>\") missing");
    }

    private String getId(Object object) {
        Field[] allFields = Stream.concat(Arrays.stream(object.getClass().getDeclaredFields()), Arrays.stream(object.getClass().getSuperclass().getDeclaredFields())).toArray(Field[]::new);
        for (Field field : allFields) {
            if (field.isAnnotationPresent(JsonApiId.class)) {
                return GetterAndSetter.callGetter(object, field.getName()).toString();
            }
        }
        throw new JsonApiException("No field with @JsonApiId is found");
    }
}
