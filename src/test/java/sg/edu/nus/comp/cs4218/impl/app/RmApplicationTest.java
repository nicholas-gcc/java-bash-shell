package sg.edu.nus.comp.cs4218.impl.app;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import sg.edu.nus.comp.cs4218.Environment;
import sg.edu.nus.comp.cs4218.exception.RmException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.CHAR_FILE_SEP;

public class RmApplicationTest {
    RmApplication rmApplication;
    OutputStream outputStream;
    static final String CWD = System.getProperty("user.dir");
    static final String TESTING_PATH = CHAR_FILE_SEP + "assets" + CHAR_FILE_SEP + "app" + CHAR_FILE_SEP + "rm";
    static final String EMPTY_DIR_NAME = "empty";
    static final String TEXT_FILE_NAME = "text.txt";
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

//    @Test
//    void run_NoRecursiveNoRmEmptyDirArgs_CorrectOutputStream() {
//        String[] args = {TEXT_FILE_NAME};
//        String expectedResult =  ;
//        assertDoesNotThrow(() -> {
//            rmApplication.run(args, System.in, outputStream);
//            assertEquals(expectedResult, outputStream.toString());
//        });
//    }
}
