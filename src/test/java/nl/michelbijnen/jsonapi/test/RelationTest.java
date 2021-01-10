package nl.michelbijnen.jsonapi.test;

import nl.michelbijnen.jsonapi.JsonApiConverter;
import nl.michelbijnen.jsonapi.test.mock.MockDataGenerator;
import nl.michelbijnen.jsonapi.test.mock.ObjectDto;
import nl.michelbijnen.jsonapi.test.mock.UserDto;
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
    public void testIfRelationshipExists() throws Exception {
        JsonApiConverter converter = new JsonApiConverter(objectDto);
        JSONObject jsonObject = new JSONObject(converter.convert());
        assertNotNull(jsonObject.getJSONObject("data").getJSONObject("relationships"));
    }

    @Test
    public void testIfRelationshipCanBeNull() throws Exception {
        objectDto.setOwner(null);
        JsonApiConverter converter = new JsonApiConverter(objectDto);
        JSONObject jsonObject = new JSONObject(converter.convert());
        assertEquals("{}", jsonObject.getJSONObject("data").getJSONObject("relationships").getJSONObject("Owner").getJSONObject("data").toString());
        objectDto = this.generator.getObjectDto();
    }

    @Test
    public void testIfRelationshipOwnerExists() throws Exception {
        JsonApiConverter converter = new JsonApiConverter(objectDto);
        JSONObject jsonObject = new JSONObject(converter.convert());
        assertNotNull(jsonObject.getJSONObject("data").getJSONObject("relationships").getJSONObject("Owner"));
    }

    @Test
    public void testIfRelationshipOwnerDataExists() throws Exception {
        JsonApiConverter converter = new JsonApiConverter(objectDto);
        JSONObject jsonObject = new JSONObject(converter.convert());
        assertNotNull(jsonObject.getJSONObject("data").getJSONObject("relationships").getJSONObject("Owner").getJSONObject("data"));
    }

    @Test
    public void testIfRelationshipOwnerDataIdWorks() throws Exception {
        JsonApiConverter converter = new JsonApiConverter(objectDto);
        JSONObject jsonObject = new JSONObject(converter.convert());
        assertEquals(objectDto.getOwner().getId(), jsonObject.getJSONObject("data").getJSONObject("relationships").getJSONObject("Owner").getJSONObject("data").getString("id"));
    }

    @Test
    public void testIfRelationshipOwnerDataTypeWorks() throws Exception {
        JsonApiConverter converter = new JsonApiConverter(objectDto);
        JSONObject jsonObject = new JSONObject(converter.convert());
        System.out.println(jsonObject);
        assertEquals("User", jsonObject.getJSONObject("data").getJSONObject("relationships").getJSONObject("Owner").getJSONObject("data").getString("type"));
    }

    @Test
    public void testIfRelationshipOwnerLinksExists() throws Exception {
        JsonApiConverter converter = new JsonApiConverter(objectDto);
        JSONObject jsonObject = new JSONObject(converter.convert());
        assertNotNull(jsonObject.getJSONObject("data").getJSONObject("relationships").getJSONObject("Owner").getJSONObject("links"));
    }

    @Test
    public void testIfRelationshipOwnerLinksSelfWorks() throws Exception {
        JsonApiConverter converter = new JsonApiConverter(objectDto);
        JSONObject jsonObject = new JSONObject(converter.convert());
        assertEquals(objectDto.getOwnerSelfRel(), jsonObject.getJSONObject("data").getJSONObject("relationships").getJSONObject("Owner").getJSONObject("links").getString("self"));
    }

    @Test
    public void testIfRelationshipOwnerLinksRelatedWorks() throws Exception {
        JsonApiConverter converter = new JsonApiConverter(objectDto);
        JSONObject jsonObject = new JSONObject(converter.convert());
        assertEquals(objectDto.getOwnerRelatedRel(), jsonObject.getJSONObject("data").getJSONObject("relationships").getJSONObject("Owner").getJSONObject("links").getString("related"));
    }
}
