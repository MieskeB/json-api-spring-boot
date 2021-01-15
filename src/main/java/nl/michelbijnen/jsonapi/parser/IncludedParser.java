package nl.michelbijnen.jsonapi.parser;

import nl.michelbijnen.jsonapi.annotation.*;
import nl.michelbijnen.jsonapi.helper.GetterAndSetter;
import org.json.JSONObject;

import java.lang.reflect.Field;

public class IncludedParser {
    JSONObject parseInclude(Object object) throws Exception {
        JSONObject include = new JSONObject();
        JSONObject attributes = new JSONObject();
        JSONObject links = new JSONObject();
        JSONObject relationship = new JSONObject();

        include.put("type", object.getClass().getAnnotation(JsonApiObject.class).value());

        for (Field relationField : object.getClass().getDeclaredFields()) {
            if (relationField.isAnnotationPresent(JsonApiId.class)) {
                include.put("id", new GetterAndSetter().callGetter(object, relationField.getName()));
            }
            if (relationField.isAnnotationPresent(JsonApiProperty.class)) {
                attributes.put(relationField.getName(), new GetterAndSetter().callGetter(object, relationField.getName()));
            }
            if (relationField.isAnnotationPresent(JsonApiRelation.class)) {
                JSONObject parsedRelationship = parseRelationship(object, relationField);
                parsedRelationship.remove("links");
                relationship.put(relationField.getName(), parsedRelationship);
            }
            if (relationField.isAnnotationPresent(JsonApiLink.class)) {
                if (relationField.getAnnotation(JsonApiLink.class).relation().equals(""))
                    links.put(relationField.getAnnotation(JsonApiLink.class).value().toString().toLowerCase(), new GetterAndSetter().callGetter(object, relationField.getName()));
            }
        }

        include.put("relationships", relationship);
        include.put("attributes", attributes);
        include.put("links", links);
        return include;
    }
}
