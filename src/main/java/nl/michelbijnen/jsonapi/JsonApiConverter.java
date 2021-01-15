package nl.michelbijnen.jsonapi;

import nl.michelbijnen.jsonapi.parser.JsonApiParser;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Collection;

public class JsonApiConverter {

    private JsonApiConverter() {
    }

    public static String convert(Object object) throws Exception {
        JsonApiParser jsonApiParser = new JsonApiParser();
        JSONObject finalJsonObject = new JSONObject();
        if (Collection.class.isAssignableFrom(object.getClass())) {
            if (((Collection<Object>) object).size() != 0) {
                final Object linksObject = ((Collection<Object>) object).iterator().next();
                finalJsonObject.put("links", jsonApiParser.parseToLinks(linksObject));
            }

            JSONArray data = new JSONArray();
            for (Object loopObject : (Collection<Object>) object) {
                data.put(JsonApiParser.parseToData(loopObject));
            }
            finalJsonObject.put("data", data);
        } else {
            finalJsonObject.put("links", JsonApiParser.parseToLinks(object));
            finalJsonObject.put("data", JsonApiParser.parseToData(object));
        }
        return finalJsonObject.toString();
    }
}
