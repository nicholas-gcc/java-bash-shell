package sg.edu.nus.comp.cs4218.impl.app;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sg.edu.nus.comp.cs4218.Environment;
import sg.edu.nus.comp.cs4218.exception.ShellException;
import sg.edu.nus.comp.cs4218.exception.WcException;
import sg.edu.nus.comp.cs4218.impl.util.IOUtils;

import java.io.*;

import static org.junit.jupiter.api.Assertions.*;
import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.*;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.CHAR_FILE_SEP;

public class WcApplicationTest {

    WcApplication wcApplication;
    InputStream stdin;
    OutputStream stdout;
    static final String TESTING_PATH = CHAR_FILE_SEP + "assets" + CHAR_FILE_SEP + "app" + CHAR_FILE_SEP + "wc";
    static final String CWD = System.getProperty("user.dir");

    @BeforeEach
    void setup() {
        wcApplication = new WcApplication();
        stdin = System.in;
        stdout = System.out;
        Environment.currentDirectory += TESTING_PATH;
    }
    @AfterEach
    void reset() {
        Environment.currentDirectory = CWD;
    }

    @Test
    void wc_RunWithNullStdout_ThrowsWcException() {
        String [] array = {};
        stdout = null;
        WcException wcException = assertThrows(WcException.class, () -> wcApplication.run(array, stdin, stdout));
        assertEquals("wc: " + ERR_NULL_STREAMS, wcException.getMessage());
    }

    @Test
    void wc_RunWithOneFile_ShouldWcCorrectly() {
        String [] array = {"wc_test1.txt"};
        assertDoesNotThrow(() -> {
            wcApplication.run(array, stdin, stdout);
        });
    }

    @Test
    void wc_RunWithMultipleFiles_ShouldWcCorrectly() {
        String [] array = {"wc_test1.txt", "wc_test2.txt"};
        assertDoesNotThrow(() -> {
            wcApplication.run(array, stdin, stdout);
        });
    }

    @Test
    void wc_RunWithStdin_ShouldWcCorrectly() {
        String [] array = {};
        assertDoesNotThrow(() -> {
            stdin = new FileInputStream(Environment.currentDirectory + CHAR_FILE_SEP + "wc_test1.txt");
            wcApplication.run(array, stdin, stdout);
        });
    }

    @Test
    void wc_RunWithArguments_ShouldWcCorrectly() {
        String [] array = {"-l", "-c", "wc_test1.txt"};
        assertDoesNotThrow(() -> {
            wcApplication.run(array, stdin, stdout);
        });
    }

    @Test
    void wc_RunWithNullStdin_ThrowsWcException() {
        String [] array = {};
        stdin = null;
        WcException wcException = assertThrows(WcException.class, () -> wcApplication.run(array, stdin, stdout));
        assertEquals("wc: " + ERR_NULL_STREAMS, wcException.getMessage());
    }

    @Test
    void wc_RunWithClosedStdout_ThrowsWcException() throws FileNotFoundException, ShellException {
        String [] array = {"output.txt"};
        stdout = new FileOutputStream("output.txt");
        IOUtils.closeOutputStream(stdout);
        WcException wcException = assertThrows(WcException.class,() -> wcApplication.run(array, stdin, stdout));
        assertEquals("wc: " + ERR_WRITE_STREAM, wcException.getMessage());
        File file = new File("output.txt");
        file.delete();
    }

    @Test
    void wc_CountFromFilesNull_ThrowsException() {
        Exception exception = assertThrows(Exception.class, () ->
                wcApplication.countFromFiles(true, true, true, null)
        );
        assertEquals(ERR_GENERAL, exception.getMessage());
    }

    @Test
    void wc_CountFromFilesNonExistingFile_ShowsFileNotFound() throws Exception {
        String result = wcApplication.countFromFiles(true, true, true, "blah");
        assertEquals("wc: " + ERR_FILE_NOT_FOUND, result);
    }

    @Test
    void wc_CountFromFilesValidFile_CountsCorrectly() {
        assertDoesNotThrow(() -> {
            wcApplication.countFromFiles(true, true, true, "wc_test1.txt");
        });
    }

    @Test
    void wc_CountFromFilesMultipleValidFiles_CountsCorrectly() {
        assertDoesNotThrow(() -> {
            wcApplication.countFromFiles(true, true, true,
                    new String[] {"wc_test1.txt", "wc_test2.txt"});
        });
    }

    @Test
    void wc_CountFromStdinNullStdin_ThrowsException() {
        Exception exception = assertThrows(Exception.class, () ->
                wcApplication.countFromStdin(true, true, true, null)
        );
        assertEquals(ERR_NULL_STREAMS, exception.getMessage());
    }

    @Test
    void wc_CountFromStdinValidFile_CountsCorrectly() {
        assertDoesNotThrow(() -> {
            stdin = new FileInputStream(Environment.currentDirectory + CHAR_FILE_SEP + "wc_test1.txt");
            wcApplication.countFromStdin(true, true, true, stdin);
        });
    }

    @Test
    void wc_GetCountReportNullInput_ThrowsException() {
        Exception exception = assertThrows(Exception.class, () ->
                wcApplication.getCountReport(null)
        );
        assertEquals(ERR_NULL_STREAMS, exception.getMessage());
    }

    @Test void wc_GetCountReportValidInput_GetsCorrectly() {
        assertDoesNotThrow(() -> {
            stdin = new FileInputStream(Environment.currentDirectory + CHAR_FILE_SEP + "wc_test1.txt");
            wcApplication.getCountReport(stdin);
        });
    }
}
