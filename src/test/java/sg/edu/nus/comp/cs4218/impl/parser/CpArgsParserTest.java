package sg.edu.nus.comp.cs4218.impl.parser;

import org.junit.jupiter.api.Test;
import sg.edu.nus.comp.cs4218.exception.InvalidArgsException;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CpArgsParserTest {

    static final String FLAG_RECURSIVE = "-r";
    static final String FAKE_FLAG = "-C";
    static final String FILE1 = "file1.txt";
    static final String FILE2 = "file2.txt";
    static final String FILE3 = "file3.txt";
    static final String FILE4 = "dir";

    CpArgsParser cpArgsParser = new CpArgsParser();

    @Test
    void parse_ValidArguments_NoExceptionThrown() {
        String[] args = {FLAG_RECURSIVE, FILE1, FILE2};
        assertDoesNotThrow(() -> cpArgsParser.parse(args));
    }

    @Test
    void parse_IllegalArgs_ThrowsInvalidArgsExceptionWithCorrectMessage() {
        String[] args = {FLAG_RECURSIVE, FAKE_FLAG};
        InvalidArgsException exception = assertThrows(InvalidArgsException.class, () -> cpArgsParser.parse(args));
        String expectedMessage = ArgsParser.ILLEGAL_FLAG_MSG + "C";
        assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    void isRecursive_HasRecursiveFlag_ReturnsTrue() throws InvalidArgsException {
        String[] args = {FLAG_RECURSIVE};
        cpArgsParser.parse(args);
        assertTrue(cpArgsParser.isRecursive());
    }

    @Test
    void getSourceFiles_1FileArgs_ReturnsEmptyArray() throws InvalidArgsException {
        String[] args = {FILE1};
        cpArgsParser.parse(args);
        assertArrayEquals(new String[0], cpArgsParser.getSourceFiles());
    }

    @Test
    void getSourceFiles_0FileArgs_ReturnsEmptyArray() throws InvalidArgsException {
        String[] args = {};
        cpArgsParser.parse(args);
        assertArrayEquals(new String[0], cpArgsParser.getSourceFiles());
    }

    @Test
    void getSourceFiles_2FileWithRecursiveFlag_ReturnsCorrectSourceFiles() throws InvalidArgsException {
        String[] args = {FLAG_RECURSIVE, FILE1, FILE2};
        cpArgsParser.parse(args);
        String[] expectedFilenames = {FILE1};
        assertArrayEquals(expectedFilenames, cpArgsParser.getSourceFiles());
    }

    @Test
    void getSourceFiles_4FileWithRecursiveFlag_ReturnsCorrectSourceFiles() throws InvalidArgsException {
        String[] args = {FLAG_RECURSIVE, FILE1, FILE2, FILE3, FILE4};
        cpArgsParser.parse(args);
        String[] expectedFilenames = {FILE1, FILE2, FILE3};
        assertArrayEquals(expectedFilenames, cpArgsParser.getSourceFiles());
    }

    @Test
    void getDestFile_0FileArgs_ReturnsNull() throws InvalidArgsException {
        String[] args = {};
        cpArgsParser.parse(args);
        assertNull(cpArgsParser.getDestFileOrFolder());
    }

    @Test
    void getDestFile_4FileWithNoOverwriteArgs_ReturnsCorrectDestFile() throws InvalidArgsException {
        String[] args = {FLAG_RECURSIVE, FILE1, FILE2, FILE3, FILE4};
        cpArgsParser.parse(args);
        assertEquals(FILE4, cpArgsParser.getDestFileOrFolder());
    }
}

