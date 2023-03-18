package sg.edu.nus.comp.cs4218.impl.parser;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sg.edu.nus.comp.cs4218.exception.InvalidArgsException;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TeeArgsParserTest {
    TeeArgsParser teeArgsParser;

    @BeforeEach
    void setup() {
        teeArgsParser = new TeeArgsParser();
    }

    @Test
    void parse_illegalArgs_throwsInvalidArgsException() {
        String[] args = {"-X", "-r", "-d"};
        assertThrows(InvalidArgsException.class, () -> {
            teeArgsParser.parse(args);
        });
    }

    @Test
    void isRecursive_hasAppendArg_returnsTrue() throws InvalidArgsException {
        String[] args = {"-a"};
        teeArgsParser.parse(args);
        assertTrue(teeArgsParser.isAppend());

    }

    @Test
    void isRecursive_hasNoAppendArg_returnsFalse() throws InvalidArgsException {
        String[] args = {};
        teeArgsParser.parse(args);
        assertFalse(teeArgsParser.isAppend());
    }

    @Test
    void getFileNames_hasFileNamesAndAppendArg_returnsCorrectFilesNames() throws InvalidArgsException {
        String[] args = {"-a", "file1.txt", "file2.txt"};
        List<String> expectedList = Arrays.asList("file1.txt", "file2.txt");
        teeArgsParser.parse(args);
        assertEquals(expectedList, teeArgsParser.getFilesNames());
    }
}
