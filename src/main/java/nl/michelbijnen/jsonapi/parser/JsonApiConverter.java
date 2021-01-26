package nl.michelbijnen.jsonapi.parser;

import org.json.JSONObject;

public class JsonApiConverter {

    private JsonApiConverter() {
    }

    public static String convert(Object object) {
        int depth = Integer.parseInt(System.getProperty("jsonapi.depth", "1"));
        return convert(object, depth);
    }

    public static String convert(Object object, int depth){
        JsonApiParser jsonApiParser = new JsonApiParser();
        JSONObject result = jsonApiParser.parse(object, depth);
        return result.toString();
    }
}
