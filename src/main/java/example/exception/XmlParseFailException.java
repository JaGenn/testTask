package example.exception;

public class XmlParseFailException extends RuntimeException {
    public XmlParseFailException(String message, Throwable cause) {
        super(message, cause);
    }

    public XmlParseFailException(String message) {
        super(message);
    }

    public XmlParseFailException(Throwable cause) {
        super(cause);
    }
}
