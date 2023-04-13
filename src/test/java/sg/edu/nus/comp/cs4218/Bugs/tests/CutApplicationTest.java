package sg.edu.nus.comp.cs4218.Bugs.tests;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import sg.edu.nus.comp.cs4218.Environment;
import sg.edu.nus.comp.cs4218.exception.CutException;
import sg.edu.nus.comp.cs4218.impl.app.CutApplication;
import sg.edu.nus.comp.cs4218.impl.util.FileSystemUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.CHAR_FILE_SEP;

public class CutApplicationTest {
    static final String CWD = System.getProperty("user.dir");
    static final String TESTING_PATH = CHAR_FILE_SEP + "assets" + CHAR_FILE_SEP + "app" + CHAR_FILE_SEP + "cut";
    static final String TEST_FILE = "test.txt";
    static final String TEST_CONTENT = "a";
    static final String CUT_CMD = "cut";

    InputStream inputStream;
    OutputStream outputStream;
    CutApplication cutApplication;
    @BeforeEach
    void setup() {
        Environment.currentDirectory += TESTING_PATH;
        inputStream = System.in;
        outputStream = new ByteArrayOutputStream();
        cutApplication = new CutApplication();
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
    @DisplayName("Rebuttal 10 - Invalid arguments with multiple flags given")
    // https://github.com/nus-cs4218/cs4218-project-2023-team28/issues/192
    void run_invalidArgsWithMultipleFlags_ReturnsInvalidError() throws Exception {
        FileSystemUtils.createEmptyFile(TEST_FILE);
        FileSystemUtils.writeStrToFile(false, TEST_CONTENT, TEST_FILE);
        String[] args = {"-c", "-b", TEST_FILE};
        String expected = "cut: Invalid indexes provided: -b";
        Throwable thrown = assertThrows(CutException.class, () -> cutApplication.run(args, inputStream, outputStream));
        assertEquals(expected, thrown.getMessage());
    }

    @Test
    @DisplayName("Rebuttal 11 and 12 - Cut indexes cannot be 0")
    // https://github.com/nus-cs4218/cs4218-project-2023-team28/issues/193
    // https://github.com/nus-cs4218/cs4218-project-2023-team28/issues/194
    void run_cutIndexZero_ReturnsInvalidIndex() throws Exception {
        FileSystemUtils.createEmptyFile(TEST_FILE);
        FileSystemUtils.writeStrToFile(false, TEST_CONTENT, TEST_FILE);
        String[] args = {"-c", "0", TEST_FILE};
        String expected = "cut: byte/character positions are numbered from 1";
        Throwable thrown = assertThrows(CutException.class, () -> cutApplication.run(args, inputStream, outputStream));
        assertEquals(expected, thrown.getMessage());
    }


}
