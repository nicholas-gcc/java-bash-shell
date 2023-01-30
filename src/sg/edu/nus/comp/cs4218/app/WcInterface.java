package sg.edu.nus.comp.cs4218.app;

import sg.edu.nus.comp.cs4218.Application;

import java.io.InputStream;

public interface WcInterface extends Application {
    /**
     * Returns string containing the number of lines, words, and bytes in input files
     *
     * @param isBytes  Boolean option to count the number of Bytes
     * @param isLines  Boolean option to count the number of lines
     * @param isWords  Boolean option to count the number of words
     * @param fileName Array of String of file names (not including "-" for reading from stdin)
     * @return
     * @throws Exception
     */
    String countFromFiles(Boolean isBytes, Boolean isLines, Boolean isWords,
                          String... fileName) throws Exception;

    /**
     * Returns string containing the number of lines, words, and bytes in standard input
     *
     * @param isBytes Boolean option to count the number of Bytes
     * @param isLines Boolean option to count the number of lines
     * @param isWords Boolean option to count the number of words
     * @param stdin   InputStream containing arguments from Stdin
     * @return
     * @throws Exception
     */
    String countFromStdin(Boolean isBytes, Boolean isLines, Boolean isWords,
                          InputStream stdin)
            throws Exception;

    /**
     * Returns string containing the number of lines, words, and bytes in standard input and file
     *
     * @param isBytes  Boolean option to count the number of Bytes
     * @param isLines  Boolean option to count the number of lines
     * @param isWords  Boolean option to count the number of words
     * @param stdin    InputStream containing arguments from Stdin
     * @param fileName Array of String of file names (including "-" for reading from stdin)
     * @return
     * @throws Exception
     */
    String countFromFileAndStdin(Boolean isBytes, Boolean isLines, Boolean isWords,
                                 InputStream stdin, String... fileName)
            throws Exception;

}
