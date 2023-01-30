package sg.edu.nus.comp.cs4218.app;

import sg.edu.nus.comp.cs4218.Application;

import java.io.InputStream;


public interface GrepInterface extends Application {
    /**
     * Returns string containing lines which match the specified pattern in the given files
     *
     * @param pattern           String specifying a regular expression in JAVA format
     * @param isCaseInsensitive Boolean option to perform case insensitive matching
     * @param isCountLines      Boolean option to only write out a count of matched lines
     * @param isPrefixFileName  Boolean option to print file name with output lines
     * @param fileNames         Array of file names (not including "-" for reading from stdin)
     * @throws Exception
     */
    String grepFromFiles(String pattern, Boolean isCaseInsensitive, Boolean isCountLines, Boolean isPrefixFileName, String... fileNames)
            throws Exception;

    /**
     * Returns string containing lines which match the specified pattern in Stdin
     *
     * @param pattern           String specifying a regular expression in JAVA format
     * @param isCaseInsensitive Boolean option to perform case insensitive matching
     * @param isCountLines      Boolean option to only write out a count of matched lines
     * @param isPrefixFileName  Boolean option to print file name with output lines
     * @param stdin             InputStream containing arguments from Stdin
     * @throws Exception
     */
    String grepFromStdin(String pattern, Boolean isCaseInsensitive, Boolean isCountLines, Boolean isPrefixFileName, InputStream stdin)
            throws Exception;

    /**
     * Returns string containing lines which match the specified pattern in Stdin and given files
     *
     * @param pattern           String specifying a regular expression in JAVA format
     * @param isCaseInsensitive Boolean option to perform case insensitive matching
     * @param isCountLines      Boolean option to only write out a count of matched lines
     * @param isPrefixFileName  Boolean option to print file name with output lines
     * @param stdin             InputStream containing arguments from Stdin
     * @param fileNames         Array of file names (including "-" for reading from stdin)
     * @throws Exception
     */
    String grepFromFileAndStdin(String pattern, Boolean isCaseInsensitive, Boolean isCountLines, Boolean isPrefixFileName, InputStream stdin, String... fileNames)
            throws Exception;
}
