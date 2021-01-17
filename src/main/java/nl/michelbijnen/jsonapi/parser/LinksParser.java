package nl.michelbijnen.jsonapi.parser;

import nl.michelbijnen.jsonapi.annotation.JsonApiLink;
import nl.michelbijnen.jsonapi.helper.GetterAndSetter;
import org.json.JSONObject;

import java.lang.reflect.Field;

class LinksParser {
    JSONObject parse(Object object) {
        return this.parse(object, "");
    }

    JSONObject parse(Object object, String relation) {
        JSONObject links = new JSONObject();
        for (Field field : object.getClass().getDeclaredFields()) {
            if (field.isAnnotationPresent(JsonApiLink.class)) {
                if (field.getAnnotation(JsonApiLink.class).relation().equals(relation)) {
                    links.put(field.getAnnotation(JsonApiLink.class).value().toString().toLowerCase(), GetterAndSetter.callGetter(object, field.getName()));
                }
            }
        }
        return links;
    }
}
