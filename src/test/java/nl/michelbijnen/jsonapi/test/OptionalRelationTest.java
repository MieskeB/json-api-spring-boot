package nl.michelbijnen.jsonapi.test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import nl.michelbijnen.jsonapi.parser.JsonApiConverter;
import nl.michelbijnen.jsonapi.test.mock.MockDataGenerator;
import nl.michelbijnen.jsonapi.test.mock.ObjectDto;
import nl.michelbijnen.jsonapi.test.mock.OptionalTestDto;
import org.junit.Before;
import org.junit.Test;

import java.util.Optional;

import static org.junit.Assert.*;

public class OptionalRelationTest {

    private MockDataGenerator generator;
    private OptionalTestDto optionalTestDto;
    private ObjectMapper mapper;

    @Before
    public void before() throws CloneNotSupportedException {
        this.generator = MockDataGenerator.getInstance();
        this.optionalTestDto = new OptionalTestDto();
        this.optionalTestDto.setId("test");
        this.optionalTestDto.setUsername("test user");
        this.optionalTestDto.setEmail("test@example.com");
        this.optionalTestDto.generate("/optionaltest", "/optionaltest");
        this.mapper = new ObjectMapper();
    }

    @Test
    public void testOptionalRelationPresent() throws Exception {
        ObjectDto obj = (ObjectDto) generator.getObjectDto().clone();
        optionalTestDto.setOptionalObject(Optional.of(obj));

        String jsonStr = JsonApiConverter.convert(optionalTestDto);
        JsonNode json = mapper.readTree(jsonStr);

        JsonNode relationships = json.get("data").get("relationships");
        assertNotNull("Relationships should exist", relationships);
        JsonNode optionalRel = relationships.get("optionalObject");
        assertNotNull("optionalObject relationship should exist", optionalRel);
        JsonNode data = optionalRel.get("data");
        assertNotNull("Data should exist in relationship", data);
        assertEquals("ID should match", obj.getId(), data.get("id").asText());
        assertEquals("Type should match", "Object", data.get("type").asText());
    }

    @Test
    public void testOptionalRelationAbsent() throws Exception {
        optionalTestDto.setOptionalObject(Optional.empty());

        String jsonStr = JsonApiConverter.convert(optionalTestDto);
        JsonNode json = mapper.readTree(jsonStr);

        JsonNode relationships = json.get("data").get("relationships");
        if (relationships != null) {
            JsonNode optionalRel = relationships.get("optionalObject");
            assertNull("optionalObject relationship should not exist when absent", optionalRel);
        }
    }

    @Test
    public void testOptionalRelationNull() throws Exception {
        optionalTestDto.setOptionalObject(null);

        String jsonStr = JsonApiConverter.convert(optionalTestDto);
        JsonNode json = mapper.readTree(jsonStr);

        JsonNode relationships = json.get("data").get("relationships");
        if (relationships != null) {
            JsonNode optionalRel = relationships.get("optionalObject");
            assertNull("optionalObject relationship should not exist when null", optionalRel);
        }
    }
}