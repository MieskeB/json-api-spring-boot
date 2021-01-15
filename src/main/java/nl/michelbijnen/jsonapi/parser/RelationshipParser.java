package nl.michelbijnen.jsonapi.parser;

import nl.michelbijnen.jsonapi.annotation.JsonApiId;
import nl.michelbijnen.jsonapi.annotation.JsonApiLink;
import nl.michelbijnen.jsonapi.annotation.JsonApiObject;
import nl.michelbijnen.jsonapi.annotation.JsonApiRelation;
import nl.michelbijnen.jsonapi.helper.GetterAndSetter;
import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.util.Collection;

class RelationshipParser {
    JSONObject parseRelationship(Object object, Field field) throws Exception {
        JSONObject relationship = new JSONObject();
        JSONObject links = new JSONObject();

        // Add the links
        for (Field relationField : object.getClass().getDeclaredFields()) {
            if (relationField.isAnnotationPresent(JsonApiLink.class)) {
                if (relationField.getAnnotation(JsonApiLink.class).relation().equals(field.getAnnotation(JsonApiRelation.class).value())) {
                    links.put(relationField.getAnnotation(JsonApiLink.class).value().toString().toLowerCase(), new GetterAndSetter().callGetter(object, relationField.getName()));
                }
            }
        }


        // Check if it is a list
        if (Collection.class.isAssignableFrom(field.getType())) {
            JSONArray dataForEach = new JSONArray();
            for (Object relationObject : (Collection<Object>) new GetterAndSetter().callGetter(object, field.getName())) {

                JSONObject dataObjectForEach = new JSONObject();
                dataObjectForEach.put("type", relationObject.getClass().getAnnotation(JsonApiObject.class).value());
                for (Field relationField : relationObject.getClass().getDeclaredFields()) {
                    if (relationField.isAnnotationPresent(JsonApiId.class)) {
                        dataObjectForEach.put("id", new GetterAndSetter().callGetter(relationObject, relationField.getName()));
                        break;
                    }
                }
                dataForEach.put(dataObjectForEach);
            }
            relationship.put("data", dataForEach);
        }
        // If it's just one data object
        else {
            Object relationObject = new GetterAndSetter().callGetter(object, field.getName());

            JSONObject data = new JSONObject();
            if (relationObject != null) {
                data.put("type", relationObject.getClass().getAnnotation(JsonApiObject.class).value());
                for (Field relationField : relationObject.getClass().getDeclaredFields()) {
                    if (relationField.isAnnotationPresent(JsonApiId.class)) {
                        data.put("id", new GetterAndSetter().callGetter(relationObject, relationField.getName()));
                        break;
                    }
                }
            }
            relationship.put("data", data);
        }

        relationship.put("links", links);

        return relationship;
    }
}
