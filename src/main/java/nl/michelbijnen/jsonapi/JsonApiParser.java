package nl.michelbijnen.jsonapi;

import nl.michelbijnen.jsonapi.annotation.*;
import nl.michelbijnen.jsonapi.helper.GetterAndSetter;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

class JsonApiParser {
    static JSONObject parseToLinks(Object object) throws Exception {
        JSONObject links = new JSONObject();
        for (Field field : object.getClass().getDeclaredFields()) {
            if (field.isAnnotationPresent(JsonApiLink.class)) {
                if (field.getAnnotation(JsonApiLink.class).relation().equals("")) {
                    links.put(field.getAnnotation(JsonApiLink.class).value().toString().toLowerCase(), new GetterAndSetter().callGetter(object, field.getName()));
                }
            }
        }
        return links;
    }

    static JSONObject parseToData(Object object) throws Exception {
        JSONObject data = new JSONObject();
        JSONObject relationships = new JSONObject();
        JSONObject attributes = new JSONObject();
        List<JSONObject> included = new ArrayList<>();

        data.put("type", object.getClass().getAnnotation(JsonApiObject.class).value());
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
                if (Collection.class.isAssignableFrom(relationObject.getClass())) {
                    for (Object loopRelationObject : (Collection<Object>) relationObject) {
                        included.add(parseInclude(loopRelationObject));
                    }
                } else {
                    included.add(parseInclude(relationObject));
                }
            }
        }

        data.put("attributes", attributes);
        data.put("relationships", relationships);
        data.put("included", included);

        return data;
    }

    static JSONObject parseRelationship(Object object, Field field) throws Exception {
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
            List<JSONObject> dataForEach = new ArrayList<>();
            for (Object relationObject : (Collection<Object>) new GetterAndSetter().callGetter(object, field.getName())) {

                JSONObject dataObjectForEach = new JSONObject();
                dataObjectForEach.put("type", relationObject.getClass().getAnnotation(JsonApiObject.class).value());
                for (Field relationField : relationObject.getClass().getDeclaredFields()) {
                    if (relationField.isAnnotationPresent(JsonApiId.class)) {
                        dataObjectForEach.put("id", new GetterAndSetter().callGetter(relationObject, relationField.getName()));
                        break;
                    }
                }
                dataForEach.add(dataObjectForEach);
            }
            relationship.put("data", dataForEach);
        }
        // If it's just one data object
        else {
            Object relationObject = new GetterAndSetter().callGetter(object, field.getName());

            JSONObject data = new JSONObject();
            data.put("type", relationObject.getClass().getAnnotation(JsonApiObject.class).value());
            for (Field relationField : relationObject.getClass().getDeclaredFields()) {
                if (relationField.isAnnotationPresent(JsonApiId.class)) {
                    data.put("id", new GetterAndSetter().callGetter(relationObject, relationField.getName()));
                    break;
                }
            }
            relationship.put("data", data);
        }

        relationship.put("links", links);

        return relationship;
    }

    static JSONObject parseInclude(Object object) throws Exception {
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
                links.put(relationField.getAnnotation(JsonApiLink.class).value().toString().toLowerCase(), new GetterAndSetter().callGetter(object, relationField.getName()));
            }
        }

        include.put("relationships", relationship);
        include.put("attributes", attributes);
        include.put("links", links);
        return include;
    }
}
