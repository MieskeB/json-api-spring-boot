package nl.michelbijnen.jsonapi.parser;

import nl.michelbijnen.jsonapi.annotation.JsonApiId;
import nl.michelbijnen.jsonapi.annotation.JsonApiObject;
import nl.michelbijnen.jsonapi.annotation.JsonApiProperty;
import nl.michelbijnen.jsonapi.annotation.JsonApiRelation;
import nl.michelbijnen.jsonapi.exception.JsonApiException;
import nl.michelbijnen.jsonapi.helper.GetterAndSetter;
import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.util.Collection;

public class DataParser {
    private Object object;

    public JSONObject parse(Object object) {
        this.object = object;
        return this.parseToData();
    }

    private JSONObject parseToData() {
        JSONObject data = new JSONObject();
        JSONObject relationships = new JSONObject();
        JSONObject attributes = new JSONObject();
        JSONArray included = new JSONArray();

        if (object.getClass().getAnnotation(JsonApiObject.class) != null) {
            data.put("type", object.getClass().getAnnotation(JsonApiObject.class).value());
        } else {
            throw new JsonApiException("@JsonApiObject(\"<classname>\") missing");
        }
        for (Field field : object.getClass().getDeclaredFields()) {
            // Add the id
            if (field.isAnnotationPresent(JsonApiId.class)) {
                data.put("id", new GetterAndSetter().callGetter(object, field.getName()));
            }
            // Add the properties
            else if (field.isAnnotationPresent(JsonApiProperty.class)) {
                attributes.put(field.getName(), new GetterAndSetter().callGetter(object, field.getName()));
            }
            // Add the relations
            else if (field.isAnnotationPresent(JsonApiRelation.class)) {
                relationships.put(field.getAnnotation(JsonApiRelation.class).value(), parseRelationship(object, field));
                Object relationObject = new GetterAndSetter().callGetter(object, field.getName());
                if (relationObject != null) {
                    if (Collection.class.isAssignableFrom(relationObject.getClass())) {
                        for (Object loopRelationObject : (Collection<Object>) relationObject) {
                            included.put(parseInclude(loopRelationObject));
                        }
                    } else {
                        included.put(parseInclude(relationObject));
                    }
                }
            }
        }

        data.put("attributes", attributes);
        data.put("relationships", relationships);
        data.put("included", included);

        return data;
    }
}
