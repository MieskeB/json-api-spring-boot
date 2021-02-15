package nl.michelbijnen.jsonapi.test;

import nl.michelbijnen.jsonapi.parser.JsonApiConverter;
import nl.michelbijnen.jsonapi.test.mock.AppleDto;
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
    private AppleDto appleDto;

    @Before
    public void before() throws CloneNotSupportedException {
        MockDataGenerator generator = MockDataGenerator.getInstance();
        this.objectDto = (ObjectDto) generator.getObjectDto().clone();
        this.userDto = (UserDto) generator.getUserDto().clone();
        this.appleDto = (AppleDto) generator.getAppleDto().clone();
    }

    @Test
    public void testIfIncludedExists() {
        JSONObject jsonObject = new JSONObject(JsonApiConverter.convert(objectDto));
        assertNotNull(jsonObject.getJSONArray("included"));
    }

    @Test
    public void testIfIncludedIdWorks() {
        JSONObject jsonObject = new JSONObject(JsonApiConverter.convert(objectDto));
        assertEquals(objectDto.getOwner().getId(), jsonObject.getJSONArray("included").getJSONObject(0).getString("id"));
    }

    @Test
    public void testIfIncludedTypeWorks() {
        JSONObject jsonObject = new JSONObject(JsonApiConverter.convert(objectDto));
        assertEquals("User", jsonObject.getJSONArray("included").getJSONObject(0).getString("type"));
    }

    //region attributes

    @Test
    public void testIfIncludedAttributesExists() {
        JSONObject jsonObject = new JSONObject(JsonApiConverter.convert(objectDto));
        assertNotNull(jsonObject.getJSONArray("included").getJSONObject(0).getJSONObject("attributes"));
    }

    @Test
    public void testIfIncludedAttributesUsernameWorks() {
        JSONObject jsonObject = new JSONObject(JsonApiConverter.convert(objectDto));
        assertEquals(objectDto.getOwner().getUsername(), jsonObject.getJSONArray("included").getJSONObject(0).getJSONObject("attributes").getString("username"));
    }

    @Test
    public void testIfIncludedAttributesEmailWorks() {
        JSONObject jsonObject = new JSONObject(JsonApiConverter.convert(objectDto));
        assertEquals(objectDto.getOwner().getEmail(), jsonObject.getJSONArray("included").getJSONObject(0).getJSONObject("attributes").getString("email"));
    }

    //endregion

    //region links

    @Test
    public void testIfIncludedLinksExists() {
        JSONObject jsonObject = new JSONObject(JsonApiConverter.convert(objectDto));
        assertNotNull(jsonObject.getJSONArray("included").getJSONObject(0).getJSONObject("links"));
    }

    @Test
    public void testIfIncludedLinksSelfWorks() {
        JSONObject jsonObject = new JSONObject(JsonApiConverter.convert(objectDto));
        assertEquals(objectDto.getOwner().getSelfRel(), jsonObject.getJSONArray("included").getJSONObject(0).getJSONObject("links").getString("self"));
    }

    @Test
    public void testIfIncludedLinksNextWorks() {
        JSONObject jsonObject = new JSONObject(JsonApiConverter.convert(objectDto));
        assertEquals(objectDto.getOwner().getNextRel(), jsonObject.getJSONArray("included").getJSONObject(0).getJSONObject("links").getString("next"));
    }

    //endregion

    //region relationships

    @Test
    public void testIfIncludedRelationshipsExists() {
        JSONObject jsonObject = new JSONObject(JsonApiConverter.convert(objectDto));
        assertNotNull(jsonObject.getJSONArray("included").getJSONObject(0).getJSONObject("relationships"));
    }

    //region mainObject

    @Test
    public void testIfIncludedRelationshipsMainObjectExists() {
        JSONObject jsonObject = new JSONObject(JsonApiConverter.convert(objectDto));
        assertNotNull(jsonObject.getJSONArray("included").getJSONObject(0).getJSONObject("relationships").getJSONObject("mainObject"));
    }

    @Test
    public void testIfIncludedRelationshipsMainObjectDataExists() {
        JSONObject jsonObject = new JSONObject(JsonApiConverter.convert(objectDto));
        assertNotNull(jsonObject.getJSONArray("included").getJSONObject(0).getJSONObject("relationships").getJSONObject("mainObject").getJSONObject("data"));
    }

    @Test
    public void testIfIncludedRelationshipsMainObjectDataIdWorks() {
        JSONObject jsonObject = new JSONObject(JsonApiConverter.convert(objectDto));
        assertEquals(objectDto.getOwner().getMainObject().getId(), jsonObject.getJSONArray("included").getJSONObject(0).getJSONObject("relationships").getJSONObject("mainObject").getJSONObject("data").getString("id"));
    }

    @Test
    public void testIfIncludedRelationshipsMainObjectDataTypeWorks() {
        JSONObject jsonObject = new JSONObject(JsonApiConverter.convert(objectDto));
        assertEquals("Object", jsonObject.getJSONArray("included").getJSONObject(0).getJSONObject("relationships").getJSONObject("mainObject").getJSONObject("data").getString("type"));
    }

    //endregion

    //region childObjects

    @Test
    public void testIfIncludedRelationshipsChildObjectsExists() {
        JSONObject jsonObject = new JSONObject(JsonApiConverter.convert(objectDto));
        assertNotNull(jsonObject.getJSONArray("included").getJSONObject(0).getJSONObject("relationships").getJSONObject("childObjects"));
    }

    @Test
    public void testIfIncludedRelationshipsChildObjectsDataExists() {
        JSONObject jsonObject = new JSONObject(JsonApiConverter.convert(objectDto));
        assertNotNull(jsonObject.getJSONArray("included").getJSONObject(0).getJSONObject("relationships").getJSONObject("childObjects").getJSONArray("data"));
    }

    @Test
    public void testIfIncludedRelationshipsChildObjectsDataIdWorks() {
        JSONObject jsonObject = new JSONObject(JsonApiConverter.convert(objectDto));
        JSONArray childObjects = jsonObject.getJSONArray("included").getJSONObject(0).getJSONObject("relationships").getJSONObject("childObjects").getJSONArray("data");
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
    public void testIfIncludedRelationshipsChildObjectsDataTypeWorks() {
        JSONObject jsonObject = new JSONObject(JsonApiConverter.convert(objectDto));
        JSONArray childObjects = jsonObject.getJSONArray("included").getJSONObject(0).getJSONObject("relationships").getJSONObject("childObjects").getJSONArray("data");
        for (int i = 0; i < childObjects.length(); i++) {
            assertEquals("Object", childObjects.getJSONObject(i).getString("type"));
        }
    }

    //endregion

    //region depth

    @Test
    public void testIfDepthIsWorking() {
        JSONObject jsonObject = new JSONObject(JsonApiConverter.convert(userDto, 2));
        JSONArray includedObjects = jsonObject.getJSONArray("included");
        for (int i = 0; i < includedObjects.length(); i++) {
            JSONObject included = includedObjects.getJSONObject(i);
            if (included.getString("id").equals(this.appleDto.getId())) {
                return;
            }
        }
        fail();
    }

    @Test
    public void testIfDepthIsCapping() {
        JSONObject jsonObject = new JSONObject(JsonApiConverter.convert(userDto, 1));
        JSONArray includedObjects = jsonObject.getJSONArray("included");
        for (int i = 0; i < includedObjects.length(); i++) {
            JSONObject included = includedObjects.getJSONObject(i);
            if (included.getString("id").equals(this.appleDto.getId())) {
                fail();
            }
        }
    }

    //endregion

    //endregion
}
