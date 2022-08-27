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
    private DataParser dataParser;
    private LinksParser linksParser;

    RelationshipParser() {
        this.dataParser = new DataParser();
        this.linksParser = new LinksParser();
    }

    /**
     * This class should add the name of the object and under it the following items:
     *
     * links
     * data
     *
     * @param object
     * @return
     */
    JSONObject parse(Object object) {
        JSONObject jsonObject = new JSONObject();

        for (Field field : object.getClass().getDeclaredFields()) {
            if (field.isAnnotationPresent(JsonApiRelation.class)) {

                Object relationObject = GetterAndSetter.callGetter(object, field.getName());

                if (relationObject == null) {
                    continue;
                }

                if (this.isList(relationObject)) {
                    jsonObject.put(field.getAnnotation(JsonApiRelation.class).value(), this.parseRelationshipAsList(object, field));
                } else {
                    jsonObject.put(field.getAnnotation(JsonApiRelation.class).value(), this.parseRelationshipAsObject(object, field));
                }
            }
        }
        return jsonObject;
    }

    private JSONObject parseRelationshipAsObject(Object object, Field field) {
        JSONObject relationship = new JSONObject();

        Object relationObject = GetterAndSetter.callGetter(object, field.getName());

        relationship.put("links", this.linksParser.parse(relationObject));
        relationship.put("data", this.dataParser.parse(relationObject, true));

        return relationship;
    }

    private JSONObject parseRelationshipAsList(Object object, Field field) {
        JSONObject relationship = new JSONObject();

        Collection<Object> relationObjectCollection = (Collection<Object>) GetterAndSetter.callGetter(object, field.getName());

        relationship.put("links", this.linksParser.parse(relationObjectCollection));

        JSONArray dataForEach = new JSONArray();
        for (Object relationObject : relationObjectCollection) {
            dataForEach.put(this.dataParser.parse(relationObject, true));
        }
        relationship.put("data", dataForEach);

        return relationship;
    }

    private boolean isList(Object object) {
        return Collection.class.isAssignableFrom(object.getClass());
    }
}
