package sg.edu.nus.comp.cs4218.impl.app;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import sg.edu.nus.comp.cs4218.Environment;
import sg.edu.nus.comp.cs4218.exception.LsException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.CHAR_FILE_SEP;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.STRING_NEWLINE;
import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.ERR_NO_OSTREAM;
import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.ERR_NULL_ARGS;
import static sg.edu.nus.comp.cs4218.impl.parser.ArgsParser.ILLEGAL_FLAG_MSG;

@SuppressWarnings("PMD.LongVariable")
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
    static final String FAKE_TEXTFILE = "fake.txt";
    static final String ERROR_INITIALS = "ls: ";

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
    void run_NullArguments_throwLsExceptionWithCorrectMessage() {
        LsException exception = assertThrows(LsException.class, () -> {
            lsApplication.run(null, System.in, outputStream);
        });
        String expectedMessage = ERROR_INITIALS + ERR_NULL_ARGS;
        assertEquals(expectedMessage, exception.getMessage());

    }

    @Test
    void run_NullOutputStream_throwLsExceptionWithCorrectMessage() {
        String[] args = {};
        LsException exception = assertThrows(LsException.class, () -> {
            lsApplication.run(args, System.in, null);
        });
        String expectedMessage = ERROR_INITIALS + ERR_NO_OSTREAM;
        assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    void run_OnlyInvalidArgs_throwLsExceptionWithCorrectMessage() {
        String[] args = {"-b", "-c"};
        LsException exception = assertThrows(LsException.class, () -> {
            lsApplication.run(args, System.in, outputStream);
        });
        String expectedMessage = String.format(ERROR_INITIALS + "%s", ILLEGAL_FLAG_MSG + 'b');
        assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    void run_InvalidAndValidArgs_throwLsExceptionWithCorrectMessage() {
        String[] args = {"-x", "-r", "-X", "-R"};
        LsException exception = assertThrows(LsException.class, () -> {
            lsApplication.run(args, System.in, outputStream);
        });
        String expectedMessage = String.format(ERROR_INITIALS + "%s", ILLEGAL_FLAG_MSG + 'r');
        assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    void run_ValidArgs_DoesNotThrowException() {
        String[] args = {"-X", "-R"};
        assertDoesNotThrow(() -> {
            lsApplication.run(args, System.in, outputStream);
        });
    }

    @Test
    void run_NonExistingFileArgs_CorrectOutputStream() throws LsException {
        String[] args = {TEST_DIR_3, FAKE_TEXTFILE};
        String expectedResult =  "ls: cannot access 'fake.txt': No such file or directory" + STRING_NEWLINE +
                TEST_DIR_3 + ":" + STRING_NEWLINE + NUM_TEXTFILE + STRING_NEWLINE + ALP_TEXTFILE2 + STRING_NEWLINE ;
        lsApplication.run(args, System.in, outputStream);
        assertEquals(expectedResult, outputStream.toString());
    }

    @Test
    void run_OneFileNameArgs_CorrectOutputStream() throws LsException {
        String[] args = {ALP_TEXTFILE1};
        String expectedResult =  ALP_TEXTFILE1 + STRING_NEWLINE;
        lsApplication.run(args, System.in, outputStream);
        assertEquals(expectedResult, outputStream.toString());
    }

    @Test
    void run_OneDirNameArgs_CorrectOutputStream() throws LsException {
        String[] args = {TEST_DIR_3};
        String expectedResult =  TEST_DIR_3 + ":" + STRING_NEWLINE + NUM_TEXTFILE + STRING_NEWLINE + ALP_TEXTFILE2 + STRING_NEWLINE;
        lsApplication.run(args, System.in, outputStream);
        assertEquals(expectedResult, outputStream.toString());
    }

    @Test
    void run_OneDirNameOneFileNameArgs_CorrectOutputStream() throws LsException {
        String[] args = {TEST_DIR_3, ALP_TEXTFILE1};
        String expectedResult =  ALP_TEXTFILE1 + STRING_NEWLINE + STRING_NEWLINE
                + TEST_DIR_3 + ":" + STRING_NEWLINE + NUM_TEXTFILE + STRING_NEWLINE + ALP_TEXTFILE2 + STRING_NEWLINE;
        lsApplication.run(args, System.in, outputStream);
        assertEquals(expectedResult, outputStream.toString());
    }

    @Test
    void run_OneDirNameOneFileNameWithRecursiveArgs_CorrectOutputStream() throws LsException {
        String[] args = {TEST_DIR_1, ALP_TEXTFILE1, "-R"};
        String expectedResult =  ALP_TEXTFILE1 + STRING_NEWLINE + STRING_NEWLINE
                + TEST_DIR_1 + ":" + STRING_NEWLINE + ALP_TEXTFILE1 + STRING_NEWLINE + TEST_DIR_1_1 + STRING_NEWLINE + TEST_DIR_1_2
                + STRING_NEWLINE + STRING_NEWLINE
                + TEST_DIR_1 + CHAR_FILE_SEP + TEST_DIR_1_1 + ":" + STRING_NEWLINE + ALP_TEXTFILE1
                + STRING_NEWLINE + STRING_NEWLINE
                + TEST_DIR_1 + CHAR_FILE_SEP + TEST_DIR_1_2 + ":" + STRING_NEWLINE + ALP_TEXTFILE1 + STRING_NEWLINE;
        lsApplication.run(args, System.in, outputStream);
        assertEquals(expectedResult, outputStream.toString());
    }

    @Test
    void run_OneDirNameOneExistingFileOneNonExistingFileWithRecursiveArgs_CorrectOutputStream() throws LsException {
        String[] args = {TEST_DIR_1, ALP_TEXTFILE1, FAKE_TEXTFILE, "-R"};
        String expectedResult =  "ls: cannot access 'fake.txt': No such file or directory" + STRING_NEWLINE
                + ALP_TEXTFILE1 + STRING_NEWLINE + STRING_NEWLINE
                + TEST_DIR_1 + ":" + STRING_NEWLINE + ALP_TEXTFILE1 + STRING_NEWLINE + TEST_DIR_1_1 + STRING_NEWLINE + TEST_DIR_1_2
                + STRING_NEWLINE + STRING_NEWLINE
                + TEST_DIR_1 + CHAR_FILE_SEP + TEST_DIR_1_1 + ":" + STRING_NEWLINE + ALP_TEXTFILE1
                + STRING_NEWLINE + STRING_NEWLINE
                + TEST_DIR_1 + CHAR_FILE_SEP + TEST_DIR_1_2 + ":" + STRING_NEWLINE + ALP_TEXTFILE1 + STRING_NEWLINE;
        lsApplication.run(args, System.in, outputStream);
        assertEquals(expectedResult, outputStream.toString());

    }

    @Test
    void run_TwoDirNameOneExistingFileOneFakeFileWithRecursiveWithSortedArgs_CorrectOutputStream() throws LsException {
        String[] args = {TEST_DIR_1, ALP_TEXTFILE1, FAKE_TEXTFILE, TEST_DIR_3, "-R" ,"-X"};
        String expectedResult =  "ls: cannot access 'fake.txt': No such file or directory" + STRING_NEWLINE
                + ALP_TEXTFILE1 + STRING_NEWLINE + STRING_NEWLINE
                + TEST_DIR_1 + ":" + STRING_NEWLINE + TEST_DIR_1_1 + STRING_NEWLINE + TEST_DIR_1_2 + STRING_NEWLINE + ALP_TEXTFILE1
                + STRING_NEWLINE + STRING_NEWLINE
                + TEST_DIR_1 + CHAR_FILE_SEP + TEST_DIR_1_1 + ":" + STRING_NEWLINE + ALP_TEXTFILE1
                + STRING_NEWLINE + STRING_NEWLINE
                + TEST_DIR_1 + CHAR_FILE_SEP + TEST_DIR_1_2 + ":" + STRING_NEWLINE + ALP_TEXTFILE1
                + STRING_NEWLINE + STRING_NEWLINE
                + TEST_DIR_3 + ":" + STRING_NEWLINE + ALP_TEXTFILE2 + STRING_NEWLINE + NUM_TEXTFILE + STRING_NEWLINE;
        lsApplication.run(args, System.in, outputStream);
        assertEquals(expectedResult, outputStream.toString());
    }

    @Test
    void run_OneDirNameOneFileNameWithSortedArgs_CorrectOutputStream() throws LsException {
        String[] args = {TEST_DIR_3, ALP_TEXTFILE1, "-X"};
        String expectedResult =  ALP_TEXTFILE1 + STRING_NEWLINE + STRING_NEWLINE
                + TEST_DIR_3 + ":" + STRING_NEWLINE + ALP_TEXTFILE2 + STRING_NEWLINE + NUM_TEXTFILE + STRING_NEWLINE;
        lsApplication.run(args, System.in, outputStream);
        assertEquals(expectedResult, outputStream.toString());
    }

    @Test
    void run_EmptyStringArg_ThrowLsExceptionWithCorrectMessage() throws LsException {
        String emptyArg = "";
        String[] args = {emptyArg};
        lsApplication.run(args, System.in, outputStream);

        String expectedMessage = ERROR_INITIALS + "cannot access '': No such file or directory" + STRING_NEWLINE;
        assertEquals(expectedMessage, outputStream.toString());
    }

    @Test
    void run_StringWithOnlySpacesArgs_ThrowLsExceptionWithCorrectMessage() throws LsException {
        String oneSpace = " ";
        String multipleSpaces = "   ";
        String[] args = {oneSpace, multipleSpaces};

        lsApplication.run(args, System.in, outputStream);

        String expectedMessage = ERROR_INITIALS + "cannot access ' ': No such file or directory" + STRING_NEWLINE +
                ERROR_INITIALS + "cannot access '   ': No such file or directory" + STRING_NEWLINE;
        assertEquals(expectedMessage, outputStream.toString());
    }

    @Test
    void run_ValidArgAndSpaceOnlyArgsAndEmptyArg_ThrowLsExceptionWithCorrectMessage() throws LsException {
        String validArg = ALP_TEXTFILE1;
        String oneSpace = " ";
        String multipleSpaces = "   ";
        String emptyArg = "";
        String[] args = {validArg, oneSpace, multipleSpaces, emptyArg};

        lsApplication.run(args, System.in, outputStream);

        String expectedMessage = ERROR_INITIALS + "cannot access ' ': No such file or directory" + STRING_NEWLINE
                + ERROR_INITIALS + "cannot access '   ': No such file or directory" + STRING_NEWLINE
                + ERROR_INITIALS + "cannot access '': No such file or directory" + STRING_NEWLINE
                + ALP_TEXTFILE1 + STRING_NEWLINE;
        assertEquals(expectedMessage, outputStream.toString());
    }

    @Test
    void run_NonExistingFileWithPwd_ThrowLsExceptionWithCorrectMessage() throws LsException {
        String nonExistingFileWithPwd = "non-exist" + CHAR_FILE_SEP + ".." + CHAR_FILE_SEP;
        String[] args = {nonExistingFileWithPwd};
        lsApplication.run(args, System.in, outputStream);
        String expectedOutput = String.format(ERROR_INITIALS + String.format("cannot access '%s': No such file or directory", nonExistingFileWithPwd)) + STRING_NEWLINE;
        assertEquals(expectedOutput, outputStream.toString());
    }

    @Test
    void run_NonDirectoryFileWithPwd_ThrowLsExceptionWithCorrectMessage() throws LsException {
        String nonDirectoryFileWithPwd = ALP_TEXTFILE1 + CHAR_FILE_SEP + ".." + CHAR_FILE_SEP;
        String[] args = {nonDirectoryFileWithPwd};
        lsApplication.run(args, System.in, outputStream);
        String expectedOutput = String.format(ERROR_INITIALS + String.format("cannot access '%s': Not a directory", nonDirectoryFileWithPwd)) + STRING_NEWLINE;
        assertEquals(expectedOutput, outputStream.toString());
    }

    @Test
    void run_NestedDirectory_CorrectOutputStream() throws LsException {
        String nestedDirectory = TEST_DIR_1 + CHAR_FILE_SEP + TEST_DIR_1_1;
        String[] args = {nestedDirectory};
        lsApplication.run(args, System.in, outputStream);
        String expectedOutput = TEST_DIR_1 + CHAR_FILE_SEP + TEST_DIR_1_1 + ":" + STRING_NEWLINE + ALP_TEXTFILE1 + STRING_NEWLINE;
        assertEquals(expectedOutput, outputStream.toString());
    }

    @Test
    void listFolderContent_EmptyFilenameArgs_returnCorrectStringOutput() throws LsException {
        boolean isRecursive = false;
        boolean isSortByExt = false;

        String expectedResult =  ALP_TEXTFILE1 + STRING_NEWLINE + TEST_DIR_1 + STRING_NEWLINE + TEST_DIR_2 + STRING_NEWLINE  + TEST_DIR_3;
        String folderContent = lsApplication.listFolderContent(isRecursive, isSortByExt);
        assertEquals(expectedResult, folderContent);
    }

    @Test
    void listFolderContent_OneFilenameArgs_returnCorrectStringOutput() throws LsException {
        boolean isRecursive = false;
        boolean isSortByExt = false;

        String expectedResult =  TEST_DIR_2 + ":" + STRING_NEWLINE + ALP_TEXTFILE1;
        String folderContent = lsApplication.listFolderContent(isRecursive, isSortByExt, TEST_DIR_2);
        assertEquals(expectedResult, folderContent);
    }

    @Test
    void listFolderContent_TwoFilenamesArgs_returnCorrectStringOutput() throws LsException {
        boolean isRecursive = false;
        boolean isSortByExt = false;

        String expectedResult =  TEST_DIR_2 + ":" + STRING_NEWLINE + ALP_TEXTFILE1
                + STRING_NEWLINE + STRING_NEWLINE
                + TEST_DIR_3 + ":" + STRING_NEWLINE + NUM_TEXTFILE + STRING_NEWLINE + ALP_TEXTFILE2;
        String folderContent = lsApplication.listFolderContent(isRecursive, isSortByExt, TEST_DIR_2, TEST_DIR_3);
        assertEquals(expectedResult, folderContent);
    }

    @Test
    void listFolderContent_EmptyFilenameWithRecursiveArgs_returnCorrectStringOutput() throws LsException {
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

        String folderContent = lsApplication.listFolderContent(isRecursive, isSortByExt);
        assertEquals(expectedResult, folderContent);
    }

    @Test
    void listFolderContent_EmptyFilenameWithSortedArgs_returnCorrectStringOutput() throws LsException {
        boolean isRecursive = false;
        boolean isSortByExt = true;

        String expectedResult = TEST_DIR_1 + STRING_NEWLINE + TEST_DIR_2 + STRING_NEWLINE  + TEST_DIR_3 + STRING_NEWLINE + ALP_TEXTFILE1;

        String folderContent = lsApplication.listFolderContent(isRecursive, isSortByExt);
        assertEquals(expectedResult, folderContent);
    }

    @Test
    void listFolderContent_TwoFilenameWithRecursiveAndSortedArgs_returnCorrectStringOutput() throws LsException {
        boolean isRecursive = true;
        boolean isSortByExt = true;

        String expectedResult = TEST_DIR_1 + ":" + STRING_NEWLINE + TEST_DIR_1_1 + STRING_NEWLINE + TEST_DIR_1_2 + STRING_NEWLINE + ALP_TEXTFILE1
                + STRING_NEWLINE + STRING_NEWLINE
                + TEST_DIR_1 + CHAR_FILE_SEP + TEST_DIR_1_1 + ":" + STRING_NEWLINE + ALP_TEXTFILE1
                + STRING_NEWLINE + STRING_NEWLINE
                + TEST_DIR_1 + CHAR_FILE_SEP + TEST_DIR_1_2 + ":" + STRING_NEWLINE + ALP_TEXTFILE1
                + STRING_NEWLINE + STRING_NEWLINE
                + TEST_DIR_3 + ":" + STRING_NEWLINE + ALP_TEXTFILE2 + STRING_NEWLINE + NUM_TEXTFILE;

        String folderContent = lsApplication.listFolderContent(isRecursive, isSortByExt, "testDir1", "testDir3");
        assertEquals(expectedResult, folderContent);
    }

}
