package nl.michelbijnen.jsonapi.test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import nl.michelbijnen.jsonapi.parser.JsonApiConverter;
import nl.michelbijnen.jsonapi.parser.JsonApiOptions;
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

    @Test
    public void testShouldIncludeAllAttributesAndNoRelationships() throws Exception {
        ObjectDto dto = (ObjectDto) objectDto.clone();
        Map<String, Set<String>> fieldsMap = new HashMap<>();
        fieldsMap.put("Object", new HashSet<>(Collections.singletonList("name")));
        JsonApiOptions jsonApiOptions = JsonApiOptions.builder()
                .fieldsByType(fieldsMap)
                .includePaths(new HashSet<>()).build();
        String result = JsonApiConverter.convert(dto, jsonApiOptions);
        JsonNode json = mapper.readTree(result);
        System.out.println(result);

        // Should include all attributes, no relationships
        assertTrue(json.has("data"));
        JsonNode data = json.get("data");
        assertEquals("Object", data.get("type").asText());
        assertTrue(data.get("attributes").has("name"));
        assertFalse(data.has("relationships"));
        assertFalse(json.has("included"));
    }

    @Test
    public void testShouldIncludeRelationshipInRelationshipsAndNoFieldsInIncluded() throws Exception {
        ObjectDto dto = (ObjectDto) objectDto.clone();
        Set<String> includes = new HashSet<>(Collections.singletonList("owner"));
        JsonApiOptions jsonApiOptions = JsonApiOptions.builder()
                .includePaths(includes).build();
        String result = JsonApiConverter.convert(dto, jsonApiOptions);
        JsonNode json = mapper.readTree(result);
        System.out.println(result);

        // Should include relationship in relationships and no fields in included
        assertTrue(json.get("data").has("relationships"));
        JsonNode rel = json.get("data").get("relationships").get("owner");
        assertEquals(dto.getOwner().getId(), rel.get("data").get("id").asText());
        assertEquals("User", rel.get("data").get("type").asText());
        assertTrue(json.has("included"));
        assertEquals(1, json.get("included").size());
        JsonNode included = json.get("included").get(0);
        assertEquals(dto.getOwner().getId(), included.get("id").asText());
        assertNull(included.get("attributes"));
    }

    @Test
    public void testShouldIncludeOnlySpecifiedAttributesAndNoRelationships() throws Exception {
        ObjectDto dto = (ObjectDto) objectDto.clone();
        Map<String, Set<String>> fields = new HashMap<>();
        fields.put("Object", new HashSet<>(Collections.singletonList("name")));
        Set<String> includes = new HashSet<>();
        JsonApiOptions jsonApiOptions = JsonApiOptions.builder()
                .fieldsByType(fields)
                .includePaths(includes).build();
        String result = JsonApiConverter.convert(dto, jsonApiOptions);
        JsonNode json = mapper.readTree(result);
        System.out.println(result);

        // Should include only name attribute, no relationships
        assertTrue(json.get("data").get("attributes").has("name"));
        assertFalse(json.get("data").has("relationships"));
    }

    @Test
    public void testShouldIncludeSingleRelationshipInRelationshipsWithoutIncluded() throws Exception {
        ObjectDto dto = (ObjectDto) objectDto.clone();
        Map<String, Set<String>> fields = new HashMap<>();
        fields.put("Object", new HashSet<>(Arrays.asList("name", "owner")));
        Set<String> includes = new HashSet<>();
        JsonApiOptions jsonApiOptions = JsonApiOptions.builder()
                .fieldsByType(fields)
                .includePaths(includes).build();
        String result = JsonApiConverter.convert(dto, jsonApiOptions);
        JsonNode json = mapper.readTree(result);
        System.out.println(result);

        // Should include owner in relationships with id/type, but not in included
        assertTrue(json.get("data").has("relationships"));
        JsonNode rel = json.get("data").get("relationships").get("owner");
        assertEquals(dto.getOwner().getId(), rel.get("data").get("id").asText());
        assertEquals("User", rel.get("data").get("type").asText());
        assertFalse(json.has("included")); // Not included since not in includes
    }

    @Test
    public void testShouldIncludeListRelationshipInRelationshipsWithoutIncluded() throws Exception {
        UserDto dto = (UserDto) userDto.clone();
        Map<String, Set<String>> fields = new HashMap<>();
        fields.put("User", new HashSet<>(Arrays.asList("email", "childObjects")));
        Set<String> includes = new HashSet<>();
        JsonApiOptions jsonApiOptions = JsonApiOptions.builder()
                .fieldsByType(fields)
                .includePaths(includes).build();
        String result = JsonApiConverter.convert(dto, jsonApiOptions);
        JsonNode json = mapper.readTree(result);
        System.out.println(result);

        // Should include childObjects in relationships with ids/types
        assertTrue(json.get("data").has("relationships"));
        JsonNode rel = json.get("data").get("relationships").get("childObjects");
        assertEquals(2, rel.get("data").size());
        assertFalse(json.has("included"));
    }

    @Test
    public void testShouldBehaveLikeIncludesWhenBothFieldsAndIncludesSpecified() throws Exception {
        ObjectDto dto = (ObjectDto) objectDto.clone();
        Map<String, Set<String>> fields = new HashMap<>();
        fields.put("Object", new HashSet<>(Collections.singletonList("owner")));
        Set<String> includes = new HashSet<>(Collections.singletonList("owner"));
        JsonApiOptions jsonApiOptions = JsonApiOptions.builder()
                .fieldsByType(fields)
                .includePaths(includes).build();
        String result = JsonApiConverter.convert(dto, jsonApiOptions);
        JsonNode json = mapper.readTree(result);

        // Should behave like includes: relationship in relationships and full in included
        assertTrue(json.get("data").has("relationships"));
        assertTrue(json.has("included"));
    }
}
