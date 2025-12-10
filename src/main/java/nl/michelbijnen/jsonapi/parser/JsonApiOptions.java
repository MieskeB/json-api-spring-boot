package nl.michelbijnen.jsonapi.parser;

import java.util.*;

import static nl.michelbijnen.jsonapi.util.JsonApiConstants.DOT;

public class JsonApiOptions {
    private final Map<String, Set<String>> fieldsByType;
    private final Set<String> includePaths;

    private JsonApiOptions(Builder b) {
        Map<String, Set<String>> fbt = new HashMap<>();
        if (b.fieldsByType != null) {
            for (Map.Entry<String, Set<String>> e : b.fieldsByType.entrySet()) {
                fbt.put(e.getKey(), e.getValue() == null ? Collections.emptySet() : new HashSet<>(e.getValue()));
            }
        }
        this.fieldsByType = Collections.unmodifiableMap(fbt);
        this.includePaths = b.includePaths == null ? Collections.emptySet() : Collections.unmodifiableSet(new HashSet<>(b.includePaths));
    }

    public static Builder builder() {
        return new Builder();
    }

    /**
     * Checks whether a sparse fieldset is configured for the given JSON:API type.
     *
     * @param type the resource type (it may be {@code null})
     * @return {@code true} if a fieldset exists for the type; {@code false} otherwise
     */
    public boolean hasFieldsForType(String type) {
        return type != null && fieldsByType.containsKey(type);
    }

    /**
     * Returns the configured sparse fieldset for the given JSON:API type.
     * <p>
     * If no fields are configured for the type, returns an empty set.
     *
     * @param type the resource type
     * @return an immutable set of field names for the type, or an empty set if none
     */
    public Set<String> fieldsForType(String type) {
        Set<String> s = fieldsByType.get(type);
        return s == null ? Collections.emptySet() : s;
    }

    /**
     * Returns the set of top-level include relation names derived from {@code includePaths}.
     * <p>
     * A top-level relation is a path without a dot (e.g., {@code "author"} from
     * {@code ["author", "author.address", "comments"]} yields {@code ["author","comments"]}).
     * Null or empty paths are ignored.
     *
     * @return an immutable set of top-level relation names; empty if none
     */
    public Set<String> topLevelIncludeRelations() {
        if (includePaths.isEmpty()) return Collections.emptySet();
        Set<String> top = new HashSet<>();
        for (String p : includePaths) {
            if (p == null || p.isEmpty()) continue;
            if (!p.contains(DOT)) {
                top.add(p);
            }
        }
        return top;
    }

    public static class Builder {
        private Map<String, Set<String>> fieldsByType;
        private Set<String> includePaths;

        public Builder fieldsByType(Map<String, Set<String>> fieldsByType) {
            this.fieldsByType = fieldsByType;
            return this;
        }

        public Builder includePaths(Set<String> includePaths) {
            this.includePaths = includePaths;
            return this;
        }

        public JsonApiOptions build() {
            return new JsonApiOptions(this);
        }
    }
}
