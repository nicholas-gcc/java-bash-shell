package sg.edu.nus.comp.cs4218.exception;

public class PasteException extends AbstractApplicationException {

    private static final long serialVersionUID = -742723164724927309L;

    public PasteException(String message) {
        super("paste: " + message);
    }
}
