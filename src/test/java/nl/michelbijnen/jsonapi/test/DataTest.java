package nl.michelbijnen.jsonapi.test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import nl.michelbijnen.jsonapi.annotation.JsonApiId;
import nl.michelbijnen.jsonapi.annotation.JsonApiObject;
import nl.michelbijnen.jsonapi.annotation.JsonApiProperty;
import nl.michelbijnen.jsonapi.exception.JsonApiException;
import nl.michelbijnen.jsonapi.parser.JsonApiConverter;
import nl.michelbijnen.jsonapi.test.mock.MockDataGenerator;
import nl.michelbijnen.jsonapi.test.mock.ObjectDto;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

public class DataTest {

    private ObjectDto objectDto;

    @Before
    public void before() throws CloneNotSupportedException {
        MockDataGenerator generator = MockDataGenerator.getInstance();
        this.objectDto = (ObjectDto) generator.getObjectDto().clone();
    }

    @Test
    public void testIfDataExists() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        System.out.println(mapper.writeValueAsString(objectDto));

        JSONObject jsonObject = new JSONObject(JsonApiConverter.convert(objectDto));
        assertNotNull(jsonObject.getJSONObject("data"));
    }

    @Test
    public void testIfIdWorks() {
        JSONObject jsonObject = new JSONObject(JsonApiConverter.convert(objectDto));
        assertEquals(objectDto.getId(), jsonObject.getJSONObject("data").getString("id"));
    }

    @Test
    public void testIfTypeWorks() {
        JSONObject jsonObject = new JSONObject(JsonApiConverter.convert(objectDto));
        assertEquals("Object", jsonObject.getJSONObject("data").getString("type"));
    }

    @Test
    public void testIfAttributesExists() {
        JSONObject jsonObject = new JSONObject(JsonApiConverter.convert(objectDto));
        assertNotNull(jsonObject.getJSONObject("data").getJSONObject("attributes"));
    }

    @Test
    public void testIfAttributeNameWorks() {
        JSONObject jsonObject = new JSONObject(JsonApiConverter.convert(objectDto));
        assertEquals(objectDto.getName(),
                jsonObject.getJSONObject("data").getJSONObject("attributes").getString("name"));
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
