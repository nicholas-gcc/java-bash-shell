package sg.edu.nus.comp.cs4218.impl.cmd;

import sg.edu.nus.comp.cs4218.Command;
import sg.edu.nus.comp.cs4218.exception.AbstractApplicationException;
import sg.edu.nus.comp.cs4218.exception.ShellException;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.OutputStream;

public class IORedirectionCommand implements Command {
    @Override
    public void evaluate(InputStream stdin, OutputStream stdout) throws AbstractApplicationException, ShellException, FileNotFoundException {
        
    }

    @Override
    public void terminate() {

    }
}
