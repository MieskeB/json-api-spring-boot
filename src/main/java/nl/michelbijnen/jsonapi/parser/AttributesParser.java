package nl.michelbijnen.jsonapi.parser;

import nl.michelbijnen.jsonapi.annotation.JsonApiProperty;
import nl.michelbijnen.jsonapi.helper.GetterAndSetter;
import org.json.JSONObject;

import java.lang.reflect.Field;

class AttributesParser {

    /**
     * This method should return all @JsonApiProperty annotated properties in one object
     *
     * @param object the object to be converted
     * @return the json of only the attributes
     */
    JSONObject parse(Object object) {
        JSONObject jsonObject = new JSONObject();
        for (Field field : object.getClass().getDeclaredFields()) {
            if (field.isAnnotationPresent(JsonApiProperty.class)) {
                jsonObject.put(field.getName(), GetterAndSetter.callGetter(object, field.getName()));
            }
        }
        return jsonObject;
    }
}
