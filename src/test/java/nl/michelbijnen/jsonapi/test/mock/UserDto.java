package nl.michelbijnen.jsonapi.test.mock;

import nl.michelbijnen.jsonapi.annotation.*;
import nl.michelbijnen.jsonapi.generator.JsonApiDtoExtendable;

import java.util.List;

@JsonApiObject("User")
public class UserDto extends JsonApiDtoExtendable implements Cloneable {
    @JsonApiProperty
    private String username;
    @JsonApiProperty
    private String email;

    @JsonApiRelation(value = "mainObject")
    private ObjectDto mainObject;

    @JsonApiRelation(value = "childObjects")
    private List<ObjectDto> childObjects;

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

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
