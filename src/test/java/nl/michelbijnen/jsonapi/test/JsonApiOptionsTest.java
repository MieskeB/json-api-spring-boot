package nl.michelbijnen.jsonapi.test;

import nl.michelbijnen.jsonapi.parser.JsonApiOptions;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.*;

public class JsonApiOptionsTest {

    @Test
    public void testBuilderDefault() {
        JsonApiOptions options = JsonApiOptions.builder().build();
        assertNotNull(options.getFieldsByType());
        assertTrue(options.getFieldsByType().isEmpty());
        assertEquals(JsonApiOptions.AttributesInclusionMode.EXCLUDE_ALL, options.getFieldInclusionMode());
        assertTrue(options.topLevelIncludeRelations().isEmpty());
    }

    @Test
    public void testBuilderWithFieldsByType() {
        Map<String, Set<String>> fields = new HashMap<>();
        fields.put("User", new HashSet<>(Arrays.asList("name", "email")));
        JsonApiOptions options = JsonApiOptions.builder()
                .fieldsByType(fields)
                .build();
        assertEquals(fields, options.getFieldsByType());
    }

    @Test
    public void testBuilderWithIncludePaths() {
        Set<String> includes = new HashSet<>(Arrays.asList("author", "comments"));
        JsonApiOptions options = JsonApiOptions.builder()
                .includePaths(includes)
                .build();
        assertEquals(new HashSet<>(Arrays.asList("author", "comments")), options.topLevelIncludeRelations());
    }

    @Test
    public void testBuilderWithFieldInclusionMode() {
        JsonApiOptions options = JsonApiOptions.builder()
                .fieldInclusionMode(JsonApiOptions.AttributesInclusionMode.INCLUDE_ALL)
                .build();
        assertEquals(JsonApiOptions.AttributesInclusionMode.INCLUDE_ALL, options.getFieldInclusionMode());
    }

    @Test
    public void testHasFieldsForType() {
        Map<String, Set<String>> fields = new HashMap<>();
        fields.put("User", new HashSet<>(Collections.singletonList("name")));
        JsonApiOptions options = JsonApiOptions.builder()
                .fieldsByType(fields)
                .build();
        assertTrue(options.hasFieldsForType("User"));
        assertFalse(options.hasFieldsForType("Post"));
        assertFalse(options.hasFieldsForType(null));
    }

    @Test
    public void testFieldsForType() {
        Map<String, Set<String>> fields = new HashMap<>();
        fields.put("User", new HashSet<>(Arrays.asList("name", "email")));
        JsonApiOptions options = JsonApiOptions.builder()
                .fieldsByType(fields)
                .build();
        assertEquals(new HashSet<>(Arrays.asList("name", "email")), options.fieldsForType("User"));
        assertTrue(options.fieldsForType("Post").isEmpty());
        assertTrue(options.fieldsForType(null).isEmpty());
    }

    @Test
    public void testTopLevelIncludeRelations() {
        Set<String> includes = new HashSet<>(Arrays.asList("author", "comments.replies"));
        JsonApiOptions options = JsonApiOptions.builder()
                .includePaths(includes)
                .build();
        Set<String> topLevel = options.topLevelIncludeRelations();
        assertTrue(topLevel.contains("author"));
        assertFalse(topLevel.contains("comments"));
        assertFalse(topLevel.contains("comments.replies"));
    }

    @Test
    public void testFieldsByTypeNormalization() {
        Map<String, Set<String>> input = new HashMap<>();
        input.put("fields[User]", new HashSet<>(Collections.singletonList("name")));
        JsonApiOptions options = JsonApiOptions.builder()
                .fieldsByType(input)
                .build();
        assertTrue(options.hasFieldsForType("User"));
        assertEquals(new HashSet<>(Collections.singletonList("name")), options.fieldsForType("User"));
    }
}