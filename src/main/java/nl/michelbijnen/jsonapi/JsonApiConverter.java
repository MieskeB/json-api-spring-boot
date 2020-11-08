package nl.michelbijnen.jsonapi;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Collection;

public class JsonApiConverter {
    private final Object object;

    public JsonApiConverter(Object object) throws Exception {
        this.object = object;
    }

    public String convert() throws Exception {
        JSONObject finalJsonObject = new JSONObject();
        if (Collection.class.isAssignableFrom(this.object.getClass())) {
            if (((Collection<Object>) this.object).size() != 0) {
                final Object linksObject = ((Collection<Object>) this.object).iterator().next();
                finalJsonObject.put("links", JsonApiParser.parseToLinks(linksObject));
            }

            JSONArray data = new JSONArray();
            for (Object loopObject : (Collection<Object>) this.object) {
                data.put(JsonApiParser.parseToData(loopObject));
            }
            finalJsonObject.put("data", data);
        } else {
            finalJsonObject.put("links", JsonApiParser.parseToLinks(this.object));
            finalJsonObject.put("data", JsonApiParser.parseToData(this.object));
        }
        return finalJsonObject.toString();
    }
}
