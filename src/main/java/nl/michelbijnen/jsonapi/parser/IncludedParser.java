package nl.michelbijnen.jsonapi.parser;

import nl.michelbijnen.jsonapi.annotation.JsonApiId;
import nl.michelbijnen.jsonapi.annotation.JsonApiObject;
import nl.michelbijnen.jsonapi.annotation.JsonApiRelation;
import nl.michelbijnen.jsonapi.helper.GetterAndSetter;
import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Stream;

class IncludedParser {
    private DataParser dataParser;
    private LinksParser linksParser;

    IncludedParser() {
        this.dataParser = new DataParser();
        this.linksParser = new LinksParser();
    }

    JSONArray parse(Object object, int maxDepth) {
        return this.parse(object, new JSONArray(), maxDepth, 0);
    }

    JSONArray parse(Object object, JSONArray includeArray, int maxDepth, int currentDepth) {
        if (currentDepth == maxDepth) {
            return includeArray;
        }

        if (!(object instanceof Iterable)) {
            parseObject(object, includeArray, maxDepth, currentDepth);

            return includeArray;
        }

        Iterable<Object> collection = (Iterable<Object>) object;
        for (Object item : collection) {
            parseObject(item, includeArray, maxDepth, currentDepth);
        }
        return includeArray;

    }

    private void parseObject(Object object, JSONArray includeArray, int maxDepth, int currentDepth) {
        for (Field relationField : object.getClass().getDeclaredFields()) {
            if (!relationField.isAnnotationPresent(JsonApiRelation.class)) {
                continue;
            }

            Object childRelationObject = GetterAndSetter.callGetter(object, relationField.getName());
            if (childRelationObject == null) {
                continue;
            }

            if (this.isList(childRelationObject)) {
                if (((Collection<Object>) childRelationObject).isEmpty()) {
                    continue;
                }
                for (Object childRelationObjectAsItem : (Collection<Object>) childRelationObject) {
                    this.addObjectToIncludeArray(includeArray, childRelationObjectAsItem);
                    this.parse(childRelationObject, includeArray, maxDepth, currentDepth + 1);
                }
            } else {
                this.addObjectToIncludeArray(includeArray, childRelationObject);
                this.parse(childRelationObject, includeArray, maxDepth, currentDepth + 1);
            }
        }
    }

    private boolean addObjectToIncludeArray(JSONArray includeArray, Object relationObject) {
        if (this.rootElementExists(includeArray, relationObject)) {
            return false;
        }

        JSONObject singleIncludeObject = this.dataParser.parse(relationObject);
        singleIncludeObject.put("links", this.linksParser.parse(relationObject));
        includeArray.put(singleIncludeObject);
        return true;
    }

    private boolean rootElementExists(JSONArray includeArray, Object relationObject) {
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

    private boolean idInIncludedArray(JSONArray includeArray, String id, String type) {
        for (int i = 0; i < includeArray.length(); i++) {
            JSONObject rootObjectInclude = includeArray.getJSONObject(i);
            if (rootObjectInclude.getString("id").equals(id) && rootObjectInclude.getString("type").equals(type)) {
                return true;
            }
        }
        return false;
    }

    private boolean isList(Object object) {
        return Collection.class.isAssignableFrom(object.getClass());
    }
}
