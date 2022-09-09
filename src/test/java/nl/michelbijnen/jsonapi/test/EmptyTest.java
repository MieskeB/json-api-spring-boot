package nl.michelbijnen.jsonapi.test;

import nl.michelbijnen.jsonapi.parser.JsonApiConverter;
import nl.michelbijnen.jsonapi.test.mock.AppleDto;
import nl.michelbijnen.jsonapi.test.mock.MockDataGenerator;
import nl.michelbijnen.jsonapi.test.mock.ObjectDto;
import nl.michelbijnen.jsonapi.test.mock.UserDto;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class EmptyTest {

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
    public void testIfEmptyObjectWillReturnEmptyObject() {
        ObjectDto test = null;
        JSONObject jsonObject = new JSONObject(JsonApiConverter.convert(test));
        try {
            jsonObject.getJSONObject("data");
        } catch (JSONException e) {
            fail();
        }
    }

    @Test
    public void testIfEmptyListOfObjectsWillReturnEmptyList() {
        List<ObjectDto> tests = new ArrayList<>();
        JSONObject jsonObject = new JSONObject(JsonApiConverter.convert(tests));
        try {
            assertEquals(0, jsonObject.getJSONArray("data").length());
        } catch (JSONException e) {
            fail();
        }
    }

    @Test
    public void testIfEmptyLinksWontGetAdded() {
        objectDto.setSelfRel("");
        objectDto.setAllSelfRel("");
        JSONObject jsonObject = new JSONObject(JsonApiConverter.convert(objectDto));
        assertThrows(JSONException.class, () -> {
            jsonObject.getJSONObject("links");
        });
    }

    @Test
    public void testIfNullLinksWontGetAdded() {
        objectDto.setSelfRel(null);
        objectDto.setAllSelfRel(null);
        JSONObject jsonObject = new JSONObject(JsonApiConverter.convert(objectDto));
        assertThrows(JSONException.class, () -> {
            jsonObject.getJSONObject("links");
        });
    }

    @Test
    public void testIfEmptyRelationWontGetAdded() {
        userDto.setMainObject(null);

        JSONObject jsonObject = new JSONObject(JsonApiConverter.convert(userDto));
        assertThrows(JSONException.class, () -> {
            jsonObject.getJSONObject("data").getJSONObject("relationships").getJSONObject("mainObject");
        });
    }

    @Test
    public void testIfNullListRelationsWontGetAdded() {
        userDto.setChildObjects(null);

        JSONObject jsonObject = new JSONObject(JsonApiConverter.convert(userDto));
        assertThrows(JSONException.class, () -> {
            jsonObject.getJSONObject("data").getJSONObject("relationships").getJSONObject("childObjects");
        });
    }

    @Test
    public void testIfEmptyListRelationsWontGetAdded() {
        userDto.setChildObjects(new ArrayList<>());

        JSONObject jsonObject = new JSONObject(JsonApiConverter.convert(userDto));
        assertThrows(JSONException.class, () -> {
            jsonObject.getJSONObject("data").getJSONObject("relationships").getJSONObject("childObjects");
        });
    }

    @Test
    public void testIfAllRelationsAreEmptyRelationshipsWontGetAdded() {
        userDto.setMainObject(null);
        userDto.setChildObjects(new ArrayList<>());

        JSONObject jsonObject = new JSONObject(JsonApiConverter.convert(userDto));
        assertThrows(JSONException.class, () -> {
            jsonObject.getJSONObject("data").getJSONObject("relationships");
        });
    }

    @Test
    public void testIfAllRelationsAreNullRelationshipsWontGetAdded() {
        userDto.setMainObject(null);
        userDto.setChildObjects(null);

        JSONObject jsonObject = new JSONObject(JsonApiConverter.convert(userDto));
        assertThrows(JSONException.class, () -> {
            jsonObject.getJSONObject("data").getJSONObject("relationships");
        });
    }

    @Test
    public void testIfEmptyRelationWontGetAddedToIncluded() {
        userDto.setMainObject(null);

        JSONObject jsonObject = new JSONObject(JsonApiConverter.convert(userDto));

        assertEquals(2, jsonObject.getJSONArray("included").length());
    }

    @Test
    public void testIfNullListRelationsWontGetAddedToIncluded() {
        userDto.setChildObjects(null);

        JSONObject jsonObject = new JSONObject(JsonApiConverter.convert(userDto));

        assertEquals(1, jsonObject.getJSONArray("included").length());
    }

    @Test
    public void testIfEmptyListRelationsWontGetAddedToIncluded() {
        userDto.setChildObjects(new ArrayList<>());

        JSONObject jsonObject = new JSONObject(JsonApiConverter.convert(userDto));

        assertEquals(1, jsonObject.getJSONArray("included").length());
    }

    @Test
    public void testIfIncludedWontGetAddedWhenAllRelationsEmpty() {
        userDto.setMainObject(null);
        userDto.setChildObjects(new ArrayList<>());

        JSONObject jsonObject = new JSONObject(JsonApiConverter.convert(userDto));

        assertThrows(JSONException.class, () -> {
            jsonObject.getJSONArray("included");
        });
    }

    @Test
    public void testIfIncludedWontGetAddedWhenAllRelationsNull() {
        userDto.setMainObject(null);
        userDto.setChildObjects(null);

        JSONObject jsonObject = new JSONObject(JsonApiConverter.convert(userDto));

        assertThrows(JSONException.class, () -> {
            jsonObject.getJSONArray("included");
        });
    }

    @Test
    public void testIfEmptyPropertyWillGetAdded() {
        userDto.setUsername("");

        JSONObject jsonObject = new JSONObject(JsonApiConverter.convert(userDto));

        try {
            jsonObject.getJSONObject("data").getJSONObject("attributes").getString("username");
        } catch (JSONException e) {
            fail();
        }
    }

    @Test
    public void testIfnullPropertyWontGetAdded() {
        userDto.setUsername(null);

        JSONObject jsonObject = new JSONObject(JsonApiConverter.convert(userDto));

        assertThrows(JSONException.class, () -> {
            jsonObject.getJSONObject("data").getJSONObject("attributes").getString("username");
        });
    }
}
