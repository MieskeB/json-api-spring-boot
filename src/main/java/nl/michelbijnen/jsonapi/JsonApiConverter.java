package nl.michelbijnen.jsonapi;

import nl.michelbijnen.jsonapi.annotation.JsonApiId;
import nl.michelbijnen.jsonapi.annotation.JsonApiObject;
import org.json.JSONObject;

public class JsonApiConverter {
    private Object object;

    public JsonApiConverter(Object object) throws Exception {
        if (!object.getClass().isAnnotationPresent(JsonApiObject.class)) {
            throw new Exception("The reference @JsonApiObject isn't present in specified class");
        }
        if (!object.getClass().isAnnotationPresent(JsonApiId.class)) {
            throw new Exception("The reference @JsonApiId isn't present in specified class");
        }
    }

    public String convert() throws Exception {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("id", object.getClass().getAnnotation(JsonApiId.class).toString());
        return jsonObject.toString();
    }
}
