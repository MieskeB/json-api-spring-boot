package nl.michelbijnen.jsonapi.test.mock;

import nl.michelbijnen.jsonapi.annotation.*;
import nl.michelbijnen.jsonapi.enumeration.JsonApiLinkType;

@JsonApiObject("Object")
public class ObjectDto {
    @JsonApiId
    private String id;

    @JsonApiProperty
    private String name;

    @JsonApiRelation("Owner")
    private UserDto owner;
    @JsonApiLink(relation = "Owner")
    private String ownerSelfRel;
    @JsonApiLink(value = JsonApiLinkType.RELATED, relation = "Owner")
    private String ownerRelatedRel;

    @JsonApiLink(JsonApiLinkType.FIRST)
    private String firstRel;
    @JsonApiLink(JsonApiLinkType.PREVIOUS)
    private String previousRel;
    @JsonApiLink
    private String selfRel;
    @JsonApiLink(JsonApiLinkType.NEXT)
    private String nextRel;
    @JsonApiLink(JsonApiLinkType.LAST)
    private String lastRel;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public UserDto getOwner() {
        return owner;
    }

    public void setOwner(UserDto owner) {
        this.owner = owner;
    }

    public String getSelfRel() {
        return selfRel;
    }

    public void setSelfRel(String selfRel) {
        this.selfRel = selfRel;
    }

    public String getNextRel() {
        return nextRel;
    }

    public void setNextRel(String nextRel) {
        this.nextRel = nextRel;
    }

    public String getFirstRel() {
        return firstRel;
    }

    public void setFirstRel(String firstRel) {
        this.firstRel = firstRel;
    }

    public String getPreviousRel() {
        return previousRel;
    }

    public void setPreviousRel(String previousRel) {
        this.previousRel = previousRel;
    }

    public String getLastRel() {
        return lastRel;
    }

    public void setLastRel(String lastRel) {
        this.lastRel = lastRel;
    }

    public String getOwnerSelfRel() {
        return ownerSelfRel;
    }

    public void setOwnerSelfRel(String ownerSelfRel) {
        this.ownerSelfRel = ownerSelfRel;
    }

    public String getOwnerRelatedRel() {
        return ownerRelatedRel;
    }

    public void setOwnerRelatedRel(String ownerRelatedRel) {
        this.ownerRelatedRel = ownerRelatedRel;
    }
}
