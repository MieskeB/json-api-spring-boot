package nl.michelbijnen.jsonapi.test;

import nl.michelbijnen.jsonapi.JsonApiConverter;
import nl.michelbijnen.jsonapi.test.mock.MockDataGenerator;
import nl.michelbijnen.jsonapi.test.mock.ObjectDto;
import nl.michelbijnen.jsonapi.test.mock.UserDto;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class ListTest {

    private List<ObjectDto> objectDtos;

    @Before
    public void before() throws CloneNotSupportedException {
        MockDataGenerator generator = MockDataGenerator.getInstance();
        this.objectDtos = new ArrayList<>();
        this.objectDtos.add((ObjectDto) generator.getObjectDto().clone());
        this.objectDtos.add((ObjectDto) generator.getObjectDto().clone());
    }

    @Test
    public void testIfLinksExists() throws Exception {
        JSONObject jsonObject = new JSONObject(JsonApiConverter.convert(objectDtos));
        assertNotNull(jsonObject.getJSONObject("links"));
    }

    @Test
    public void testIfDataExists() throws Exception {
        JSONObject jsonObject = new JSONObject(JsonApiConverter.convert(objectDtos));
        assertNotNull(jsonObject.getJSONArray("data"));
    }

    @Test
    public void testIfDataContainsId() throws Exception {
        JSONObject jsonObject = new JSONObject(JsonApiConverter.convert(objectDtos));
        assertEquals(objectDtos.get(0).getId(), jsonObject.getJSONArray("data").getJSONObject(0).getString("id"));
        assertEquals(objectDtos.get(1).getId(), jsonObject.getJSONArray("data").getJSONObject(1).getString("id"));
    }

    @Test
    public void testIfDataContainsType() throws Exception {
        JSONObject jsonObject = new JSONObject(JsonApiConverter.convert(objectDtos));
        assertEquals("Object", jsonObject.getJSONArray("data").getJSONObject(0).getString("type"));
        assertEquals("Object", jsonObject.getJSONArray("data").getJSONObject(1).getString("type"));
    }
}
