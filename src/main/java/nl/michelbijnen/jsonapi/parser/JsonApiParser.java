package nl.michelbijnen.jsonapi.parser;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.Collection;

class JsonApiParser {
    private LinksParser linksParser;
    private DataParser dataParser;
    private IncludedParser includedParser;

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
        if (object == null) {
            ObjectNode nullObject = mapper.createObjectNode();
            nullObject.set("data", mapper.createObjectNode());
            return nullObject;
        }
        if (this.isList(object)) {
            return this.convertObjectAsList(object, maxDepth, mapper);
        } else {
            return this.convertObjectAsObject(object, maxDepth, mapper);
        }
    }

    private ObjectNode convertObjectAsList(Object object, int maxDepth, ObjectMapper mapper) {
        ObjectNode jsonObject = mapper.createObjectNode();

        if (((Collection<Object>) object).size() == 0) {
            jsonObject.set("data", mapper.createArrayNode());
            return jsonObject;
        }

        ObjectNode parsedLinks = this.linksParser.parse(object, mapper);
        if (parsedLinks.size() > 0) {
            jsonObject.set("links", parsedLinks);
        }

        ArrayNode dataJsonArray = mapper.createArrayNode();
        for (Object loopObject : (Collection<Object>) object) {
            dataJsonArray.add(this.dataParser.parse(loopObject, mapper));
        }
        jsonObject.set("data", dataJsonArray);

        ArrayNode includedJsonArray = mapper.createArrayNode();
        for (Object loopObject : (Collection<Object>) object) {
            this.includedParser.parse(loopObject, includedJsonArray, maxDepth, 0, mapper);
        }
        if (includedJsonArray.size() != 0)
            jsonObject.set("included", includedJsonArray);

        return jsonObject;
    }

    private ObjectNode convertObjectAsObject(Object object, int maxDepth, ObjectMapper mapper) {
        ObjectNode jsonObject = mapper.createObjectNode();
        jsonObject.set("data", this.dataParser.parse(object, mapper));

        ObjectNode parsedLinks = this.linksParser.parse(object, mapper);
        if (parsedLinks.size() > 0)
            jsonObject.set("links", parsedLinks);

        ArrayNode parsedIncluded = this.includedParser.parse(object, maxDepth, mapper);
        if (parsedIncluded.size() != 0)
            jsonObject.set("included", parsedIncluded);

        return jsonObject;
    }

    private boolean isList(Object object) {
        return Collection.class.isAssignableFrom(object.getClass());
    }
}