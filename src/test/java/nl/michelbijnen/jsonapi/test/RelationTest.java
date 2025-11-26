package nl.michelbijnen.jsonapi.test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import nl.michelbijnen.jsonapi.parser.JsonApiConverter;
import nl.michelbijnen.jsonapi.test.mock.MockDataGenerator;
import nl.michelbijnen.jsonapi.test.mock.ObjectDto;
import nl.michelbijnen.jsonapi.test.mock.UserDto;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.*;

public class RelationTest {

    private MockDataGenerator generator;

    private ObjectDto objectDto;
    private UserDto userDto;
    private ObjectMapper mapper;

    @Before
    public void before() throws CloneNotSupportedException {
        this.generator = MockDataGenerator.getInstance();
        this.objectDto = (ObjectDto) generator.getObjectDto().clone();
        this.userDto = (UserDto) generator.getUserDto().clone();
        this.mapper = new ObjectMapper();
    }

    @Test
    public void testIfRelationshipExists() throws Exception {
        JsonNode json = mapper.readTree(JsonApiConverter.convert(objectDto));
        assertNotNull(json.get("data").get("relationships"));
    }

    @Test
    public void testIfRelationshipCanBeNull() throws Exception {
        objectDto.setOwner(null);
        JsonNode json = mapper.readTree(JsonApiConverter.convert(objectDto));
        assertNull(json.get("data").get("relationships").get("owner"));
        objectDto = this.generator.getObjectDto();
    }

    @Test
    public void testIfRelationshipOwnerExists() throws Exception {
        JsonNode json = mapper.readTree(JsonApiConverter.convert(objectDto));
        assertNotNull(json.get("data").get("relationships").get("owner"));
    }

    @Test
    public void testIfRelationshipOwnerDataExists() throws Exception {
        JsonNode json = mapper.readTree(JsonApiConverter.convert(objectDto));
        assertNotNull(json.get("data").get("relationships").get("owner").get("data"));
    }

    @Test
    public void testIfRelationshipOwnerDataIdWorks() throws Exception {
        JsonNode json = mapper.readTree(JsonApiConverter.convert(objectDto));
        assertEquals(objectDto.getOwner().getId(), json.get("data").get("relationships").get("owner").get("data").get("id").asText());
    }

    @Test
    public void testIfRelationshipOwnerDataTypeWorks() throws Exception {
        JsonNode json = mapper.readTree(JsonApiConverter.convert(objectDto));
        assertEquals("User", json.get("data").get("relationships").get("owner").get("data").get("type").asText());
    }

    @Test
    public void testIfRelationshipOwnerLinksExists() throws Exception {
        JsonNode json = mapper.readTree(JsonApiConverter.convert(objectDto));
        assertNotNull(json.get("data").get("relationships").get("owner").get("links"));
    }

    @Test
    public void testIfRelationshipOwnerLinksSelfWorks() throws Exception {
        JsonNode json = mapper.readTree(JsonApiConverter.convert(objectDto));
        assertEquals("http://localhost:8080/user/" + objectDto.getOwner().getId(), json.get("data").get("relationships").get("owner").get("links").get("self").asText());
    }

    @Test
    @Ignore("Planned for future update")
    public void testIfRelationshipOwnerLinksRelatedWorks() throws Exception {
        JsonNode json = mapper.readTree(JsonApiConverter.convert(objectDto));
        assertEquals("http://localhost:8080/object/" + objectDto.getId() + "/user/" + objectDto.getOwner().getId(), json.get("data").get("relationships").get("owner").get("links").get("related").asText());
    }

    @Test
    public void testNoDuplicatesInRelationship() throws Exception {
        // Arrange: Get the user and force duplicate in to-many relationship
        UserDto user = generator.getUserDto();
        List<ObjectDto> childObjects = new ArrayList<>();
        ObjectDto child1 = user.getChildObjects().get(0);
        childObjects.add(child1);
        childObjects.add(child1); // Intentional duplicate
        user.setChildObjects(childObjects);

        JsonNode json = mapper.readTree(JsonApiConverter.convert(user, 1));

        // Act: Extract the relationship data for childObjects
        JsonNode data = json.get("data");
        JsonNode relationships = data.get("relationships");
        JsonNode childRel = relationships.get("childObjects");
        ArrayNode relData = (ArrayNode) childRel.get("data");

        // Assert: Only one unique entry in data array
        assertEquals("Relationship data should have no duplicates", 1, relData.size());

        // Additional check for uniqueness
        Set<String> typeIdSet = new HashSet<>();
        for (int i = 0; i < relData.size(); i++) {
            JsonNode obj = relData.get(i);
            String type = obj.get("type").asText();
            String id = obj.get("id").asText();
            String key = type + ":" + id;
            assertFalse("Duplicate in relationship data: " + key, typeIdSet.contains(key));
            typeIdSet.add(key);
        }
    }
}
