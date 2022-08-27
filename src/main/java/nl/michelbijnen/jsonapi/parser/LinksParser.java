package nl.michelbijnen.jsonapi.parser;

import nl.michelbijnen.jsonapi.annotation.JsonApiLink;
import nl.michelbijnen.jsonapi.enumeration.JsonApiLinkType;
import nl.michelbijnen.jsonapi.helper.GetterAndSetter;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Stream;

class LinksParser {
    JSONObject parse(Object object) {
        JSONObject links = new JSONObject();
        boolean asList = this.isList(object);
        if (asList) {
            if (((Collection<Object>)object).size() == 0) {
                return links;
            }
            object = ((Collection<Object>) object).iterator().next();
        }
        Field[] allFields = Stream.concat(Arrays.stream(object.getClass().getDeclaredFields()), Arrays.stream(object.getClass().getSuperclass().getDeclaredFields())).toArray(Field[]::new);
        for (Field field : allFields) {
            if (field.isAnnotationPresent(JsonApiLink.class)) {
                if (field.getAnnotation(JsonApiLink.class).value().equals(JsonApiLinkType.ALL_SELF)) {
                    if (asList) {
                        links.put(JsonApiLinkType.SELF.toString().toLowerCase(), GetterAndSetter.callGetter(object, field.getName()));
                    }
                }
                else if (field.getAnnotation(JsonApiLink.class).value().equals(JsonApiLinkType.SELF)) {
                    if (!asList) {
                        links.put(field.getAnnotation(JsonApiLink.class).value().toString().toLowerCase(), GetterAndSetter.callGetter(object, field.getName()));
                    }
                }
                else {
                    links.put(field.getAnnotation(JsonApiLink.class).value().toString().toLowerCase(), GetterAndSetter.callGetter(object, field.getName()));
                }
            }
        }
        return links;
    }

    private boolean isList(Object object) {
        return Collection.class.isAssignableFrom(object.getClass());
    }
}
