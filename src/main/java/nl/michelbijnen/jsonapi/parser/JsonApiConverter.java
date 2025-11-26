package nl.michelbijnen.jsonapi.parser;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

public class JsonApiConverter {

    private JsonApiConverter() {
    }

    public static String convert(Object object) {
        int depth = Integer.parseInt(System.getProperty("jsonapi.depth", "1"));
        return convert(object, depth);
    }

    public static String convert(Object object, int depth){
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        JsonApiParser jsonApiParser = new JsonApiParser();
        ObjectNode result = jsonApiParser.parse(object, depth, mapper);
        try {
            return mapper.writeValueAsString(result);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error converting to JSON", e);
        }
    }

    public static String convert(Object object, JsonApiOptions options) {
        int depth = Integer.parseInt(System.getProperty("jsonapi.depth", "1"));
        return convert(object, depth, options);
    }

    public static String convert(Object object, int depth, JsonApiOptions options) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        JsonApiParser jsonApiParser = new JsonApiParser();
        ObjectNode result = jsonApiParser.parse(object, depth, mapper, options);
        try {
            return mapper.writeValueAsString(result);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error converting to JSON", e);
        }
    }
}