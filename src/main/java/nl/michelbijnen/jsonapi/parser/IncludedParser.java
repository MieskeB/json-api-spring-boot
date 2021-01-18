package nl.michelbijnen.jsonapi.parser;

import nl.michelbijnen.jsonapi.annotation.*;
import nl.michelbijnen.jsonapi.helper.GetterAndSetter;
import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.util.Collection;

class IncludedParser {
    private DataParser dataParser;
    private LinksParser linksParser;

    IncludedParser() {
        this.dataParser = new DataParser();
        this.linksParser = new LinksParser();
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
                        JSONObject singleIncludeObject = this.dataParser.parse(relationObjectSingle);
                        singleIncludeObject.put("links", this.linksParser.parse(relationObjectSingle));
                        include.put(singleIncludeObject);
                    }
                } else {
                    JSONObject singleIncludeObject = this.dataParser.parse(relationObject);
                    singleIncludeObject.put("links", this.linksParser.parse(relationObject));
                    include.put(singleIncludeObject);
                }
            }
        }

        return include;
    }

    private boolean isList(Object object) {
        return Collection.class.isAssignableFrom(object.getClass());
    }
}
