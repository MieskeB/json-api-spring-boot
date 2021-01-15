package nl.michelbijnen.jsonapi;

import nl.michelbijnen.jsonapi.parser.DataParser;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Collection;

public class JsonApiConverter {

    private JsonApiConverter() {
    }

    public static String convert(Object object) throws Exception {
        DataParser dataParser = new DataParser();
        JSONObject finalJsonObject = new JSONObject();
        if (Collection.class.isAssignableFrom(object.getClass())) {
            if (((Collection<Object>) object).size() != 0) {
                final Object linksObject = ((Collection<Object>) object).iterator().next();
                finalJsonObject.put("links", dataParser.parseToLinks(linksObject));
            }

            JSONArray data = new JSONArray();
            for (Object loopObject : (Collection<Object>) object) {
                data.put(DataParser.parseToData(loopObject));
            }
            finalJsonObject.put("data", data);
        } else {
            finalJsonObject.put("links", DataParser.parseToLinks(object));
            finalJsonObject.put("data", DataParser.parseToData(object));
        }
        return finalJsonObject.toString();
    }
}
