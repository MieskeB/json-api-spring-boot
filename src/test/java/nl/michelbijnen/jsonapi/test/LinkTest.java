package nl.michelbijnen.jsonapi.test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import nl.michelbijnen.jsonapi.parser.JsonApiConverter;
import nl.michelbijnen.jsonapi.test.mock.MockDataGenerator;
import nl.michelbijnen.jsonapi.test.mock.ObjectDto;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class LinkTest {

    private ObjectDto objectDto;
    private ObjectMapper mapper;

    @Before
    public void before() throws CloneNotSupportedException {
        MockDataGenerator generator = MockDataGenerator.getInstance();
        this.objectDto = (ObjectDto) generator.getObjectDto().clone();
        this.mapper = new ObjectMapper();
    }

    @Test
    public void testIfLinksExists() throws Exception {
        JsonNode json = mapper.readTree(JsonApiConverter.convert(objectDto));
        assertNotNull(json.get("links"));
    }

    @Test
    @Ignore("Planned for future update")
    public void testIfFirstRelWorks() throws Exception {
        JsonNode json = mapper.readTree(JsonApiConverter.convert(objectDto));
        assertEquals("http://localhost:8080/object/" + this.objectDto.getId() + "?page=0", json.get("links").get("first").asText());
    }

    @Test
    @Ignore("Planned for future update")
    public void testIfPreviousRelWorks() throws Exception {
        JsonNode json = mapper.readTree(JsonApiConverter.convert(objectDto));
        assertEquals("http://localhost:8080/object/" + this.objectDto.getId() + "?page=0", json.get("links").get("previous").asText());
    }

    @Test
    public void testIfSelfRelWorks() throws Exception {
        JsonNode json = mapper.readTree(JsonApiConverter.convert(objectDto));
        assertEquals("http://localhost:8080/object/" + this.objectDto.getId(), json.get("links").get("self").asText());
    }

    @Test
    public void testIfMalformedUrlThrowsException() throws Exception {
        String tempUrl = this.objectDto.getSelfRel();
        this.objectDto.setSelfRel("NotValidUrl");
        JsonNode json = mapper.readTree(JsonApiConverter.convert(this.objectDto));
        this.objectDto.setSelfRel(tempUrl);
        try {
            json.get("links");
            json.get("links").get("self");
            fail();
        } catch (NullPointerException e) {
            assertTrue(e.getMessage().endsWith("\"com.fasterxml.jackson.databind.JsonNode.get(String)\" is null"));
        }
    }

    @Test
    @Ignore("Planned for future update")
    public void testIfNextRelWorks() throws Exception {
        JsonNode json = mapper.readTree(JsonApiConverter.convert(objectDto));
        assertEquals("http://localhost:8080/object/" + this.objectDto.getId() + "?page=2", json.get("links").get("next").asText());
    }

    @Test
    @Ignore("Planned for future update")
    public void testIfLastRelWorks() throws Exception {
        JsonNode json = mapper.readTree(JsonApiConverter.convert(objectDto));
        assertEquals("http://localhost:8080/object/" + this.objectDto.getId() + "?page=2", json.get("links").get("last").asText());
    }
}
