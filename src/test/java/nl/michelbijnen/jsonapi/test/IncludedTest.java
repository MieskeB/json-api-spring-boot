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

public class IncludedTest {

    private ObjectDto objectDto;
    private UserDto userDto;

    @Before
    public void before() {
        MockDataGenerator generator = MockDataGenerator.getInstance();
        this.objectDto = generator.getObjectDto();
        this.userDto = generator.getUserDto();
    }

    @Test
    public void testIfIncludedExists() throws Exception {
        JsonApiConverter converter = new JsonApiConverter(objectDto);
        JSONObject jsonObject = new JSONObject(converter.convert());
        assertNotNull(jsonObject.getJSONObject("data").getJSONArray("included"));
    }

    @Test
    public void testIfIncludedIdWorks() throws Exception {
        JsonApiConverter converter = new JsonApiConverter(objectDto);
        JSONObject jsonObject = new JSONObject(converter.convert());
        assertEquals(objectDto.getOwner().getId(), jsonObject.getJSONObject("data").getJSONArray("included").getJSONObject(0).getString("id"));
    }

    @Test
    public void testIfIncludedTypeWorks() throws Exception {
        JsonApiConverter converter = new JsonApiConverter(objectDto);
        JSONObject jsonObject = new JSONObject(converter.convert());
        assertEquals("User", jsonObject.getJSONObject("data").getJSONArray("included").getJSONObject(0).getString("type"));
    }

    //region attributes

    @Test
    public void testIfIncludedAttributesExists() throws Exception {
        JsonApiConverter converter = new JsonApiConverter(objectDto);
        JSONObject jsonObject = new JSONObject(converter.convert());
        assertNotNull(jsonObject.getJSONObject("data").getJSONArray("included").getJSONObject(0).getJSONObject("attributes"));
    }

    @Test
    public void testIfIncludedAttributesUsernameWorks() throws Exception {
        JsonApiConverter converter = new JsonApiConverter(objectDto);
        JSONObject jsonObject = new JSONObject(converter.convert());
        assertEquals(objectDto.getOwner().getUsername(), jsonObject.getJSONObject("data").getJSONArray("included").getJSONObject(0).getJSONObject("attributes").getString("username"));
    }

    @Test
    public void testIfIncludedAttributesEmailWorks() throws Exception {
        JsonApiConverter converter = new JsonApiConverter(objectDto);
        JSONObject jsonObject = new JSONObject(converter.convert());
        assertEquals(objectDto.getOwner().getEmail(), jsonObject.getJSONObject("data").getJSONArray("included").getJSONObject(0).getJSONObject("attributes").getString("email"));
    }

    //endregion

    //region links

    @Test
    public void testIfIncludedLinksExists() throws Exception {
        JsonApiConverter converter = new JsonApiConverter(objectDto);
        JSONObject jsonObject = new JSONObject(converter.convert());
        assertNotNull(jsonObject.getJSONObject("data").getJSONArray("included").getJSONObject(0).getJSONObject("links"));
    }

    @Test
    public void testIfIncludedLinksSelfWorks() throws Exception {
        JsonApiConverter converter = new JsonApiConverter(objectDto);
        JSONObject jsonObject = new JSONObject(converter.convert());
        assertEquals(objectDto.getOwner().getSelfRel(), jsonObject.getJSONObject("data").getJSONArray("included").getJSONObject(0).getJSONObject("links").getString("self"));
    }

    @Test
    public void testIfIncludedLinksNextWorks() throws Exception {
        JsonApiConverter converter = new JsonApiConverter(objectDto);
        JSONObject jsonObject = new JSONObject(converter.convert());
        assertEquals(objectDto.getOwner().getNextRel(), jsonObject.getJSONObject("data").getJSONArray("included").getJSONObject(0).getJSONObject("links").getString("next"));
    }

    //endregion

    //region relationships

    //region mainObject

    @Test
    public void testIfIncludedRelationshipsExists() throws Exception {
        JsonApiConverter converter = new JsonApiConverter(objectDto);
        JSONObject jsonObject = new JSONObject(converter.convert());
        assertNotNull(jsonObject.getJSONObject("data").getJSONArray("included").getJSONObject(0).getJSONObject("relationships"));
    }

    @Test
    public void testIfIncludedRelationshipsMainObjectExists() throws Exception {
        JsonApiConverter converter = new JsonApiConverter(objectDto);
        JSONObject jsonObject = new JSONObject(converter.convert());
        assertNotNull(jsonObject.getJSONObject("data").getJSONArray("included").getJSONObject(0).getJSONObject("relationships").getJSONObject("mainObject"));
    }

    @Test
    public void testIfIncludedRelationshipsMainObjectDataExists() throws Exception {
        JsonApiConverter converter = new JsonApiConverter(objectDto);
        JSONObject jsonObject = new JSONObject(converter.convert());
        assertNotNull(jsonObject.getJSONObject("data").getJSONArray("included").getJSONObject(0).getJSONObject("relationships").getJSONObject("mainObject").getJSONObject("data"));
    }

    @Test
    public void testIfIncludedRelationshipsMainObjectDataIdWorks() throws Exception {
        JsonApiConverter converter = new JsonApiConverter(objectDto);
        JSONObject jsonObject = new JSONObject(converter.convert());
        assertEquals(objectDto.getOwner().getMainObject().getId(), jsonObject.getJSONObject("data").getJSONArray("included").getJSONObject(0).getJSONObject("relationships").getJSONObject("mainObject").getJSONObject("data").getString("id"));
    }

    @Test
    public void testIfIncludedRelationshipsMainObjectDataTypeWorks() throws Exception {
        JsonApiConverter converter = new JsonApiConverter(objectDto);
        JSONObject jsonObject = new JSONObject(converter.convert());
        assertEquals("Object", jsonObject.getJSONObject("data").getJSONArray("included").getJSONObject(0).getJSONObject("relationships").getJSONObject("mainObject").getJSONObject("data").getString("type"));
    }

    //endregion

    //region childObjects

    //endregion

    //endregion
}
