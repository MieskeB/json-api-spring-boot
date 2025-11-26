package nl.michelbijnen.jsonapi.test.mock;

import nl.michelbijnen.jsonapi.annotation.JsonApiObject;
import nl.michelbijnen.jsonapi.annotation.JsonApiProperty;
import nl.michelbijnen.jsonapi.annotation.JsonApiRelation;
import nl.michelbijnen.jsonapi.generator.JsonApiDtoExtendable;

@JsonApiObject("Object")
public class ObjectDto extends JsonApiDtoExtendable implements Cloneable {
    @JsonApiProperty
    private String name;

    @JsonApiRelation("owner")
    private UserDto owner;

    @JsonApiRelation("apple")
    private AppleDto apple;

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

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    public AppleDto getApple() {
        return apple;
    }

    public void setApple(AppleDto apple) {
        this.apple = apple;
    }
}
