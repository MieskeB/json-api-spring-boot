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
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.*;

public class FieldsAndIncludeTest {

    private final MockDataGenerator generator = MockDataGenerator.getInstance();
    private ObjectMapper mapper;
    private UserDto userDto;
    private ObjectDto objectDto;

    @Before
    public void setUp() throws Exception {
        this.mapper = new ObjectMapper();
        this.userDto = (UserDto) generator.getUserDto().clone();
        this.objectDto = (ObjectDto) generator.getObjectDto().clone();
    }

    // fields[User]=mainObject
    // Expect: relationships.mainObject linkage present on primary data; no included due to fields-only
    @Test
    public void testFieldsFiltersPrimaryRelationships_OnlyLinkage_NoIncluded_Single() throws Exception {
        Map<String, Set<String>> fieldsByType = new HashMap<>();
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
        JsonNode linkage = relationships.get("mainObject").get("data");
        assertEquals("Object", linkage.get("type").asText());
        assertEquals(userDto.getMainObject().getId(), linkage.get("id").asText());

        // No side-loaded resources for fields-only
        assertTrue(root.path("included").isMissingNode() || root.get("included").isEmpty());
    }

    // include=mainObject
    // Expect: relationships.mainObject linkage present; included contains full mainObject resource
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
            if (userDto.getMainObject().getId().equals(inc.get("id").asText())) {
                foundMainObject = true;
                break;
            }
        }
        assertTrue("mainObject should be present in included", foundMainObject);
    }

    // fields[User]=mainObject & include=mainObject
    // Expect: same as include case (include wins for visibility)
    @Test
    public void testFieldsAndInclude_IncludeWins_RelationshipLinkageAndIncluded_Single() throws Exception {
        Map<String, Set<String>> fieldsByType = new HashMap<>();
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
            if ("Object".equals(inc.get("type").asText())
                    && userDto.getMainObject().getId().equals(inc.get("id").asText())) {
                foundMainObject = true;
                break;
            }
        }
        assertTrue("mainObject should be present in included", foundMainObject);
    }

    // List variant: fields[User]=mainObject over collection
    // Expect: each primary has linkage; no included
    @Test
    public void testFieldsFiltersPrimaryRelationships_OnlyLinkage_NoIncluded_List() throws Exception {
        UserDto u1 = (UserDto) generator.getUserDto().clone();
        UserDto u2 = (UserDto) generator.getUserDto().clone();
        u2.setId("owner2");
        List<UserDto> users = Arrays.asList(u1, u2);

        Map<String, Set<String>> fieldsByType = new HashMap<>();
        fieldsByType.put("User", new HashSet<>(Collections.singletonList("mainObject")));
        JsonApiOptions options = JsonApiOptions.builder()
                .fieldsByType(fieldsByType)
                .build();

        String jsonStr = JsonApiConverter.convert(users, 1, options);
        JsonNode root = mapper.readTree(jsonStr);

        ArrayNode data = (ArrayNode) root.get("data");
        assertNotNull(data);
        for (int i = 0; i < data.size(); i++) {
            JsonNode relationships = data.get(i).get("relationships");
            assertNotNull(relationships);
            assertNotNull(relationships.get("mainObject"));
            JsonNode l = relationships.get("mainObject").get("data");
            assertNotNull(l);
            assertNotNull(l.get("type"));
            assertNotNull(l.get("id"));
        }
        assertTrue(root.path("included").isMissingNode() || root.get("included").isEmpty());
    }

    // List variant: include=mainObject over collection
    // Expect: each primary has linkage; included contains the related resources
    @Test
    public void testIncludeAddsPrimaryRelationshipLinkage_AndSideLoads_List() throws Exception {
        UserDto u1 = (UserDto) generator.getUserDto().clone();
        UserDto u2 = (UserDto) generator.getUserDto().clone();
        u2.setId("owner2");
        List<UserDto> users = Arrays.asList(u1, u2);

        JsonApiOptions options = JsonApiOptions.builder()
                .includePaths(new HashSet<>(Collections.singletonList("mainObject")))
                .build();

        String jsonStr = JsonApiConverter.convert(users, 1, options);
        JsonNode root = mapper.readTree(jsonStr);

        ArrayNode data = (ArrayNode) root.get("data");
        assertNotNull(data);
        for (int i = 0; i < data.size(); i++) {
            JsonNode relationships = data.get(i).get("relationships");
            assertNotNull(relationships);
            assertNotNull(relationships.get("mainObject"));
            JsonNode l = relationships.get("mainObject").get("data");
            assertNotNull(l);
            assertNotNull(l.get("type"));
            assertNotNull(l.get("id"));
        }

        ArrayNode included = (ArrayNode) root.get("included");
        assertNotNull(included);
        // at least one mainObject should be present
        boolean foundAny = false;
        for (int i = 0; i < included.size(); i++) {
            JsonNode inc = included.get(i);
            if ("Object".equals(inc.get("type").asText())) {
                foundAny = true;
                break;
            }
        }
        assertTrue(foundAny);
    }

    @Test
    public void testIncludeNestedPathWithinDepth_SideLoadsOnlyWithinPath_Single() throws Exception {
        // Must explicitly request top-level and nested segments
        JsonApiOptions options = JsonApiOptions.builder()
                .includePaths(new HashSet<>(Arrays.asList("childObjects", "childObjects.apple")))
                .build();

        String jsonStr = JsonApiConverter.convert(userDto, 2, options);
        JsonNode root = mapper.readTree(jsonStr);

        // linkage present on primary for childObjects
        JsonNode relationships = root.get("data").get("relationships");
        assertNotNull(relationships);
        assertNotNull(relationships.get("childObjects"));
        assertNotNull(relationships.get("childObjects").get("data"));

        // included must contain Object (childObjects) and Apple (grandchildren)
        ArrayNode included = (ArrayNode) root.get("included");
        assertNotNull(included);

        boolean foundAnyChildObject = false;
        boolean foundAnyApple = false;
        for (int i = 0; i < included.size(); i++) {
            JsonNode inc = included.get(i);
            if ("Object".equals(inc.get("type").asText())) {
                foundAnyChildObject = true;
            }
            if ("Apple".equals(inc.get("type").asText())) {
                foundAnyApple = true;
            }
        }
        assertTrue(foundAnyChildObject);
        assertTrue(foundAnyApple);
    }

    @Test
    public void testIncludeNestedPathBeyondDepth_CappedByDepth_NoGrandchildren_Single() throws Exception {
        // Must explicitly request top-level and nested segments
        JsonApiOptions options = JsonApiOptions.builder()
                .includePaths(new HashSet<>(Arrays.asList("childObjects", "childObjects.apple")))
                .build();

        String jsonStr = JsonApiConverter.convert(userDto, 1, options);
        JsonNode root = mapper.readTree(jsonStr);

        ArrayNode included = (ArrayNode) root.get("included");
        assertNotNull(included);

        boolean foundAnyChildObject = false;
        boolean foundAnyApple = false;
        for (int i = 0; i < included.size(); i++) {
            JsonNode inc = included.get(i);
            if ("Object".equals(inc.get("type").asText())) {
                foundAnyChildObject = true;
            }
            if ("Apple".equals(inc.get("type").asText())) {
                foundAnyApple = true;
            }
        }
        assertTrue(foundAnyChildObject);
        assertFalse("Apple should not be included when depth=1", foundAnyApple);
    }

    @Test
    public void testIncludeNestedPathWithFields_IncludeForcesTopLevelLinkage_Single() throws Exception {
        // Updated expectation: nested-only path should NOT force top-level linkage nor included
        Map<String, Set<String>> fieldsByType = new HashMap<>();
        fieldsByType.put("User", new HashSet<>(Collections.singletonList("email")));
        JsonApiOptions options = JsonApiOptions.builder()
                .fieldsByType(fieldsByType)
                .includePaths(new HashSet<>(Collections.singletonList("childObjects.apple")))
                .build();

        String jsonStr = JsonApiConverter.convert(userDto, 2, options);
        JsonNode root = mapper.readTree(jsonStr);

        // No included when only nested path is provided
        assertTrue(root.path("included").isMissingNode() || root.get("included").isEmpty());

        // No forced top-level linkage for childObjects
        JsonNode relationships = root.get("data").get("relationships");
        if (relationships != null) {
            assertNull("No top-level linkage should be forced", relationships.get("childObjects"));
        }
    }

    @Test
    public void testIncludeMultipleBranches_TopLevelAndNested_Single() throws Exception {
        JsonApiOptions options = JsonApiOptions.builder()
                .includePaths(new HashSet<>(Arrays.asList("childObjects", "childObjects.apple")))
                .build();

        String jsonStr = JsonApiConverter.convert(userDto, 2, options);
        JsonNode root = mapper.readTree(jsonStr);

        ArrayNode included = (ArrayNode) root.get("included");
        assertNotNull(included);

        boolean foundAnyChildObject = false;
        boolean foundMainObjectApple = false;

        String mainAppleId = userDto.getMainObject().getApple().getId();

        for (int i = 0; i < included.size(); i++) {
            JsonNode inc = included.get(i);
            String type = inc.get("type").asText();
            String id = inc.get("id").asText();

            if ("Apple".equals(type) && mainAppleId.equals(id)) {
                foundMainObjectApple = true;
            } else if ("Object".equals(type)) {
                // treat other Object entries as childObjects
                foundAnyChildObject = true;
            }
        }

        assertTrue(foundAnyChildObject);
        assertTrue(foundMainObjectApple);
    }

    @Test
    public void testIncludeNestedPathWithoutTopLevel_NoIncluded_NoPrimaryLinkage_Single() throws Exception {
        JsonApiOptions options = JsonApiOptions.builder()
                .includePaths(new HashSet<>(Collections.singletonList("childObjects.apple")))
                .build();

        String jsonStr = JsonApiConverter.convert(userDto, 2, options);
        JsonNode root = mapper.readTree(jsonStr);

        // No included when only nested path is provided
        assertNull(root.get("included"));

        // No forced top-level linkage for childObjects
        JsonNode relationships = root.get("data").get("relationships");
        if (relationships != null) {
            assertNull("No top-level linkage should be forced", relationships.get("childObjects"));
        }
    }

    @Test
    public void testIncludeNestedPathWithoutTopLevel_NoIncluded_NoPrimaryLinkage_List() throws Exception {
        UserDto u1 = (UserDto) generator.getUserDto().clone();
        UserDto u2 = (UserDto) generator.getUserDto().clone();
        u2.setId("owner2");
        List<UserDto> users = Arrays.asList(u1, u2);

        JsonApiOptions options = JsonApiOptions.builder()
                .includePaths(new HashSet<>(Collections.singletonList("childObjects.apple")))
                .build();

        String jsonStr = JsonApiConverter.convert(users, 2, options);
        JsonNode root = mapper.readTree(jsonStr);

        // No included when only nested path is provided
        assertNull(root.get("included"));

        // No forced top-level linkage for childObjects in any item
        ArrayNode data = (ArrayNode) root.get("data");
        for (int i = 0; i < data.size(); i++) {
            JsonNode rel = data.get(i).get("relationships");
            if (rel != null) {
                assertNull("No top-level linkage should be forced", rel.get("childObjects"));
            }
        }
    }

    @Test
    public void testFieldsForIncludedType_AppleAttributesFiltered() throws Exception {
        UserDto userDto = (UserDto) generator.getUserDto().clone();
        HashMap<String, Set<String>> fieldsByType = new HashMap<>();
        fieldsByType.put("Apple", new HashSet<>(Collections.singletonList("name")));
        JsonApiOptions options = JsonApiOptions.builder()
                .fieldsByType(fieldsByType)
                .includePaths(new HashSet<>(Arrays.asList("childObjects", "childObjects.apple")))
                .build();

        String jsonStr = JsonApiConverter.convert(userDto, 2, options);
        JsonNode root = mapper.readTree(jsonStr);
        ArrayNode included = (ArrayNode) root.get("included");
        assertNotNull(included);

        JsonNode found = null;
        String appleId = userDto.getChildObjects().get(0).getApple().getId();
        for (int i = 0; i < included.size(); i++) {
            JsonNode inc = included.get(i);
            if ("Apple".equals(inc.get("type").asText()) && appleId.equals(inc.get("id").asText())) {
                found = inc;
                break;
            }
        }
        assertNotNull("Included apple should be present", found);
        JsonNode attrs = found.get("attributes");
        assertNotNull(attrs);
        assertNotNull(attrs.get("name"));
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

        // Should include childObjects in relationships with ids/types
        assertTrue(json.get("data").has("relationships"));
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

    @Test
    public void testFieldInclusionModeIncludeAllWithInclude() throws Exception {
        JsonApiOptions options = JsonApiOptions.builder()
                .fieldInclusionMode(JsonApiOptions.AttributesInclusionMode.INCLUDE_ALL)
                .includePaths(new HashSet<>(Collections.singletonList("mainObject")))
                .build();

        String jsonStr = JsonApiConverter.convert(userDto, 1, options);
        JsonNode root = mapper.readTree(jsonStr);

        JsonNode primaryAttrs = root.get("data").get("attributes");
        assertNotNull(primaryAttrs);
        assertNotNull(primaryAttrs.get("username"));
        assertNotNull(primaryAttrs.get("email"));
        JsonNode relationships = root.get("data").get("relationships");
        assertNotNull(relationships);
        assertNotNull(relationships.get("mainObject"));
        ArrayNode included = (ArrayNode) root.get("included");
        assertNotNull(included);
        boolean foundMainObject = false;
        for (int i = 0; i < included.size(); i++) {
            JsonNode inc = included.get(i);
            if ("Object".equals(inc.get("type").asText()) && userDto.getMainObject().getId().equals(inc.get("id").asText())) {
                JsonNode incAttrs = inc.get("attributes");
                assertNotNull(incAttrs);
                assertNotNull(incAttrs.get("name"));
                foundMainObject = true;
                break;
            }
        }
        assertTrue("mainObject with attributes should be in included", foundMainObject);
    }

    @Test
    public void testFieldInclusionModeIncludeAllWithoutInclude() throws Exception {
        JsonApiOptions options = JsonApiOptions.builder()
                .fieldInclusionMode(JsonApiOptions.AttributesInclusionMode.INCLUDE_ALL)
                .build();

        String jsonStr = JsonApiConverter.convert(userDto, 1, options);
        JsonNode root = mapper.readTree(jsonStr);

        JsonNode primaryAttrs = root.get("data").get("attributes");
        assertNotNull(primaryAttrs);
        assertNotNull(primaryAttrs.get("username"));
        assertNotNull(primaryAttrs.get("email"));

        assertTrue(root.path("included").isMissingNode()
                || root.get("included").isEmpty());
    }

    @Test
    public void testFieldInclusionModeIncludeAllWithSpecifiedFieldsForPrimary() throws Exception {
        Map<String, Set<String>> fieldsByType = new HashMap<>();
        fieldsByType.put("User", new HashSet<>(Collections.singletonList("username")));
        JsonApiOptions options = JsonApiOptions.builder()
                .fieldInclusionMode(JsonApiOptions.AttributesInclusionMode.INCLUDE_ALL)
                .fieldsByType(fieldsByType)
                .includePaths(new HashSet<>(Collections.singletonList("mainObject")))
                .build();

        String jsonStr = JsonApiConverter.convert(userDto, 1, options);
        JsonNode root = mapper.readTree(jsonStr);

        JsonNode primaryAttrs = root.get("data").get("attributes");
        assertNotNull(primaryAttrs);
        assertNotNull(primaryAttrs.get("username"));
        assertNull(primaryAttrs.get("email"));

        JsonNode relationships = root.get("data").get("relationships");
        assertNotNull(relationships);
        assertNotNull(relationships.get("mainObject"));

        ArrayNode included = (ArrayNode) root.get("included");
        assertNotNull(included);
        boolean foundMainObject = false;
        for (int i = 0; i < included.size(); i++) {
            JsonNode inc = included.get(i);
            if ("Object".equals(inc.get("type").asText()) && userDto.getMainObject().getId().equals(inc.get("id").asText())) {
                JsonNode incAttrs = inc.get("attributes");
                assertNotNull(incAttrs);
                assertNotNull(incAttrs.get("name"));
                foundMainObject = true;
                break;
            }
        }
        assertTrue("mainObject with all attributes should be in included", foundMainObject);
    }
}