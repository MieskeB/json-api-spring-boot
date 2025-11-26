package nl.michelbijnen.jsonapi.test.mock;

import nl.michelbijnen.jsonapi.annotation.JsonApiObject;
import nl.michelbijnen.jsonapi.annotation.JsonApiProperty;
import nl.michelbijnen.jsonapi.annotation.JsonApiRelation;
import nl.michelbijnen.jsonapi.generator.JsonApiDtoExtendable;

import java.util.Optional;

@JsonApiObject("OptionalTest")
public class OptionalTestDto extends JsonApiDtoExtendable implements Cloneable {
    @JsonApiProperty
    private String username;
    @JsonApiProperty
    private String email;

    @JsonApiRelation(value = "optionalObject")
    private Optional<ObjectDto> optionalObject;

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

    public Optional<ObjectDto> getOptionalObject() {
        return optionalObject;
    }

    public void setOptionalObject(Optional<ObjectDto> optionalObject) {
        this.optionalObject = optionalObject;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}