package nl.michelbijnen.jsonapi.test;

import nl.michelbijnen.jsonapi.JsonApiConverter;
import nl.michelbijnen.jsonapi.test.mock.MockDataGenerator;
import nl.michelbijnen.jsonapi.test.mock.ObjectDto;
import nl.michelbijnen.jsonapi.test.mock.UserDto;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class UserTest {

    private UserDto userDto;

    @Before
    public void before() throws CloneNotSupportedException {
        MockDataGenerator generator = MockDataGenerator.getInstance();
        this.userDto = (UserDto) generator.getUserDto().clone();
    }

    @Test
    public void testIfLinksExists() throws Exception {
        JSONObject jsonObject = new JSONObject(JsonApiConverter.convert(userDto));
        assertNotNull(jsonObject.getJSONObject("links"));
    }

    @Test
    public void testIfDataExists() throws Exception {
        JSONObject jsonObject = new JSONObject(JsonApiConverter.convert(userDto));
        assertNotNull(jsonObject.getJSONObject("data"));
    }

    @Test
    public void testIfDataContainsId() throws Exception {
        JSONObject jsonObject = new JSONObject(JsonApiConverter.convert(userDto));
        assertEquals(userDto.getId(), jsonObject.getJSONObject("data").getString("id"));
    }

    @Test
    public void testIfDataContainsType() throws Exception {
        JSONObject jsonObject = new JSONObject(JsonApiConverter.convert(userDto));
        assertEquals("User", jsonObject.getJSONObject("data").getString("type"));
    }

    @Test
    public void testIfEmptyArrayWillWork() throws Exception {
        String result = JsonApiConverter.convert(userDto);
        assertEquals("{\"data\":[]}", result);
    }
}
