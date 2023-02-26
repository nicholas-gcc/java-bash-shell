package sg.edu.nus.comp.cs4218.exception;

public class UniqException extends AbstractApplicationException{
    public UniqException(String message) {
        super("uniq: " + message);
    }
}
