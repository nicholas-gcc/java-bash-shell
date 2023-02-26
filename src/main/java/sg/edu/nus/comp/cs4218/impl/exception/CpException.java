package sg.edu.nus.comp.cs4218.impl.exception;

import sg.edu.nus.comp.cs4218.exception.AbstractApplicationException;

public class CpException extends AbstractApplicationException {
    public CpException(String message) {
        super("cp: " + message);
    }
}
