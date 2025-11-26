package nl.michelbijnen.jsonapi.parser;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.Collection;

class JsonApiParser {
    private final LinksParser linksParser;
    private final DataParser dataParser;
    private final IncludedParser includedParser;

    JsonApiParser() {
        this.linksParser = new LinksParser();
        this.dataParser = new DataParser();
        this.includedParser = new IncludedParser();
    }

    /**
     * This method should combine three things:
     * Data
     * Links
     * Included
     *
     * @param object   the object to be converted
     * @param maxDepth the depth of the models to return
     * @return The converted json
     */
    ObjectNode parse(Object object, int maxDepth, ObjectMapper mapper) {
        return parse(object, maxDepth, mapper, null);
    }

    // NEW
    ObjectNode parse(Object object, int maxDepth, ObjectMapper mapper, JsonApiOptions options) {
        if (object == null) {
            ObjectNode nullObject = mapper.createObjectNode();
            nullObject.set("data", mapper.createObjectNode());
            return nullObject;
        }
        if (this.isList(object)) {
            return this.convertObjectAsList(object, maxDepth, mapper, options);
        } else {
            return this.convertObjectAsObject(object, maxDepth, mapper, options);
        }
    }

    private ObjectNode convertObjectAsList(Object object, int maxDepth, ObjectMapper mapper, JsonApiOptions options) {
        ObjectNode jsonObject = mapper.createObjectNode();

        if (((Collection<Object>) object).isEmpty()) {
            jsonObject.set("data", mapper.createArrayNode());
            return jsonObject;
        }

        ObjectNode parsedLinks = this.linksParser.parse(object, mapper);
        if (!parsedLinks.isEmpty()) {
            jsonObject.set("links", parsedLinks);
        }

        ArrayNode dataJsonArray = mapper.createArrayNode();
        for (Object loopObject : (Collection<Object>) object) {
            dataJsonArray.add(this.dataParser.parse(loopObject, false, mapper, options, true));
        }
        jsonObject.set("data", dataJsonArray);

        // Only build included when no options (legacy) or includePaths is non-empty
        boolean shouldBuildIncluded = (options == null) ||
                (options.getIncludePaths() != null && !options.getIncludePaths().isEmpty());

        if (shouldBuildIncluded) {
            ArrayNode includedJsonArray = mapper.createArrayNode();
            for (Object loopObject : (Collection<Object>) object) {
                this.includedParser.parse(loopObject, includedJsonArray, maxDepth, 0, mapper, options);
            }
            if (!includedJsonArray.isEmpty())
                jsonObject.set("included", includedJsonArray);
        }

        return jsonObject;
    }

    private ObjectNode convertObjectAsObject(Object object, int maxDepth, ObjectMapper mapper, JsonApiOptions options) {
        ObjectNode jsonObject = mapper.createObjectNode();
        jsonObject.set("data", this.dataParser.parse(object, false, mapper, options, true));

        ObjectNode parsedLinks = this.linksParser.parse(object, mapper);
        if (!parsedLinks.isEmpty())
            jsonObject.set("links", parsedLinks);

        // Only build included when no options (legacy) or includePaths is non-empty
        boolean shouldBuildIncluded = (options == null) ||
                (options.getIncludePaths() != null && !options.getIncludePaths().isEmpty());

        if (shouldBuildIncluded) {
            ArrayNode parsedIncluded = this.includedParser.parse(object, maxDepth, mapper, options);
            if (!parsedIncluded.isEmpty())
                jsonObject.set("included", parsedIncluded);
        }

        return jsonObject;
    }

    private boolean isList(Object object) {
        return Collection.class.isAssignableFrom(object.getClass());
    }
}