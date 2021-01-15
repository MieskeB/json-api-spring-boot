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

public class LinkTest {

    private ObjectDto objectDto;
    private UserDto userDto;

    @Before
    public void before() throws CloneNotSupportedException {
        MockDataGenerator generator = MockDataGenerator.getInstance();
        this.objectDto = (ObjectDto) generator.getObjectDto().clone();
        this.userDto = (UserDto) generator.getUserDto().clone();
    }

    @Test
    public void testIfLinksExists() throws Exception {
        JSONObject jsonObject = new JSONObject(JsonApiConverter.convert(objectDto));
        assertNotNull(jsonObject.getJSONObject("links"));
    }

    @Test
    public void testIfFirstRelWorks() throws Exception {
        JSONObject jsonObject = new JSONObject(JsonApiConverter.convert(objectDto));
        assertEquals(objectDto.getFirstRel(), jsonObject.getJSONObject("links").getString("first"));
    }

    @Test
    public void testIfPreviousRelWorks() throws Exception {
        JSONObject jsonObject = new JSONObject(JsonApiConverter.convert(objectDto));
        assertEquals(objectDto.getPreviousRel(), jsonObject.getJSONObject("links").getString("previous"));
    }

    @Test
    public void testIfSelfRelWorks() throws Exception {
        JSONObject jsonObject = new JSONObject(JsonApiConverter.convert(objectDto));
        assertEquals(objectDto.getSelfRel(), jsonObject.getJSONObject("links").getString("self"));
    }

    @Test
    public void testIfNextRelWorks() throws Exception {
        JSONObject jsonObject = new JSONObject(JsonApiConverter.convert(objectDto));
        assertEquals(objectDto.getNextRel(), jsonObject.getJSONObject("links").getString("next"));
    }

    @Test
    public void testIfLastRelWorks() throws Exception {
        JSONObject jsonObject = new JSONObject(JsonApiConverter.convert(objectDto));
        assertEquals(objectDto.getLastRel(), jsonObject.getJSONObject("links").getString("last"));
    }
}
