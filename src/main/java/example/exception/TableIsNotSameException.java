package example.exception;

public class TableIsNotSameException extends RuntimeException {
    public TableIsNotSameException(String message) {
        super(message);
    }

    public TableIsNotSameException(String message, Throwable cause) {
        super(message, cause);
    }
}
