package sg.edu.nus.comp.cs4218.exception;

public class CdException extends AbstractApplicationException {

    private static final long serialVersionUID = -4730922172179294678L;

    public CdException(String message) {
        super("cd: " + message);
    }
}