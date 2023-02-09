package sg.edu.nus.comp.cs4218.exception;

public class InvalidArgsException extends Exception {
    public InvalidArgsException(String message) {
        super(message);
    }

    public InvalidArgsException(String message, Throwable cause) {
        super(message, cause);
    }
}
