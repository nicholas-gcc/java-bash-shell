package sg.edu.nus.comp.cs4218.impl.app;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sg.edu.nus.comp.cs4218.Environment;
import sg.edu.nus.comp.cs4218.exception.CdException;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.ERR_NO_ARGS;
import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.ERR_FILE_NOT_FOUND;
import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.ERR_IS_NOT_DIR;

public class CdApplicationTest {
    CdApplication cdApplication;

    @BeforeEach
    void setup() {
        cdApplication = new CdApplication();
    }

    @Test
    void cd_NoArgs_ThrowsCdException() {
        String string = " ";
        CdException cdException = assertThrows(CdException.class, () -> cdApplication.changeToDirectory(string));
        assertEquals("cd: " + ERR_NO_ARGS, cdException.getMessage());
    }

    @Test
    void cd_PathDoesNotExist_ThrowsCdException() {
        String string = "blah";
        CdException cdException = assertThrows(CdException.class, () -> cdApplication.changeToDirectory(string));
        assertEquals("cd: " + ERR_FILE_NOT_FOUND, cdException.getMessage());
    }

    @Test
    void cd_PathExist_ShouldCdCorrectly() throws CdException {
        String string = "src" + File.separator + "test";
        String directory = Environment.currentDirectory;
        cdApplication.changeToDirectory(string);
        assertEquals(directory + File.separator + string, Environment.currentDirectory);
        Environment.currentDirectory = directory;

    }

    @Test
    void cd_PathNotDirectory_ThrowsCdException() {
        String string = "README.md";
        CdException cdException = assertThrows(CdException.class, () -> cdApplication.changeToDirectory(string));
        assertEquals("cd: " + ERR_IS_NOT_DIR, cdException.getMessage());
    }

    @Test
    void cd_PathParentDirectory_ShouldCdCorrectly() throws CdException {
        String string = "..";
        String directory = Environment.currentDirectory;
        String parentDirectory = new File(directory).getParent();
        cdApplication.changeToDirectory(string);
        assertEquals(parentDirectory, Environment.currentDirectory);
        Environment.currentDirectory = directory;
    }

    @Test
    void cd_PathCurrentDirectory_ShouldCdCorrectly() throws CdException {
        String string = ".";
        String directory = Environment.currentDirectory;
        cdApplication.changeToDirectory(string);
        assertEquals(directory, Environment.currentDirectory);
    }
}
