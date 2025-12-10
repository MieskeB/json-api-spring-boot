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

import static nl.michelbijnen.jsonapi.util.JsonApiConstants.*;

class DataParser {

    /**
     * Parses the given object into a JSON:API data node.
     * Populates type, id, and (if present) attributes and relationships.
     *
     * @param object the object to parse
     * @param mapper the ObjectMapper used to construct result nodes
     * @return an ObjectNode representing the object's JSON:API data
     */
    ObjectNode parse(Object object, ObjectMapper mapper) {
        return this.parse(object, false, mapper);
    }

    /**
     * Parses the given object into a JSON:API data node, optionally as a relation.
     * When {@code asRelation} is true, only {@code type} and {@code id} are included.
     * Otherwise, attributes and relationships are also populated.
     *
     * @param object     the object to parse
     * @param asRelation whether to emit only a relationship identifier object (type, id)
     * @param mapper     the ObjectMapper used to construct result nodes
     * @return an ObjectNode representing the object's JSON:API data
     */
    ObjectNode parse(Object object, boolean asRelation, ObjectMapper mapper) {
        ObjectNode data = mapper.createObjectNode();

        data.put(TYPE, this.getType(object));
        data.put(ID, this.getId(object));

        if (asRelation) {
            return data;
        }

        AttributesParser attributesParser = new AttributesParser();
        ObjectNode attributes = attributesParser.parse(object, mapper);
        if (!attributes.isEmpty()) {
            data.set(ATTRIBUTES, attributes);
        }

        RelationshipParser relationshipParser = new RelationshipParser();
        ObjectNode parsedRelationships = relationshipParser.parse(object, mapper);
        if (!parsedRelationships.isEmpty())
            data.set(RELATIONSHIPS, parsedRelationships);

        return data;
    }

    /**
     * Parses the given object into a JSON:API data node with options, optionally as a relation.
     * - Attributes are filtered using {@link JsonApiOptions} fields by type (if provided).
     * - Relationships are limited to those allowed by field filtering
     * also include top-level include relations from {@link JsonApiOptions#topLevelIncludeRelations()}.
     *
     * @param object  the object to parse
     * @param mapper  the ObjectMapper used to construct result nodes
     * @param options parse options (it may be null) for field and include filtering
     * @return an ObjectNode representing the object's JSON:API data
     */
    ObjectNode parse(Object object, ObjectMapper mapper, JsonApiOptions options) {
        if (options == null) {
            return parse(object, false, mapper);
        }

        ObjectNode data = mapper.createObjectNode();

        String type = this.getType(object);
        data.put(TYPE, type);
        data.put(ID, this.getId(object));

        AttributesParser attributesParser = new AttributesParser();
        ObjectNode attributes = attributesParser.parse(object, mapper, options);
        if (!attributes.isEmpty()) {
            data.set(ATTRIBUTES, attributes);
        }

        Set<String> allowed = null;
        Set<String> actualRelNames = new HashSet<>();
        for (Field f : object.getClass().getDeclaredFields()) {
            nl.michelbijnen.jsonapi.annotation.JsonApiRelation ann =
                    f.getAnnotation(nl.michelbijnen.jsonapi.annotation.JsonApiRelation.class);
            if (ann != null) {
                actualRelNames.add(ann.value());
            }
        }
        if (options.hasFieldsForType(type)) {
            Set<String> fromFields = new HashSet<>(options.fieldsForType(type));
            fromFields.retainAll(actualRelNames);
            allowed = fromFields;
        } else if (options.getFieldInclusionMode() == JsonApiOptions.AttributesInclusionMode.INCLUDE_ALL) {
            allowed = new HashSet<>(actualRelNames);
        }

        Set<String> fromInclude = options.topLevelIncludeRelations();
        if (fromInclude != null && !fromInclude.isEmpty()) {
            if (allowed == null) {
                allowed = new HashSet<>();
            }
            allowed.addAll(fromInclude);
        }

        if (allowed == null) {
            allowed = Collections.emptySet();
        }

        RelationshipParser relationshipParser = new RelationshipParser();
        ObjectNode parsedRelationships = relationshipParser.parse(object, mapper, allowed);
        if (!parsedRelationships.isEmpty())
            data.set(RELATIONSHIPS, parsedRelationships);

        return data;
    }

    private String getType(Object object) {
        if (object.getClass().getAnnotation(JsonApiObject.class) != null) {
            return object.getClass().getAnnotation(JsonApiObject.class).value();
        }
        throw new JsonApiException(JSON_API_OBJECT_MISSING);
    }

    private String getId(Object object) {
        Field[] allFields = Stream.concat(Arrays.stream(object.getClass().getDeclaredFields()),
                Arrays.stream(object.getClass().getSuperclass().getDeclaredFields())).toArray(Field[]::new);
        for (Field field : allFields) {
            if (field.isAnnotationPresent(JsonApiId.class)) {
                return GetterAndSetter.callGetter(object, field.getName()).toString();
            }
        }
        throw new JsonApiException(JSON_API_ID_MISSING);
    }
}