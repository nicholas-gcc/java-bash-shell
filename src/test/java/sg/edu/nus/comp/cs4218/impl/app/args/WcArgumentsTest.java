package sg.edu.nus.comp.cs4218.impl.app.args;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sg.edu.nus.comp.cs4218.exception.WcException;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.ERR_INVALID_FLAG;

public class WcArgumentsTest {

    WcArguments wcArguments;
    private static final String USER_DIR = "user.dir";

    @BeforeAll
    static void setDirectory() {
        String currentDirectory = System.getProperty(USER_DIR);
        System.setProperty(USER_DIR, currentDirectory + File.separator + "assets"
                + File.separator + "app" + File.separator + "wc");
    }

    @BeforeEach
    void setup() {
        wcArguments = new WcArguments();
    }

    @AfterAll
    static void resetDirectory() {
        String originalDirectory = System.getProperty(USER_DIR).replace(File.separator + "assets"
                + File.separator + "app" + File.separator + "wc", "");
        System.setProperty(USER_DIR, originalDirectory);
    }

    @Test
    void wcArgs_SingleFileParse_ShouldParseCorrectly() throws WcException {
        String [] array = {"wc_test1.txt"};
        wcArguments.parse(array);
        List arrayList = Arrays.asList(array);
        assertEquals(arrayList, wcArguments.getFiles());
    }

    @Test
    void wcArgs_MultipleFileParse_ShouldParseCorrectly() throws WcException {
        String [] array = {"wc_test1.txt", "wc_test2.txt"};
        wcArguments.parse(array);
        List arrayList = Arrays.asList(array);
        assertEquals(arrayList, wcArguments.getFiles());
    }

    @Test
    void wcArgs_NoFileParse_ShouldParseCorrectly() throws WcException {
        String [] array = {};
        wcArguments.parse(array);
        List arrayList = Arrays.asList(array);
        assertEquals(arrayList, wcArguments.getFiles());
    }

    @Test
    void wcArgs_NoFlags_ShouldParseCorrectly() throws WcException {
        String [] array = {};
        wcArguments.parse(array);
        assertEquals(true, wcArguments.isLines());
        assertEquals(true, wcArguments.isBytes());
        assertEquals(true, wcArguments.isWords());
    }

    @Test
    void wcArgs_LinesFlag_ShouldParseCorrectly() throws WcException {
        String [] array = {"-l"};
        wcArguments.parse(array);
        assertEquals(true, wcArguments.isLines());
        assertEquals(false, wcArguments.isBytes());
        assertEquals(false, wcArguments.isWords());
    }

    @Test
    void wcArgs_LinesBytesFlag_ShouldParseCorrectly() throws WcException {
        String [] array = {"-l", "-c"};
        wcArguments.parse(array);
        assertEquals(true, wcArguments.isLines());
        assertEquals(true, wcArguments.isBytes());
        assertEquals(false, wcArguments.isWords());
    }

    @Test
    void wcArgs_BytesWordFlag_ShouldParseCorrectly() throws WcException {
        String [] array = {"-c", "-w"};
        wcArguments.parse(array);
        assertEquals(false, wcArguments.isLines());
        assertEquals(true, wcArguments.isBytes());
        assertEquals(true, wcArguments.isWords());
    }

    @Test
    void wcArgs_InvalidFlag_ThrowsWcException() {
        String [] array = {"-a"};
        WcException wcException = assertThrows(WcException.class, () -> wcArguments.parse(array));
        assertEquals("wc: " + ERR_INVALID_FLAG, wcException.getMessage());
    }
}
