package sg.edu.nus.comp.cs4218.impl.exception;

import sg.edu.nus.comp.cs4218.exception.AbstractApplicationException;

public class WcException extends AbstractApplicationException {

    private static final long serialVersionUID = -8535567786679220113L;

    public WcException(String message) {
        super("wc: " + message);
    }
}