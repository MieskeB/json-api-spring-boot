package nl.michelbijnen.jsonapi;

import nl.michelbijnen.jsonapi.annotation.JsonApiId;
import nl.michelbijnen.jsonapi.annotation.JsonApiLink;
import nl.michelbijnen.jsonapi.annotation.JsonApiObject;
import org.json.JSONObject;

import java.lang.reflect.Field;

public class JsonApiConverter {
    private final Object object;

    public JsonApiConverter(Object object) throws Exception {
        if (!object.getClass().isAnnotationPresent(JsonApiObject.class)) {
            throw new Exception("The reference @JsonApiObject isn't present in specified class");
        }
        if (!object.getClass().isAnnotationPresent(JsonApiId.class)) {
            throw new Exception("The reference @JsonApiId isn't present in specified class");
        }

        this.object = object;
    }

    public String convert() throws Exception {
        JSONObject finalJsonObject = new JSONObject();
        finalJsonObject.put("links", this.parseToLinks());
        finalJsonObject.put("data", this.parseToData());
        return finalJsonObject.toString();
    }

    private JSONObject parseToLinks() throws Exception {
        JSONObject links = new JSONObject();
        for (Field field : this.object.getClass().getDeclaredFields()) {
            if (field.isAnnotationPresent(JsonApiLink.class)) {
                links.put(field.getAnnotation(JsonApiLink.class).linkType().toString(), field.get(this.object));
            }
        }
        return links;
    }

    private JSONObject parseToData() {
        return new JSONObject();
    }
}
