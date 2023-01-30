package sg.edu.nus.comp.cs4218.app;

import sg.edu.nus.comp.cs4218.Application;

import java.io.InputStream;

public interface CatInterface extends Application {
    /**
     * Returns string containing the content of the specified file
     *
     * @param isLineNumber Prefix lines with their corresponding line number starting from 1
     * @param fileName     Array of String of file names (not including "-" for reading from stdin)
     * @return
     * @throws Exception
     */
    String catFiles(Boolean isLineNumber, String... fileName) throws Exception;

    /**
     * Returns string containing the content of the standard input
     *
     * @param isLineNumber Prefix lines with their corresponding line number starting from 1
     * @param stdin        InputStream containing arguments from Stdin
     * @return
     * @throws Exception
     */
    String catStdin(Boolean isLineNumber, InputStream stdin)
            throws Exception;


    /**
     * Returns string containing the content of the standard input and specified file
     *
     * @param isLineNumber Prefix lines with their corresponding line number starting from 1
     * @param stdin        InputStream containing arguments from Stdin
     * @param fileName     Array of String of file names (including "-" for reading from stdin)
     * @return
     * @throws Exception
     */
    String catFileAndStdin(Boolean isLineNumber, InputStream stdin, String... fileName) throws Exception;
}
