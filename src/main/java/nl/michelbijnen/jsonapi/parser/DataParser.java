package nl.michelbijnen.jsonapi.parser;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import nl.michelbijnen.jsonapi.annotation.JsonApiId;
import nl.michelbijnen.jsonapi.annotation.JsonApiObject;
import nl.michelbijnen.jsonapi.exception.JsonApiException;
import nl.michelbijnen.jsonapi.helper.GetterAndSetter;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
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

    // in DataParser.java
    ObjectNode parse(Object object, boolean asRelation, ObjectMapper mapper, JsonApiOptions options, boolean isPrimaryResource) {
        if (options == null) {
            return parse(object, asRelation, mapper);
        }

        ObjectNode data = mapper.createObjectNode();

        String type = this.getType(object);
        data.put("type", type);
        data.put("id", this.getId(object));

        if (asRelation) {
            return data;
        }

        AttributesParser attributesParser = new AttributesParser();
        ObjectNode attributes = attributesParser.parse(object, mapper, options);
        if (!attributes.isEmpty()) {
            data.set("attributes", attributes);
        }

        Set<String> allowed = null;
        Set<String> actualRelNames = new HashSet<>();
        for (Field f : object.getClass().getDeclaredFields()) {
            nl.michelbijnen.jsonapi.annotation.JsonApiRelation ann = f.getAnnotation(nl.michelbijnen.jsonapi.annotation.JsonApiRelation.class);
            if (ann != null) {
                actualRelNames.add(ann.value());
            }
        }
        if (options.hasFieldsForType(type)) {
            Set<String> fromFields = new HashSet<>(options.fieldsForType(type));
            fromFields.retainAll(actualRelNames);
            allowed = fromFields;
        }

        if (isPrimaryResource) {
            Set<String> fromInclude = options.topLevelIncludeRelations();
            if (fromInclude != null && !fromInclude.isEmpty()) {
                if (allowed == null) {
                    allowed = new HashSet<>();
                }
                allowed.addAll(fromInclude);
            }
        }

        if (allowed == null) {
            allowed = Collections.emptySet();
        }

        RelationshipParser relationshipParser = new RelationshipParser();
        ObjectNode parsedRelationships = relationshipParser.parse(object, mapper, allowed);
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