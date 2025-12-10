package nl.michelbijnen.jsonapi.test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import nl.michelbijnen.jsonapi.parser.JsonApiConverter;
import nl.michelbijnen.jsonapi.test.mock.MockDataGenerator;
import nl.michelbijnen.jsonapi.test.mock.ObjectDto;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class ListTest {

    private List<ObjectDto> objectDtoList;
    private ObjectMapper mapper;

    @Before
    public void before() throws CloneNotSupportedException {
        MockDataGenerator generator = MockDataGenerator.getInstance();
        this.objectDtoList = new ArrayList<>();
        this.objectDtoList.add((ObjectDto) generator.getObjectDto().clone());
        this.objectDtoList.add((ObjectDto) generator.getObjectDto().clone());
        this.mapper = new ObjectMapper();
    }

    @Test
    public void testIfLinksExists() throws Exception {
        JsonNode json = mapper.readTree(JsonApiConverter.convert(objectDtoList));
        assertNotNull(json.get("links"));
    }

    @Test
    public void testIfSelfRelIsCreatedProperly() throws Exception {
        JsonNode json = mapper.readTree(JsonApiConverter.convert(objectDtoList));
        assertEquals("http://localhost:8080/object", json.get("links").get("self").asText());
    }

    @Test
    public void testIfDataExists() throws Exception {
        JsonNode json = mapper.readTree(JsonApiConverter.convert(objectDtoList));
        assertNotNull(json.get("data"));
    }

    @Test
    public void testIfDataContainsId() throws Exception {
        JsonNode json = mapper.readTree(JsonApiConverter.convert(objectDtoList));
        assertEquals(objectDtoList.get(0).getId(), json.get("data").get(0).get("id").asText());
        assertEquals(objectDtoList.get(1).getId(), json.get("data").get(1).get("id").asText());
    }

    @Test
    public void testIfDataContainsType() throws Exception {
        JsonNode json = mapper.readTree(JsonApiConverter.convert(objectDtoList));
        assertEquals("Object", json.get("data").get(0).get("type").asText());
        assertEquals("Object", json.get("data").get(1).get("type").asText());
    }
}
