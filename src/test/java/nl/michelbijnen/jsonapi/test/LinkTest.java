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
    public void before() {
        MockDataGenerator generator = MockDataGenerator.getInstance();
        this.objectDto = generator.getObjectDto();
        this.userDto = generator.getUserDto();
    }

    @Test
    public void testIfLinksAreCreated() throws Exception {
        JsonApiConverter converter = new JsonApiConverter(objectDto);
        JSONObject jsonObject = new JSONObject(converter.convert());
        assertNotNull(jsonObject.getJSONObject("links"));
    }

    @Test
    public void testIfFirstRelWorks() throws Exception {
        JsonApiConverter converter = new JsonApiConverter(objectDto);
        JSONObject jsonObject = new JSONObject(converter.convert());
        assertEquals(objectDto.getFirstRel(), jsonObject.getJSONObject("links").getString("first"));
    }

    @Test
    public void testIfPreviousRelWorks() throws Exception {
        JsonApiConverter converter = new JsonApiConverter(objectDto);
        JSONObject jsonObject = new JSONObject(converter.convert());
        assertEquals(objectDto.getPreviousRel(), jsonObject.getJSONObject("links").getString("previous"));
    }

    @Test
    public void testIfSelfRelWorks() throws Exception {
        JsonApiConverter converter = new JsonApiConverter(objectDto);
        JSONObject jsonObject = new JSONObject(converter.convert());
        assertEquals(objectDto.getSelfRel(), jsonObject.getJSONObject("links").getString("self"));
    }

    @Test
    public void testIfNextRelWorks() throws Exception {
        JsonApiConverter converter = new JsonApiConverter(objectDto);
        JSONObject jsonObject = new JSONObject(converter.convert());
        assertEquals(objectDto.getNextRel(), jsonObject.getJSONObject("links").getString("next"));
    }

    @Test
    public void testIfLastRelWorks() throws Exception {
        JsonApiConverter converter = new JsonApiConverter(objectDto);
        JSONObject jsonObject = new JSONObject(converter.convert());
        assertEquals(objectDto.getLastRel(), jsonObject.getJSONObject("links").getString("last"));
    }
}
