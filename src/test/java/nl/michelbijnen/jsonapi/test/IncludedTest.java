package nl.michelbijnen.jsonapi.test;

import nl.michelbijnen.jsonapi.parser.JsonApiConverter;
import nl.michelbijnen.jsonapi.test.mock.MockDataGenerator;
import nl.michelbijnen.jsonapi.test.mock.ObjectDto;
import nl.michelbijnen.jsonapi.test.mock.UserDto;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class IncludedTest {

    private ObjectDto objectDto;
    private UserDto userDto;

    @Before
    public void before() throws CloneNotSupportedException {
        MockDataGenerator generator = MockDataGenerator.getInstance();
        this.objectDto = (ObjectDto) generator.getObjectDto().clone();
        this.userDto = (UserDto) generator.getUserDto().clone();
    }

    @Test
    public void testIfIncludedExists() throws Exception {
        JSONObject jsonObject = new JSONObject(JsonApiConverter.convert(objectDto));
        assertNotNull(jsonObject.getJSONObject("data").getJSONArray("included"));
    }

    @Test
    public void testIfIncludedIdWorks() throws Exception {
        JSONObject jsonObject = new JSONObject(JsonApiConverter.convert(objectDto));
        assertEquals(objectDto.getOwner().getId(), jsonObject.getJSONObject("data").getJSONArray("included").getJSONObject(0).getString("id"));
    }

    @Test
    public void testIfIncludedTypeWorks() throws Exception {
        JSONObject jsonObject = new JSONObject(JsonApiConverter.convert(objectDto));
        assertEquals("User", jsonObject.getJSONObject("data").getJSONArray("included").getJSONObject(0).getString("type"));
    }

    //region attributes

    @Test
    public void testIfIncludedAttributesExists() throws Exception {
        JSONObject jsonObject = new JSONObject(JsonApiConverter.convert(objectDto));
        assertNotNull(jsonObject.getJSONObject("data").getJSONArray("included").getJSONObject(0).getJSONObject("attributes"));
    }

    @Test
    public void testIfIncludedAttributesUsernameWorks() throws Exception {
        JSONObject jsonObject = new JSONObject(JsonApiConverter.convert(objectDto));
        assertEquals(objectDto.getOwner().getUsername(), jsonObject.getJSONObject("data").getJSONArray("included").getJSONObject(0).getJSONObject("attributes").getString("username"));
    }

    @Test
    public void testIfIncludedAttributesEmailWorks() throws Exception {
        JSONObject jsonObject = new JSONObject(JsonApiConverter.convert(objectDto));
        assertEquals(objectDto.getOwner().getEmail(), jsonObject.getJSONObject("data").getJSONArray("included").getJSONObject(0).getJSONObject("attributes").getString("email"));
    }

    //endregion

    //region links

    @Test
    public void testIfIncludedLinksExists() throws Exception {
        JSONObject jsonObject = new JSONObject(JsonApiConverter.convert(objectDto));
        assertNotNull(jsonObject.getJSONObject("data").getJSONArray("included").getJSONObject(0).getJSONObject("links"));
    }

    @Test
    public void testIfIncludedLinksSelfWorks() throws Exception {
        JSONObject jsonObject = new JSONObject(JsonApiConverter.convert(objectDto));
        assertEquals(objectDto.getOwner().getSelfRel(), jsonObject.getJSONObject("data").getJSONArray("included").getJSONObject(0).getJSONObject("links").getString("self"));
    }

    @Test
    public void testIfIncludedLinksNextWorks() throws Exception {
        JSONObject jsonObject = new JSONObject(JsonApiConverter.convert(objectDto));
        assertEquals(objectDto.getOwner().getNextRel(), jsonObject.getJSONObject("data").getJSONArray("included").getJSONObject(0).getJSONObject("links").getString("next"));
    }

    //endregion

    //region relationships

    @Test
    public void testIfIncludedRelationshipsExists() throws Exception {
        JSONObject jsonObject = new JSONObject(JsonApiConverter.convert(objectDto));
        assertNotNull(jsonObject.getJSONObject("data").getJSONArray("included").getJSONObject(0).getJSONObject("relationships"));
    }

    //region mainObject

    @Test
    public void testIfIncludedRelationshipsMainObjectExists() throws Exception {
        JSONObject jsonObject = new JSONObject(JsonApiConverter.convert(objectDto));
        assertNotNull(jsonObject.getJSONObject("data").getJSONArray("included").getJSONObject(0).getJSONObject("relationships").getJSONObject("mainObject"));
    }

    @Test
    public void testIfIncludedRelationshipsMainObjectDataExists() throws Exception {
        JSONObject jsonObject = new JSONObject(JsonApiConverter.convert(objectDto));
        assertNotNull(jsonObject.getJSONObject("data").getJSONArray("included").getJSONObject(0).getJSONObject("relationships").getJSONObject("mainObject").getJSONObject("data"));
    }

    @Test
    public void testIfIncludedRelationshipsMainObjectDataIdWorks() throws Exception {
        JSONObject jsonObject = new JSONObject(JsonApiConverter.convert(objectDto));
        assertEquals(objectDto.getOwner().getMainObject().getId(), jsonObject.getJSONObject("data").getJSONArray("included").getJSONObject(0).getJSONObject("relationships").getJSONObject("mainObject").getJSONObject("data").getString("id"));
    }

    @Test
    public void testIfIncludedRelationshipsMainObjectDataTypeWorks() throws Exception {
        JSONObject jsonObject = new JSONObject(JsonApiConverter.convert(objectDto));
        assertEquals("Object", jsonObject.getJSONObject("data").getJSONArray("included").getJSONObject(0).getJSONObject("relationships").getJSONObject("mainObject").getJSONObject("data").getString("type"));
    }

    //endregion

    //region childObjects

    @Test
    public void testIfIncludedRelationshipsChildObjectsExists() throws Exception {
        JSONObject jsonObject = new JSONObject(JsonApiConverter.convert(objectDto));
        assertNotNull(jsonObject.getJSONObject("data").getJSONArray("included").getJSONObject(0).getJSONObject("relationships").getJSONObject("childObjects"));
    }

    @Test
    public void testIfIncludedRelationshipsChildObjectsDataExists() throws Exception {
        JSONObject jsonObject = new JSONObject(JsonApiConverter.convert(objectDto));
        assertNotNull(jsonObject.getJSONObject("data").getJSONArray("included").getJSONObject(0).getJSONObject("relationships").getJSONObject("childObjects").getJSONArray("data"));
    }

    @Test
    public void testIfIncludedRelationshipsChildObjectsDataIdWorks() throws Exception {
        JSONObject jsonObject = new JSONObject(JsonApiConverter.convert(objectDto));
        JSONArray childObjects = jsonObject.getJSONObject("data").getJSONArray("included").getJSONObject(0).getJSONObject("relationships").getJSONObject("childObjects").getJSONArray("data");
        {
            boolean found = false;
            for (int i = 0; i < childObjects.length(); i++) {
                if (childObjects.getJSONObject(i).getString("id").equals(objectDto.getOwner().getChildObjects().get(0).getId())) {
                    found = true;
                    break;
                }
            }
            if (!found) fail();
        }
        {
            boolean found = false;
            for (int i = 0; i < childObjects.length(); i++) {
                if (childObjects.getJSONObject(i).getString("id").equals(objectDto.getOwner().getChildObjects().get(1).getId())) {
                    found = true;
                    break;
                }
            }
            if (!found) fail();
        }
    }

    @Test
    public void testIfIncludedRelationshipsChildObjectsDataTypeWorks() throws Exception {
        JSONObject jsonObject = new JSONObject(JsonApiConverter.convert(objectDto));
        JSONArray childObjects = jsonObject.getJSONObject("data").getJSONArray("included").getJSONObject(0).getJSONObject("relationships").getJSONObject("childObjects").getJSONArray("data");
        for (int i = 0; i < childObjects.length(); i++) {
            assertEquals("Object", childObjects.getJSONObject(i).getString("type"));
        }
    }

    //endregion

    //endregion
}
