package nl.michelbijnen.jsonapi;

import nl.michelbijnen.jsonapi.annotation.JsonApiId;
import nl.michelbijnen.jsonapi.annotation.JsonApiLink;
import nl.michelbijnen.jsonapi.annotation.JsonApiObject;
import nl.michelbijnen.jsonapi.annotation.JsonApiProperty;
import nl.michelbijnen.jsonapi.helper.GetterAndSetter;
import org.json.JSONObject;

import java.lang.reflect.Field;

public class JsonApiConverter {
    private final Object object;

    public JsonApiConverter(Object object) throws Exception {
        if (!object.getClass().isAnnotationPresent(JsonApiObject.class)) {
            throw new Exception("The reference @JsonApiObject isn't present in specified class");
        }
//        TODO fix following lines
//        if (!object.getClass().isAnnotationPresent(JsonApiId.class)) {
//            throw new Exception("The reference @JsonApiId isn't present in specified class");
//        }

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
                links.put(field.getAnnotation(JsonApiLink.class).value().toString().toLowerCase(), new GetterAndSetter().callGetter(this.object, field.getName()));
            }
        }
        return links;
    }

    private JSONObject parseToData() throws Exception {
        JSONObject data = new JSONObject();

        data.put("type", this.object.getClass().getAnnotation(JsonApiObject.class).value());
        for (Field field : this.object.getClass().getDeclaredFields()) {
            if (field.isAnnotationPresent(JsonApiId.class)) {
                data.put("id", new GetterAndSetter().callGetter(this.object, field.getName()));
            } else if (field.isAnnotationPresent(JsonApiProperty.class)) {
                data.put(field.getName(), new GetterAndSetter().callGetter(this.object, field.getName()));
            }
        }

        return data;
    }
}
