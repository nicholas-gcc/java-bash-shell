package sg.edu.nus.comp.cs4218.Bugs.tests;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import sg.edu.nus.comp.cs4218.Environment;
import sg.edu.nus.comp.cs4218.impl.app.CatApplication;
import sg.edu.nus.comp.cs4218.impl.util.FileSystemUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.CHAR_FILE_SEP;

public class CatApplicationTest {
    static final String CWD = System.getProperty("user.dir");
    static final String TESTING_PATH = CHAR_FILE_SEP + "assets" + CHAR_FILE_SEP + "app" + CHAR_FILE_SEP + "cat";
    static final String TEST_FILE = "test.txt";
    static final String TEST_CONTENT = "a";
    static final String CAT_CMD = "cat";

    InputStream inputStream;
    OutputStream outputStream;
    @BeforeEach
    void setup() {
        Environment.currentDirectory += TESTING_PATH;
        inputStream = System.in;
        outputStream = new ByteArrayOutputStream();
    }

    @AfterEach
    void reset() throws IOException {
        Environment.currentDirectory = CWD;
        inputStream.close();
        outputStream.close();
    }

    @AfterEach
    void deleteFiles() throws Exception {
        if (FileSystemUtils.fileOrDirExist(TEST_FILE)) {
            FileSystemUtils.deleteFileOrDir(TEST_FILE);
        }
    }

    @Test
    @DisplayName("Rebuttal 2 - Cat two same files with no newline at end of file")
    // https://github.com/nus-cs4218/cs4218-project-2023-team28/issues/184
    void run_catTwoSameFilesWithNoNewlineAtEndOfFile_ReturnsOneLine() throws Exception {
        FileSystemUtils.createEmptyFile(TEST_FILE);
        FileSystemUtils.writeStrToFile(false, TEST_CONTENT, TEST_FILE);
        String[] args = {TEST_FILE, TEST_FILE};
        CatApplication catApplication = new CatApplication();
        catApplication.run(args, inputStream, outputStream);
        assertEquals(TEST_CONTENT + TEST_CONTENT, outputStream.toString());
    }

    @Test
    @DisplayName("Rebuttal 3 - Cat with no args opens stdin")
    // https://github.com/nus-cs4218/cs4218-project-2023-team28/issues/185
    void run_catWithNoArgs_ReturnsStdin() throws Exception {
        String[] args = {};
        CatApplication catApplication = new CatApplication();
        InputStream inputStream = new ByteArrayInputStream("test".getBytes());
        catApplication.run(args, inputStream, outputStream);
        assertEquals("test", outputStream.toString());
    }

    @Test
    @DisplayName("Rebuttal 4 - Cat stdin with line numbers")
    // https://github.com/nus-cs4218/cs4218-project-2023-team28/issues/186
    void run_catStdinWithLineNumbers_ReturnsStdinWithLineNumbers() throws Exception {
        String[] args = {"-n", "-"};
        CatApplication catApplication = new CatApplication();
        InputStream inputStream = new ByteArrayInputStream("hello".getBytes());
        catApplication.run(args, inputStream, outputStream);
        assertEquals("1 hello", outputStream.toString());
    }
}
