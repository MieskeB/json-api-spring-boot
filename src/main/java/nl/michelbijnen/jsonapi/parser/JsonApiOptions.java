package nl.michelbijnen.jsonapi.parser;

import java.util.*;

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

    public Set<String> getIncludePaths() {
        return includePaths;
    }

    public boolean hasFieldsForType(String type) {
        return type != null && fieldsByType.containsKey(type);
    }

    public Set<String> fieldsForType(String type) {
        Set<String> s = fieldsByType.get(type);
        return s == null ? Collections.emptySet() : s;
    }

    public Set<String> topLevelIncludeRelations() {
        if (includePaths.isEmpty()) return Collections.emptySet();
        Set<String> top = new HashSet<>();
        for (String p : includePaths) {
            if (p == null || p.isEmpty()) continue;
            if (!p.contains(".")) {
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
