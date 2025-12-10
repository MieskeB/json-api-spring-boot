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

import java.util.*;

import static org.junit.Assert.*;

public class RelationTest {

    private MockDataGenerator generator;

    private ObjectDto object;
    private UserDto user;
    private ObjectMapper mapper;

    @Before
    public void before() throws CloneNotSupportedException {
        this.generator = MockDataGenerator.getInstance();
        this.object = (ObjectDto) generator.getObjectDto().clone();
        this.user = (UserDto) generator.getUserDto().clone();
        this.mapper = new ObjectMapper();
    }

    @Test
    public void testIfRelationshipExists() throws Exception {
        JsonNode json = mapper.readTree(JsonApiConverter.convert(object));
        assertNotNull(json.get("data").get("relationships"));
    }

    @Test
    public void testIfRelationshipCanBeNull() throws Exception {
        object.setOwner(null);
        JsonNode json = mapper.readTree(JsonApiConverter.convert(object));
        assertNull(json.get("data").get("relationships").get("owner"));
        object = this.generator.getObjectDto();
    }

    @Test
    public void testIfRelationshipOwnerExists() throws Exception {
        JsonNode json = mapper.readTree(JsonApiConverter.convert(object));
        assertNotNull(json.get("data").get("relationships").get("owner"));
    }

    @Test
    public void testIfRelationshipOwnerDataExists() throws Exception {
        JsonNode json = mapper.readTree(JsonApiConverter.convert(object));
        assertNotNull(json.get("data").get("relationships").get("owner").get("data"));
    }

    @Test
    public void testIfRelationshipOwnerDataIdWorks() throws Exception {
        JsonNode json = mapper.readTree(JsonApiConverter.convert(object));
        assertEquals(object.getOwner().getId(), json.get("data").get("relationships").get("owner").get("data").get("id").asText());
    }

    @Test
    public void testIfRelationshipOwnerDataTypeWorks() throws Exception {
        JsonNode json = mapper.readTree(JsonApiConverter.convert(object));
        assertEquals("User", json.get("data").get("relationships").get("owner").get("data").get("type").asText());
    }

    @Test
    public void testIfRelationshipOwnerLinksExists() throws Exception {
        JsonNode json = mapper.readTree(JsonApiConverter.convert(object));
        assertNotNull(json.get("data").get("relationships").get("owner").get("links"));
    }

    @Test
    public void testIfRelationshipOwnerLinksSelfWorks() throws Exception {
        JsonNode json = mapper.readTree(JsonApiConverter.convert(object));
        assertEquals("http://localhost:8080/user/" + object.getOwner().getId(), json.get("data").get("relationships").get("owner").get("links").get("self").asText());
    }

    @Test
    @Ignore("Planned for future update")
    public void testIfRelationshipOwnerLinksRelatedWorks() throws Exception {
        JsonNode json = mapper.readTree(JsonApiConverter.convert(object));
        assertEquals("http://localhost:8080/object/" + object.getId() + "/user/" + object.getOwner().getId(), json.get("data").get("relationships").get("owner").get("links").get("related").asText());
    }

    @Test
    public void testNoDuplicatesInRelationship() throws Exception {
        UserDto user = generator.getUserDto();
        List<ObjectDto> childObjects = new ArrayList<>();
        ObjectDto child1 = user.getChildObjects().get(0);
        childObjects.add(child1);
        childObjects.add(child1); // Intentional duplicate
        user.setChildObjects(childObjects);

        JsonNode json = mapper.readTree(JsonApiConverter.convert(user, 1));

        JsonNode data = json.get("data");
        JsonNode relationships = data.get("relationships");
        JsonNode childRel = relationships.get("childObjects");
        ArrayNode relData = (ArrayNode) childRel.get("data");

        assertEquals("Relationship data should have no duplicates", 1, relData.size());

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
