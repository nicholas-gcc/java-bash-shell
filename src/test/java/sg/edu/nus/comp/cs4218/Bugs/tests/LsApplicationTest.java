package sg.edu.nus.comp.cs4218.Bugs.tests;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import sg.edu.nus.comp.cs4218.Environment;
import sg.edu.nus.comp.cs4218.exception.LsException;
import sg.edu.nus.comp.cs4218.impl.app.LsApplication;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static sg.edu.nus.comp.cs4218.Bugs.utils.StringUtils.CHAR_FILE_SEP;
import static sg.edu.nus.comp.cs4218.Bugs.utils.StringUtils.STRING_NEWLINE;

@SuppressWarnings({"PMD.LongVariable"})
public class LsApplicationTest {
    LsApplication lsApplication;
    static final String CWD = System.getProperty("user.dir");
    static final String TESTING_PATH = CHAR_FILE_SEP + "src" + CHAR_FILE_SEP + "test" + CHAR_FILE_SEP
            + "java" + CHAR_FILE_SEP + "sg" + CHAR_FILE_SEP + "edu" + CHAR_FILE_SEP + "nus" + CHAR_FILE_SEP + "comp"
            + CHAR_FILE_SEP + "cs4218" + CHAR_FILE_SEP + "Bugs" + CHAR_FILE_SEP + "assets" + CHAR_FILE_SEP + "app"
            + CHAR_FILE_SEP + "ls";
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
    @DisplayName("Rebuttal 26")
    void run_EmptyStringArg_OutputsCorrectErrMsg() throws LsException {
        String emptyArg = "";
        String[] args = {emptyArg};
        lsApplication.run(args, System.in, outputStream);

        String expectedMessage = ERROR_INITIALS + "cannot access '': No such file or directory" + STRING_NEWLINE;
        assertEquals(expectedMessage, outputStream.toString());
    }

    @Test
    @DisplayName("Rebuttal 27")
    void run_NonExistingFileWithPwd_OutputsCorrectErrMsg() throws LsException {
        String nonExistingFileWithPwd = "non-exist" + CHAR_FILE_SEP + ".." + CHAR_FILE_SEP;
        String[] args = {nonExistingFileWithPwd};
        lsApplication.run(args, System.in, outputStream);
        String expectedOutput = String.format(ERROR_INITIALS + String.format("cannot access '%s': No such file or directory", nonExistingFileWithPwd)) + STRING_NEWLINE;
        assertEquals(expectedOutput, outputStream.toString());
    }

    @Test
    @DisplayName("Rebuttal 28")
    void run_NonDirectoryFileWithPwd_OutputsCorrectErrMsg() throws LsException {
        String nonDirectoryFileWithPwd = "abc.txt" + CHAR_FILE_SEP + ".." + CHAR_FILE_SEP;
        String[] args = {nonDirectoryFileWithPwd};
        lsApplication.run(args, System.in, outputStream);
        String expectedOutput = String.format(ERROR_INITIALS + String.format("cannot access '%s': Not a directory", nonDirectoryFileWithPwd)) + STRING_NEWLINE;
        assertEquals(expectedOutput, outputStream.toString());
    }
}
