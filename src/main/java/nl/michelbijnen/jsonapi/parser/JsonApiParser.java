package nl.michelbijnen.jsonapi.parser;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.Collection;

import static nl.michelbijnen.jsonapi.util.JsonApiConstants.*;

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
     * Parses the given object into a JSON:API document node using the specified depth.
     * <p>
     * Delegates to {@link #parse(Object, int, ObjectMapper, JsonApiOptions)} with {@code options = null}.
     *
     * @param object   the object (or collection) to parse
     * @param maxDepth maximum relation traversal depth
     * @param mapper   Jackson {@link ObjectMapper} used to build nodes
     * @return the JSON:API document as an {@link ObjectNode}
     */
    ObjectNode parse(Object object, int maxDepth, ObjectMapper mapper) {
        return parse(object, maxDepth, mapper, null);
    }

    /**
     * Parses the given object into a JSON:API document node with optional parsing options.
     * <p>
     * Behavior:
     * - If {@code object} is {@code null}, returns a document with an empty {@code data} object.
     * - If {@code object} is a collection, serializes it as a JSON:API array document.
     * - Otherwise, serializes it as a single-resource document.
     * - Honors {@code maxDepth} for relation traversal and applies any {@link JsonApiOptions}.
     *
     * @param object   the object (or collection) to parse; may be {@code null}
     * @param maxDepth maximum relation traversal depth
     * @param mapper   Jackson {@link ObjectMapper} used to build nodes
     * @param options  parsing options (e.g., sparse fieldsets, include paths); may be {@code null}
     * @return the JSON:API document as an {@link ObjectNode}
     */
    ObjectNode parse(Object object, int maxDepth, ObjectMapper mapper, JsonApiOptions options) {
        if (object == null) {
            ObjectNode nullObject = mapper.createObjectNode();
            nullObject.set(DATA, mapper.createObjectNode());
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
            jsonObject.set(DATA, mapper.createArrayNode());
            return jsonObject;
        }

        ObjectNode parsedLinks = this.linksParser.parse(object, mapper);
        if (!parsedLinks.isEmpty()) {
            jsonObject.set(LINKS, parsedLinks);
        }

        ArrayNode dataJsonArray = mapper.createArrayNode();
        for (Object loopObject : (Collection<Object>) object) {
            dataJsonArray.add(this.dataParser.parse(loopObject, mapper, options));
        }
        jsonObject.set(DATA, dataJsonArray);

        boolean shouldBuildIncluded = (options == null) ||
                (options.topLevelIncludeRelations() != null && !options.topLevelIncludeRelations().isEmpty());

        if (shouldBuildIncluded) {
            ArrayNode includedJsonArray = mapper.createArrayNode();
            for (Object loopObject : (Collection<Object>) object) {
                this.includedParser.parse(loopObject, includedJsonArray, maxDepth, 0, mapper, options);
            }
            if (!includedJsonArray.isEmpty())
                jsonObject.set(INCLUDED, includedJsonArray);
        }

        return jsonObject;
    }

    private ObjectNode convertObjectAsObject(Object object, int maxDepth, ObjectMapper mapper, JsonApiOptions options) {
        ObjectNode jsonObject = mapper.createObjectNode();
        jsonObject.set(DATA, this.dataParser.parse(object, mapper, options));

        ObjectNode parsedLinks = this.linksParser.parse(object, mapper);
        if (!parsedLinks.isEmpty())
            jsonObject.set(LINKS, parsedLinks);

        boolean shouldBuildIncluded = (options == null) ||
                (options.topLevelIncludeRelations() != null && !options.topLevelIncludeRelations().isEmpty());

        if (shouldBuildIncluded) {
            ArrayNode parsedIncluded = this.includedParser.parse(object, maxDepth, mapper, options);
            if (!parsedIncluded.isEmpty())
                jsonObject.set(INCLUDED, parsedIncluded);
        }

        return jsonObject;
    }

    private boolean isList(Object object) {
        return Collection.class.isAssignableFrom(object.getClass());
    }
}