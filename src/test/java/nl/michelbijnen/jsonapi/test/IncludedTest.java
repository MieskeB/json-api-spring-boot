package nl.michelbijnen.jsonapi.test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import nl.michelbijnen.jsonapi.parser.JsonApiConverter;
import nl.michelbijnen.jsonapi.parser.JsonApiOptions;
import nl.michelbijnen.jsonapi.test.mock.AppleDto;
import nl.michelbijnen.jsonapi.test.mock.MockDataGenerator;
import nl.michelbijnen.jsonapi.test.mock.ObjectDto;
import nl.michelbijnen.jsonapi.test.mock.UserDto;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.util.*;

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
    public void testFieldsFiltersPrimaryRelationships_OnlyLinkage_NoIncluded_Single() throws Exception {
        HashMap<String, Set<String>> fieldsByType = new HashMap<>();
        fieldsByType.put("User", new HashSet<>(Collections.singletonList("mainObject")));
        JsonApiOptions options = JsonApiOptions.builder()
                .fieldsByType(fieldsByType)
                .build();

        String jsonStr = JsonApiConverter.convert(userDto, 1, options);
        JsonNode root = mapper.readTree(jsonStr);

        JsonNode relationships = root.get("data").get("relationships");
        assertNotNull(relationships);
        assertNotNull(relationships.get("mainObject"));
        assertNotNull(relationships.get("mainObject").get("data"));
        assertNull(root.get("included"));
    }

    @Test
    public void testIncludeAddsPrimaryRelationshipLinkage_AndSideLoads_Single() throws Exception {
        JsonApiOptions options = JsonApiOptions.builder()
                .includePaths(new HashSet<>(Collections.singletonList("mainObject")))
                .build();

        String jsonStr = JsonApiConverter.convert(userDto, 1, options);
        JsonNode root = mapper.readTree(jsonStr);

        JsonNode relationships = root.get("data").get("relationships");
        assertNotNull(relationships);
        assertNotNull(relationships.get("mainObject"));
        assertNotNull(relationships.get("mainObject").get("data"));

        ArrayNode included = (ArrayNode) root.get("included");
        assertNotNull(included);
        boolean foundMainObject = false;
        for (int i = 0; i < included.size(); i++) {
            JsonNode inc = included.get(i);
            if ("Object".equals(inc.get("type").asText()) &&
                    userDto.getMainObject().getId().equals(inc.get("id").asText())) {
                foundMainObject = true;
                break;
            }
        }
        assertTrue(foundMainObject);
    }

    @Test
    public void testFieldsAndInclude_IncludeWins_RelationshipLinkageAndIncluded_Single() throws Exception {
        HashMap<String, Set<String>> fieldsByType = new HashMap<>();
        fieldsByType.put("User", new HashSet<>(Collections.singletonList("mainObject")));
        JsonApiOptions options = JsonApiOptions.builder()
                .fieldsByType(fieldsByType)
                .includePaths(new HashSet<>(Collections.singletonList("mainObject")))
                .build();

        String jsonStr = JsonApiConverter.convert(userDto, 1, options);
        JsonNode root = mapper.readTree(jsonStr);

        JsonNode relationships = root.get("data").get("relationships");
        assertNotNull(relationships);
        assertNotNull(relationships.get("mainObject"));
        assertNotNull(relationships.get("mainObject").get("data"));

        ArrayNode included = (ArrayNode) root.get("included");
        assertNotNull(included);
        boolean foundMainObject = false;
        for (int i = 0; i < included.size(); i++) {
            JsonNode inc = included.get(i);
            if ("Object".equals(inc.get("type").asText()) &&
                    userDto.getMainObject().getId().equals(inc.get("id").asText())) {
                foundMainObject = true;
                break;
            }
        }
        assertTrue(foundMainObject);
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

        JsonNode json = mapper.readTree(JsonApiConverter.convert(users, 2));

        ArrayNode included = (ArrayNode) json.get("included");

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

    @Test
    public void testFieldsFiltersAttributesOnly_Email_NoRelationships() throws Exception {
        HashMap<String, Set<String>> fieldsByType = new HashMap<>();
        fieldsByType.put("User", new HashSet<>(Collections.singletonList("email")));
        JsonApiOptions options = JsonApiOptions.builder()
                .fieldsByType(fieldsByType)
                .build();

        String jsonStr = JsonApiConverter.convert(userDto, 1, options);
        JsonNode root = mapper.readTree(jsonStr);

        JsonNode data = root.get("data");
        JsonNode attrs = data.get("attributes");
        assertNotNull(attrs);
        assertNotNull(attrs.get("email"));
        assertNull("username should be filtered out", attrs.get("username"));

        assertNull(data.get("relationships"));
    }

    @Test
    public void testFieldsForIncludedType_ObjectAttributesFiltered() throws Exception {
        HashMap<String, Set<String>> fieldsByType = new HashMap<>();
        fieldsByType.put("Object", new HashSet<>(Collections.singletonList("name")));
        JsonApiOptions options = JsonApiOptions.builder()
                .fieldsByType(fieldsByType)
                .includePaths(new HashSet<>(Collections.singletonList("mainObject")))
                .build();

        String jsonStr = JsonApiConverter.convert(userDto, 1, options);
        JsonNode root = mapper.readTree(jsonStr);
        ArrayNode included = (ArrayNode) root.get("included");
        assertNotNull(included);

        JsonNode found = null;
        for (int i = 0; i < included.size(); i++) {
            JsonNode inc = included.get(i);
            if ("Object".equals(inc.get("type").asText()) &&
                    userDto.getMainObject().getId().equals(inc.get("id").asText())) {
                found = inc;
                break;
            }
        }
        assertNotNull("Included mainObject should be present", found);
        JsonNode attrs = found.get("attributes");
        assertNotNull(attrs);
        assertNotNull(attrs.get("name"));
    }

    @Test
    public void testIncludedDedupAcrossMultiplePaths() throws Exception {
        JsonApiOptions options = JsonApiOptions.builder()
                .includePaths(new HashSet<>(Arrays.asList("childObjects.apple", "mainObject.apple", "childObjects", "mainObject")))
                .build();

        String jsonStr = JsonApiConverter.convert(userDto, 2, options);
        JsonNode root = mapper.readTree(jsonStr);
        ArrayNode included = (ArrayNode) root.get("included");
        assertNotNull(included);

        String appleId = userDto.getMainObject().getApple().getId();
        int count = 0;
        for (int i = 0; i < included.size(); i++) {
            JsonNode inc = included.get(i);
            if ("Apple".equals(inc.get("type").asText()) && appleId.equals(inc.get("id").asText())) {
                count++;
            }
        }
        assertEquals("Apple should appear only once in included despite multiple paths", 1, count);
    }

    @Test
    public void testConvertNullPrimary_ReturnsEmptyDataObject() throws Exception {
        JsonNode root = mapper.readTree(JsonApiConverter.convert(null));
        assertNotNull(root.get("data"));
        assertTrue(root.get("data").isObject());
        assertEquals(0, root.get("data").size());
    }

    @Test
    public void testConvertEmptyListPrimary_ReturnsEmptyDataArray_NoIncluded() throws Exception {
        List<UserDto> empty = Collections.emptyList();
        JsonNode root = mapper.readTree(JsonApiConverter.convert(empty));
        assertNotNull(root.get("data"));
        assertTrue(root.get("data").isArray());
        assertEquals(0, root.get("data").size());
        assertTrue(root.path("included").isMissingNode() || root.get("included").isEmpty());
    }

    @Test
    public void testIncludeSkipsEmptyCollections_NoIncludedProduced() throws Exception {
        userDto.setChildObjects(new ArrayList<>());
        JsonApiOptions options = JsonApiOptions.builder()
                .includePaths(new HashSet<>(Collections.singletonList("childObjects")))
                .build();

        String jsonStr = JsonApiConverter.convert(userDto, 1, options);
        JsonNode root = mapper.readTree(jsonStr);
        assertTrue(root.path("included").isMissingNode() || root.get("included").isEmpty());
    }
}
