package nl.michelbijnen.jsonapi.parser;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import static nl.michelbijnen.jsonapi.util.JsonApiConstants.*;

public class JsonApiConverter {

    private static final ObjectMapper MAPPER;
    static {
        MAPPER = new ObjectMapper();
        MAPPER.registerModule(new JavaTimeModule());
    }

    private JsonApiConverter() {
    }

    /**
     * Converts the given object to a JSON:API document as a JSON string.
     * <p>
     * Uses the system property {@code jsonapi.depth} (default configured by {@code JSON_API_DEPTH_DEFAULT_VALUE})
     * to determine maximum relation traversal depth.
     *
     * @param object the object to serialize
     * @return the JSON representation of the JSON:API document
     * @throws RuntimeException if serialization fails
     */
    public static String convert(Object object) {
        int depth = Integer.parseInt(System.getProperty(JSON_API_DEPTH_PROPERTY, JSON_API_DEPTH_DEFAULT_VALUE));
        return convert(object, depth);
    }

    /**
     * Converts the given object to a JSON:API document as a JSON string using the provided depth.
     * <p>
     * Registers {@link com.fasterxml.jackson.datatype.jsr310.JavaTimeModule} on the {@link ObjectMapper}.
     *
     * @param object the object to serialize
     * @param depth  maximum relation traversal depth
     * @return the JSON representation of the JSON:API document
     * @throws RuntimeException if serialization fails
     */
    public static String convert(Object object, int depth){
        JsonApiParser jsonApiParser = new JsonApiParser();
        ObjectNode result = jsonApiParser.parse(object, depth, MAPPER);
        try {
            return MAPPER.writeValueAsString(result);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(JSON_API_ERROR_CONVERTING_TO_JSON, e);
        }
    }

    /**
     * Converts the given object to a JSON:API document as a JSON string with options.
     * <p>
     * Uses the system property {@code jsonapi.depth} (default configured by {@code JSON_API_DEPTH_DEFAULT_VALUE})
     * to determine maximum relation traversal depth. See {@link JsonApiOptions} for parsing behavior customizations.
     *
     * @param object  the object to serialize
     * @param options options influencing parsing behavior; may be {@code null}
     * @return the JSON representation of the JSON:API document
     * @throws RuntimeException if serialization fails
     */
    public static String convert(Object object, JsonApiOptions options) {
        int depth = Integer.parseInt(System.getProperty(JSON_API_DEPTH_PROPERTY, JSON_API_DEPTH_DEFAULT_VALUE));
        return convert(object, depth, options);
    }

    /**
     * Converts the given object to a JSON:API document as a JSON string using the provided depth and options.
     * <p>
     * Registers {@link com.fasterxml.jackson.datatype.jsr310.JavaTimeModule} on the {@link ObjectMapper}.
     * See {@link JsonApiOptions} for parsing behavior customizations.
     *
     * @param object  the object to serialize
     * @param depth   maximum relation traversal depth
     * @param options options influencing parsing behavior; may be {@code null}
     * @return the JSON representation of the JSON:API document
     * @throws RuntimeException if serialization fails
     */
    public static String convert(Object object, int depth, JsonApiOptions options) {
        JsonApiParser jsonApiParser = new JsonApiParser();
        ObjectNode result = jsonApiParser.parse(object, depth, MAPPER, options);
        try {
            return MAPPER.writeValueAsString(result);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(JSON_API_ERROR_CONVERTING_TO_JSON, e);
        }
    }
}