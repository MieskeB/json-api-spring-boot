package nl.michelbijnen.jsonapi;

import nl.michelbijnen.jsonapi.annotation.JsonApiId;
import nl.michelbijnen.jsonapi.annotation.JsonApiLink;
import nl.michelbijnen.jsonapi.annotation.JsonApiObject;
import nl.michelbijnen.jsonapi.annotation.JsonApiProperty;
import nl.michelbijnen.jsonapi.enumeration.JsonApiLinkType;

@JsonApiObject("User")
public class UserDto {
    @JsonApiId
    private String id;
    @JsonApiProperty
    private String username;
    @JsonApiProperty
    private String email;
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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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
