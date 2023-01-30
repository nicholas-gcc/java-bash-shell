package sg.edu.nus.comp.cs4218.impl.exception;

import sg.edu.nus.comp.cs4218.exception.AbstractApplicationException;

public class GrepException extends AbstractApplicationException {

    private static final long serialVersionUID = -5883292222072101576L;

    public GrepException(String message) {
        super("grep: " + message);
    }

}
