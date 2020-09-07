package nl.michelbijnen.jsonapi.test;

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

    @JsonApiLink
    private String selfRel;
    @JsonApiLink(JsonApiLinkType.NEXT)
    private String nextRel;

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
}
