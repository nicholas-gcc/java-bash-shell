package sg.edu.nus.comp.cs4218.impl.app;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import sg.edu.nus.comp.cs4218.Environment;
import sg.edu.nus.comp.cs4218.exception.TeeException;
import sg.edu.nus.comp.cs4218.impl.util.FileSystemUtils;

import java.io.*;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.CHAR_FILE_SEP;

public class TeeApplicationTest {

    TeeApplication teeApplication;
    InputStream inputStream;
    OutputStream outputStream;
    static final String CWD = System.getProperty("user.dir");
    static final String TESTING_PATH = CHAR_FILE_SEP + "assets" + CHAR_FILE_SEP + "app" + CHAR_FILE_SEP + "tee";
    static final String TEST_INPUT = "Hello World";
    static final String TEXT_FILE_NAME1 = "text1.txt";
    static final String TEXT_FILE_NAME2 = "text2.txt";
    static final String SAMPLE_CONTENT = "Sample content.";
    static final String APPEND_ARG = "-a";

    @BeforeEach
    void setup() {
        teeApplication = new TeeApplication();
        outputStream = new ByteArrayOutputStream();
    }

    @BeforeEach
    void setCurrentDirectory() {
        Environment.currentDirectory += TESTING_PATH;
        outputStream = new ByteArrayOutputStream();
    }

    @AfterEach
    void reset() throws IOException {
        Environment.currentDirectory = CWD;
        inputStream.close();
        outputStream.close();
    }

    @Test
    @Disabled
    void run_NullArguments_throwsTeeException(){
        assertThrows(TeeException.class, () -> {
            teeApplication.run(null, System.in, outputStream);
        });
    }

    @Test
    @Disabled
    void run_NullOutputStream_throwsTeeException(){
        String[] args = {};
        assertThrows(TeeException.class, () -> {
            teeApplication.run(args, System.in, null);
        });
    }

    @Test
    @Disabled
    void run_OnlyInvalidArgs_throwsTeeException() {
        String[] args = {"-b", "-c"};
        assertThrows(TeeException.class, () -> {
            teeApplication.run(args, System.in, outputStream);
        });
    }

    @Test
    @Disabled
    void run_InvalidAndValidArgs_throwsTeeException() {
        String[] args = {APPEND_ARG, "-d", "-A", "-D"};
        assertThrows(TeeException.class, () -> {
            teeApplication.run(args, System.in, outputStream);
        });
    }

    @Test
    @Disabled
    void run_ValidArgs_DoesNotThrowException() {
        String[] args = {APPEND_ARG};
        assertDoesNotThrow(() -> {
            teeApplication.run(args, System.in, outputStream);
        });
    }

    @Test
    @Disabled
    void run_EmptyFileArgs_CorrectOutputStream() {
        String[] args = {};
        inputStream = new ByteArrayInputStream(TEST_INPUT.getBytes());
        assertDoesNotThrow(() -> {
            teeApplication.run(args, System.in, outputStream);
            assertEquals(TEST_INPUT, outputStream.toString());
        });
    }

    @Test
    @Disabled
    void run_OneFileArgs_CorrectOutputStream() {
        String[] args = {TEXT_FILE_NAME1};
        inputStream = new ByteArrayInputStream(TEST_INPUT.getBytes());

        assertDoesNotThrow(() -> {
            FileSystemUtils.createEmptyFile(TEXT_FILE_NAME1);
            teeApplication.run(args, System.in, outputStream);
            assertEquals(TEST_INPUT, FileSystemUtils.readFileContent(TEXT_FILE_NAME1));
            FileSystemUtils.deleteFile(TEXT_FILE_NAME1);
        });
    }

    @Test
    @Disabled
    void run_TwoFileArgs_CorrectOutputStream() {
        String[] args = {TEXT_FILE_NAME1, TEXT_FILE_NAME2};
        inputStream = new ByteArrayInputStream(TEST_INPUT.getBytes());

        assertDoesNotThrow(() -> {
            FileSystemUtils.createEmptyFile(TEXT_FILE_NAME1);
            FileSystemUtils.createEmptyFile(TEXT_FILE_NAME2);

            teeApplication.run(args, System.in, outputStream);
            assertEquals(TEST_INPUT, FileSystemUtils.readFileContent(TEXT_FILE_NAME1));
            assertEquals(TEST_INPUT, FileSystemUtils.readFileContent(TEXT_FILE_NAME2));
            FileSystemUtils.deleteFile(TEXT_FILE_NAME1);
            FileSystemUtils.deleteFile(TEXT_FILE_NAME2);
        });
    }

    @Test
    @Disabled
    void run_OneFileWithAppendArgs_CorrectOutputStream() {
        String[] args = {TEXT_FILE_NAME1 , APPEND_ARG};
        inputStream = new ByteArrayInputStream(TEST_INPUT.getBytes());

        assertDoesNotThrow(() -> {
            FileSystemUtils.createEmptyFile(TEXT_FILE_NAME1);
            FileSystemUtils.appendStrToFile(TEXT_FILE_NAME1, SAMPLE_CONTENT);
            teeApplication.run(args, System.in, outputStream);
            assertEquals(SAMPLE_CONTENT + TEST_INPUT, FileSystemUtils.readFileContent(TEXT_FILE_NAME1));
            FileSystemUtils.deleteFile(TEXT_FILE_NAME1);
        });
    }
}
