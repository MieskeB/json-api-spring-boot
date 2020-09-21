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

public class UserTest {

    private UserDto userDto;

    @Before
    public void before() {
        MockDataGenerator generator = MockDataGenerator.getInstance();
        this.userDto = generator.getUserDto();
    }

    @Test
    public void testIfLinksExists() throws Exception {
        JsonApiConverter converter = new JsonApiConverter(this.userDto);
        JSONObject jsonObject = new JSONObject(converter.convert());
        assertNotNull(jsonObject.getJSONObject("links"));
    }

    @Test
    public void testIfDataExists() throws Exception {
        JsonApiConverter converter = new JsonApiConverter(this.userDto);
        JSONObject jsonObject = new JSONObject(converter.convert());
        assertNotNull(jsonObject.getJSONObject("data"));
    }

    @Test
    public void testIfDataContainsId() throws Exception {
        JsonApiConverter converter = new JsonApiConverter(this.userDto);
        JSONObject jsonObject = new JSONObject(converter.convert());
        assertEquals(userDto.getId(), jsonObject.getJSONObject("data").getString("id"));
    }

    @Test
    public void testIfDataContainsType() throws Exception {
        JsonApiConverter converter = new JsonApiConverter(this.userDto);
        JSONObject jsonObject = new JSONObject(converter.convert());
        assertEquals("User", jsonObject.getJSONObject("data").getString("type"));
    }
}
