package sg.edu.nus.comp.cs4218.exception;

public class SortException extends AbstractApplicationException {

    private static final long serialVersionUID = 3894758187716957490L;
    public static final String INVALID_CMD = "Invalid command code.";
    public static final String PROB_SORT_FILE = "Problem sort from file: ";
    public static final String PROB_SORT_STDIN = "Problem sort from stdin: ";

    public SortException(String message) {
        super("sort: " + message);
    }
    public SortException(Exception exception, String message) {
        super("sort: " + message);
    }
}