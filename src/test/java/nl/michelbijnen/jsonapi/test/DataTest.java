package nl.michelbijnen.jsonapi.test;

import nl.michelbijnen.jsonapi.parser.JsonApiConverter;
import nl.michelbijnen.jsonapi.test.mock.MockDataGenerator;
import nl.michelbijnen.jsonapi.test.mock.ObjectDto;
import nl.michelbijnen.jsonapi.test.mock.UserDto;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class DataTest {

    private ObjectDto objectDto;
    private UserDto userDto;

    @Before
    public void before() throws CloneNotSupportedException {
        MockDataGenerator generator = MockDataGenerator.getInstance();
        this.objectDto = (ObjectDto) generator.getObjectDto().clone();
        this.userDto = (UserDto) generator.getUserDto().clone();
    }

    @Test
    public void testIfDataExists() {
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
        assertEquals(objectDto.getName(), jsonObject.getJSONObject("data").getJSONObject("attributes").getString("name"));
    }
}
