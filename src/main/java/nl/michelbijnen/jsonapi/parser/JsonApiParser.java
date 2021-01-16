package nl.michelbijnen.jsonapi.parser;

import org.json.JSONArray;
import org.json.JSONObject;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.Collection;

public class JsonApiParser {

    /**
     * This method should combine three things:
     * Data
     * Links
     * Included
     *
     * @param object the object to be converted
     * @return The converted json
     */
    JSONObject parse(Object object) {
        if (isList(object)) {
            return this.convertObjectAsList(object);
        } else {
            return this.convertObjectAsObject(object);
        }
    }

    private JSONObject convertObjectAsList(Object object) {
        JSONObject jsonObject = new JSONObject();

        if (((Collection<Object>) object).size() == 0) {
            jsonObject.put("data", new JSONObject());
            return jsonObject;
        }

        final Object linksObject = ((Collection<Object>) object).iterator().next();
        jsonObject.put("links", this.links(linksObject));

        JSONArray dataJsonArray = new JSONArray();
        for (Object loopObject : (Collection<Object>) object) {
            dataJsonArray.put(this.data(loopObject));
        }
        jsonObject.put("data", dataJsonArray);

        JSONArray includedJsonArray = new JSONArray();
        for (Object loopObject : (Collection<Object>) object) {
            for (Object includedObject : this.included(loopObject)) {
                includedJsonArray.put(includedObject);
            }
        }
        jsonObject.put("included", this.included(object));

        return jsonObject;
    }

    private JSONObject convertObjectAsObject(Object object) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("data", this.data(object));
        jsonObject.put("links", this.links(object));
        jsonObject.put("included", this.included(object));
        return jsonObject;
    }

    private JSONObject data(Object object) {
        throw new NotImplementedException();
    }

    private JSONObject links(Object object) {
        throw new NotImplementedException();
    }

    private JSONArray included(Object object) {
        throw new NotImplementedException();
    }

    private boolean isList(Object object) {
        return Collection.class.isAssignableFrom(object.getClass());
    }
}
