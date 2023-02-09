package sg.edu.nus.comp.cs4218.exception;

public class LsException extends AbstractApplicationException {

    private static final long serialVersionUID = 5001961656291923161L;

    public LsException(String message) {
        super("ls: " + message);
    }

}
