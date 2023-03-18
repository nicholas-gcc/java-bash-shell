package sg.edu.nus.comp.cs4218.impl.util;

import mockit.Mock;
import mockit.MockUp;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import sg.edu.nus.comp.cs4218.Environment;
import sg.edu.nus.comp.cs4218.exception.ShellException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.ERR_INVALID_APP;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.CHAR_FILE_SEP;


public class ApplicationRunnerTest {

    static final String CWD = System.getProperty("user.dir");
    static final String TESTING_PATH = CHAR_FILE_SEP + "assets" + CHAR_FILE_SEP + "util" + CHAR_FILE_SEP + "ApplicationRunner";
    static final String SAMPLE_FILE = "sample.txt";
    static final String NEW_FILE = "new.txt";
    static final String APP_ECHO = "echo";
    static final String APP_CD = "cd";
    static final String APP_WC = "wc";
    static final String APP_CP = "cp";
    static final String APP_GREP = "grep";
    static final String APP_CUT = "cut";
    static final String APP_EXIT = "exit";
    static final String APP_LS = "ls";
    static final String APP_PASTE = "paste";
    static final String APP_UNIQ = "uniq";
    static final String APP_MV = "mv";
    static final String APP_SORT = "sort";
    static final String APP_RM = "rm";
    static final String APP_TEE = "tee";
    static final String APP_CAT = "cat";
    static final String INVALID_APP = "invalidapp";

    ApplicationRunner applicationRunner = new ApplicationRunner();
    OutputStream outputStream;

    @BeforeEach
    void setup() {
        Environment.currentDirectory += TESTING_PATH;
        outputStream = new ByteArrayOutputStream();
    }

    @AfterEach
    void reset() throws IOException {
        Environment.currentDirectory = this.CWD;
        outputStream.close();
    }

    @Test
    void runApp_EchoAppArg_NoExceptionThrown() {
        String[] argsArray = {"helloworld"};
        assertDoesNotThrow(() -> applicationRunner.runApp(APP_ECHO, argsArray, System.in, outputStream));
    }

    @Test
    void runApp_CdAppArg_NoExceptionThrown() {
        String[] argsArray = {"."};
        assertDoesNotThrow(() -> applicationRunner.runApp(APP_CD, argsArray, System.in, outputStream));
    }

    @Test
    void runApp_WcAppArg_NoExceptionThrown() {
        String[] argsArray = {SAMPLE_FILE};
        assertDoesNotThrow(() -> applicationRunner.runApp(APP_WC, argsArray, System.in, outputStream));
    }

    @Test
    void runApp_CpAppArg_NoExceptionThrown() {
        String[] argsArray = {SAMPLE_FILE, SAMPLE_FILE};
        assertDoesNotThrow(() -> applicationRunner.runApp(APP_CP, argsArray, System.in, outputStream));
    }

    @Test
    void runApp_GrepAppArg_NoExceptionThrown() {
        String pattern = "a";
        String[] argsArray = {pattern, SAMPLE_FILE};
        assertDoesNotThrow(() -> applicationRunner.runApp(APP_GREP, argsArray, System.in, outputStream));
    }

    @Test
    void runApp_CutAppArg_NoExceptionThrown() {
        String list = "1,8";
        String[] argsArray = {"-c", list, SAMPLE_FILE};
        assertDoesNotThrow(() -> applicationRunner.runApp(APP_CUT, argsArray, System.in, outputStream));
    }

    @Test
    void runApp_ExitAppArg_NoExceptionThrown() {
        new MockUp<System>() {
            @Mock
            public void exit(int value) {
                throw new RuntimeException(String.valueOf(value));
            }
        };
        String[] argsArray = {};
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> applicationRunner.runApp(APP_EXIT, argsArray, System.in, outputStream));
        assertEquals("0", exception.getMessage());
    }

    @Test
    void runApp_LsAppArg_NoExceptionThrown() {
        String[] argsArray = {};
        assertDoesNotThrow(() -> applicationRunner.runApp(APP_LS, argsArray, System.in, outputStream));
    }

    @Test
    void runApp_PasteAppArg_NoExceptionThrown() {
        String[] argsArray = {SAMPLE_FILE, SAMPLE_FILE};
        assertDoesNotThrow(() -> applicationRunner.runApp(APP_PASTE, argsArray, System.in, outputStream));
    }

    @Test
    void runApp_UniqAppArg_NoExceptionThrown() {
        String[] argsArray = {SAMPLE_FILE};
        assertDoesNotThrow(() -> applicationRunner.runApp(APP_UNIQ, argsArray, System.in, outputStream));
    }

    @Test
    void runApp_MvAppArg_NoExceptionThrown() {
        String[] argsArray = {SAMPLE_FILE,SAMPLE_FILE};
        assertDoesNotThrow(() -> applicationRunner.runApp(APP_MV, argsArray, System.in, outputStream));
    }

    @Test
    void runApp_SortAppArg_NoExceptionThrown() {
        String[] argsArray = {SAMPLE_FILE};
        assertDoesNotThrow(() -> applicationRunner.runApp(APP_SORT, argsArray, System.in, outputStream));
    }

    @Test
    void runApp_RmAppArg_NoExceptionThrown() throws IOException {
        File file = new File(Paths.get(Environment.currentDirectory).resolve(NEW_FILE).toString());
        file.createNewFile();
        String[] argsArray = {NEW_FILE};
        assertDoesNotThrow(() -> applicationRunner.runApp(APP_RM, argsArray, System.in, outputStream));
    }

    @Test
    void runApp_TeeAppArg_NoExceptionThrown() throws IOException {
        String[] argsArray = {};
        InputStream inputStream = new ByteArrayInputStream("".getBytes());
        assertDoesNotThrow(() -> applicationRunner.runApp(APP_TEE, argsArray, inputStream, outputStream));
    }

    @Test
    void runApp_CatAppArg_NoExceptionThrown() {
        String[] argsArray = {SAMPLE_FILE};
        assertDoesNotThrow(() -> applicationRunner.runApp(APP_CAT, argsArray, System.in, outputStream));
    }

    @Test
    void runApp_InvalidAppArg_ThrowsShellException() {
        String[] argsArray = {};
        ShellException exception = assertThrows(ShellException.class, () -> applicationRunner.runApp(INVALID_APP, argsArray, System.in, outputStream));
        String expectedMessage = "shell: " + INVALID_APP + ": " + ERR_INVALID_APP;
        assertEquals(expectedMessage, exception.getMessage());
    }
}
