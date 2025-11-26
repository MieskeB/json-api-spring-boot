package nl.michelbijnen.jsonapi.test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import nl.michelbijnen.jsonapi.parser.JsonApiConverter;
import nl.michelbijnen.jsonapi.test.mock.AppleDto;
import nl.michelbijnen.jsonapi.test.mock.MockDataGenerator;
import nl.michelbijnen.jsonapi.test.mock.ObjectDto;
import nl.michelbijnen.jsonapi.test.mock.UserDto;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.*;

public class IncludedTest {

    private ObjectDto objectDto;
    private UserDto userDto;
    private AppleDto appleDto;
    MockDataGenerator generator = MockDataGenerator.getInstance();
    private ObjectMapper mapper;

    @Before
    public void before() throws CloneNotSupportedException {
        this.objectDto = (ObjectDto) generator.getObjectDto().clone();
        this.userDto = (UserDto) generator.getUserDto().clone();
        this.appleDto = (AppleDto) generator.getAppleDto().clone();
        this.mapper = new ObjectMapper();
    }

    @Test
    public void testIfIncludedExists() throws Exception {
        JsonNode json = mapper.readTree(JsonApiConverter.convert(objectDto));
        assertNotNull(json.get("included"));
    }

    @Test
    public void testIfIncludedIdWorks() throws Exception {
        JsonNode json = mapper.readTree(JsonApiConverter.convert(objectDto));
        assertEquals(objectDto.getOwner().getId(), json.get("included").get(0).get("id").asText());
    }

    @Test
    public void testIfIncludedTypeWorks() throws Exception {
        JsonNode json = mapper.readTree(JsonApiConverter.convert(objectDto));
        assertEquals("User", json.get("included").get(0).get("type").asText());
    }

    //region attributes

    @Test
    public void testIfIncludedAttributesExists() throws Exception {
        JsonNode json = mapper.readTree(JsonApiConverter.convert(objectDto));
        assertNotNull(json.get("included").get(0).get("attributes"));
    }

    @Test
    public void testIfIncludedAttributesUsernameWorks() throws Exception {
        JsonNode json = mapper.readTree(JsonApiConverter.convert(objectDto));
        assertEquals(objectDto.getOwner().getUsername(), json.get("included").get(0).get("attributes").get("username").asText());
    }

    @Test
    public void testIfIncludedAttributesEmailWorks() throws Exception {
        JsonNode json = mapper.readTree(JsonApiConverter.convert(objectDto));
        assertEquals(objectDto.getOwner().getEmail(), json.get("included").get(0).get("attributes").get("email").asText());
    }

    //endregion

    //region links

    @Test
    public void testIfIncludedLinksExists() throws Exception {
        JsonNode json = mapper.readTree(JsonApiConverter.convert(objectDto));
        assertNotNull(json.get("included").get(0).get("links"));
    }

    @Test
    public void testIfIncludedLinksSelfWorks() throws Exception {
        JsonNode json = mapper.readTree(JsonApiConverter.convert(objectDto));
        assertEquals("http://localhost:8080/user/" + this.userDto.getId(), json.get("included").get(0).get("links").get("self").asText());
    }

    @Test
    @Ignore("Planned for future update")
    public void testIfIncludedLinksNextWorks() throws Exception {
        JsonNode json = mapper.readTree(JsonApiConverter.convert(objectDto));
        assertEquals("http://localhost:8080/user/" + this.userDto.getId() + "?page=2", json.get("included").get(0).get("links").get("next").asText());
    }

    //endregion

    //region relationships

    @Test
    public void testIfIncludedRelationshipsExists() throws Exception {
        JsonNode json = mapper.readTree(JsonApiConverter.convert(objectDto));
        assertNotNull(json.get("included").get(0).get("relationships"));
    }

    //region mainObject

    @Test
    public void testIfIncludedRelationshipsMainObjectExists() throws Exception {
        JsonNode json = mapper.readTree(JsonApiConverter.convert(objectDto));
        assertNotNull(json.get("included").get(0).get("relationships").get("mainObject"));
    }

    @Test
    public void testIfIncludedRelationshipsMainObjectDataExists() throws Exception {
        JsonNode json = mapper.readTree(JsonApiConverter.convert(objectDto));
        assertNotNull(json.get("included").get(0).get("relationships").get("mainObject").get("data"));
    }

    @Test
    public void testIfIncludedRelationshipsMainObjectDataIdWorks() throws Exception {
        JsonNode json = mapper.readTree(JsonApiConverter.convert(objectDto));
        assertEquals(objectDto.getOwner().getMainObject().getId(), json.get("included").get(0).get("relationships").get("mainObject").get("data").get("id").asText());
    }

    @Test
    public void testIfIncludedRelationshipsMainObjectDataTypeWorks() throws Exception {
        JsonNode json = mapper.readTree(JsonApiConverter.convert(objectDto));
        assertEquals("Object", json.get("included").get(0).get("relationships").get("mainObject").get("data").get("type").asText());
    }

    //endregion

    //region childObjects

    @Test
    public void testIfIncludedRelationshipsChildObjectsExists() throws Exception {
        JsonNode json = mapper.readTree(JsonApiConverter.convert(objectDto));
        assertNotNull(json.get("included").get(0).get("relationships").get("childObjects"));
    }

    @Test
    public void testIfIncludedRelationshipsChildObjectsDataExists() throws Exception {
        JsonNode json = mapper.readTree(JsonApiConverter.convert(objectDto));
        assertNotNull(json.get("included").get(0).get("relationships").get("childObjects").get("data"));
    }

    @Test
    public void testIfIncludedRelationshipsChildObjectsDataIdWorks() throws Exception {
        JsonNode json = mapper.readTree(JsonApiConverter.convert(objectDto));
        ArrayNode childObjects = (ArrayNode) json.get("included").get(0).get("relationships").get("childObjects").get("data");
        {
            boolean found = false;
            for (int i = 0; i < childObjects.size(); i++) {
                if (childObjects.get(i).get("id").asText().equals(objectDto.getOwner().getChildObjects().get(0).getId())) {
                    found = true;
                    break;
                }
            }
            if (!found) fail();
        }
        {
            boolean found = false;
            for (int i = 0; i < childObjects.size(); i++) {
                if (childObjects.get(i).get("id").asText().equals(objectDto.getOwner().getChildObjects().get(1).getId())) {
                    found = true;
                    break;
                }
            }
            if (!found) fail();
        }
    }

    @Test
    public void testIfIncludedRelationshipsChildObjectsDataTypeWorks() throws Exception {
        JsonNode json = mapper.readTree(JsonApiConverter.convert(objectDto));
        ArrayNode childObjects = (ArrayNode) json.get("included").get(0).get("relationships").get("childObjects").get("data");
        for (int i = 0; i < childObjects.size(); i++) {
            assertEquals("Object", childObjects.get(i).get("type").asText());
        }
    }

    //endregion

    //region depth

    @Test
    public void testIfDepthIsWorking() throws Exception {
        JsonNode json = mapper.readTree(JsonApiConverter.convert(userDto, 2));
        ArrayNode includedObjects = (ArrayNode) json.get("included");
        for (int i = 0; i < includedObjects.size(); i++) {
            JsonNode included = includedObjects.get(i);
            if (included.get("id").asText().equals(this.appleDto.getId())) {
                return;
            }
        }
        fail();
    }

    @Test
    public void testIfDepthIsCapping() throws Exception {
        JsonNode json = mapper.readTree(JsonApiConverter.convert(userDto, 1));
        ArrayNode includedObjects = (ArrayNode) json.get("included");
        for (int i = 0; i < includedObjects.size(); i++) {
            JsonNode included = includedObjects.get(i);
            if (included.get("id").asText().equals(this.appleDto.getId())) {
                fail();
            }
        }
    }

    //endregion

    //endregion

    //region double relations inside included

    @Test
    public void testIfDoubleRelationsAreAddedOnlyOnceToIncludedFromList() throws Exception {
        userDto.getChildObjects().add(userDto.getChildObjects().get(0));
        ArrayNode included = (ArrayNode) mapper.readTree(JsonApiConverter.convert(userDto)).get("included");
        for (int i = 0; i < included.size(); i++) {
            String id = included.get(i).get("id").asText();
            String type = included.get(i).get("type").asText();

            int c = 0;
            for (int j = 0; j < included.size(); j++) {
                String idToTest = included.get(j).get("id").asText();
                String typeToTest = included.get(j).get("type").asText();

                if (id.equals(idToTest) && type.equals(typeToTest)) {
                    c++;
                    if (c >= 2) {
                        fail("Item id " + idToTest + " of type " + typeToTest + " exist multiple times in included");
                    }
                }
            }
        }
    }

    @Test
    public void testIfDoubleRelationsAreAddedOnlyOnceToIncluded() throws Exception {
        ArrayNode included = (ArrayNode) mapper.readTree(JsonApiConverter.convert(userDto, 4)).get("included");
        for (int i = 0; i < included.size(); i++) {
            String id = included.get(i).get("id").asText();
            String type = included.get(i).get("type").asText();

            int c = 0;
            for (int j = 0; j < included.size(); j++) {
                String idToTest = included.get(j).get("id").asText();
                String typeToTest = included.get(j).get("type").asText();

                if (id.equals(idToTest) && type.equals(typeToTest)) {
                    c++;
                    if (c >= 2) {
                        fail("Item id " + idToTest + " of type " + typeToTest + " exist multiple times in included");
                    }
                }
            }
        }
    }

    @Test
    public void testNoDuplicatesInIncluded() throws Exception {
        // Arrange: Create two users sharing the same relations (shallow clone)
        UserDto user1 = generator.getUserDto();
        UserDto user2 = null;
        try {
            user2 = (UserDto) user1.clone();
        } catch (CloneNotSupportedException e) {
            fail("Clone failed: " + e.getMessage());
        }
        user2.setId("owner2");
        user2.setUsername("the owner 2");
        user2.setEmail("owner2@michelbijnen.nl");

        List<UserDto> users = Arrays.asList(user1, user2);

        // Use depth=2 to include nested relations like apples
        JsonNode json = mapper.readTree(JsonApiConverter.convert(users, 2));

        // Act: Extract included array
        ArrayNode included = (ArrayNode) json.get("included");

        // Assert: No duplicates based on type and id
        Set<String> typeIdSet = new HashSet<>();
        for (int i = 0; i < included.size(); i++) {
            JsonNode obj = included.get(i);
            String type = obj.get("type").asText();
            String id = obj.get("id").asText();
            String key = type + ":" + id;
            assertFalse("Duplicate in included: " + key, typeIdSet.contains(key));
            typeIdSet.add(key);
        }
    }

    //endregion
}
