package nl.michelbijnen.jsonapi.test;

import nl.michelbijnen.jsonapi.parser.JsonApiConverter;
import nl.michelbijnen.jsonapi.test.mock.MockDataGenerator;
import nl.michelbijnen.jsonapi.test.mock.ObjectDto;
import nl.michelbijnen.jsonapi.test.mock.UserDto;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class RelationTest {

    private MockDataGenerator generator;

    private ObjectDto objectDto;
    private UserDto userDto;

    @Before
    public void before() throws CloneNotSupportedException {
        this.generator = MockDataGenerator.getInstance();
        this.objectDto = (ObjectDto) generator.getObjectDto().clone();
        this.userDto = (UserDto) generator.getUserDto().clone();
    }

    @Test
    public void testIfRelationshipExists() {
        JSONObject jsonObject = new JSONObject(JsonApiConverter.convert(objectDto));
        assertNotNull(jsonObject.getJSONObject("data").getJSONObject("relationships"));
    }

    @Test
    public void testIfRelationshipCanBeNull() {
        objectDto.setOwner(null);
        JSONObject jsonObject = new JSONObject(JsonApiConverter.convert(objectDto));
        assertThrows(JSONException.class, () -> jsonObject.getJSONObject("data").getJSONObject("relationships").getJSONObject("Owner"));
        objectDto = this.generator.getObjectDto();
    }

    @Test
    public void testIfRelationshipOwnerExists() {
        JSONObject jsonObject = new JSONObject(JsonApiConverter.convert(objectDto));
        assertNotNull(jsonObject.getJSONObject("data").getJSONObject("relationships").getJSONObject("Owner"));
    }

    @Test
    public void testIfRelationshipOwnerDataExists() {
        JSONObject jsonObject = new JSONObject(JsonApiConverter.convert(objectDto));
        assertNotNull(jsonObject.getJSONObject("data").getJSONObject("relationships").getJSONObject("Owner").getJSONObject("data"));
    }

    @Test
    public void testIfRelationshipOwnerDataIdWorks() {
        JSONObject jsonObject = new JSONObject(JsonApiConverter.convert(objectDto));
        assertEquals(objectDto.getOwner().getId(), jsonObject.getJSONObject("data").getJSONObject("relationships").getJSONObject("Owner").getJSONObject("data").getString("id"));
    }

    @Test
    public void testIfRelationshipOwnerDataTypeWorks() {
        JSONObject jsonObject = new JSONObject(JsonApiConverter.convert(objectDto));
        assertEquals("User", jsonObject.getJSONObject("data").getJSONObject("relationships").getJSONObject("Owner").getJSONObject("data").getString("type"));
    }

    @Test
    public void testIfRelationshipOwnerLinksExists() {
        JSONObject jsonObject = new JSONObject(JsonApiConverter.convert(objectDto));
        assertNotNull(jsonObject.getJSONObject("data").getJSONObject("relationships").getJSONObject("Owner").getJSONObject("links"));
    }

    @Test
    public void testIfRelationshipOwnerLinksSelfWorks() {
        JSONObject jsonObject = new JSONObject(JsonApiConverter.convert(objectDto));
        assertEquals(objectDto.getOwnerSelfRel(), jsonObject.getJSONObject("data").getJSONObject("relationships").getJSONObject("Owner").getJSONObject("links").getString("self"));
    }

    @Test
    public void testIfRelationshipOwnerLinksRelatedWorks() {
        JSONObject jsonObject = new JSONObject(JsonApiConverter.convert(objectDto));
        assertEquals(objectDto.getOwnerRelatedRel(), jsonObject.getJSONObject("data").getJSONObject("relationships").getJSONObject("Owner").getJSONObject("links").getString("related"));
    }
}
