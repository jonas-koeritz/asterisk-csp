package opencsp.exceptions;

public class UnsupportedMessageFormatException extends Exception {
    public UnsupportedMessageFormatException() {
        super("Only ECMA-323 compliant TCP without SOAP Transport is supported.");
    }

    public UnsupportedMessageFormatException(String msg) {
        super(msg);
    }
}
