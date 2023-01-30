package sg.edu.nus.comp.cs4218.exception;

public class RmException extends AbstractApplicationException {

    private static final long serialVersionUID = 6616752571518808461L;

    public RmException(String message) {
        super("rm: " + message);
    }
}