package sg.edu.nus.comp.cs4218.impl.app;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import sg.edu.nus.comp.cs4218.Environment;
import sg.edu.nus.comp.cs4218.exception.RmException;
import sg.edu.nus.comp.cs4218.impl.util.FileSystemUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.CHAR_FILE_SEP;

public class RmApplicationTest {
    RmApplication rmApplication;
    OutputStream outputStream;
    static final String CWD = System.getProperty("user.dir");
    static final String TESTING_PATH = CHAR_FILE_SEP + "assets" + CHAR_FILE_SEP + "app" + CHAR_FILE_SEP + "rm";
    static final String RECURSIVE_ARG = "-r";
    static final String RM_EMPTY_DIR_ARG = "-d";

    static final String EMPTY_DIR_NAME = "empty";
    static final String DIR_NAME = "dir";
    static final String SUB_DIR_NAME = "subDir";
    static final String TEXT_FILE_NAME1 = "text1.txt";
    static final String TEXT_FILE_NAME2 = "text2.txt";
    @BeforeEach
    void setup() {
        rmApplication = new RmApplication();
    }

    @BeforeEach
    void setCurrentDirectory() {
        Environment.currentDirectory += TESTING_PATH;
        outputStream = new ByteArrayOutputStream();
    }

    @AfterEach
    void reset() throws IOException {
        Environment.currentDirectory = CWD;
        outputStream.close();
    }

    @Test
    @Disabled
    void run_NullArguments_throwsRmException(){
        assertThrows(RmException.class, () -> {
            rmApplication.run(null, System.in, outputStream);
        });
    }

    @Test
    @Disabled
    void run_NullOutputStream_throwsRmException(){
        String[] args = {};
        assertThrows(RmException.class, () -> {
            rmApplication.run(args, System.in, null);
        });
    }

    @Test
    @Disabled
    void run_OnlyInvalidArgs_throwsRmException() {
        String[] args = {"-b", "-c"};
        assertThrows(RmException.class, () -> {
            rmApplication.run(args, System.in, outputStream);
        });
    }

    @Test
    @Disabled
    void run_InvalidAndValidArgs_throwsRmException() {
        String[] args = {"-r", "-d", "-R", "-D"};
        assertThrows(RmException.class, () -> {
            rmApplication.run(args, System.in, outputStream);
        });
    }

    @Test
    @Disabled
    void run_ValidArgs_DoesNotThrowException() {
        String[] args = {"-r", "-d"};
        assertDoesNotThrow(() -> {
            rmApplication.run(args, System.in, outputStream);
        });
    }

    @Test
    @Disabled
    void run_OneFileArgs_CorrectOutputStream() {
        String[] args = {TEXT_FILE_NAME1};
        assertDoesNotThrow(() -> {
            FileSystemUtils.createFile(TEXT_FILE_NAME1);
            rmApplication.run(args, System.in, outputStream);
            assertFalse(FileSystemUtils.fileOrDirExist(TEXT_FILE_NAME1));
        });
    }

    @Test
    @Disabled
    void run_NonExistingFileArgs_CorrectOutputStream() {
        String[] args = {"fakefile.txt"};
        String expectedResult =  "rm: No such file exist";
        assertDoesNotThrow(() -> {
            rmApplication.run(args, System.in, outputStream);
            assertEquals(expectedResult, outputStream.toString());
        });
    }

    @Test
    @Disabled
    void run_TwoFileArgs_CorrectOutputStream() {
        String[] args = {TEXT_FILE_NAME1, TEXT_FILE_NAME2};
        assertDoesNotThrow(() -> {
            FileSystemUtils.createFile(TEXT_FILE_NAME1);
            FileSystemUtils.createFile(TEXT_FILE_NAME2);
            rmApplication.run(args, System.in, outputStream);
            assertFalse(FileSystemUtils.fileOrDirExist(TEXT_FILE_NAME1));
            assertFalse(FileSystemUtils.fileOrDirExist(TEXT_FILE_NAME2));
        });
    }

    @Test
    @Disabled
    void run_OneDirWithRecursiveArgs_CorrectOutputStream() {
        String[] args = {DIR_NAME, RECURSIVE_ARG};
        assertDoesNotThrow(() -> {
            FileSystemUtils.createDir(DIR_NAME);
            String prevDir = Environment.currentDirectory;
            // Moves current working directory to the directory created
            Environment.currentDirectory += CHAR_FILE_SEP + DIR_NAME;
            FileSystemUtils.createDir(SUB_DIR_NAME);
            // Reset current working directory to working directory prior to moving
            Environment.currentDirectory = prevDir;

            rmApplication.run(args, System.in, outputStream);
            assertFalse(FileSystemUtils.fileOrDirExist(DIR_NAME));
        });
    }
}
