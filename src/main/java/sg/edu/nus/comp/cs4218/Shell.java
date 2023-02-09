package sg.edu.nus.comp.cs4218;

import sg.edu.nus.comp.cs4218.exception.AbstractApplicationException;
import sg.edu.nus.comp.cs4218.exception.ShellException;

import java.io.FileNotFoundException;
import java.io.OutputStream;

public interface Shell {

    /**
     * Parses and evaluates user's command line.
     */
    void parseAndEvaluate(String cmdline, OutputStream stdout) throws AbstractApplicationException, ShellException, FileNotFoundException;
}
