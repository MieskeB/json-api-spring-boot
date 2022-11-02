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
     * <p>
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

                JSONObject relation;
                if (this.isList(relationObject)) {
                    relation = this.parseRelationshipAsList(object, field);
                } else {
                    relation = this.parseRelationshipAsObject(object, field);
                }
                if (!relation.isEmpty())
                    jsonObject.put(field.getAnnotation(JsonApiRelation.class).value(), relation);
            }
        }
        return jsonObject;
    }

    private JSONObject parseRelationshipAsObject(Object object, Field field) {
        JSONObject relationship = new JSONObject();

        Object relationObject = GetterAndSetter.callGetter(object, field.getName());

        JSONObject linksParser = this.linksParser.parse(relationObject);
        if (!linksParser.isEmpty())
            relationship.put("links", linksParser);

        JSONObject dataParser = this.dataParser.parse(relationObject, true);
        if (!dataParser.isEmpty())
            relationship.put("data", dataParser);

        return relationship;
    }

    private JSONObject parseRelationshipAsList(Object object, Field field) {
        JSONObject relationship = new JSONObject();

        Collection<Object> relationObjectCollection = (Collection<Object>) GetterAndSetter.callGetter(object, field.getName());

        JSONObject linksParser = this.linksParser.parse(relationObjectCollection);
        if (!linksParser.isEmpty())
            relationship.put("links", linksParser);

        JSONArray dataForEach = new JSONArray();
        for (Object relationObject : relationObjectCollection) {
            dataForEach.put(this.dataParser.parse(relationObject, true));
        }
        if (!dataForEach.isEmpty())
            relationship.put("data", dataForEach);

        return relationship;
    }

    private boolean isList(Object object) {
        return Collection.class.isAssignableFrom(object.getClass());
    }
}
