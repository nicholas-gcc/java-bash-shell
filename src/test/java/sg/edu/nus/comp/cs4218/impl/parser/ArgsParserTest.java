package sg.edu.nus.comp.cs4218.impl.parser;

import org.junit.jupiter.api.Test;
import sg.edu.nus.comp.cs4218.exception.InvalidArgsException;
import sg.edu.nus.comp.cs4218.impl.stubs.ArgsParserStub;

import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ArgsParserTest {
    ArgsParser parser = new ArgsParserStub();

    static final String FLAG1 = "-A";
    static final String FLAG2 = "-B";
    static final String FAKE_FLAG = "-C";
    static final String FILE1 = "file1.txt";
    static final String FILE2 = "dir";

    @Test
    void parse_ValidArguments_NoExceptionThrown() {
        String[] args = {FLAG1, FLAG2, FILE1, FILE2};
        assertDoesNotThrow(() -> parser.parse(args));
    }

    @Test
    void parse_InvalidArguments_InvalidArgsExceptionThrownWithCorrectMessage() {
        String[] args = {FLAG1, FLAG2, FAKE_FLAG, FILE1, FILE2};
        InvalidArgsException exception = assertThrows(InvalidArgsException.class, () -> parser.parse(args));
        String expectedMessage = ArgsParser.ILLEGAL_FLAG_MSG + "C";
        assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    void parse_Flag1ArgWithFiles_ParseFlag1ArgCorrectly() throws InvalidArgsException {
        String[] args = {FLAG1, FILE1, FILE2};
        parser.parse(args);
        assertTrue(((ArgsParserStub) parser).isFlag1());
        assertFalse(((ArgsParserStub) parser).isFlag2());
    }

    @Test
    void parse_Flag2ArgWithFiles_ParseFlag2ArgCorrectly() throws InvalidArgsException {
        String[] args = {FLAG2, FILE1, FILE2};
        parser.parse(args);
        assertFalse(((ArgsParserStub) parser).isFlag1());
        assertTrue(((ArgsParserStub) parser).isFlag2());
    }

    @Test
    void parse_Flag1And2ArgsWithFiles_ParseFlagArgsCorrectly() throws InvalidArgsException {
        String[] args = {FLAG1, FLAG2, FILE1, FILE2};
        parser.parse(args);
        assertTrue(((ArgsParserStub) parser).isFlag1());
        assertTrue(((ArgsParserStub) parser).isFlag2());
    }

    @Test
    void parse_Flag1And2ArgsWithFiles_ParseFilesCorrectly() throws InvalidArgsException {
        String[] args = {FLAG1, FLAG2, FILE1, FILE2};
        parser.parse(args);
        List<String> filenames = ((ArgsParserStub) parser).getNonFlagArgs();
        String[] expectedFilenames = {FILE1, FILE2};
        assertEquals(new HashSet<>(List.of(expectedFilenames)), new HashSet<>(filenames));
    }
}
