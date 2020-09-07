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
        JSONObject included = new JSONObject();

        data.put("type", this.object.getClass().getAnnotation(JsonApiObject.class).value());
        for (Field field : this.object.getClass().getDeclaredFields()) {
            // Add the id
            if (field.isAnnotationPresent(JsonApiId.class)) {
                data.put("id", new GetterAndSetter().callGetter(this.object, field.getName()));
            }
            // Add the properties
            else if (field.isAnnotationPresent(JsonApiProperty.class)) {
                data.put(field.getName(), new GetterAndSetter().callGetter(this.object, field.getName()));
            }
            // Add the relations
            else if (field.isAnnotationPresent(JsonApiRelation.class)) {
                relationships.put(field.getAnnotation(JsonApiRelation.class).value(), this.parseRelationship(field));
            }
        }

        data.put("relationships", relationships);

        return data;
    }

    private JSONObject parseRelationship(Field field) throws Exception {
        JSONObject relationship = new JSONObject();
        JSONObject links = new JSONObject();

        // Add the links
        for (Field relationField : this.object.getClass().getDeclaredFields()) {
            if (relationField.isAnnotationPresent(JsonApiLink.class)) {
                if (relationField.getAnnotation(JsonApiLink.class).relation().equals(field.getAnnotation(JsonApiRelation.class).value())) {
                    links.put(relationField.getAnnotation(JsonApiLink.class).value().toString().toLowerCase(), new GetterAndSetter().callGetter(this.object, relationField.getName()));
                }
            }
        }


        // Check if it is a list
        if (Collection.class.isAssignableFrom(field.getType())) {
            List<JSONObject> dataForEach = new ArrayList<>();
            for (Object relationObject : (Collection<Object>) new GetterAndSetter().callGetter(this.object, field.getName())) {

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
            Object relationObject = new GetterAndSetter().callGetter(this.object, field.getName());

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
}
