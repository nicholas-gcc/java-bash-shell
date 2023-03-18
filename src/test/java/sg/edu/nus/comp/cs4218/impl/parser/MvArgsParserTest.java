package sg.edu.nus.comp.cs4218.impl.parser;

import org.junit.jupiter.api.Test;
import sg.edu.nus.comp.cs4218.exception.InvalidArgsException;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class MvArgsParserTest {
    static final String FLAG_NO_OVERWRITE = "-n";
    static final String FAKE_FLAG = "-C";
    static final String FILE1 = "file1.txt";
    static final String FILE2 = "file2.txt";
    static final String FILE3 = "file3.txt";
    static final String FILE4 = "dir";

    MvArgsParser mvArgsParser = new MvArgsParser();

    @Test
    void parse_ValidArguments_NoExceptionThrown() {
        String[] args = {FLAG_NO_OVERWRITE, FILE1, FILE2};
        assertDoesNotThrow(() -> mvArgsParser.parse(args));
    }

    @Test
    void parse_IllegalArgs_ThrowsInvalidArgsExceptionWithCorrectMessage() {
        String[] args = {FLAG_NO_OVERWRITE, FAKE_FLAG};
        InvalidArgsException exception = assertThrows(InvalidArgsException.class, () -> mvArgsParser.parse(args));
        String expectedMessage = ArgsParser.ILLEGAL_FLAG_MSG + "C";
        assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    void shouldOverwrite_HasNoOverwriteArg_ReturnsFalse() throws InvalidArgsException {
        String[] args = {FLAG_NO_OVERWRITE};
        mvArgsParser.parse(args);
        assertFalse(mvArgsParser.shouldOverwrite());
    }

    @Test
    void shouldOverwrite_DoesNotHaveNoOverwriteArg_ReturnsFalse() throws InvalidArgsException {
        String[] args = {"n"};
        mvArgsParser.parse(args);
        assertTrue(mvArgsParser.shouldOverwrite());
    }

    @Test
    void getSourceFiles_1FileArgs_ReturnsEmptyArray() throws InvalidArgsException {
        String[] args = {FILE1};
        mvArgsParser.parse(args);
        assertArrayEquals(new String[0], mvArgsParser.getSourceFiles());
    }

    @Test
    void getSourceFiles_0FileArgs_ReturnsEmptyArray() throws InvalidArgsException {
        String[] args = {};
        mvArgsParser.parse(args);
        assertArrayEquals(new String[0], mvArgsParser.getSourceFiles());
    }

    @Test
    void getSourceFiles_2FileWithNoOverwriteArgs_ReturnsCorrectSourceFiles() throws InvalidArgsException {
        String[] args = {FLAG_NO_OVERWRITE, FILE1, FILE2};
        mvArgsParser.parse(args);
        String[] expectedFilenames = {FILE1};
        assertArrayEquals(expectedFilenames, mvArgsParser.getSourceFiles());
    }

    @Test
    void getSourceFiles_4FileWithNoOverwriteArgs_ReturnsCorrectSourceFiles() throws InvalidArgsException {
        String[] args = {FLAG_NO_OVERWRITE, FILE1, FILE2, FILE3, FILE4};
        mvArgsParser.parse(args);
        String[] expectedFilenames = {FILE1, FILE2, FILE3};
        assertArrayEquals(expectedFilenames, mvArgsParser.getSourceFiles());
    }

    @Test
    void getDestFile_0FileArgs_ReturnsNull() throws InvalidArgsException {
        String[] args = {};
        mvArgsParser.parse(args);
        assertNull(mvArgsParser.getDestFile());
    }
    @Test
    void getDestFile_4FileWithNoOverwriteArgs_ReturnsCorrectDestFile() throws InvalidArgsException {
        String[] args = {FLAG_NO_OVERWRITE, FILE1, FILE2, FILE3, FILE4};
        mvArgsParser.parse(args);
        assertEquals(FILE4, mvArgsParser.getDestFile());
    }
}
