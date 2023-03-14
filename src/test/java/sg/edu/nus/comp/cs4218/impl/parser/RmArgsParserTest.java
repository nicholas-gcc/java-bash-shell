package sg.edu.nus.comp.cs4218.impl.parser;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sg.edu.nus.comp.cs4218.exception.InvalidArgsException;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class RmArgsParserTest {
    RmArgsParser rmArgsParser;

    @BeforeEach
    void setup() {
        rmArgsParser = new RmArgsParser();
    }

    @Test
    void parse_illegalArgs_throwsInvalidArgsException() {
        String[] args = {"-X", "-r", "-d"};
        assertThrows(InvalidArgsException.class, () -> {
            rmArgsParser.parse(args);
            assertFalse(rmArgsParser.isRecursive());
        });
    }

    @Test
    void isRecursive_hasRecursiveArg_returnsTrue() {
        String[] args = {"-r"};
        assertDoesNotThrow(() -> {
            rmArgsParser.parse(args);
            assertTrue(rmArgsParser.isRecursive());
        });
    }

    @Test
    void isRecursive_hasNoRecursiveArg_returnsFalse() {
        String[] args = {"-d", "r"};
        assertDoesNotThrow(() -> {
            rmArgsParser.parse(args);
            assertFalse(rmArgsParser.isRecursive());
        });
    }

    @Test
    void isRmEmptyDir_hasEmptyFolderArg_returnsTrue() {
        String[] args = {"-d"};
        assertDoesNotThrow(() -> {
            rmArgsParser.parse(args);
            assertTrue(rmArgsParser.isRmEmptyDir());
        });
    }

    @Test
    void isRmEmptyDir_hasNoEmptyFolderDirArg_returnsFalse() {
        String[] args = {"d", "-r"};
        assertDoesNotThrow(() -> {
            rmArgsParser.parse(args);
            assertFalse(rmArgsParser.isRmEmptyDir());
        });
    }

    @Test
    void getFilesOrDirNames_hasFileAndDirNames_returnsFilesAndDirNames() {
        String[] args = {"-d", "-r", "dir", "file1.txt", "file2.txt"};
        List<String> expectedList = Arrays.asList("dir", "file1.txt", "file2.txt");
        assertDoesNotThrow(() -> {
            rmArgsParser.parse(args);
            assertEquals(expectedList, rmArgsParser.getFilesOrDirNames());
        });
    }

}
