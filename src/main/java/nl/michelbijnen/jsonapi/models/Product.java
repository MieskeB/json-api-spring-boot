package nl.michelbijnen.jsonapi.models;

import nl.michelbijnen.jsonapi.annotation.JsonApiObject;
import nl.michelbijnen.jsonapi.annotation.JsonApiProperty;
import nl.michelbijnen.jsonapi.annotation.JsonApiRelation;
import nl.michelbijnen.jsonapi.generator.JsonApiDtoExtendable;

@JsonApiObject("Product")
public class Product extends JsonApiDtoExtendable {

    @JsonApiProperty
    private String name;

    @JsonApiProperty
    private Double price;

    @JsonApiRelation("category")
    private Category category;

    public Product(String id, String name, Double price,Category category) {
        this.setId(id);
        this.name = name;
        this.price = price;
        this.generate("/product", "/products");
        this.category = category;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }
}
