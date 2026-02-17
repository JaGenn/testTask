package example.exception;

public class DDLGenerationException extends RuntimeException {
    public DDLGenerationException(String message) {
        super(message);
    }

    public DDLGenerationException(String message, Throwable cause) {
        super(message, cause);
    }
}
