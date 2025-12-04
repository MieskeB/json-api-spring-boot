package nl.michelbijnen.jsonapi.util;

public final class JsonApiConstants {

    private JsonApiConstants() {}

    public static final String JSON_API_DEPTH_PROPERTY = "jsonapi.depth";
    public static final String JSON_API_DEPTH_DEFAULT_VALUE = "1";
    public static final String JSON_API_BASE_URL_PROPERTY = "jsonapi.baseUrl";
    public static final String JSON_API_BASE_URL_DEFAULT_VALUE = "http://localhost:8080";


    public static final String DATA = "data";
    public static final String ID = "id";
    public static final String TYPE = "type";
    public static final String ATTRIBUTES = "attributes";
    public static final String RELATIONSHIPS = "relationships";
    public static final String INCLUDED = "included";
    public static final String LINKS = "links";

    public static final String DOT = ".";
    public static final String SLASH = "/";

    public static final String ID_NOT_ENTERED = "Id not entered";
    public static final String GETTER_DOES_NOT_EXIST = "Getter for field '%s' does not exist";
    public static final String JSON_API_OBJECT_MISSING = "@JsonApiObject(\"<classname>\") missing";
    public static final String JSON_API_ID_MISSING = "No field with @JsonApiId is found";
    public static final String JSON_API_ERROR_CONVERTING_TO_JSON = "Error converting to JSON";

}
