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
     * @param object   the object to be converted
     * @param maxDepth the depth of the models to return
     * @return The converted json
     */
    JSONObject parse(Object object, int maxDepth) {
        if (object == null) {
            JSONObject nullObject = new JSONObject();
            nullObject.put("data", new JSONObject());
            return nullObject;
        }
        if (this.isList(object)) {
            return this.convertObjectAsList(object, maxDepth);
        } else {
            return this.convertObjectAsObject(object, maxDepth);
        }
    }

    private JSONObject convertObjectAsList(Object object, int maxDepth) {
        JSONObject jsonObject = new JSONObject();

        if (((Collection<Object>) object).size() == 0) {
            jsonObject.put("data", new JSONArray());
            return jsonObject;
        }

        JSONObject parsedLinks = this.linksParser.parse(object);
        if (!parsedLinks.isEmpty()) {
            jsonObject.put("links", parsedLinks);
        }

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
        if (includedJsonArray.length() != 0)
            jsonObject.put("included", includedJsonArray);

        return jsonObject;
    }

    private JSONObject convertObjectAsObject(Object object, int maxDepth) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("data", this.dataParser.parse(object));

        JSONObject parsedLinks = this.linksParser.parse(object);
        if (!parsedLinks.isEmpty())
            jsonObject.put("links", parsedLinks);

        JSONArray parsedIncluded = this.includedParser.parse(object, maxDepth);
        if (parsedIncluded.length() != 0)
            jsonObject.put("included", parsedIncluded);

        return jsonObject;
    }

    private boolean isList(Object object) {
        return Collection.class.isAssignableFrom(object.getClass());
    }
}
