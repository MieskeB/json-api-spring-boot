package nl.michelbijnen.jsonapi.test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import nl.michelbijnen.jsonapi.annotation.JsonApiId;
import nl.michelbijnen.jsonapi.annotation.JsonApiObject;
import nl.michelbijnen.jsonapi.annotation.JsonApiProperty;
import nl.michelbijnen.jsonapi.exception.JsonApiException;
import nl.michelbijnen.jsonapi.parser.JsonApiConverter;
import nl.michelbijnen.jsonapi.test.mock.MockDataGenerator;
import nl.michelbijnen.jsonapi.test.mock.ObjectDto;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

public class DataTest {

    private ObjectDto objectDto;
    private ObjectMapper mapper;

    @Before
    public void before() throws CloneNotSupportedException {
        MockDataGenerator generator = MockDataGenerator.getInstance();
        this.objectDto = (ObjectDto) generator.getObjectDto().clone();
        this.mapper = new ObjectMapper();
    }

    @Test
    public void testIfDataExists() throws Exception {
        JsonNode json = mapper.readTree(JsonApiConverter.convert(objectDto));
        assertNotNull(json.get("data"));
    }

    @Test
    public void testIfIdWorks() throws Exception {
        JsonNode json = mapper.readTree(JsonApiConverter.convert(objectDto));
        assertEquals(objectDto.getId(), json.get("data").get("id").asText());
    }

    @Test
    public void testIfTypeWorks() throws Exception {
        JsonNode json = mapper.readTree(JsonApiConverter.convert(objectDto));
        assertEquals("Object", json.get("data").get("type").asText());
    }

    @Test
    public void testIfAttributesExists() throws Exception {
        JsonNode json = mapper.readTree(JsonApiConverter.convert(objectDto));
        assertNotNull(json.get("data").get("attributes"));
    }

    @Test
    public void testIfAttributeNameWorks() throws Exception {
        JsonNode json = mapper.readTree(JsonApiConverter.convert(objectDto));
        assertEquals(objectDto.getName(), json.get("data").get("attributes").get("name").asText());
    }

    @Test
    public void testIfDtoWithoutObjectAnnotationThrowsError() {
        final class WrongObject {
            @JsonApiId
            private String id;
            @JsonApiProperty
            private String apple;

            public String getId() {
                return this.id;
            }

            public void setId(String id) {
                this.id = id;
            }

            public String getApple() {
                return this.apple;
            }

            public void setApple(String apple) {
                this.apple = apple;
            }
        }

        WrongObject wrongObject = new WrongObject();
        wrongObject.setId("id-123");
        wrongObject.setApple("apple");

        try {
            JsonApiConverter.convert(wrongObject);
            fail();
        } catch (JsonApiException e) {
            assertEquals("@JsonApiObject(\"<classname>\") missing", e.getMessage());
        }
    }

    @Test
    public void testIfDtoWithoutIdAnnotationThrowsError() {
        @JsonApiObject("WrongObject")
        final class WrongObject {
            @JsonApiProperty
            private String id;
            @JsonApiProperty
            private String apple;

            public String getId() {
                return this.id;
            }

            public void setId(String id) {
                this.id = id;
            }

            public String getApple() {
                return this.apple;
            }

            public void setApple(String apple) {
                this.apple = apple;
            }
        }

        WrongObject wrongObject = new WrongObject();
        wrongObject.setId("id-123");
        wrongObject.setApple("apple");

        try {
            JsonApiConverter.convert(wrongObject);
            fail();
        } catch (JsonApiException e) {
            assertEquals("No field with @JsonApiId is found", e.getMessage());
        }
    }
}
