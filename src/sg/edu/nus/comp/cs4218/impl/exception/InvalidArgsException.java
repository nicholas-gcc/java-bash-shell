package sg.edu.nus.comp.cs4218.impl.exception;

public class InvalidArgsException extends Exception {

    private static final long serialVersionUID = 7060671468402158735L;

    public InvalidArgsException(String message) {
        super(message);
    }

    public InvalidArgsException(String message, Throwable cause) {
        super(message, cause);
    }
}
