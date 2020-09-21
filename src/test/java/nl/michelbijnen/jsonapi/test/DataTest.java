package nl.michelbijnen.jsonapi.test;

import nl.michelbijnen.jsonapi.JsonApiConverter;
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
    public void before() {
        MockDataGenerator generator = MockDataGenerator.getInstance();
        this.objectDto = generator.getObjectDto();
        this.userDto = generator.getUserDto();
    }

    @Test
    public void testIfDataExists() throws Exception {
        JsonApiConverter converter = new JsonApiConverter(objectDto);
        JSONObject jsonObject = new JSONObject(converter.convert());
        assertNotNull(jsonObject.getJSONObject("data"));
    }

    @Test
    public void testIfIdWorks() throws Exception {
        JsonApiConverter converter = new JsonApiConverter(objectDto);
        JSONObject jsonObject = new JSONObject(converter.convert());
        assertEquals(objectDto.getId(), jsonObject.getJSONObject("data").getString("id"));
    }

    @Test
    public void testIfTypeWorks() throws Exception {
        JsonApiConverter converter = new JsonApiConverter(objectDto);
        JSONObject jsonObject = new JSONObject(converter.convert());
        assertEquals("Object", jsonObject.getJSONObject("data").getString("type"));
    }

    @Test
    public void testIfAttributesExists() throws Exception {
        JsonApiConverter converter = new JsonApiConverter(objectDto);
        JSONObject jsonObject = new JSONObject(converter.convert());
        assertNotNull(jsonObject.getJSONObject("data").getJSONObject("attributes"));
    }

    @Test
    public void testIfAttributeNameWorks() throws Exception {
        JsonApiConverter converter = new JsonApiConverter(objectDto);
        JSONObject jsonObject = new JSONObject(converter.convert());
        assertEquals(objectDto.getName(), jsonObject.getJSONObject("data").getJSONObject("attributes").getString("name"));
    }
}
