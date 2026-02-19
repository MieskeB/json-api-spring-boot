package nl.michelbijnen.jsonapi.models;

import nl.michelbijnen.jsonapi.annotation.JsonApiObject;
import nl.michelbijnen.jsonapi.annotation.JsonApiProperty;
import nl.michelbijnen.jsonapi.generator.JsonApiDtoExtendable;

@JsonApiObject("Category")
public class Category extends JsonApiDtoExtendable {

    @JsonApiProperty
    private String title;

    public Category(String id, String title) {
        this.setId(id);
        this.title = title;
        this.generate("/category", "/categories");
    }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
}
