package sg.edu.nus.comp.cs4218.Bugs.tests;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sg.edu.nus.comp.cs4218.Environment;
import sg.edu.nus.comp.cs4218.impl.app.CdApplication;
import sg.edu.nus.comp.cs4218.exception.CdException;

import java.io.InputStream;
import java.io.OutputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CdApplicationTest {
    CdApplication cdApplication;
    InputStream stdin;
    OutputStream stdout;

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
    void cd_RunNoArgs_ShouldCdCorrectly() throws CdException {
        String [] array = {};
        String expectedDirectory =  System.getProperty("user.home");
        cdApplication.run(array, stdin, stdout);
        assertEquals(expectedDirectory, Environment.currentDirectory);
    }
}
