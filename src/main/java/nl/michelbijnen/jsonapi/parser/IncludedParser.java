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

class IncludedParser {
    private final DataParser dataParser;
    private final LinksParser linksParser;

    IncludedParser() {
        this.dataParser = new DataParser();
        this.linksParser = new LinksParser();
    }

    ArrayNode parse(Object object, int maxDepth, ObjectMapper mapper) {
        return this.parse(object, mapper.createArrayNode(), maxDepth, 0, mapper);
    }

    ArrayNode parse(Object object, ArrayNode includeArray, int maxDepth, int currentDepth, ObjectMapper mapper) {
        if (currentDepth == maxDepth) {
            return includeArray;
        }

        if (!(object instanceof Iterable)) {
            parseObject(object, includeArray, maxDepth, currentDepth, mapper);

            return includeArray;
        }

        Iterable<Object> collection = (Iterable<Object>) object;
        for (Object item : collection) {
            parseObject(item, includeArray, maxDepth, currentDepth, mapper);
        }
        return includeArray;

    }

    ArrayNode parse(Object object, int maxDepth, ObjectMapper mapper, JsonApiOptions options) {
        return this.parse(object, mapper.createArrayNode(), maxDepth, 0, mapper, options);
    }

    ArrayNode parse(Object object, ArrayNode includeArray, int maxDepth, int currentDepth, ObjectMapper mapper, JsonApiOptions options) {
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

    private void parseObject(Object object, ArrayNode includeArray, int maxDepth, int currentDepth, ObjectMapper mapper) {
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
                    continue; // Skip absent Optional
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
                    this.addObjectToIncludeArray(includeArray, childRelationObjectAsItem, mapper);
                    this.parse(childRelationObjectAsItem, includeArray, maxDepth, currentDepth + 1, mapper);
                }
            } else {
                this.addObjectToIncludeArray(includeArray, childRelationObject, mapper);
                this.parse(childRelationObject, includeArray, maxDepth, currentDepth + 1, mapper);
            }
        }
    }

    // options-aware parseObject
    private void parseObject(Object object, ArrayNode includeArray, int maxDepth, int currentDepth, ObjectMapper mapper, JsonApiOptions options) {
        Set<String> allowTop = (options == null) ? null : options.topLevelIncludeRelations();
        boolean hasFilter = options != null && allowTop != null && !allowTop.isEmpty();

        for (Field relationField : object.getClass().getDeclaredFields()) {
            if (!relationField.isAnnotationPresent(JsonApiRelation.class)) {
                continue;
            }
            String relName = relationField.getAnnotation(JsonApiRelation.class).value();

            if (hasFilter && currentDepth == 0 && !allowTop.contains(relName)) {
                continue; // restrict to requested top-level includes
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
                    this.addObjectToIncludeArray(includeArray, childRelationObjectAsItem, mapper);
                    this.parse(childRelationObjectAsItem, includeArray, maxDepth, currentDepth + 1, mapper, options);
                }
            } else {
                this.addObjectToIncludeArray(includeArray, childRelationObject, mapper);
                this.parse(childRelationObject, includeArray, maxDepth, currentDepth + 1, mapper, options);
            }
        }
    }

    private boolean addObjectToIncludeArray(ArrayNode includeArray, Object relationObject, ObjectMapper mapper) {
        if (this.rootElementExists(includeArray, relationObject)) {
            return false;
        }

        ObjectNode singleIncludeObject = this.dataParser.parse(relationObject, mapper);
        ObjectNode links = this.linksParser.parse(relationObject, mapper);
        if (!links.isEmpty()) {
            singleIncludeObject.set("links", links);
        }
        includeArray.add(singleIncludeObject);
        return true;
    }

    private boolean rootElementExists(ArrayNode includeArray, Object relationObject) {
        Field[] allFields = Stream.concat(Arrays.stream(relationObject.getClass().getDeclaredFields()), Arrays.stream(relationObject.getClass().getSuperclass().getDeclaredFields())).toArray(Field[]::new);
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

    private boolean idInIncludedArray(ArrayNode includeArray, String id, String type) {
        for (int i = 0; i < includeArray.size(); i++) {
            ObjectNode rootObjectInclude = (ObjectNode) includeArray.get(i);
            if (rootObjectInclude.get("id").asText().equals(id) && rootObjectInclude.get("type").asText().equals(type)) {
                return true;
            }
        }
        return false;
    }

    private boolean isList(Object object) {
        return Collection.class.isAssignableFrom(object.getClass());
    }
}