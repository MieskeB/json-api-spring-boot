package nl.michelbijnen.jsonapi.test;

import nl.michelbijnen.jsonapi.annotation.*;
import nl.michelbijnen.jsonapi.enumeration.JsonApiLinkType;

import java.util.List;

@JsonApiObject("User")
public class UserDto {
    @JsonApiId
    private String id;

    @JsonApiProperty
    private String username;
    @JsonApiProperty
    private String email;

    @JsonApiRelation(value = "mainObject", self = "http://localhost:8080/user/1/objects", related = "http://localhost:8080/user/1/objets")
    private ObjectDto mainObject;

    @JsonApiRelation(value = "childObjects", self = "http://localhost:8080/user/1/objects", related = "http://localhost:8080/user/1/objets")
    private List<ObjectDto> childObjects;

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

    public ObjectDto getMainObject() {
        return mainObject;
    }

    public void setMainObject(ObjectDto mainObject) {
        this.mainObject = mainObject;
    }

    public List<ObjectDto> getChildObjects() {
        return childObjects;
    }

    public void setChildObjects(List<ObjectDto> childObjects) {
        this.childObjects = childObjects;
    }
}
