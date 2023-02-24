package sg.edu.nus.comp.cs4218.impl.app;

import sg.edu.nus.comp.cs4218.app.RmInterface;
import sg.edu.nus.comp.cs4218.exception.RmException;

import java.io.InputStream;
import java.io.OutputStream;

public class RmApplication implements RmInterface {

    @Override
    public void remove(Boolean isEmptyFolder, Boolean isRecursive, String... fileName) throws RmException {
        // TODO: Implement logic for removing file
    }

    @Override
    public void run(String[] args, InputStream stdin, OutputStream stdout)
            throws RmException {
        // TODO: Implement logic for running RmApplication
    }

}
