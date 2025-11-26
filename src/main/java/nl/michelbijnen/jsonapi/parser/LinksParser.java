package nl.michelbijnen.jsonapi.parser;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import nl.michelbijnen.jsonapi.annotation.JsonApiLink;
import nl.michelbijnen.jsonapi.enumeration.JsonApiLinkType;
import nl.michelbijnen.jsonapi.helper.GetterAndSetter;

import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Stream;

class LinksParser {
    ObjectNode parse(Object object, ObjectMapper mapper) {
        ObjectNode links = mapper.createObjectNode();
        boolean asList = this.isList(object);
        if (asList) {
            if (((Collection<Object>) object).isEmpty()) {
                return links;
            }
            object = ((Collection<Object>) object).iterator().next();
        }
        Field[] allFields = Stream.concat(Arrays.stream(object.getClass().getDeclaredFields()),
                Arrays.stream(object.getClass().getSuperclass().getDeclaredFields())).toArray(Field[]::new);
        for (Field field : allFields) {
            if (field.isAnnotationPresent(JsonApiLink.class)) {
                if (field.getAnnotation(JsonApiLink.class).value().equals(JsonApiLinkType.ALL_SELF)) {
                    if (asList) {
                        Object href = GetterAndSetter.callGetter(object, field.getName());
                        if (isValidUrl(href))
                            links.put(JsonApiLinkType.SELF.toString().toLowerCase(), href.toString());
                    }
                } else if (field.getAnnotation(JsonApiLink.class).value().equals(JsonApiLinkType.SELF)) {
                    if (!asList) {
                        Object href = GetterAndSetter.callGetter(object, field.getName());
                        if (isValidUrl(href))
                            links.put(field.getAnnotation(JsonApiLink.class).value().toString().toLowerCase(), href.toString());
                    }
                }
                // TODO next, previous, first, last, related rels for later updates
                // else {
                // Object href = GetterAndSetter.callGetter(object, field.getName());
                // if (isValidUrl(href))
                // links.put(field.getAnnotation(JsonApiLink.class).value().toString().toLowerCase(),
                // href);
                // }
            }
        }
        return links;
    }

    private boolean isList(Object object) {
        return Collection.class.isAssignableFrom(object.getClass());
    }

    private boolean isValidUrl(Object urlToCheckO) {
        if (urlToCheckO == null)
            return false;

        String urlToCheck = urlToCheckO.toString();

        if (urlToCheck.isEmpty())
            return false;

        try {
            new URL(urlToCheck);
            return true;
        } catch (MalformedURLException e) {
            System.out.println("Warning: invalid url not added - " + urlToCheck);
            return false;
        }
    }
}
