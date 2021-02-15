package nl.michelbijnen.jsonapi.parser;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Collection;

class JsonApiParser {
    private LinksParser linksParser;
    private DataParser dataParser;
    private IncludedParser includedParser;

    JsonApiParser() {
        this.linksParser = new LinksParser();
        this.dataParser = new DataParser();
        this.includedParser = new IncludedParser();
    }

    /**
     * This method should combine three things:
     * Data
     * Links
     * Included
     *
     * @param object the object to be converted
     * @param maxDepth the depth of the models to return
     * @return The converted json
     */
    JSONObject parse(Object object, int maxDepth) {
        if (this.isList(object)) {
            return this.convertObjectAsList(object, maxDepth);
        } else {
            return this.convertObjectAsObject(object, maxDepth);
        }
    }

    private JSONObject convertObjectAsList(Object object, int maxDepth) {
        JSONObject jsonObject = new JSONObject();

        if (((Collection<Object>) object).size() == 0) {
            jsonObject.put("data", new JSONObject());
            return jsonObject;
        }

        final Object linksObject = ((Collection<Object>) object).iterator().next();
        jsonObject.put("links", this.linksParser.parse(linksObject));

        JSONArray dataJsonArray = new JSONArray();
        for (Object loopObject : (Collection<Object>) object) {
            dataJsonArray.put(this.dataParser.parse(loopObject));
        }
        jsonObject.put("data", dataJsonArray);

        JSONArray includedJsonArray = new JSONArray();
        for (Object loopObject : (Collection<Object>) object) {
            for (Object includedObject : this.includedParser.parse(loopObject, maxDepth)) {
                includedJsonArray.put(includedObject);
            }
        }
        jsonObject.put("included", includedJsonArray);

        return jsonObject;
    }

    private JSONObject convertObjectAsObject(Object object, int maxDepth) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("data", this.dataParser.parse(object));
        jsonObject.put("links", this.linksParser.parse(object));
        jsonObject.put("included", this.includedParser.parse(object, maxDepth));
        return jsonObject;
    }

    private boolean isList(Object object) {
        return Collection.class.isAssignableFrom(object.getClass());
    }
}
