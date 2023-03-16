package sg.edu.nus.comp.cs4218.exception;

public class SortException extends AbstractApplicationException {

    private static final long serialVersionUID = 3894758187716957490L;

    public SortException(String message) {
        super("sort: " + message);
    }
    public SortException(String message, Throwable cause) {
        super("sort: " + message, cause);
    }
}