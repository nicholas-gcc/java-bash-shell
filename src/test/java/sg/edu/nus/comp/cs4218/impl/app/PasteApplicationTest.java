package sg.edu.nus.comp.cs4218.impl.app;

import org.junit.jupiter.api.Test;
import sg.edu.nus.comp.cs4218.exception.AbstractApplicationException;
import sg.edu.nus.comp.cs4218.exception.PasteException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static sg.edu.nus.comp.cs4218.impl.app.GrepApplication.IS_DIRECTORY;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.ERR_IS_DIR;
import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.ERR_NO_OSTREAM;
import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.ERR_FILE_DIR_NOT_FOUND;
import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.ERR_NULL_ARGS;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.CHAR_FILE_SEP;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.CHAR_TAB;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.STRING_NEWLINE;

public class PasteApplicationTest {
    private final PasteApplication pasteApplication = new PasteApplication();
    private static final String FILE_NAME_A = "A.txt";
    private static final String FILE_NAME_B = "B.txt";
    private static final String FILE_NAME_C = "C.txt";
    private static final String FILE_EMPTY_1 = "Empty1.txt";
    private static final String FILE_EMPTY_2 = "Empty2.txt";
    private static final String FILE_NAME_AB = "AB.txt";
    private static final String FOLDER_NAME = "assets" + CHAR_FILE_SEP + "app" + CHAR_FILE_SEP + "paste";
    private static final String FILE_NON_EXISTENT = "NonExistent.txt";
    private static final String PASTE_EX_PREFIX = "paste: ";
    @Test
    void mergeFile_OnlyOneFile_ReturnsCorrectString() throws Exception {
        String expected = "A" + STRING_NEWLINE + "B" + STRING_NEWLINE + "C" + STRING_NEWLINE + "D" + STRING_NEWLINE;
        String actual = pasteApplication.mergeFile(false, FOLDER_NAME + CHAR_FILE_SEP + FILE_NAME_A);
        assertEquals(expected, actual);
    }
    
    @Test
    void mergeFile_MergeTwoEmptyFilesNotSerial_ReturnsEmptyString() throws Exception {
        String expected = "";
        String actual = pasteApplication.mergeFile(false, FOLDER_NAME + CHAR_FILE_SEP + FILE_EMPTY_1,
                FOLDER_NAME + CHAR_FILE_SEP + FILE_EMPTY_2);
        assertEquals(expected, actual);

    }

    @Test
    void mergeFile_MergeEmptyWithNonEmptyFileNotSerial_ReturnsNonEmptyString() throws Exception {
        String expected = CHAR_TAB + "A" + STRING_NEWLINE + CHAR_TAB + "B" +
                STRING_NEWLINE + CHAR_TAB + "C" + STRING_NEWLINE + CHAR_TAB + "D" + STRING_NEWLINE;
        String actual = pasteApplication.mergeFile(false, FOLDER_NAME + CHAR_FILE_SEP + FILE_EMPTY_1,
                FOLDER_NAME + CHAR_FILE_SEP + FILE_NAME_A);
        assertEquals(expected, actual);
    }

    @Test
    void mergeFile_MergeNonEmptyWithEmptyFileNotSerial_ReturnsNonEmptyString() throws Exception {
        String expected = "A" + CHAR_TAB + STRING_NEWLINE + "B" + CHAR_TAB + STRING_NEWLINE + "C" + CHAR_TAB +
                STRING_NEWLINE + "D" + CHAR_TAB + STRING_NEWLINE;
        String actual = pasteApplication.mergeFile(false, FOLDER_NAME + CHAR_FILE_SEP + FILE_NAME_A,
                FOLDER_NAME + CHAR_FILE_SEP + FILE_EMPTY_1);
        assertEquals(expected, actual);
    }

    @Test
    void mergeFile_MergeTwoNonEmptyFilesNotSerial_ReturnsNonEmptyString() throws Exception {
        String expected = "A" + CHAR_TAB + "1" + STRING_NEWLINE +
                "B" + CHAR_TAB + "2" + STRING_NEWLINE +
                "C" + CHAR_TAB + "3" + STRING_NEWLINE +
                "D" + CHAR_TAB + "4" + STRING_NEWLINE;
        String actual = pasteApplication.mergeFile(false, FOLDER_NAME + CHAR_FILE_SEP + FILE_NAME_A,
                FOLDER_NAME + CHAR_FILE_SEP + FILE_NAME_B);
        assertEquals(expected, actual);
    }

    @Test
    void mergeFile_MergeTwoNonEmptyFilesIsSerial_ReturnsNonEmptyString() throws Exception {
        String expected = "A" + CHAR_TAB + "B" + CHAR_TAB + "C" + CHAR_TAB + "D" + STRING_NEWLINE +
                "1" + CHAR_TAB + "2" + CHAR_TAB + "3" + CHAR_TAB + "4" + STRING_NEWLINE;
        String actual = pasteApplication.mergeFile(true, FOLDER_NAME + CHAR_FILE_SEP + FILE_NAME_A,
                FOLDER_NAME + CHAR_FILE_SEP + FILE_NAME_B);
        assertEquals(expected, actual);
    }

    @Test
    void mergeFile_MergeTwoNonEmptyFilesWithDiffLineCountNotSerial_ReturnsCorrectString() throws Exception {
        String expected = "A" + CHAR_TAB + "1" + STRING_NEWLINE +
                "B" + CHAR_TAB + "2" + STRING_NEWLINE +
                "C" + CHAR_TAB + "3" + STRING_NEWLINE +
                "D" + CHAR_TAB + "4" + STRING_NEWLINE +
                CHAR_TAB + "5" + STRING_NEWLINE +
                CHAR_TAB + "6" + STRING_NEWLINE;
        String actual = pasteApplication.mergeFile(false, FOLDER_NAME + CHAR_FILE_SEP + FILE_NAME_A,
                FOLDER_NAME + CHAR_FILE_SEP + FILE_NAME_C);
        assertEquals(expected, actual);
    }

    @Test
    void mergeFile_MergeThreeNonEmptyFilesNotSerial_ReturnsCorrectString() throws Exception {
        String expected = "A" + CHAR_TAB + "1" + CHAR_TAB + "1" + STRING_NEWLINE +
                "B" + CHAR_TAB + "2" + CHAR_TAB + "2" + STRING_NEWLINE +
                "C" + CHAR_TAB + "3" + CHAR_TAB + "3" + STRING_NEWLINE +
                "D" + CHAR_TAB + "4" + CHAR_TAB + "4" + STRING_NEWLINE +
                CHAR_TAB + CHAR_TAB + "5" + STRING_NEWLINE +
                CHAR_TAB + CHAR_TAB + "6" + STRING_NEWLINE;
        String actual = pasteApplication.mergeFile(false, FOLDER_NAME + CHAR_FILE_SEP + FILE_NAME_A,
                FOLDER_NAME + CHAR_FILE_SEP + FILE_NAME_B,
                FOLDER_NAME + CHAR_FILE_SEP + FILE_NAME_C);
        assertEquals(expected, actual);
    }

    @Test
    void mergeStdin_OnlyOneStdinNotSerial_ReturnsCorrect() throws Exception {
        String inputString = "A" + STRING_NEWLINE + "B" + STRING_NEWLINE + "C" + STRING_NEWLINE + "D" + STRING_NEWLINE;
        InputStream stdin = new ByteArrayInputStream(inputString.getBytes());
        String expected = "A" + STRING_NEWLINE + "B" + STRING_NEWLINE + "C" + STRING_NEWLINE + "D" + STRING_NEWLINE;
        String actual = pasteApplication.mergeStdin(false, stdin);
        assertEquals(expected, actual);
    }

    @Test
    void mergeFileAndStdin_StdInAndOneFile_ReturnsCorrect() throws Exception {
        String inputString = "A" + STRING_NEWLINE + "B" + STRING_NEWLINE + "C" + STRING_NEWLINE + "D" + STRING_NEWLINE;
        InputStream stdin = new ByteArrayInputStream(inputString.getBytes());
        String expected = "A" + CHAR_TAB + "1" + STRING_NEWLINE +
                "B" + CHAR_TAB + "2" + STRING_NEWLINE +
                "C" + CHAR_TAB + "3" + STRING_NEWLINE +
                "D" + CHAR_TAB + "4" + STRING_NEWLINE;
        String actual = pasteApplication.mergeFileAndStdin(false, stdin, FOLDER_NAME +
                CHAR_FILE_SEP + FILE_NAME_B);
        assertEquals(expected, actual);
    }

    // If the FILE arg is -, paste reads from standard input.
    @Test
    void run_OnlyStdInWithDashFlag_ReturnsCorrect() throws AbstractApplicationException {
        String[] args = {"-"};
        String inputString = "A" + STRING_NEWLINE + "B" + STRING_NEWLINE + "C" + STRING_NEWLINE + "D" + STRING_NEWLINE;
        InputStream stdin = new ByteArrayInputStream(inputString.getBytes());
        String expected = "A" + STRING_NEWLINE + "B" + STRING_NEWLINE + "C" + STRING_NEWLINE + "D" + STRING_NEWLINE;
        OutputStream outputStream = new ByteArrayOutputStream();
        pasteApplication.run(args, stdin, outputStream);
        assertEquals(expected, outputStream.toString());
    }

    @Test
    void run_WithOnlyFiles_ReturnsCorrect() throws AbstractApplicationException {
        String[] args = {FOLDER_NAME + CHAR_FILE_SEP + FILE_NAME_A, FOLDER_NAME + CHAR_FILE_SEP + FILE_NAME_B};
        String expected = "A" + CHAR_TAB + "1" + STRING_NEWLINE +
                "B" + CHAR_TAB + "2" + STRING_NEWLINE +
                "C" + CHAR_TAB + "3" + STRING_NEWLINE +
                "D" + CHAR_TAB + "4" + STRING_NEWLINE;
        OutputStream outputStream = new ByteArrayOutputStream();
        pasteApplication.run(args, null, outputStream);
        assertEquals(expected, outputStream.toString());
    }

    @Test
    void run_WithStdinAndFiles_ReturnsCorrect() throws AbstractApplicationException {
        String[] args = {"-", FOLDER_NAME + CHAR_FILE_SEP + FILE_NAME_B, FOLDER_NAME + CHAR_FILE_SEP + FILE_NAME_C};
        String inputString = "A" + STRING_NEWLINE + "B" + STRING_NEWLINE + "C" + STRING_NEWLINE + "D" + STRING_NEWLINE;
        InputStream stdin = new ByteArrayInputStream(inputString.getBytes());
        String expected = "A" + CHAR_TAB + "1" + CHAR_TAB + "1" + STRING_NEWLINE +
                "B" + CHAR_TAB + "2" + CHAR_TAB + "2" + STRING_NEWLINE +
                "C" + CHAR_TAB + "3" + CHAR_TAB + "3" + STRING_NEWLINE +
                "D" + CHAR_TAB + "4" + CHAR_TAB + "4" + STRING_NEWLINE +
                CHAR_TAB + CHAR_TAB + "5" + STRING_NEWLINE +
                CHAR_TAB + CHAR_TAB + "6" + STRING_NEWLINE;
        OutputStream outputStream = new ByteArrayOutputStream();
        pasteApplication.run(args, stdin, outputStream);
        assertEquals(expected, outputStream.toString());
    }

    // if this example is confusing, refer to project documentation example for paste
    @Test
    void run_WithStdinAndFileAndStdin_ReturnsCorrect() throws AbstractApplicationException {
        String[] args = {"-", FOLDER_NAME + CHAR_FILE_SEP + FILE_NAME_B, "-"};
        String inputString = "A" + STRING_NEWLINE + "B" + STRING_NEWLINE + "C" + STRING_NEWLINE + "D" + STRING_NEWLINE;
        InputStream stdin = new ByteArrayInputStream(inputString.getBytes());
        String expected = "A" + CHAR_TAB + "1" + CHAR_TAB + "B" + STRING_NEWLINE +
                "C" + CHAR_TAB + "2" + CHAR_TAB + "D" + STRING_NEWLINE +
                CHAR_TAB + "3" + CHAR_TAB + STRING_NEWLINE +
                CHAR_TAB + "4" + CHAR_TAB + STRING_NEWLINE;
        OutputStream outputStream = new ByteArrayOutputStream();
        pasteApplication.run(args, stdin, outputStream);
        assertEquals(expected, outputStream.toString());
    }

    @Test
    void run_WithDirAsFileName_ThrowsException() throws AbstractApplicationException {
        String[] args = {FOLDER_NAME};
        String expected = PASTE_EX_PREFIX + FOLDER_NAME + ": " + ERR_IS_DIR;
        OutputStream outputStream = new ByteArrayOutputStream();
        Throwable thrown = assertThrows(PasteException.class, () -> {
            pasteApplication.run(args, null, outputStream);
        });
        assertEquals(expected, thrown.getMessage());
    }

    @Test
    void run_WithNonExistingFile_ThrowsException() {
        String[] args = {FOLDER_NAME + CHAR_FILE_SEP + FILE_NON_EXISTENT};
        OutputStream outputStream = new ByteArrayOutputStream();
        Throwable thrown = assertThrows(PasteException.class,
                () -> pasteApplication.run(args, System.in, outputStream));
        assertEquals(thrown.getMessage(),
                PASTE_EX_PREFIX + FOLDER_NAME + CHAR_FILE_SEP + FILE_NON_EXISTENT + ": " + ERR_FILE_DIR_NOT_FOUND);
    }

    @Test
    void testRun_WithNullArgs_ThrowsException() {
        OutputStream outputStream = new ByteArrayOutputStream();
        Throwable thrown = assertThrows(PasteException.class,
                () -> pasteApplication.run(null, System.in, outputStream));
        assertEquals(PASTE_EX_PREFIX + ERR_NULL_ARGS, thrown.getMessage());
    }

    @Test
    void run_StdoutNull_ThrowsException() {
        String[] args = {FOLDER_NAME + CHAR_FILE_SEP + FILE_NAME_A};
        Throwable thrown = assertThrows(PasteException.class,
                () -> pasteApplication.run(args, System.in, null));
        assertEquals(PASTE_EX_PREFIX + ERR_NO_OSTREAM, thrown.getMessage());
    }
}
