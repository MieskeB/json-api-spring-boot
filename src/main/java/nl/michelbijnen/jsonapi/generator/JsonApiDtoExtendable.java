package nl.michelbijnen.jsonapi.generator;

import nl.michelbijnen.jsonapi.annotation.JsonApiId;
import nl.michelbijnen.jsonapi.annotation.JsonApiLink;
import nl.michelbijnen.jsonapi.enumeration.JsonApiLinkType;
import nl.michelbijnen.jsonapi.exception.JsonApiException;

public abstract class JsonApiDtoExtendable {
    @JsonApiId
    private String id;

    @JsonApiLink
    private String selfRel;
    @JsonApiLink(JsonApiLinkType.ALL_SELF)
    private String allSelfRel;

    // self
    @JsonApiLink(JsonApiLinkType.FIRST)
    private String firstRel;
    @JsonApiLink(JsonApiLinkType.LAST)
    private String lastRel;
    @JsonApiLink(JsonApiLinkType.NEXT)
    private String nextRel;
    @JsonApiLink(JsonApiLinkType.PREVIOUS)
    private String previousRel;

    // relation
    @JsonApiLink(JsonApiLinkType.RELATED)
    private String relatedRel;

    /**
     * Generates the links
     *
     * @param self the URI of the self endpoint (ex: "/user") (id should not be included since it is automatically gotten from the override)
     * @param all  the URI of the all endpoint (ex: "/")
     */
    public void generate(String self, String all) {
        if (this.id == null || this.id.isEmpty()) {
            throw new JsonApiException("Id not entered");
        }

        String baseUrl = System.getProperty("baseUrl", "http://localhost:8080");

        // check if trailing / baseUrl
        if (baseUrl.endsWith("/")) {
            baseUrl = baseUrl.substring(0, baseUrl.length() - 1);
        }

        // check if / before self and all
        if (!self.startsWith("/")) {
            self = "/" + self;
        }
        if (!all.startsWith("/")) {
            all = "/" + all;
        }

        // check if / after self
        if (!self.endsWith("/")) {
            self = self + "/";
        }

        this.selfRel = baseUrl + self + id;
        this.allSelfRel = baseUrl + all;

        // TODO usesPagination
    }


    //region getters and setters

    public String getSelfRel() {
        return selfRel;
    }

    public String getFirstRel() {
        return firstRel;
    }

    public String getLastRel() {
        return lastRel;
    }

    public String getNextRel() {
        return nextRel;
    }

    public String getPreviousRel() {
        return previousRel;
    }

    public String getRelatedRel() {
        return relatedRel;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAllSelfRel() {
        return allSelfRel;
    }

    public void setSelfRel(String selfRel) {
        this.selfRel = selfRel;
    }

    public void setAllSelfRel(String allSelfRel) {
        this.allSelfRel = allSelfRel;
    }

    public void setFirstRel(String firstRel) {
        this.firstRel = firstRel;
    }

    public void setLastRel(String lastRel) {
        this.lastRel = lastRel;
    }

    public void setNextRel(String nextRel) {
        this.nextRel = nextRel;
    }

    public void setPreviousRel(String previousRel) {
        this.previousRel = previousRel;
    }

    public void setRelatedRel(String relatedRel) {
        this.relatedRel = relatedRel;
    }

    //endregion
}
