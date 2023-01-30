package sg.edu.nus.comp.cs4218.exception;

public class DirectoryNotFoundException extends Exception {
    private static final long serialVersionUID = 9208237916723540057L;

    public DirectoryNotFoundException(String message) {
        super(message);
    }
}
