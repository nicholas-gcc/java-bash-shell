package sg.edu.nus.comp.cs4218.impl.app;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Path;

import sg.edu.nus.comp.cs4218.Environment;
import sg.edu.nus.comp.cs4218.exception.LsException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.CHAR_FILE_SEP;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.STRING_NEWLINE;

public class LsApplicationTest {
    LsApplication lsApplication;
    static final String CWD = System.getProperty("user.dir");
    static final String TESTING_PATH = CHAR_FILE_SEP + "assets" + CHAR_FILE_SEP + "app" + CHAR_FILE_SEP + "ls";
    static final String TEST_DIR_1 = "testDir1";
    static final String TEST_DIR_2 = "testDir2";
    static final String TEST_DIR_3 = "testDir3";
    static final String TEST_DIR_1_1 = "testDir1-1";
    static final String TEST_DIR_1_2 = "testDir1-2";
    static final String ALP_TEXTFILE1 = "abc.txt";
    static final String ALP_TEXTFILE2 = "abc.rtf";
    static final String NUM_TEXTFILE = "123.txt";

    OutputStream outputStream;
    @BeforeEach
    void setup() {
        lsApplication = new LsApplication();
    }

    @BeforeEach
    void setCurrentDirectory() {
        Environment.currentDirectory += TESTING_PATH;
        outputStream = new ByteArrayOutputStream();
    }

    @AfterEach
    void resetCurrentDirectory() throws IOException {
        Environment.currentDirectory = this.CWD;
        outputStream.close();
    }

    @Test
    void run_NullArguments_throwLsException() {
        assertThrows(LsException.class, () -> {
            lsApplication.run(null, System.in, outputStream);
        });
    }

    @Test
    void run_NullOutputStream_throwLsException() {
        String[] args = {};
        assertThrows(LsException.class, () -> {
            lsApplication.run(args, System.in, null);
        });
    }

    @Test
    void run_OnlyInvalidArgs_throwLsException() {
        String[] args = {"-b", "-c"};
        assertThrows(LsException.class, () -> {
            lsApplication.run(args, System.in, outputStream);
        });
    }

    @Test
    void run_InvalidAndValidArgs_throwLsException() {
        String[] args = {"-x", "-r", "-X", "-R"};
        assertThrows(LsException.class, () -> {
            lsApplication.run(args, System.in, outputStream);
        });
    }

    @Test
    void run_ValidArgs_DoesNotThrowException() {
        String[] args = {"-X", "-R"};
        assertDoesNotThrow(() -> {
            lsApplication.run(args, System.in, outputStream);
        });
    }

    @Test
    void run_NoRecursiveNoSortedArgs_CorrectOutputStream() {
        String[] args = {TEST_DIR_3};
        String expectedResult =  TEST_DIR_3 + ":" + STRING_NEWLINE + NUM_TEXTFILE + STRING_NEWLINE + ALP_TEXTFILE2 + STRING_NEWLINE;
        assertDoesNotThrow(() -> {
            lsApplication.run(args, System.in, outputStream);
            assertEquals(expectedResult, outputStream.toString());
        });
    }

    @Test
    void run_NonExistingFileArgs_CorrectOutputStream() {
        String[] args = {TEST_DIR_3, "fakefile.txt"};
        String expectedResult =  TEST_DIR_3 + ":" + STRING_NEWLINE + NUM_TEXTFILE + STRING_NEWLINE + ALP_TEXTFILE2 + STRING_NEWLINE +
                STRING_NEWLINE + "ls: cannot access 'fakefile.txt': No such file or directory" + STRING_NEWLINE;
        assertDoesNotThrow(() -> {
            lsApplication.run(args, System.in, outputStream);
            assertEquals(expectedResult, outputStream.toString());
        });
    }

    @Test
    void listFolderContent_EmptyFilenameArgs_returnCorrectStringOutput() {
        boolean isRecursive = false;
        boolean isSortByExt = false;

        String expectedResult =  ALP_TEXTFILE1 + STRING_NEWLINE + TEST_DIR_1 + STRING_NEWLINE + TEST_DIR_2 + STRING_NEWLINE  + TEST_DIR_3;
        assertDoesNotThrow(() -> {
            String folderContent = lsApplication.listFolderContent(isRecursive, isSortByExt);
            assertEquals(expectedResult, folderContent);
        });
    }

    @Test
    void listFolderContent_OneFilenameArgs_returnCorrectStringOutput() {
        boolean isRecursive = false;
        boolean isSortByExt = false;

        String expectedResult =  TEST_DIR_2 + ":" + STRING_NEWLINE + ALP_TEXTFILE1;
        assertDoesNotThrow(() -> {
            String folderContent = lsApplication.listFolderContent(isRecursive, isSortByExt, TEST_DIR_2);
            assertEquals(expectedResult, folderContent);
        });
    }

    @Test
    void listFolderContent_TwoFilenamesArgs_returnCorrectStringOutput() {
        boolean isRecursive = false;
        boolean isSortByExt = false;

        String expectedResult =  TEST_DIR_2 + ":" + STRING_NEWLINE + ALP_TEXTFILE1
                + STRING_NEWLINE + STRING_NEWLINE
                + TEST_DIR_3 + ":" + STRING_NEWLINE + NUM_TEXTFILE + STRING_NEWLINE + ALP_TEXTFILE2;
        assertDoesNotThrow(() -> {
            String folderContent = lsApplication.listFolderContent(isRecursive, isSortByExt, TEST_DIR_2, TEST_DIR_3);
            assertEquals(expectedResult, folderContent);
        });
    }

    @Test
    void listFolderContent_EmptyFilenameWithRecursiveArgs_returnCorrectStringOutput() {
        boolean isRecursive = true;
        boolean isSortByExt = false;

        // Each line represents a folder's content except for those with newlines only
        String expectedResult =  "." + CHAR_FILE_SEP + ":" + STRING_NEWLINE + ALP_TEXTFILE1 + STRING_NEWLINE + TEST_DIR_1 + STRING_NEWLINE + TEST_DIR_2 + STRING_NEWLINE  + TEST_DIR_3
                + STRING_NEWLINE + STRING_NEWLINE
                + TEST_DIR_1 + ":" + STRING_NEWLINE + ALP_TEXTFILE1 + STRING_NEWLINE + TEST_DIR_1_1 + STRING_NEWLINE + TEST_DIR_1_2
                + STRING_NEWLINE + STRING_NEWLINE
                + TEST_DIR_1 + CHAR_FILE_SEP + TEST_DIR_1_1 + ":" + STRING_NEWLINE + ALP_TEXTFILE1
                + STRING_NEWLINE + STRING_NEWLINE
                + TEST_DIR_1 + CHAR_FILE_SEP + TEST_DIR_1_2 + ":" + STRING_NEWLINE + ALP_TEXTFILE1
                + STRING_NEWLINE + STRING_NEWLINE
                + TEST_DIR_2 + ":" + STRING_NEWLINE + ALP_TEXTFILE1
                + STRING_NEWLINE + STRING_NEWLINE
                + TEST_DIR_3 + ":" + STRING_NEWLINE + NUM_TEXTFILE + STRING_NEWLINE + ALP_TEXTFILE2;

        assertDoesNotThrow(() -> {
            String folderContent = lsApplication.listFolderContent(isRecursive, isSortByExt);
            assertEquals(expectedResult, folderContent);
        });
    }

    @Test
    void listFolderContent_EmptyFilenameWithSortedArgs_returnCorrectStringOutput() {
        boolean isRecursive = false;
        boolean isSortByExt = true;

        String expectedResult = TEST_DIR_1 + STRING_NEWLINE + TEST_DIR_2 + STRING_NEWLINE  + TEST_DIR_3 + STRING_NEWLINE + ALP_TEXTFILE1;

        assertDoesNotThrow(() -> {
            String folderContent = lsApplication.listFolderContent(isRecursive, isSortByExt);
            assertEquals(expectedResult, folderContent);
        });
    }

    @Test
    void listFolderContent_TwoFilenameWithRecursiveAndSortedArgs_returnCorrectStringOutput() {
        boolean isRecursive = true;
        boolean isSortByExt = true;

        String expectedResult = TEST_DIR_1 + ":" + STRING_NEWLINE + TEST_DIR_1_1 + STRING_NEWLINE + TEST_DIR_1_2 + STRING_NEWLINE + ALP_TEXTFILE1
                + STRING_NEWLINE + STRING_NEWLINE
                + TEST_DIR_1 + CHAR_FILE_SEP + TEST_DIR_1_1 + ":" + STRING_NEWLINE + ALP_TEXTFILE1
                + STRING_NEWLINE + STRING_NEWLINE
                + TEST_DIR_1 + CHAR_FILE_SEP + TEST_DIR_1_2 + ":" + STRING_NEWLINE + ALP_TEXTFILE1
                + STRING_NEWLINE + STRING_NEWLINE
                + TEST_DIR_3 + ":" + STRING_NEWLINE + ALP_TEXTFILE2 + STRING_NEWLINE + NUM_TEXTFILE;

        assertDoesNotThrow(() -> {
            String folderContent = lsApplication.listFolderContent(isRecursive, isSortByExt, "testDir1", "testDir3");
            assertEquals(expectedResult, folderContent);
        });
    }

}
