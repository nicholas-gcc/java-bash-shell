package sg.edu.nus.comp.cs4218.impl.app;

import sg.edu.nus.comp.cs4218.app.TeeInterface;
import sg.edu.nus.comp.cs4218.exception.AbstractApplicationException;

import java.io.InputStream;
import java.io.OutputStream;

public class TeeApplication implements TeeInterface {
    public String teeFromStdin(Boolean isAppend, InputStream stdin, String... fileName) throws Exception {
        // TODO: Implement method
        return "";
    }

    public void run(String[] args, InputStream stdin, OutputStream stdout)
            throws AbstractApplicationException {
        // TODO: Implement method
    }

}
