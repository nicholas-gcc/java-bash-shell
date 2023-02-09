package sg.edu.nus.comp.cs4218.exception;

public class CatException extends AbstractApplicationException {

    private static final long serialVersionUID = 2333796686823942499L;

    public CatException(String message) {
        super("cat: " + message);
    }
}