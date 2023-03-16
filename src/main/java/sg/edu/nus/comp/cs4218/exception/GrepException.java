package sg.edu.nus.comp.cs4218.exception;

public class GrepException extends AbstractApplicationException {

    private static final long serialVersionUID = -5883292222072101576L;

    public GrepException(String message) {
        super("grep: " + message);
    }

    public GrepException(String message, Throwable cause) {
        super("grep: " + message, cause);
    }

}
