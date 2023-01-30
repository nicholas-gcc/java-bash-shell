package sg.edu.nus.comp.cs4218.impl.exception;

import sg.edu.nus.comp.cs4218.exception.AbstractApplicationException;

public class SortException extends AbstractApplicationException {

    private static final long serialVersionUID = 3894758187716957490L;

    public SortException(String message) {
        super("sort: " + message);
    }
}