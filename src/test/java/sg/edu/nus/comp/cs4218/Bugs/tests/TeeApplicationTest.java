package sg.edu.nus.comp.cs4218.Bugs.tests;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import sg.edu.nus.comp.cs4218.Bugs.utils.FileSystemUtils;
import sg.edu.nus.comp.cs4218.Environment;
import sg.edu.nus.comp.cs4218.impl.app.TeeApplication;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static sg.edu.nus.comp.cs4218.Bugs.utils.StringUtils.CHAR_FILE_SEP;
import static sg.edu.nus.comp.cs4218.Bugs.utils.StringUtils.STRING_NEWLINE;

public class TeeApplicationTest {
    TeeApplication teeApplication;
    InputStream inputStream;
    OutputStream outputStream;
    static final String CWD = System.getProperty("user.dir");
    static final String TESTING_PATH = CHAR_FILE_SEP + "src" + CHAR_FILE_SEP + "test" + CHAR_FILE_SEP
            + "java" + CHAR_FILE_SEP + "sg" + CHAR_FILE_SEP + "edu" + CHAR_FILE_SEP + "nus" + CHAR_FILE_SEP + "comp"
            + CHAR_FILE_SEP + "cs4218" + CHAR_FILE_SEP + "Bugs" + CHAR_FILE_SEP + "assets" + CHAR_FILE_SEP + "app"
            + CHAR_FILE_SEP + "tee";
    static final String TEST_INPUT = "Hello World";
    static final String TEXT_FILE_NAME1 = "text1.txt";
    static final String NEW_FILE = "new.txt";
    static final String SAMPLE_DIR = "sample";
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

        if (FileSystemUtils.fileOrDirExist(NEW_FILE)) {
            FileSystemUtils.deleteFileOrDir(NEW_FILE);
        }

        if (FileSystemUtils.fileOrDirExist(SAMPLE_DIR)) {
            FileSystemUtils.deleteFileOrDir(SAMPLE_DIR);
        }

        Environment.currentDirectory = CWD;
        outputStream.close();
    }

    @Test
    @DisplayName("Rebuttal 39")
    void run_OneNonExistingFileArgs_CreatesAndWritesToFile() throws Exception {
        String[] args = {NEW_FILE};
        inputStream = new ByteArrayInputStream(TEST_INPUT.getBytes());
        teeApplication.run(args, inputStream, outputStream);
        assertEquals(TEST_INPUT + STRING_NEWLINE, FileSystemUtils.readFileContent(NEW_FILE));
    }

    @Test
    @DisplayName("Rebuttal 40")
    void run_OneDirArgs_OutputsBothErrMsgAndInput() throws Exception {
        String[] args = {SAMPLE_DIR};
        inputStream = new ByteArrayInputStream(TEST_INPUT.getBytes());

        FileSystemUtils.createEmptyDir(SAMPLE_DIR);

        teeApplication.run(args, inputStream, outputStream);
        String expectedOutput = ERROR_INITIALS + String.format("%s: Is a directory", SAMPLE_DIR) + STRING_NEWLINE + TEST_INPUT + STRING_NEWLINE;
        assertEquals(expectedOutput, outputStream.toString());
    }
}
