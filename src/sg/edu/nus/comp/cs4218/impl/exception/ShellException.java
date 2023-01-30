package sg.edu.nus.comp.cs4218.impl.exception;

public class ShellException extends Exception {

    private static final long serialVersionUID = -4439395674558704575L;

    public ShellException(String message) {
        super("shell: " + message);
    }
}