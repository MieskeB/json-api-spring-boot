package nl.michelbijnen.jsonapi.parser;

import nl.michelbijnen.jsonapi.annotation.JsonApiId;
import nl.michelbijnen.jsonapi.annotation.JsonApiRelation;
import nl.michelbijnen.jsonapi.helper.GetterAndSetter;
import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.util.Collection;

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

        for (Field relationField : object.getClass().getDeclaredFields()) {
            if (!relationField.isAnnotationPresent(JsonApiRelation.class)) {
                continue;
            }

            Object childRelationObject = GetterAndSetter.callGetter(object, relationField.getName());
            if (childRelationObject == null) {
                continue;
            }

            if (this.rootElementExists(includeArray, childRelationObject)) {
                continue;
            }

            this.addObjectToIncludeArray(includeArray, childRelationObject);

            this.parse(childRelationObject, includeArray, maxDepth, currentDepth + 1);
        }

        return includeArray;
    }

    private void addObjectToIncludeArray(JSONArray includeArray, Object relationObject) {
        if (this.isList(relationObject)) {
            for (Object relationObjectSingle : (Collection<Object>) relationObject) {
                JSONObject singleIncludeObject = this.dataParser.parse(relationObjectSingle);
                singleIncludeObject.put("links", this.linksParser.parse(relationObjectSingle));
                includeArray.put(singleIncludeObject);
            }
        } else {
            JSONObject singleIncludeObject = this.dataParser.parse(relationObject);
            singleIncludeObject.put("links", this.linksParser.parse(relationObject));
            includeArray.put(singleIncludeObject);
        }
    }

    private boolean rootElementExists(JSONArray includeArray, Object relationObject) {
        for (Field insideRelationField : relationObject.getClass().getDeclaredFields()) {
            if (!insideRelationField.isAnnotationPresent(JsonApiId.class)) {
                continue;
            }

            String id = String.valueOf(GetterAndSetter.callGetter(relationObject, insideRelationField.getName()));

            if (idInIncludedArray(includeArray, id)) {
                return true;
            }
        }
        return false;
    }

    private boolean idInIncludedArray(JSONArray includeArray, String id) {
        for (int i = 0; i < includeArray.length(); i++) {
            JSONObject rootObjectInclude = includeArray.getJSONObject(i);
            if (rootObjectInclude.getString("id").equals(id)) {
                return true;
            }
        }
        return false;
    }

    private boolean isList(Object object)    {
        return Collection.class.isAssignableFrom(object.getClass());
    }
}
