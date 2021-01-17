package nl.michelbijnen.jsonapi.parser;

import nl.michelbijnen.jsonapi.annotation.*;
import nl.michelbijnen.jsonapi.helper.GetterAndSetter;
import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.util.Collection;

class IncludedParser {
    private DataParser dataParser;

    IncludedParser() {
        this.dataParser = new DataParser();
    }

    JSONArray parse(Object object) {
        JSONArray include = new JSONArray();

        for (Field relationField : object.getClass().getDeclaredFields()) {
            if (relationField.isAnnotationPresent(JsonApiRelation.class)) {
                Object relationObject = GetterAndSetter.callGetter(object, relationField.getName());

                if (relationObject == null) {
                    continue;
                }

                if (this.isList(relationObject)) {
                    for (Object relationObjectSingle : (Collection<Object>) relationObject) {
                        include.put(this.dataParser.parse(relationObjectSingle));
                    }
                } else {
                    include.put(this.dataParser.parse(object));
                }
            }
        }

        return include;
    }

    private boolean isList(Object object) {
        return Collection.class.isAssignableFrom(object.getClass());
    }
}
