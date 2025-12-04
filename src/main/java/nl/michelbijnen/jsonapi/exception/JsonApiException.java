package nl.michelbijnen.jsonapi.exception;

public class JsonApiException extends RuntimeException {

    /**
     * Constructs a new JsonApiException with the specified detail message.
     *
     * @param message the detail message
     */
    public JsonApiException(String message) {
        super(message);
    }
}
