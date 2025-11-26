package nl.michelbijnen.jsonapi.parser;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import nl.michelbijnen.jsonapi.annotation.JsonApiId;
import nl.michelbijnen.jsonapi.annotation.JsonApiObject;
import nl.michelbijnen.jsonapi.exception.JsonApiException;
import nl.michelbijnen.jsonapi.helper.GetterAndSetter;

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
    ObjectNode parse(Object object, ObjectMapper mapper) {
        return this.parse(object, false, mapper);
    }

    ObjectNode parse(Object object, boolean asRelation, ObjectMapper mapper) {
        ObjectNode data = mapper.createObjectNode();

        data.put("type", this.getType(object));
        data.put("id", this.getId(object));

        if (asRelation) {
            return data;
        }

        AttributesParser attributesParser = new AttributesParser();
        ObjectNode attributes = attributesParser.parse(object, mapper);
        if (!attributes.isEmpty()) {
            data.set("attributes", attributes);
        }

        RelationshipParser relationshipParser = new RelationshipParser();
        ObjectNode parsedRelationships = relationshipParser.parse(object, mapper);
        if (!parsedRelationships.isEmpty())
            data.set("relationships", parsedRelationships);

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