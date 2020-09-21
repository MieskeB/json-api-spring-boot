package nl.michelbijnen.jsonapi.test.mock;

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

    @JsonApiRelation(value = "mainObject")
    private ObjectDto mainObject;
    @JsonApiLink(relation = "mainObject")
    private String mainObjectSelfRel;
    @JsonApiLink(value = JsonApiLinkType.RELATED, relation = "mainObject")
    private String mainObjectRelatedRel;

    @JsonApiRelation(value = "childObjects")
    private List<ObjectDto> childObjects;
    @JsonApiLink(relation = "childObjects")
    private String childObjectSelfRel;
    @JsonApiLink(value = JsonApiLinkType.RELATED, relation = "childObjects")
    private String childObjectRelatedRel;

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

    public String getMainObjectSelfRel() {
        return mainObjectSelfRel;
    }

    public void setMainObjectSelfRel(String mainObjectSelfRel) {
        this.mainObjectSelfRel = mainObjectSelfRel;
    }

    public String getMainObjectRelatedRel() {
        return mainObjectRelatedRel;
    }

    public void setMainObjectRelatedRel(String mainObjectRelatedRel) {
        this.mainObjectRelatedRel = mainObjectRelatedRel;
    }

    public String getChildObjectSelfRel() {
        return childObjectSelfRel;
    }

    public void setChildObjectSelfRel(String childObjectSelfRel) {
        this.childObjectSelfRel = childObjectSelfRel;
    }

    public String getChildObjectRelatedRel() {
        return childObjectRelatedRel;
    }

    public void setChildObjectRelatedRel(String childObjectRelatedRel) {
        this.childObjectRelatedRel = childObjectRelatedRel;
    }
}
