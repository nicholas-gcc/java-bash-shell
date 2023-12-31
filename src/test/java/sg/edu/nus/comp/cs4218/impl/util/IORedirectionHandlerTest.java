package sg.edu.nus.comp.cs4218.impl.util;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sg.edu.nus.comp.cs4218.Environment;
import sg.edu.nus.comp.cs4218.exception.ShellException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.CHAR_FILE_SEP;


public class IORedirectionHandlerTest {
    private static final String FILENAME = "file.txt";
    private static final String FILENAME_2 = "file2.txt";
    private static final String TEST_MSG = "Test Message";
    private static List<String> argsList = Arrays.asList("paste", "<", FILENAME);
    private static ArgumentResolver argumentResolver;
    private static InputStream origInputStream;
    private static OutputStream origOutputStream;
    static final String CWD = System.getProperty("user.dir");
    static final String TESTING_PATH = CHAR_FILE_SEP + "assets" + CHAR_FILE_SEP + "util" + CHAR_FILE_SEP + "IORedirectionHandler";

    @BeforeEach
    void setup() {
        Environment.currentDirectory += TESTING_PATH;
        argumentResolver = new ArgumentResolver();
        origInputStream = new ByteArrayInputStream(new byte[0]);
        origOutputStream = new ByteArrayOutputStream();
    }

    @AfterEach
    void reset() throws Exception {
        origInputStream.close();
        origOutputStream.close();

        if (FileSystemUtils.fileOrDirExist(FILENAME)) {
            FileSystemUtils.deleteFileOrDir(FILENAME);
        }
        if (FileSystemUtils.fileOrDirExist(FILENAME_2)) {
            FileSystemUtils.deleteFileOrDir(FILENAME_2);
        }

        Environment.currentDirectory = CWD;

    }

    @Test
    void extract_Arguments_ShouldNotThrowException() throws Exception {
        FileSystemUtils.createEmptyFile(FILENAME);
        FileSystemUtils.writeStrToFile(false, TEST_MSG, FILENAME);

        IORedirectionHandler handler = new IORedirectionHandler( argsList, origInputStream,
                origOutputStream, argumentResolver);
        assertDoesNotThrow(() -> {
            handler.extractRedirOptions();
        });
        handler.closeAllStreams();
    }

    @Test
    void extract_Arguments_NoRedirArgShouldBePopulatedCorrectly() throws Exception {
        FileSystemUtils.createEmptyFile(FILENAME);
        FileSystemUtils.writeStrToFile(false, TEST_MSG, FILENAME);

        IORedirectionHandler handler = new IORedirectionHandler(argsList, origInputStream,
                origOutputStream, argumentResolver);
        handler.extractRedirOptions();
        assertEquals(Arrays.asList("paste"), handler.getNoRedirArgsList());
        handler.closeAllStreams();
    }

    @Test
    void extract_Arguments_InputStreamShouldBeAlteredCorrectly() throws Exception {
        FileSystemUtils.createEmptyFile(FILENAME);
        FileSystemUtils.writeStrToFile(false, TEST_MSG, FILENAME);

        IORedirectionHandler handler = new IORedirectionHandler(argsList, origInputStream,
                origOutputStream, argumentResolver);
        handler.extractRedirOptions();
        assertEquals(FileSystemUtils.readFileContent(FILENAME),
                new String(handler.getInputStream().readAllBytes()));
        handler.closeAllStreams();
    }

    @Test
    void extract_Arguments_OutputStreamShouldBeAlteredCorrectly() throws Exception {
        FileSystemUtils.createEmptyFile(FILENAME);
        FileSystemUtils.writeStrToFile(false, TEST_MSG, FILENAME);

        List<String> argList1 = Arrays.asList("ls", ">", FILENAME);
        IORedirectionHandler handler1 = new IORedirectionHandler(argList1, origInputStream,
                origOutputStream, argumentResolver);
        handler1.extractRedirOptions();
        assertEquals(FILENAME, handler1.getOutputFilePath());
        handler1.closeAllStreams();
    }

    @Test
    void extract_ArgumentsAppend_OutputStreamShouldBeAlteredCorrectly() throws Exception {
        FileSystemUtils.createEmptyFile(FILENAME);
        FileSystemUtils.writeStrToFile(false, TEST_MSG, FILENAME);

        List<String> argList1 = Arrays.asList("ls", ">", ">", FILENAME);
        IORedirectionHandler handler1 = new IORedirectionHandler(argList1, origInputStream,
                origOutputStream, argumentResolver);
        handler1.extractRedirOptions();
        assertEquals(FILENAME, handler1.getOutputFilePath());
        handler1.closeAllStreams();

    }

    @Test
    void extract_ArgumentsNoAppend_IsAppendIsFalse() throws Exception {
        FileSystemUtils.createEmptyFile(FILENAME);
        FileSystemUtils.writeStrToFile(false, TEST_MSG, FILENAME);

        IORedirectionHandler handler = new IORedirectionHandler(argsList, origInputStream,
                origOutputStream, argumentResolver);
        handler.extractRedirOptions();
        assertEquals(false, handler.isAppend());
        handler.closeAllStreams();
    }

    @Test
    void extract_ArgumentsAppend_IsAppendIsTrue() throws Exception {
        FileSystemUtils.createEmptyFile(FILENAME);
        FileSystemUtils.writeStrToFile(false, TEST_MSG, FILENAME);

        List<String> argList1 = Arrays.asList("ls", ">", ">", FILENAME);
        IORedirectionHandler handler1 = new IORedirectionHandler(argList1, origInputStream,
                origOutputStream, argumentResolver);
        handler1.extractRedirOptions();
        assertEquals(true, handler1.isAppend());
        handler1.closeAllStreams();
    }
    @Test
    void extract_EmptyArguments_ShouldThrowShellException() throws Exception {
        FileSystemUtils.createEmptyFile(FILENAME);
        FileSystemUtils.writeStrToFile(false, TEST_MSG, FILENAME);

        IORedirectionHandler handler1 = new IORedirectionHandler(new ArrayList(), origInputStream,
                origOutputStream, argumentResolver);
        assertThrows(ShellException.class, () -> {
            handler1.extractRedirOptions();
        });
        handler1.closeAllStreams();
    }

    @Test
    void extract_WrongRedirArg_ShouldThrowShellException() throws Exception {
        FileSystemUtils.createEmptyFile(FILENAME);
        FileSystemUtils.writeStrToFile(false, TEST_MSG, FILENAME);

        List<String> argList1 = Arrays.asList("ls", "<", ">", FILENAME);
        IORedirectionHandler handler1 = new IORedirectionHandler(argList1, origInputStream,
                origOutputStream, argumentResolver);
        assertThrows(ShellException.class, () -> {
            handler1.extractRedirOptions();
        });

        List<String> argList2 = Arrays.asList("ls", "<", "<", FILENAME);
        IORedirectionHandler handler2 = new IORedirectionHandler(argList2, origInputStream,
                origOutputStream, argumentResolver);
        assertThrows(ShellException.class, () -> {
            handler2.extractRedirOptions();
        });

        List<String> argList3 = Arrays.asList("ls", ">", "<", FILENAME);
        IORedirectionHandler handler3 = new IORedirectionHandler(argList3, origInputStream,
                origOutputStream, argumentResolver);
        assertThrows(ShellException.class, () -> {
            handler3.extractRedirOptions();
        });
        handler1.closeAllStreams();
        handler2.closeAllStreams();
        handler3.closeAllStreams();
    }

    @Test
    void extract_TooManyFiles_ShouldThrowShellException() throws Exception {
        FileSystemUtils.createEmptyFile(FILENAME);
        FileSystemUtils.writeStrToFile(false, TEST_MSG, FILENAME);

        List<String> argList1 = Arrays.asList("ls", ">", FILENAME, FILENAME);
        IORedirectionHandler handler1 = new IORedirectionHandler(argList1, origInputStream,
                origOutputStream, argumentResolver);
        assertThrows(ShellException.class, () -> {
            handler1.extractRedirOptions();
        });
        handler1.closeAllStreams();
    }

    @Test
    void extract_TooFewFiles_ShouldThrowShellException() throws ShellException {
        List<String> argList1 = Arrays.asList("ls", ">");

        IORedirectionHandler handler1 = new IORedirectionHandler(argList1, origInputStream,
                origOutputStream, argumentResolver);
        assertThrows(ShellException.class, () -> {
            handler1.extractRedirOptions();
        });

        List<String> argList2 = Arrays.asList("ls", ">", ">");
        IORedirectionHandler handler2 = new IORedirectionHandler(argList2, origInputStream,
                origOutputStream, argumentResolver);
        assertThrows(ShellException.class, () -> {
            handler2.extractRedirOptions();
        });
        handler1.closeAllStreams();
        handler2.closeAllStreams();
    }

    @Test
    void extract_BothRedirChar_BothInputOutputStreamShouldBeAlteredCorrectly() throws Exception {
        FileSystemUtils.createEmptyFile(FILENAME);
        FileSystemUtils.writeStrToFile(false, TEST_MSG, FILENAME);
        FileSystemUtils.createEmptyFile(FILENAME_2);

        List<String> argList1 = Arrays.asList("ls", "<", FILENAME, ">", FILENAME_2);
        IORedirectionHandler handler1 = new IORedirectionHandler(argList1, origInputStream,
                origOutputStream, argumentResolver);
        handler1.extractRedirOptions();

        assertEquals(FileSystemUtils.readFileContent(FILENAME),
                new String(handler1.getInputStream().readAllBytes()));

        assertEquals(FILENAME_2,
                handler1.getOutputFilePath());

        List<String> argList2 = Arrays.asList("ls", ">", FILENAME_2, "<", FILENAME);
        IORedirectionHandler handler2 = new IORedirectionHandler(argList2, origInputStream,
                origOutputStream, argumentResolver);
        handler2.extractRedirOptions();

        assertEquals(FileSystemUtils.readFileContent(FILENAME),
                new String(handler2.getInputStream().readAllBytes()));

        assertEquals(FILENAME_2,
                handler2.getOutputFilePath());

        handler1.closeAllStreams();
        handler2.closeAllStreams();
    }

    @Test
    void getNoRedirArgsList_extractSuccessful_ShouldReturnCorrectResult() throws Exception {
        FileSystemUtils.createEmptyFile(FILENAME);
        FileSystemUtils.writeStrToFile(false, TEST_MSG, FILENAME);

        FileSystemUtils.createEmptyFile(FILENAME_2);
        List<String> argList1 = Arrays.asList("ls", "<", FILENAME, ">", FILENAME_2);
        IORedirectionHandler handler1 = new IORedirectionHandler(argList1, origInputStream,
                origOutputStream, argumentResolver);
        handler1.extractRedirOptions();
        assertEquals(Arrays.asList("ls"), handler1.getNoRedirArgsList());
        handler1.closeAllStreams();
    }

    @Test
    void closeAllStreams_ValidStreams_NoExceptionThrown() {
        IORedirectionHandler handler = new IORedirectionHandler( argsList, origInputStream,
                origOutputStream, argumentResolver);
        assertDoesNotThrow(() -> {
            handler.closeAllStreams();
        });
    }
}

