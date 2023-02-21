package sg.edu.nus.comp.cs4218.impl.app;

import org.junit.After;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sg.edu.nus.comp.cs4218.Environment;
import sg.edu.nus.comp.cs4218.exception.CdException;

import static org.junit.jupiter.api.Assertions.*;

import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.*;

public class CdApplicationTest {
    CdApplication cdApplication;

    @BeforeEach
    void setup() {
        cdApplication = new CdApplication();
    }

    @Test
    void cd_NoArgs_ShouldCdCorrectly() throws CdException {
        String string = " ";
        Exception exception = assertThrows(Exception.class, () -> cdApplication.changeToDirectory(string));
        assertEquals("cd: " + ERR_NO_ARGS, exception.getMessage());
    }

    @Test
    void cd_PathDoesNotExist_ShouldCdCorrectly() throws CdException {
        String string = "blah";
        Exception exception = assertThrows(Exception.class, () -> cdApplication.changeToDirectory(string));
        assertEquals("cd: " + ERR_FILE_NOT_FOUND, exception.getMessage());
    }

    @Test
    void cd_PathExist_ShouldCdCorrectly() throws CdException {
        String string = "src/test";
        String directory = Environment.currentDirectory;
        cdApplication.changeToDirectory(string);
        assertEquals(directory + "/src/test", Environment.currentDirectory);
        Environment.currentDirectory = directory;

    }

    @Test
    void cd_PathNotDirectory_ShouldCdCorrectly() throws CdException {
        String string = "README.md";
        Exception exception = assertThrows(Exception.class, () -> cdApplication.changeToDirectory(string));
        assertEquals("cd: " + ERR_IS_NOT_DIR, exception.getMessage());
    }
}
