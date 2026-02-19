package nl.michelbijnen.jsonapi.test;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = nl.michelbijnen.jsonapi.example.DemoApplication.class)
@AutoConfigureMockMvc
class ProductControllerTest {

    @Autowired
    private MockMvc mvc;

    @Test
    void testGetProductReturnsOk() throws Exception {
        mvc.perform(get("/products/1").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.data.attributes.name").value("Laptop"))
                .andExpect(jsonPath("$.data.relationships.category").exists());
    }
}
