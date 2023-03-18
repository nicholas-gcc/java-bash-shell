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

public class RmArgsParserTest {
    static final String FLAG_IS_RECURSIVE = "-r";
    static final String FLAG_EMPTY_FOLDER = "-d";
    static final String FAKE_FLAG = "-C";
    static final String FILE1 = "file1.txt";
    static final String FILE2 = "dir";
    RmArgsParser rmArgsParser = new RmArgsParser();

    @Test
    void parse_ValidArguments_NoExceptionThrown() {
        String[] args = {FLAG_IS_RECURSIVE, FLAG_EMPTY_FOLDER, FILE1, FILE2};
        assertDoesNotThrow(() -> rmArgsParser.parse(args));
    }

    @Test
    void parse_IllegalArgs_ThrowsInvalidArgsExceptionWithCorrectMessage() {
        String[] args = {FLAG_IS_RECURSIVE, FLAG_EMPTY_FOLDER, FAKE_FLAG};
        InvalidArgsException exception = assertThrows(InvalidArgsException.class, () -> rmArgsParser.parse(args));
        String expectedMessage = ArgsParser.ILLEGAL_FLAG_MSG + "C";
        assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    void isRecursive_HasRecursiveArg_ReturnsTrue() throws InvalidArgsException {
        String[] args = {FLAG_IS_RECURSIVE};
        rmArgsParser.parse(args);
        assertTrue(rmArgsParser.isRecursive());
    }

    @Test
    void isRecursive_HasNoRecursiveArg_ReturnsFalse() throws InvalidArgsException {
        String[] args = {FLAG_EMPTY_FOLDER, "r"};
        rmArgsParser.parse(args);
        assertFalse(rmArgsParser.isRecursive());
    }

    @Test
    void isRmEmptyDir_HasEmptyFolderArg_ReturnsTrue() throws InvalidArgsException {
        String[] args = {FLAG_EMPTY_FOLDER};
        rmArgsParser.parse(args);
        assertTrue(rmArgsParser.isRmEmptyDir());
    }

    @Test
    void isRmEmptyDir_HasNoEmptyFolderDirArg_ReturnsFalse() throws InvalidArgsException {
        String[] args = {"d", FLAG_IS_RECURSIVE};
        rmArgsParser.parse(args);
        assertFalse(rmArgsParser.isRmEmptyDir());
    }

    @Test
    void getFilesOrDirNames_HasFileAndDirNames_ReturnsFilesAndDirNames() throws InvalidArgsException {
        String[] args = {FLAG_IS_RECURSIVE, FLAG_EMPTY_FOLDER, FILE1, FILE2};
        rmArgsParser.parse(args);
        List<String> filenames = rmArgsParser.getFilesOrDirNames();
        String[] expectedFilenames = {FILE1, FILE2};
        assertEquals(new HashSet<>(List.of(expectedFilenames)), new HashSet<>(filenames));
    }

}
