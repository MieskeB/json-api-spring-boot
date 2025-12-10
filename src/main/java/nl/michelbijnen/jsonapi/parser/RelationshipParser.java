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
import java.util.Set;

import static nl.michelbijnen.jsonapi.util.JsonApiConstants.*;

class RelationshipParser {
    private final DataParser dataParser;
    private final LinksParser linksParser;

    RelationshipParser() {
        this.dataParser = new DataParser();
        this.linksParser = new LinksParser();
    }

    /**
     * Builds a JSON:API relationships object for the given domain object.
     * <p>
     * Delegates to {@link #parse(Object, ObjectMapper, java.util.Set)} with {@code allowedRelationshipNames = null}.
     *
     * @param object the domain object whose relations are inspected
     * @param mapper Jackson {@link com.fasterxml.jackson.databind.ObjectMapper} used to create nodes
     * @return an {@link com.fasterxml.jackson.databind.node.ObjectNode} containing relationship entries; possibly empty
     */
    ObjectNode parse(Object object, ObjectMapper mapper) {
        return parse(object, mapper, null);
    }

    /**
     * Builds a JSON:API relationships object for the given domain object, optionally filtering by relation name.
     * <p>
     * Behavior:
     * - Iterates declared fields annotated with {@link nl.michelbijnen.jsonapi.annotation.JsonApiRelation}.
     * - If {@code allowedRelationshipNames} is non-null, only relations whose annotation value is contained in the set are included.
     * - Null relation values are skipped.
     * - For collection relations, delegates to {@code parseRelationshipAsList}; otherwise to {@code parseRelationshipAsObject}.
     * - Only non-empty relationship nodes are added under their relation name.
     *
     * @param object                   the domain object whose relations are inspected
     * @param mapper                   Jackson {@link com.fasterxml.jackson.databind.ObjectMapper} used to create nodes
     * @param allowedRelationshipNames optional whitelist of relation names to include; may be {@code null}
     * @return an {@link com.fasterxml.jackson.databind.node.ObjectNode} mapping relation names to relationship objects; possibly empty
     */
    ObjectNode parse(Object object, ObjectMapper mapper, Set<String> allowedRelationshipNames) {
        ObjectNode jsonObject = mapper.createObjectNode();

        for (Field field : object.getClass().getDeclaredFields()) {
            if (!field.isAnnotationPresent(JsonApiRelation.class)) {
                continue;
            }
            String relName = field.getAnnotation(JsonApiRelation.class).value();
            if (allowedRelationshipNames != null && !allowedRelationshipNames.contains(relName)) {
                continue;
            }

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
                jsonObject.set(relName, relation);
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
                return mapper.createObjectNode();
            }
        }
        ObjectNode linksParsed = this.linksParser.parse(relationObject, mapper);
        if (!linksParsed.isEmpty())
            relationship.set(LINKS, linksParsed);

        ObjectNode dataParsed = this.dataParser.parse(relationObject, true, mapper);
        if (!dataParsed.isEmpty())
            relationship.set(DATA, dataParsed);

        return relationship;
    }

    private ObjectNode parseRelationshipAsList(Object object, Field field, ObjectMapper mapper) {
        ObjectNode relationship = mapper.createObjectNode();

        Collection<Object> relationObjectCollection = (Collection<Object>) GetterAndSetter
                .callGetter(object, field.getName());

        ObjectNode linksParsed = this.linksParser.parse(relationObjectCollection, mapper);
        if (!linksParsed.isEmpty())
            relationship.set(LINKS, linksParsed);

        ArrayNode dataForEach = mapper.createArrayNode();
        for (Object relationObject : relationObjectCollection) {
            ObjectNode dataObj = this.dataParser.parse(relationObject, true, mapper);
            if (!this.dataExistsInArray(dataForEach, dataObj)) {
                dataForEach.add(dataObj);
            }
        }
        if (!dataForEach.isEmpty())
            relationship.set(DATA, dataForEach);

        return relationship;
    }

    private boolean dataExistsInArray(ArrayNode dataArray, ObjectNode newData) {
        String id = newData.get(ID).asText();
        String type = newData.get(TYPE).asText();
        for (JsonNode existing : dataArray) {
            if (existing.get(ID).asText().equals(id) && existing.get(TYPE).asText().equals(type)) {
                return true;
            }
        }
        return false;
    }

    private boolean isList(Object object) {
        return Collection.class.isAssignableFrom(object.getClass());
    }
}