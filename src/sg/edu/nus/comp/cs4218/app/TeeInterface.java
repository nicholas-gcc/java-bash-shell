package sg.edu.nus.comp.cs4218.app;

import sg.edu.nus.comp.cs4218.Application;

import java.io.InputStream;

public interface TeeInterface extends Application {

    /**
     * Reads from standard input and write to both the standard output and files
     *
     * @param isAppend Boolean option to append the standard input to the contents of the input files
     * @param stdin    InputStream containing arguments from Stdin
     * @param fileName Array of String of file names
     * @return
     * @throws Exception
     */
    String teeFromStdin(Boolean isAppend, InputStream stdin, String... fileName) throws Exception;
}
