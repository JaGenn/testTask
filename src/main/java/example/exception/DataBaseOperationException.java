package example.exception;

public class DataBaseOperationException extends RuntimeException {
    public DataBaseOperationException(String message) {
        super(message);
    }

    public DataBaseOperationException(String message, Throwable cause) {
        super(message, cause);
    }
}
