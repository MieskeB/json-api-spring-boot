package nl.michelbijnen.jsonapi.test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import nl.michelbijnen.jsonapi.parser.JsonApiConverter;
import nl.michelbijnen.jsonapi.test.mock.AppleDto;
import nl.michelbijnen.jsonapi.test.mock.MockDataGenerator;
import nl.michelbijnen.jsonapi.test.mock.ObjectDto;
import nl.michelbijnen.jsonapi.test.mock.UserDto;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class EmptyTest {

    private ObjectDto objectDto;
    private UserDto userDto;
    private ObjectMapper mapper;

    @Before
    public void before() throws CloneNotSupportedException {
        MockDataGenerator generator = MockDataGenerator.getInstance();
        this.objectDto = (ObjectDto) generator.getObjectDto().clone();
        this.userDto = (UserDto) generator.getUserDto().clone();
        this.mapper = new ObjectMapper();
    }

    @Test
    public void testIfEmptyObjectWillReturnEmptyObject() throws Exception {
        ObjectDto test = null;
        JsonNode json = mapper.readTree(JsonApiConverter.convert(test));
        assertNotNull(json.get("data"));
        assertTrue(json.get("data").isObject());
        assertEquals(0, json.get("data").size());
    }

    @Test
    public void testIfEmptyListOfObjectsWillReturnEmptyList() throws Exception {
        List<ObjectDto> tests = new ArrayList<>();
        JsonNode json = mapper.readTree(JsonApiConverter.convert(tests));
        assertNotNull(json.get("data"));
        assertTrue(json.get("data").isArray());
        assertEquals(0, json.get("data").size());
    }

    @Test
    public void testIfEmptyLinksWontGetAdded() throws Exception {
        objectDto.setSelfRel("");
        objectDto.setAllSelfRel("");
        JsonNode json = mapper.readTree(JsonApiConverter.convert(objectDto));
        assertNull(json.get("links"));
    }

    @Test
    public void testIfNullLinksWontGetAdded() throws Exception {
        objectDto.setSelfRel(null);
        objectDto.setAllSelfRel(null);
        JsonNode json = mapper.readTree(JsonApiConverter.convert(objectDto));
        assertNull(json.get("links"));
    }

    @Test
    public void testIfEmptyRelationWontGetAdded() throws Exception {
        userDto.setMainObject(null);

        JsonNode json = mapper.readTree(JsonApiConverter.convert(userDto));
        assertNull(json.get("data").get("relationships").get("mainObject"));
    }

    @Test
    public void testIfNullListRelationsWontGetAdded() throws Exception {
        userDto.setChildObjects(null);

        JsonNode json = mapper.readTree(JsonApiConverter.convert(userDto));
        assertNull(json.get("data").get("relationships").get("childObjects"));
    }

    @Test
    public void testIfEmptyListRelationsWontGetAdded() throws Exception {
        userDto.setChildObjects(new ArrayList<>());

        JsonNode json = mapper.readTree(JsonApiConverter.convert(userDto));
        assertNull(json.get("data").get("relationships").get("childObjects"));
    }

    @Test
    public void testIfAllRelationsAreEmptyRelationshipsWontGetAdded() throws Exception {
        userDto.setMainObject(null);
        userDto.setChildObjects(new ArrayList<>());

        JsonNode json = mapper.readTree(JsonApiConverter.convert(userDto));
        assertNull(json.get("data").get("relationships"));
    }

    @Test
    public void testIfAllRelationsAreNullRelationshipsWontGetAdded() throws Exception {
        userDto.setMainObject(null);
        userDto.setChildObjects(null);

        JsonNode json = mapper.readTree(JsonApiConverter.convert(userDto));
        assertNull(json.get("data").get("relationships"));
    }

    @Test
    public void testIfEmptyRelationWontGetAddedToIncluded() throws Exception {
        userDto.setMainObject(null);

        JsonNode json = mapper.readTree(JsonApiConverter.convert(userDto));

        assertTrue(2 >= json.get("included").size());
    }

    @Test
    public void testIfNullListRelationsWontGetAddedToIncluded() throws Exception {
        userDto.setChildObjects(null);

        JsonNode json = mapper.readTree(JsonApiConverter.convert(userDto));

        assertEquals(1, json.get("included").size());
    }

    @Test
    public void testIfEmptyListRelationsWontGetAddedToIncluded() throws Exception {
        userDto.setChildObjects(new ArrayList<>());

        JsonNode json = mapper.readTree(JsonApiConverter.convert(userDto));

        assertEquals(1, json.get("included").size());
    }

    @Test
    public void testIfIncludedWontGetAddedWhenAllRelationsEmpty() throws Exception {
        userDto.setMainObject(null);
        userDto.setChildObjects(new ArrayList<>());

        JsonNode json = mapper.readTree(JsonApiConverter.convert(userDto));

        assertNull(json.get("included"));
    }

    @Test
    public void testIfIncludedWontGetAddedWhenAllRelationsNull() throws Exception {
        userDto.setMainObject(null);
        userDto.setChildObjects(null);

        JsonNode json = mapper.readTree(JsonApiConverter.convert(userDto));

        assertNull(json.get("included"));
    }

    @Test
    public void testIfEmptyPropertyWillGetAdded() throws Exception {
        userDto.setUsername("");

        JsonNode json = mapper.readTree(JsonApiConverter.convert(userDto));

        assertNotNull(json.get("data").get("attributes").get("username"));
        assertEquals("", json.get("data").get("attributes").get("username").asText());
    }

    @Test
    public void testIfnullPropertyWontGetAdded() throws Exception {
        userDto.setUsername(null);

        JsonNode json = mapper.readTree(JsonApiConverter.convert(userDto));
        JsonNode jsonNode = json.get("data").get("attributes").get("username");
        assertNull(jsonNode);
    }
}
