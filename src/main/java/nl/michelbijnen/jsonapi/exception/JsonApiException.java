package nl.michelbijnen.jsonapi.exception;

public class JsonApiException extends RuntimeException {
    public JsonApiException() {
        super();
    }

    public JsonApiException(String message) {
        super(message);
    }

    public JsonApiException(String message, Throwable cause) {
        super(message, cause);
    }

    public JsonApiException(Throwable cause) {
        super(cause);
    }

    protected JsonApiException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
