package sg.edu.nus.comp.cs4218.exception;

public class MvException extends AbstractApplicationException {
    public MvException(String message) {
        super("mv: " + message);
    }
}

