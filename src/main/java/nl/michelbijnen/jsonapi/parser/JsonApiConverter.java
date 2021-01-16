package nl.michelbijnen.jsonapi.parser;

import org.json.JSONObject;

public class JsonApiConverter {

    private JsonApiConverter() {
    }

    public static String convert(Object object) throws Exception {
        JsonApiParser jsonApiParser = new JsonApiParser();
        JSONObject result = jsonApiParser.parse(object);
        return result.toString();
    }
}
