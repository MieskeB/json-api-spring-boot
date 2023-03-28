package nl.michelbijnen.jsonapi.exception;

public class JsonApiException extends RuntimeException {
    public JsonApiException(String message) {
        super(message);
    }
}
