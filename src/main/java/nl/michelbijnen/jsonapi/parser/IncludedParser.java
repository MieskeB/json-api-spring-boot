package nl.michelbijnen.jsonapi.parser;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import nl.michelbijnen.jsonapi.annotation.JsonApiId;
import nl.michelbijnen.jsonapi.annotation.JsonApiObject;
import nl.michelbijnen.jsonapi.annotation.JsonApiRelation;
import nl.michelbijnen.jsonapi.helper.GetterAndSetter;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import static nl.michelbijnen.jsonapi.util.JsonApiConstants.*;

class IncludedParser {
    private final DataParser dataParser;
    private final LinksParser linksParser;

    IncludedParser() {
        this.dataParser = new DataParser();
        this.linksParser = new LinksParser();
    }

    /**
     * Recursively parses relation fields from the given object (or collection) into {@code includeArray}
     * up to {@code maxDepth}, starting at {@code currentDepth}.
     * <p>
     * - Iterates over fields annotated with {@link JsonApiRelation}.
     * - Supports relation values that are single objects, {@link Collection}s, or {@link Optional}s.
     * - Avoids duplicates in {@code includeArray} based on resource type and id.
     *
     * @param object       the root object or an {@link Iterable} of objects to traverse
     * @param includeArray the target "included" array to populate
     * @param maxDepth     maximum relation depth to traverse (inclusive of the root at depth 0)
     * @param currentDepth current traversal depth
     * @param mapper       Jackson {@link ObjectMapper} used to create/compose nodes
     */
    void parse(Object object, ArrayNode includeArray, int maxDepth, int currentDepth, ObjectMapper mapper) {
        if (currentDepth == maxDepth) {
            return;
        }

        if (!(object instanceof Iterable)) {
            parseObject(object, includeArray, maxDepth, currentDepth, mapper);
            return;
        }

        Iterable<Object> collection = (Iterable<Object>) object;
        for (Object item : collection) {
            parseObject(item, includeArray, maxDepth, currentDepth, mapper);
        }
    }

    /**
     * Convenience overload that creates a new {@link ArrayNode} and parses {@code object} into it,
     * up to {@code maxDepth}.
     *
     * @param object   the root object or an {@link Iterable} of objects
     * @param maxDepth maximum relation depth to traverse
     * @param mapper   Jackson {@link ObjectMapper}
     * @return the populated "included" array
     */
    ArrayNode parse(Object object, int maxDepth, ObjectMapper mapper, JsonApiOptions options) {
        return this.parse(object, mapper.createArrayNode(), maxDepth, 0, mapper, options);
    }

    /**
     * Recursively parses relation fields with optional top-level include filtering from {@link JsonApiOptions}.
     * <p>
     * When {@code options.topLevelIncludeRelations()} is non-empty, only relations whose names match that set
     * are traversed at depth 0. Deeper levels are not restricted by this filter.
     *
     * @param object       the root object or an {@link Iterable} of objects to traverse
     * @param includeArray the target "included" array
     * @param maxDepth     maximum relation depth
     * @param currentDepth current traversal depth
     * @param mapper       Jackson {@link ObjectMapper}
     * @param options      options providing top-level relation filtering; may be {@code null}
     * @return the populated "included" array
     */
    ArrayNode parse(Object object, ArrayNode includeArray, int maxDepth, int currentDepth, ObjectMapper mapper,
                    JsonApiOptions options) {
        if (currentDepth == maxDepth) {
            return includeArray;
        }

        if (!(object instanceof Iterable)) {
            parseObject(object, includeArray, maxDepth, currentDepth, mapper, options);
            return includeArray;
        }

        Iterable<Object> collection = (Iterable<Object>) object;
        for (Object item : collection) {
            parseObject(item, includeArray, maxDepth, currentDepth, mapper, options);
        }
        return includeArray;
    }

    private void parseObject(Object object, ArrayNode includeArray, int maxDepth, int currentDepth,
                             ObjectMapper mapper) {
        for (Field relationField : object.getClass().getDeclaredFields()) {
            if (!relationField.isAnnotationPresent(JsonApiRelation.class)) {
                continue;
            }

            Object childRelationObject = GetterAndSetter.callGetter(object, relationField.getName());
            if (childRelationObject instanceof Optional) {
                Optional<?> opt = (Optional<?>) childRelationObject;
                if (opt.isPresent()) {
                    childRelationObject = opt.get();
                } else {
                    continue;
                }
            }
            if (childRelationObject == null) {
                continue;
            }

            if (this.isList(childRelationObject)) {
                if (((Collection<Object>) childRelationObject).isEmpty()) {
                    continue;
                }
                for (Object childRelationObjectAsItem : (Collection<Object>) childRelationObject) {
                    this.addObjectToIncludeArray(includeArray, childRelationObjectAsItem, mapper,null);
                    this.parse(childRelationObjectAsItem, includeArray, maxDepth, currentDepth + 1, mapper);
                }
            } else {
                this.addObjectToIncludeArray(includeArray, childRelationObject, mapper,null);
                this.parse(childRelationObject, includeArray, maxDepth, currentDepth + 1, mapper);
            }
        }
    }

    private void parseObject(Object object, ArrayNode includeArray, int maxDepth, int currentDepth, ObjectMapper mapper,
                             JsonApiOptions options) {
        Set<String> allowTop = (options == null) ? null : options.topLevelIncludeRelations();
        boolean hasFilter = options != null && allowTop != null && !allowTop.isEmpty();

        for (Field relationField : object.getClass().getDeclaredFields()) {
            if (!relationField.isAnnotationPresent(JsonApiRelation.class)) {
                continue;
            }
            String relName = relationField.getAnnotation(JsonApiRelation.class).value();

            if (hasFilter && currentDepth == 0 && !allowTop.contains(relName)) {
                continue;
            }

            Object childRelationObject = getChildRelationObject(object, relationField);
            if (childRelationObject == null) continue;

            if (this.isList(childRelationObject)) {
                if (((Collection<Object>) childRelationObject).isEmpty()) {
                    continue;
                }
                for (Object childRelationObjectAsItem : (Collection<Object>) childRelationObject) {
                    this.addObjectToIncludeArray(includeArray, childRelationObjectAsItem, mapper, options);
                    this.parse(childRelationObjectAsItem, includeArray, maxDepth, currentDepth + 1, mapper, options);
                }
            } else {
                this.addObjectToIncludeArray(includeArray, childRelationObject, mapper, options);
                this.parse(childRelationObject, includeArray, maxDepth, currentDepth + 1, mapper, options);
            }
        }
    }

    private static Object getChildRelationObject(Object object, Field relationField) {
        Object childRelationObject = GetterAndSetter.callGetter(object, relationField.getName());
        if (childRelationObject instanceof Optional) {
            Optional<?> opt = (Optional<?>) childRelationObject;
            if (opt.isPresent()) {
                childRelationObject = opt.get();
            } else {
                return null;
            }
        }
        return childRelationObject;
    }

    private void addObjectToIncludeArray(ArrayNode includeArray, Object relationObject, ObjectMapper mapper,
                                         JsonApiOptions options) {
        if (rootElementExists(includeArray, relationObject)) {
            return;
        }

        ObjectNode singleIncludeObject = (options != null) ?
                this.dataParser.parse(relationObject, mapper, options) :
                this.dataParser.parse(relationObject, mapper);
        ObjectNode links = this.linksParser.parse(relationObject, mapper);
        if (!links.isEmpty()) {
            singleIncludeObject.set(LINKS, links);
        }
        includeArray.add(singleIncludeObject);
    }

    private static boolean rootElementExists(ArrayNode includeArray, Object relationObject) {
        Field[] allFields = Stream.concat(Arrays.stream(relationObject.getClass().getDeclaredFields()),
                Arrays.stream(relationObject.getClass().getSuperclass().getDeclaredFields())).toArray(Field[]::new);
        for (Field insideRelationField : allFields) {
            if (!insideRelationField.isAnnotationPresent(JsonApiId.class)) {
                continue;
            }

            String id = String.valueOf(GetterAndSetter.callGetter(relationObject, insideRelationField.getName()));
            String type = relationObject.getClass().getAnnotation(JsonApiObject.class).value();

            if (idInIncludedArray(includeArray, id, type)) {
                return true;
            }
        }
        return false;
    }

    private static boolean idInIncludedArray(ArrayNode includeArray, String id, String type) {
        for (int i = 0; i < includeArray.size(); i++) {
            ObjectNode rootObjectInclude = (ObjectNode) includeArray.get(i);
            if (rootObjectInclude.get(ID).asText().equals(id) && rootObjectInclude.get(TYPE).asText().equals(type)) {
                return true;
            }
        }
        return false;
    }

    private boolean isList(Object object) {
        return Collection.class.isAssignableFrom(object.getClass());
    }
}