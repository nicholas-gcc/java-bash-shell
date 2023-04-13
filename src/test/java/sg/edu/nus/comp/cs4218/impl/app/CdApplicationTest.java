package sg.edu.nus.comp.cs4218.impl.app;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sg.edu.nus.comp.cs4218.Environment;
import sg.edu.nus.comp.cs4218.exception.CdException;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.ERR_NO_ARGS;
import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.ERR_FILE_DIR_NOT_FOUND;
import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.ERR_IS_NOT_DIR;
import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.ERR_NULL_ARGS;

public class CdApplicationTest {
    CdApplication cdApplication;
    InputStream stdin;
    OutputStream stdout;
    private static final String CD_EX_PREFIX = "cd: ";

    @BeforeEach
    void setup() {
        cdApplication = new CdApplication();
        stdin = System.in;
        stdout = System.out;
    }

    @AfterEach
    void resetDir() {
        Environment.currentDirectory = System.getProperty("user.dir");
    }

    @Test
    void cd_NoArgs_ThrowsCdException() {
        String string = "";
        CdException cdException = assertThrows(CdException.class, () -> cdApplication.changeToDirectory(string));
        assertEquals(CD_EX_PREFIX + ERR_NO_ARGS, cdException.getMessage());
    }

    @Test
    void cd_PathDoesNotExist_ThrowsCdException() {
        String string = "blah";
        CdException cdException = assertThrows(CdException.class, () -> cdApplication.changeToDirectory(string));
        assertEquals(CD_EX_PREFIX + ERR_FILE_DIR_NOT_FOUND, cdException.getMessage());
    }

    @Test
    void cd_PathExist_ShouldCdCorrectly() throws CdException {
        String string = "src" + File.separator + "test";
        String initialDirectory = Environment.currentDirectory;
        cdApplication.changeToDirectory(string);
        assertEquals(initialDirectory + File.separator + string, Environment.currentDirectory);
    }

    @Test
    void cd_PathNotDirectory_ThrowsCdException() {
        String string = "README.md";
        CdException cdException = assertThrows(CdException.class, () -> cdApplication.changeToDirectory(string));
        assertEquals(CD_EX_PREFIX + ERR_IS_NOT_DIR, cdException.getMessage());
    }

    @Test
    void cd_PathParentDirectory_ShouldCdCorrectly() throws CdException {
        String string = "..";
        String initialDirectory = Environment.currentDirectory;
        String parentDirectory = new File(initialDirectory).getParent();
        cdApplication.changeToDirectory(string);
        assertEquals(parentDirectory, Environment.currentDirectory);
    }

    @Test
    void cd_PathCurrentDirectory_ShouldCdCorrectly() throws CdException {
        String string = ".";
        String initialDirectory = Environment.currentDirectory;
        cdApplication.changeToDirectory(string);
        assertEquals(initialDirectory, Environment.currentDirectory);
    }

    @Test
    void cd_RunWithNullArguments_ThrowsException() {
        CdException cdException = assertThrows(CdException.class, () -> cdApplication.run(null, stdin, stdout));
        assertEquals(CD_EX_PREFIX + ERR_NULL_ARGS, cdException.getMessage());
    }

    @Test
    void cd_Run_ShouldCdCorrectly() throws CdException {
        String [] array = {"src" + File.separator + "test"};
        String initialDirectory = Environment.currentDirectory;
        cdApplication.run(array, stdin, stdout);
        assertEquals(initialDirectory + File.separator + array[0], Environment.currentDirectory);
    }

    @Test
    void cd_RunNoArgs_ShouldCdCorrectly() throws CdException {
        String [] array = {};
        String expectedDirectory =  System.getProperty("user.home");
        cdApplication.run(array, stdin, stdout);
        assertEquals(expectedDirectory, Environment.currentDirectory);
    }
}
