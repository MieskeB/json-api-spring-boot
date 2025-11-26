package nl.michelbijnen.jsonapi.parser;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import nl.michelbijnen.jsonapi.annotation.JsonApiRelation;
import nl.michelbijnen.jsonapi.helper.GetterAndSetter;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Optional;

class RelationshipParser {
    private final DataParser dataParser;
    private final LinksParser linksParser;

    RelationshipParser() {
        this.dataParser = new DataParser();
        this.linksParser = new LinksParser();
    }

    /**
     * This class should add the name of the object and under it the following items:
     * <p>
     * links
     * data
     *
     * @param object
     * @return
     */
    ObjectNode parse(Object object, ObjectMapper mapper) {
        ObjectNode jsonObject = mapper.createObjectNode();

        for (Field field : object.getClass().getDeclaredFields()) {
            if (field.isAnnotationPresent(JsonApiRelation.class)) {

                Object relationObject = GetterAndSetter.callGetter(object, field.getName());

                if (relationObject == null) {
                    continue;
                }

                ObjectNode relation;
                if (this.isList(relationObject)) {
                    relation = this.parseRelationshipAsList(object, field, mapper);
                } else {
                    relation = this.parseRelationshipAsObject(object, field, mapper);
                }
                if (!relation.isEmpty())
                    jsonObject.set(field.getAnnotation(JsonApiRelation.class).value(), relation);
            }
        }
        return jsonObject;
    }

    private ObjectNode parseRelationshipAsObject(Object object, Field field, ObjectMapper mapper) {
        ObjectNode relationship = mapper.createObjectNode();

        Object relationObject = GetterAndSetter.callGetter(object, field.getName());
        if (relationObject instanceof Optional) {
            Optional<?> opt = (Optional<?>) relationObject;
            if (opt.isPresent()) {
                relationObject = opt.get();
            } else {
                return mapper.createObjectNode(); // Return empty relationship for absent Optional
            }
        }
        ObjectNode linksParsed = this.linksParser.parse(relationObject, mapper);
        if (!linksParsed.isEmpty())
            relationship.set("links", linksParsed);

        ObjectNode dataParsed = this.dataParser.parse(relationObject, true, mapper);
        if (!dataParsed.isEmpty())
            relationship.set("data", dataParsed);

        return relationship;
    }

    private ObjectNode parseRelationshipAsList(Object object, Field field, ObjectMapper mapper) {
        ObjectNode relationship = mapper.createObjectNode();

        Collection<Object> relationObjectCollection = (Collection<Object>) GetterAndSetter.callGetter(object, field.getName());

        ObjectNode linksParsed = this.linksParser.parse(relationObjectCollection, mapper);
        if (!linksParsed.isEmpty())
            relationship.set("links", linksParsed);

        ArrayNode dataForEach = mapper.createArrayNode();
        for (Object relationObject : relationObjectCollection) {
            ObjectNode dataObj = this.dataParser.parse(relationObject, true, mapper);
            if (!this.dataExistsInArray(dataForEach, dataObj)) {
                dataForEach.add(dataObj);
            }
        }
        if (!dataForEach.isEmpty())
            relationship.set("data", dataForEach);

        return relationship;
    }

    private boolean dataExistsInArray(ArrayNode dataArray, ObjectNode newData) {
        String id = newData.get("id").asText();
        String type = newData.get("type").asText();
        for (JsonNode existing : dataArray) {
            if (existing.get("id").asText().equals(id) && existing.get("type").asText().equals(type)) {
                return true;
            }
        }
        return false;
    }

    private boolean isList(Object object) {
        return Collection.class.isAssignableFrom(object.getClass());
    }
}
