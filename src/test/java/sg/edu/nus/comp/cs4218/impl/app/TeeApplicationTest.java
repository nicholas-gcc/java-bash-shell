package sg.edu.nus.comp.cs4218.impl.app;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sg.edu.nus.comp.cs4218.Environment;
import sg.edu.nus.comp.cs4218.exception.TeeException;
import sg.edu.nus.comp.cs4218.impl.util.FileSystemUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.CHAR_FILE_SEP;
import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.ERR_NO_OSTREAM;
import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.ERR_NULL_ARGS;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.STRING_NEWLINE;

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
    static final String FAKE_FILE = "fake.txt";
    static final String SAMPLE_DIR = "sample";

    static final String APPEND_ARG = "-a";
    static final String ERROR_INITIALS = "tee: ";

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
    void reset() throws Exception {
        Environment.currentDirectory = CWD + TESTING_PATH;
        if (FileSystemUtils.fileOrDirExist(TEXT_FILE_NAME1)) {
            FileSystemUtils.deleteFileOrDir(TEXT_FILE_NAME1);
        }

        if (FileSystemUtils.fileOrDirExist(TEXT_FILE_NAME2)) {
            FileSystemUtils.deleteFileOrDir(TEXT_FILE_NAME2);
        }

        if (FileSystemUtils.fileOrDirExist(FAKE_FILE)) {
            FileSystemUtils.deleteFileOrDir(FAKE_FILE);
        }

        Environment.currentDirectory = CWD;
        outputStream.close();
    }

    @Test
    void run_NullArguments_throwsTeeException(){
        TeeException exception = assertThrows(TeeException.class, () -> {
            teeApplication.run(null, System.in, outputStream);
        });
        String expectedMessage = ERROR_INITIALS + ERR_NULL_ARGS;
        assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    void run_NullOutputStream_throwsTeeException(){
        String[] args = {};
        TeeException exception = assertThrows(TeeException.class, () -> {
            teeApplication.run(args, System.in, null);
        });
        String expectedMessage = ERROR_INITIALS + ERR_NO_OSTREAM;
        assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    void run_OnlyInvalidArgs_throwsTeeException() {
        String[] args = {"-b", "-c"};
        TeeException exception = assertThrows(TeeException.class, () -> {
            teeApplication.run(args, System.in, outputStream);
        });
        String expectedMessage = ERROR_INITIALS + "illegal option -- b";
        assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    void run_InvalidAndValidArgs_throwsTeeException() {
        String[] args = {APPEND_ARG, "-a", "-A"};
        TeeException exception = assertThrows(TeeException.class, () -> {
            teeApplication.run(args, System.in, outputStream);
        });
        String expectedMessage = ERROR_INITIALS + "illegal option -- A";
        assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    void run_ValidArgs_DoesNotThrowException() {
        String[] args = {APPEND_ARG};
        inputStream = new ByteArrayInputStream(TEST_INPUT.getBytes());
        assertDoesNotThrow(() -> teeApplication.run(args, inputStream, outputStream));
    }

    @Test
    void run_EmptyFileArgs_CorrectOutputStream() throws TeeException {
        String[] args = {};
        inputStream = new ByteArrayInputStream(TEST_INPUT.getBytes());
        teeApplication.run(args, inputStream, outputStream);
        assertEquals(TEST_INPUT + STRING_NEWLINE, outputStream.toString());
    }

    @Test
    void run_OneEmptyFileArgs_CorrectOutputStream() throws Exception {
        String[] args = {TEXT_FILE_NAME1};
        inputStream = new ByteArrayInputStream(TEST_INPUT.getBytes());
        FileSystemUtils.createEmptyFile(TEXT_FILE_NAME1);
        teeApplication.run(args, inputStream, outputStream);
        assertEquals(TEST_INPUT + STRING_NEWLINE, FileSystemUtils.readFileContent(TEXT_FILE_NAME1));
    }

    @Test
    void run_OneNonEmptyFileArgs_CorrectOutputStream() throws Exception {
        String[] args = {TEXT_FILE_NAME1};
        inputStream = new ByteArrayInputStream(TEST_INPUT.getBytes());
        FileSystemUtils.createEmptyFile(TEXT_FILE_NAME1);
        FileSystemUtils.writeStrToFile(false, SAMPLE_CONTENT + SAMPLE_CONTENT + SAMPLE_CONTENT, TEXT_FILE_NAME1);
        teeApplication.run(args, inputStream, outputStream);
        assertEquals(TEST_INPUT + STRING_NEWLINE, FileSystemUtils.readFileContent(TEXT_FILE_NAME1));
    }

    @Test
    void run_OneNonExistingFileArgs_WritesToNewFileCorrectly() throws Exception {
        String[] args = {FAKE_FILE};
        inputStream = new ByteArrayInputStream(TEST_INPUT.getBytes());
        teeApplication.run(args, inputStream, outputStream);
        assertEquals(TEST_INPUT + STRING_NEWLINE, FileSystemUtils.readFileContent(FAKE_FILE));
    }

    @Test
    void run_OneDirArgs_CorrectOutputStream() throws TeeException {
        String[] args = {SAMPLE_DIR};
        inputStream = new ByteArrayInputStream(TEST_INPUT.getBytes());
        teeApplication.run(args, inputStream, outputStream);
        String expectedOutput = ERROR_INITIALS + String.format("%s: Is a directory", SAMPLE_DIR) + STRING_NEWLINE + TEST_INPUT + STRING_NEWLINE;
        assertEquals(expectedOutput, outputStream.toString());
    }

    @Test
    void run_TwoFileArgs_CorrectOutputStream() throws Exception {
        String[] args = {TEXT_FILE_NAME1, TEXT_FILE_NAME2};
        inputStream = new ByteArrayInputStream(TEST_INPUT.getBytes());

        FileSystemUtils.createEmptyFile(TEXT_FILE_NAME1);
        FileSystemUtils.createEmptyFile(TEXT_FILE_NAME2);

        teeApplication.run(args, inputStream, outputStream);
        assertEquals(TEST_INPUT + STRING_NEWLINE, FileSystemUtils.readFileContent(TEXT_FILE_NAME1));
        assertEquals(TEST_INPUT + STRING_NEWLINE, FileSystemUtils.readFileContent(TEXT_FILE_NAME2));
    }

    @Test
    void run_OneExistingFileOneNonExistingFileArgs_WritesToFilesCorrectly() throws Exception {
        String[] args = {TEXT_FILE_NAME1, FAKE_FILE};
        inputStream = new ByteArrayInputStream(TEST_INPUT.getBytes());

        FileSystemUtils.createEmptyFile(TEXT_FILE_NAME1);
        teeApplication.run(args, inputStream, outputStream);
        assertEquals(TEST_INPUT + STRING_NEWLINE, FileSystemUtils.readFileContent(FAKE_FILE));
        assertEquals(TEST_INPUT + STRING_NEWLINE, FileSystemUtils.readFileContent(TEXT_FILE_NAME1));
    }

    @Test
    void run_OneFileOneDirArgs_OutputsCorrectErrorMessage() throws Exception {
        String[] args = {TEXT_FILE_NAME1, SAMPLE_DIR};
        inputStream = new ByteArrayInputStream(TEST_INPUT.getBytes());

        FileSystemUtils.createEmptyFile(TEXT_FILE_NAME1);
        teeApplication.run(args, inputStream, outputStream);
        String expectedOutput = ERROR_INITIALS + String.format("%s: Is a directory", SAMPLE_DIR) + STRING_NEWLINE + TEST_INPUT + STRING_NEWLINE;
        assertEquals(expectedOutput, outputStream.toString());
    }

    @Test
    void run_OneFileWithIsAppendArgs_CorrectOutputStream() throws Exception {
        String[] args = {TEXT_FILE_NAME1 , APPEND_ARG};
        inputStream = new ByteArrayInputStream(TEST_INPUT.getBytes());

        FileSystemUtils.createEmptyFile(TEXT_FILE_NAME1);
        FileSystemUtils.writeStrToFile(false, SAMPLE_CONTENT + STRING_NEWLINE, TEXT_FILE_NAME1);
        teeApplication.run(args, inputStream, outputStream);
        assertEquals(SAMPLE_CONTENT + STRING_NEWLINE + TEST_INPUT + STRING_NEWLINE, FileSystemUtils.readFileContent(TEXT_FILE_NAME1));
    }

    @Test
    void teeFromStdin_NoFiles_ReturnsCorrectString() throws TeeException {
        String[] filenames = {};
        inputStream = new ByteArrayInputStream(TEST_INPUT.getBytes());
        String output = teeApplication.teeFromStdin(false, inputStream, filenames);
        assertEquals(TEST_INPUT + STRING_NEWLINE, output);
    }

    @Test
    void teeFromStdin_OneFile_WritesToFile() throws Exception {
        String[] filenames = {TEXT_FILE_NAME1};
        inputStream = new ByteArrayInputStream(TEST_INPUT.getBytes());
        FileSystemUtils.createEmptyFile(TEXT_FILE_NAME1);
        teeApplication.teeFromStdin(false, inputStream, filenames);
        assertEquals(TEST_INPUT + STRING_NEWLINE, FileSystemUtils.readFileContent(TEXT_FILE_NAME1));
    }

    @Test
    void teeFromStdin_TwoFiles_WritesToFile() throws Exception {
        String[] filenames = {TEXT_FILE_NAME1, TEXT_FILE_NAME2};
        inputStream = new ByteArrayInputStream(TEST_INPUT.getBytes());
        FileSystemUtils.createEmptyFile(TEXT_FILE_NAME1);
        FileSystemUtils.createEmptyFile(TEXT_FILE_NAME2);
        teeApplication.teeFromStdin(false, inputStream, filenames);
        assertEquals(TEST_INPUT + STRING_NEWLINE, FileSystemUtils.readFileContent(TEXT_FILE_NAME1));
        assertEquals(TEST_INPUT + STRING_NEWLINE, FileSystemUtils.readFileContent(TEXT_FILE_NAME2));
    }

    @Test
    void teeFromStdin_OneFiles_AppendsToFile() throws Exception {
        String[] filenames = {TEXT_FILE_NAME1};
        inputStream = new ByteArrayInputStream(TEST_INPUT.getBytes());
        FileSystemUtils.createEmptyFile(TEXT_FILE_NAME1);
        FileSystemUtils.writeStrToFile(false, SAMPLE_CONTENT + STRING_NEWLINE, TEXT_FILE_NAME1);
        teeApplication.teeFromStdin(true, inputStream, filenames);
        assertEquals(SAMPLE_CONTENT + STRING_NEWLINE + TEST_INPUT + STRING_NEWLINE, FileSystemUtils.readFileContent(TEXT_FILE_NAME1));
    }

    @Test
    void teeFromStdin_NonExistingFile_ThrowsTeeExceptionAndCorrectMessage() throws Exception {
        String[] filenames = {FAKE_FILE};
        inputStream = new ByteArrayInputStream(TEST_INPUT.getBytes());
        teeApplication.teeFromStdin(false, inputStream, filenames);
        assertEquals(TEST_INPUT + STRING_NEWLINE, FileSystemUtils.readFileContent(FAKE_FILE));
    }

}
