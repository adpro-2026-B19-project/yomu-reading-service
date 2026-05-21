package id.ac.ui.cs.advprog.yomureadingservice.client;

/**
 * Thrown when the Reading Service is unavailable or returns an unexpected error.
 */
public class ReadingServiceException extends RuntimeException {

    public ReadingServiceException(String message) {
        super(message);
    }

    public ReadingServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
