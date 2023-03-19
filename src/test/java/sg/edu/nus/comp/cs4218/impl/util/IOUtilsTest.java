package sg.edu.nus.comp.cs4218.impl.util;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sg.edu.nus.comp.cs4218.Environment;
import sg.edu.nus.comp.cs4218.exception.ShellException;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.ERR_FILE_NOT_FOUND;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.CHAR_FILE_SEP;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.STRING_NEWLINE;

@SuppressWarnings({"PMD.CloseResource"})
public class IOUtilsTest {
    static final String CWD = System.getProperty("user.dir");
    static final String TESTING_PATH = CHAR_FILE_SEP + "assets" + CHAR_FILE_SEP + "util" + CHAR_FILE_SEP + "IOUtils";
    static final String SAMPLE_FILE = "sample.txt";
    static final String FAKE_FILE = "fake.txt";
    static final String SAMPLE_DIR = "sample";
    static final String NEW_FILE = "new.txt";
    static final String SHELL_ERR = "shell: ";


    @BeforeEach
    void setCurrentDirectory() {
        Environment.currentDirectory += TESTING_PATH;
    }

    @AfterEach
    void reset() {
        Environment.currentDirectory = CWD;
    }

    @Test
    void openInputStream_ExistingFile_ReturnsInputStreamWithFileContent() throws ShellException, IOException {
        InputStream inputStream = IOUtils.openInputStream(SAMPLE_FILE);
        String expectedContent = "This is a sample text." + STRING_NEWLINE + "This is the second line.";
        List<String> output = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        String line;
        while ((line = reader.readLine()) != null) {
            output.add(line);
        }
        reader.close();
        inputStream.close();
        assertEquals(expectedContent, String.join(STRING_NEWLINE, output));
    }

    @Test
    void openInputStream_NonFile_ThrowsShellExceptionWithCorrectMessage(){
        ShellException exception = assertThrows(ShellException.class, () -> {
            InputStream inputStream = IOUtils.openInputStream(FAKE_FILE);
            inputStream.close();
        });
        String expectedMessage = SHELL_ERR + ERR_FILE_NOT_FOUND;
        assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    void openInputStream_ExistingDir_ThrowsShellExceptionWithCorrectMessage(){
        ShellException exception = assertThrows(ShellException.class, () -> {
            InputStream inputStream = IOUtils.openInputStream(SAMPLE_DIR);
            inputStream.close();
        });
        String expectedMessage = SHELL_ERR + ERR_FILE_NOT_FOUND;
        assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    void openOutputStream_ExistingFile_NoExceptionThrown() throws IOException {
        File file = new File(Environment.currentDirectory + CHAR_FILE_SEP + NEW_FILE);
        file.createNewFile();
        assertDoesNotThrow(() -> {
            OutputStream outputStream = IOUtils.openOutputStream(NEW_FILE);
            outputStream.close();
        });
        file.delete();
    }

    @Test
    void openOutputStream_NonExistingFile_ThrowsShellExceptionWithCorrectMessage() {
        ShellException exception = assertThrows(ShellException.class, () -> IOUtils.openOutputStream(FAKE_FILE));
        String expectedMessage = SHELL_ERR + ERR_FILE_NOT_FOUND;
        assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    void openOutputStream_ExistingDir_ThrowsShellExceptionWithCorrectMessage() {
        ShellException exception = assertThrows(ShellException.class, () -> IOUtils.openOutputStream(SAMPLE_DIR));
        String expectedMessage = SHELL_ERR + ERR_FILE_NOT_FOUND;
        assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    void openOutputStream_ExistingFileIsAppend_NoExceptionThrown() throws IOException {
        File file = new File(Environment.currentDirectory + CHAR_FILE_SEP + NEW_FILE);
        file.createNewFile();
        assertDoesNotThrow(() -> {
            OutputStream outputStream = IOUtils.openOutputStream(NEW_FILE, true);
            outputStream.close();
        });
        file.delete();
    }

    @Test
    void openOutputStream_ExistingFileIsNotAppend_NoExceptionThrown() throws IOException {
        File file = new File(Environment.currentDirectory + CHAR_FILE_SEP + NEW_FILE);
        file.createNewFile();
        assertDoesNotThrow(() -> {
            OutputStream outputStream = IOUtils.openOutputStream(NEW_FILE, false);
            outputStream.close();
        });
        file.delete();
    }

    @Test
    void openOutputStream_NonExistingFileIsAppend_ThrowsShellExceptionWithCorrectMessage() {
        ShellException exception = assertThrows(ShellException.class, () -> IOUtils.openOutputStream(FAKE_FILE, true));
        String expectedMessage = SHELL_ERR + ERR_FILE_NOT_FOUND;
        assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    void openOutputStream_ExistingDirIsAppend_ThrowsShellExceptionWithCorrectMessage() {
        ShellException exception = assertThrows(ShellException.class, () -> IOUtils.openOutputStream(SAMPLE_DIR, true));
        String expectedMessage = SHELL_ERR + ERR_FILE_NOT_FOUND;
        assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    void closeInputStream_ValidInputStream_NoExceptionThrown() {
        InputStream inputStream = new ByteArrayInputStream("".getBytes());
        assertDoesNotThrow(() -> IOUtils.closeInputStream(inputStream));
    }

    @Test
    void closeOutputStream_ValidOutputStream_NoExceptionThrown() {
        OutputStream outputStream = new ByteArrayOutputStream();
        assertDoesNotThrow(() -> IOUtils.closeOutputStream(outputStream));
    }

    @Test
    void resolveFilePath_FileName_ReturnsCorrectPath() {
        Path path = IOUtils.resolveFilePath(SAMPLE_FILE);
        assertEquals(Environment.currentDirectory + CHAR_FILE_SEP + SAMPLE_FILE, path.toString());
    }

    @Test
    void resolveFilePath_DirName_ReturnsCorrectPath() {
        Path path = IOUtils.resolveFilePath(SAMPLE_DIR);
        assertEquals(Environment.currentDirectory + CHAR_FILE_SEP + SAMPLE_DIR, path.toString());
    }

    @Test
    void resolveFilePath_EmptyFileName_ReturnsCorrectPath() {
        Path path = IOUtils.resolveFilePath("");
        assertEquals(Environment.currentDirectory, path.toString());
    }

    @Test
    void getLinesFromInputStream_ValidInputStream_ReturnsCorrectContent() throws Exception {
        String absolutePath = Paths.get(Environment.currentDirectory).resolve(SAMPLE_FILE).toString();
        InputStream inputStream = new FileInputStream(absolutePath);
        List<String> expectedContent = List.of(new String[]{"This is a sample text.", "This is the second line."});
        assertEquals(expectedContent, IOUtils.getLinesFromInputStream(inputStream));
        inputStream.close();
    }

    @Test
    void convertStreamToString_ValidInputStream_ReturnsCorrectContent() throws IOException {
        String absolutePath = Paths.get(Environment.currentDirectory).resolve(SAMPLE_FILE).toString();
        InputStream inputStream = new FileInputStream(absolutePath);
        String expectedContent = "This is a sample text." + STRING_NEWLINE + "This is the second line." + STRING_NEWLINE;
        assertEquals(expectedContent, IOUtils.convertStreamToString(inputStream));
        inputStream.close();
    }
}
