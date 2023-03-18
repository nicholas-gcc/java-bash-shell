package sg.edu.nus.comp.cs4218.impl.parser;

import org.junit.jupiter.api.Test;
import sg.edu.nus.comp.cs4218.exception.InvalidArgsException;

import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class LsArgsParserTest {
    LsArgsParser lsArgsParser = new LsArgsParser();
    static final String FLAG_IS_RECURSIVE = "-R";
    static final String FLAG_SORT_BY_EXT = "-X";
    static final String FAKE_FLAG = "-C";
    static final String FILE1 = "file1.txt";
    static final String FILE2 = "dir";

    @Test
    void parse_ValidArguments_NoExceptionThrown() {
        String[] args = {FLAG_IS_RECURSIVE, FLAG_SORT_BY_EXT, FILE1, FILE2};
        assertDoesNotThrow(() -> lsArgsParser.parse(args));
    }

    @Test
    void parse_InvalidArguments_ThrowsInvalidArgsExceptionWithCorrectMessage() {
        String[] args = {FLAG_IS_RECURSIVE, FLAG_SORT_BY_EXT, FAKE_FLAG, FILE1, FILE2};
        InvalidArgsException exception = assertThrows(InvalidArgsException.class, () -> lsArgsParser.parse(args));
        String expectedMessage = ArgsParser.ILLEGAL_FLAG_MSG + "C";
        assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    void isRecursive_HasRecursiveArg_ReturnsTrue() throws InvalidArgsException {
        String[] args = {FLAG_IS_RECURSIVE};
        lsArgsParser.parse(args);
        assertTrue(lsArgsParser.isRecursive());

    }

    @Test
    void isRecursive_HasNoRecursiveArg_ReturnsFalse() throws InvalidArgsException {
        String[] args = {"-X", "r"};
        lsArgsParser.parse(args);
        assertFalse(lsArgsParser.isRecursive());
    }

    @Test
    void isSortByExt_HasSortByExtArg_ReturnsTrue() throws InvalidArgsException {
        String[] args = {FLAG_SORT_BY_EXT};
        lsArgsParser.parse(args);
        assertTrue(lsArgsParser.isSortByExt());
    }

    @Test
    void isSortByExt_HasNoSortByExtArg_ReturnsFalse() throws InvalidArgsException {
        String[] args = {"X", "-R"};
        lsArgsParser.parse(args);
        assertFalse(lsArgsParser.isSortByExt());
    }

    @Test
    void getFilesOrDirNames_FilesAndFlagArgs_ReturnsCorrectFilenames() throws InvalidArgsException {
        String[] args = {FLAG_IS_RECURSIVE, FLAG_SORT_BY_EXT, FILE1, FILE2};;
        lsArgsParser.parse(args);
        List<String> filenames = lsArgsParser.getFilesOrDirNames();
        String[] expectedFilenames = {FILE1, FILE2};
        assertEquals(new HashSet<>(List.of(expectedFilenames)), new HashSet<>(filenames));
    }
}
