package id.ac.ui.cs.advprog.yomureadingservice.client;

/**
 * Thrown when the Reading Service responds with HTTP 404.
 */
public class ReadingServiceNotFoundException extends RuntimeException {

    public ReadingServiceNotFoundException(String message) {
        super(message);
    }

    public ReadingServiceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
