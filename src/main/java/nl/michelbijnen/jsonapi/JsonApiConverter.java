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
        if (Collection.class.isAssignableFrom(this.object.getClass())) {
            List<JSONObject> returnValues = new ArrayList<>();
            for (Object loopObject : (Collection<Object>) this.object) {
                JSONObject loopJsonObject = new JSONObject();
                loopJsonObject.put("links", JsonApiParser.parseToLinks(loopObject));
                loopJsonObject.put("data", JsonApiParser.parseToData(loopObject));
                returnValues.add(loopJsonObject);
            }
        }
        else {
            finalJsonObject.put("links", JsonApiParser.parseToLinks(this.object));
            finalJsonObject.put("data", JsonApiParser.parseToData(this.object));
        }
        return finalJsonObject.toString();
    }
}
