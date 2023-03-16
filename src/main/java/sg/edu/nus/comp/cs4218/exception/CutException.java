package sg.edu.nus.comp.cs4218.exception;

public class CutException extends AbstractApplicationException {

    public CutException(String message) {
        super("cut: " + message);
    }

    public CutException(String message, Throwable cause) {
        super("cut: " + message, cause);
    }
}
