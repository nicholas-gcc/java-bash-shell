package sg.edu.nus.comp.cs4218.exception;

public class WcException extends AbstractApplicationException {

    private static final long serialVersionUID = -8535567786679220113L;

    public WcException(String message) {
        super("wc: " + message);
    }

    public WcException(Exception exception, String message) {
        super("wc: " + message);
    }
}