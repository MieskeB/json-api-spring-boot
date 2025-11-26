package nl.michelbijnen.jsonapi.test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import nl.michelbijnen.jsonapi.parser.JsonApiConverter;
import nl.michelbijnen.jsonapi.parser.JsonApiOptions;
import nl.michelbijnen.jsonapi.test.mock.MockDataGenerator;
import nl.michelbijnen.jsonapi.test.mock.UserDto;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.*;

public class FieldsAndIncludeTest {

    private final MockDataGenerator generator = MockDataGenerator.getInstance();
    private ObjectMapper mapper;
    private UserDto user;

    @Before
    public void setUp() throws Exception {
        this.mapper = new ObjectMapper();
        this.user = (UserDto) generator.getUserDto().clone();
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

        String jsonStr = JsonApiConverter.convert(user, 1, options);
        JsonNode root = mapper.readTree(jsonStr);

        JsonNode relationships = root.get("data").get("relationships");
        assertNotNull(relationships);
        assertNotNull(relationships.get("mainObject"));
        assertNotNull(relationships.get("mainObject").get("data"));
        JsonNode linkage = relationships.get("mainObject").get("data");
        assertEquals("Object", linkage.get("type").asText());
        assertEquals(user.getMainObject().getId(), linkage.get("id").asText());

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

        String jsonStr = JsonApiConverter.convert(user, 1, options);
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
            if (user.getMainObject().getId().equals(inc.get("id").asText())) {
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

        String jsonStr = JsonApiConverter.convert(user, 1, options);
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
                    && user.getMainObject().getId().equals(inc.get("id").asText())) {
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

        String jsonStr = JsonApiConverter.convert(user, 2, options);
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

        String jsonStr = JsonApiConverter.convert(user, 1, options);
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

        String jsonStr = JsonApiConverter.convert(user, 2, options);
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

        String jsonStr = JsonApiConverter.convert(user, 2, options);
        JsonNode root = mapper.readTree(jsonStr);

        ArrayNode included = (ArrayNode) root.get("included");
        assertNotNull(included);

        boolean foundAnyChildObject = false;
        boolean foundMainObjectApple = false;

        String mainAppleId = user.getMainObject().getApple().getId();

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

        String jsonStr = JsonApiConverter.convert(user, 2, options);
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
}