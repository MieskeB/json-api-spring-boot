package nl.michelbijnen.jsonapi.parser;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import nl.michelbijnen.jsonapi.annotation.JsonApiProperty;
import nl.michelbijnen.jsonapi.helper.GetterAndSetter;

import java.lang.reflect.Field;

class AttributesParser {

    /**
     * Parses all fields annotated with {@link nl.michelbijnen.jsonapi.annotation.JsonApiProperty}
     * from the given object into a JSON attributes node.
     *
     * @param object the source object to extract attributes from
     * @param mapper the ObjectMapper used to create and populate the node
     * @return an ObjectNode containing only the attributes of the object
     */
    ObjectNode parse(Object object, ObjectMapper mapper) {
        return parse(object, mapper, null);
    }

    /**
     * Parses attributes for the given object, honoring field filtering defined in {@link JsonApiOptions}.
     * If options specify fields for the object's JSON:API type, only those fields are included.
     *
     * @param object  the source object to extract attributes from
     * @param mapper  the ObjectMapper used to create and populate the node
     * @param options optional parse options (it may be null) used for field filtering by type
     * @return an ObjectNode containing the filtered attributes
     */
    ObjectNode parse(Object object, ObjectMapper mapper, JsonApiOptions options) {
        ObjectNode jsonObject = mapper.createObjectNode();

        String type = null;
        if (options != null) {
            nl.michelbijnen.jsonapi.annotation.JsonApiObject ann =
                    object.getClass().getAnnotation(nl.michelbijnen.jsonapi.annotation.JsonApiObject.class);
            if (ann != null) type = ann.value();
        }

        for (Field field : object.getClass().getDeclaredFields()) {
            if (!field.isAnnotationPresent(JsonApiProperty.class)) continue;

            if (options != null && type != null && options.hasFieldsForType(type)) {
                if (!options.fieldsForType(type).contains(field.getName())) {
                    continue;
                }
            }

            Object value = GetterAndSetter.callGetter(object, field.getName());
            jsonObject.set(field.getName(), mapper.valueToTree(value));
        }
        return jsonObject;
    }
}