package nl.michelbijnen.jsonapi;

import nl.michelbijnen.jsonapi.annotation.*;
import nl.michelbijnen.jsonapi.helper.GetterAndSetter;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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
                if (field.getAnnotation(JsonApiLink.class).relation().equals("")) {
                    links.put(field.getAnnotation(JsonApiLink.class).value().toString().toLowerCase(), new GetterAndSetter().callGetter(this.object, field.getName()));
                }
            }
        }
        return links;
    }

    private JSONObject parseToData() throws Exception {
        JSONObject data = new JSONObject();
        JSONObject relationships = new JSONObject();
        JSONObject attributes = new JSONObject();
        List<JSONObject> included = new ArrayList<>();

        data.put("type", this.object.getClass().getAnnotation(JsonApiObject.class).value());
        for (Field field : this.object.getClass().getDeclaredFields()) {
            // Add the id
            if (field.isAnnotationPresent(JsonApiId.class)) {
                data.put("id", new GetterAndSetter().callGetter(this.object, field.getName()));
            }
            // Add the properties
            else if (field.isAnnotationPresent(JsonApiProperty.class)) {
                attributes.put(field.getName(), new GetterAndSetter().callGetter(this.object, field.getName()));
            }
            // Add the relations
            else if (field.isAnnotationPresent(JsonApiRelation.class)) {
                relationships.put(field.getAnnotation(JsonApiRelation.class).value(), JsonApiParser.parseRelationship(this.object, field));
                Object relationObject = new GetterAndSetter().callGetter(this.object, field.getName());
                if (Collection.class.isAssignableFrom(relationObject.getClass())) {
                    for (Object loopRelationObject : (Collection<Object>) relationObject) {
                        included.add(JsonApiParser.parseInclude(loopRelationObject));
                    }
                }
                else {
                    included.add(JsonApiParser.parseInclude(relationObject));
                }
            }
        }

        data.put("attributes", attributes);
        data.put("relationships", relationships);
        data.put("included", included);

        return data;
    }
}
