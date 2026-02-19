package nl.michelbijnen.jsonapi.example;

import nl.michelbijnen.jsonapi.models.Category;
import nl.michelbijnen.jsonapi.models.Product;
import nl.michelbijnen.jsonapi.parser.JsonApiConverter;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/products")
public class ProductController {

    @GetMapping("/{id}")
    public ResponseEntity<String> getProduct(@PathVariable String id) {
        Category category = new Category("1", "Electronics");
        Product product = new Product(id, "Laptop", 1200.0, category);
        String json = JsonApiConverter.convert(product, 2);
        return ResponseEntity.ok(json);
    }
}
