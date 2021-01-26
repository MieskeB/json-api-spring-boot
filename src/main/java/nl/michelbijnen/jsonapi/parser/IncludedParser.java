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

    JSONArray parse(Object object, JSONArray includeRoot, int maxDepth, int currentDepth) {
        if (currentDepth > maxDepth && maxDepth != 0) {
            return includeRoot;
        }

        for (Field relationField : object.getClass().getDeclaredFields()) {
            if (!relationField.isAnnotationPresent(JsonApiRelation.class)) {
                continue;
            }

            Object childRelationObject = GetterAndSetter.callGetter(object, relationField.getName());
            if (childRelationObject == null) {
                continue;
            }

            boolean rootElementExists = this.rootElementExists(includeRoot, childRelationObject);

            if (rootElementExists) {
                return includeRoot;
            }

            this.addRelationWithChildrenToIncludeRoot(includeRoot, maxDepth, currentDepth, childRelationObject);
        }

        return includeRoot;
    }

    private void addRelationWithChildrenToIncludeRoot(JSONArray includeRoot, int maxDepth, int currentDepth, Object relationObject) {
        this.addRelationObjectToIncludeRoot(includeRoot, maxDepth, currentDepth, relationObject);
        int newDepth = maxDepth != 0 ? currentDepth + 1 : 0;
        this.parse(relationObject, includeRoot, maxDepth, newDepth);
    }

    private void addRelationObjectToIncludeRoot(JSONArray includeRoot, int maxDepth, int currentDepth, Object relationObject) {
        if (this.isList(relationObject)) {
            for (Object relationObjectSingle : (Collection<Object>) relationObject) {
                JSONObject singleIncludeObject = this.dataParser.parse(relationObjectSingle);
                singleIncludeObject.put("links", this.linksParser.parse(relationObjectSingle));
                includeRoot.put(singleIncludeObject);
                int newDepth = maxDepth != 0 ? currentDepth + 1 : 0;
                this.parse(relationObjectSingle, includeRoot, maxDepth, newDepth);
            }
        } else {
            JSONObject singleIncludeObject = this.dataParser.parse(relationObject);
            singleIncludeObject.put("links", this.linksParser.parse(relationObject));
            includeRoot.put(singleIncludeObject);
        }
    }

    private boolean rootElementExists(JSONArray includeRoot, Object relationObject) {
        for (Field insideRelationField : relationObject.getClass().getDeclaredFields()) {
            if (!insideRelationField.isAnnotationPresent(JsonApiId.class)) {
                continue;
            }

            String id = String.valueOf(GetterAndSetter.callGetter(relationObject, insideRelationField.getName()));
            boolean rootElementExists = idInRoot(includeRoot, id);

            if (rootElementExists) {
                return true;
            }
        }
        return false;
    }

    private boolean idInRoot(JSONArray includeRoot, String id) {
        for (int i = 0; i < includeRoot.length(); i++) {
            JSONObject rootObjectInclude = includeRoot.getJSONObject(i);
            if (rootObjectInclude.getString("id").equals(id)) {
                return true;
            }
        }
        return false;
    }

    private boolean isList(Object object) {
        return Collection.class.isAssignableFrom(object.getClass());
    }
}
