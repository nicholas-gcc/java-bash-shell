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

public class PasteArgsParserTest {

    static final String FLAG_IS_SERIAL = "-s";
    static final String FAKE_FLAG = "-C";
    static final String FILE1 = "file1.txt";
    static final String FILE2 = "file2.txt";
    PasteArgsParser pasteArgsParser = new PasteArgsParser();

    @Test
    void parse_ValidArguments_NoExceptionThrown() {
        String[] args = {FLAG_IS_SERIAL, FILE1, FILE2};
        assertDoesNotThrow(() -> pasteArgsParser.parse(args));
    }

    @Test
    void parse_InvalidArguments_InvalidArgsExceptionThrownWithCorrectMessage() {
        String[] args = {FLAG_IS_SERIAL, FAKE_FLAG, FILE1, FILE2};
        InvalidArgsException exception = assertThrows(InvalidArgsException.class, () -> pasteArgsParser.parse(args));
        String expectedMessage = ArgsParser.ILLEGAL_FLAG_MSG + "C";
        assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    void isSerial_HasSerialArg_ReturnsTrue() throws InvalidArgsException {
        String[] args = {FLAG_IS_SERIAL, FILE1};
        pasteArgsParser.parse(args);
        assertTrue(pasteArgsParser.isSerial());
    }

    @Test
    void isSerial_HasNoSerialArg_ReturnsTrue() throws InvalidArgsException {
        String[] args = {"s", FILE1,};
        pasteArgsParser.parse(args);
        assertFalse(pasteArgsParser.isSerial());
    }

    @Test
    void getFilesNames_HasFileNames_ReturnsFilesNames() throws InvalidArgsException {
        String[] args = {FLAG_IS_SERIAL, FILE1, FILE2};
        pasteArgsParser.parse(args);
        List<String> filenames = pasteArgsParser.getFileNames();
        String[] expectedFilenames = {FILE1, FILE2};
        assertEquals(new HashSet<>(List.of(expectedFilenames)), new HashSet<>(filenames));
    }
}
