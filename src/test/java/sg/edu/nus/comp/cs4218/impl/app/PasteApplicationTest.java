package sg.edu.nus.comp.cs4218.impl.app;

import org.junit.jupiter.api.Test;
import sg.edu.nus.comp.cs4218.exception.PasteException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static sg.edu.nus.comp.cs4218.impl.app.GrepApplication.IS_DIRECTORY;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.ERR_FILE_DIR_NOT_FOUND;
import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.ERR_NULL_ARGS;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.CHAR_FILE_SEP;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.CHAR_TAB;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.STRING_NEWLINE;

public class PasteApplicationTest {
    private final PasteApplication pasteApplication = new PasteApplication();
    private static String fileNameA = "A.txt";
    private static String fileNameB = "B.txt";
    private static String fileNameC = "C.txt";
    private static String fileEmpty1 = "Empty1.txt";
    private static String fileEmpty2 = "Empty2.txt";
    private static String fileNameAB = "AB.txt";
    private static String folderName = "assets" + CHAR_FILE_SEP + "app" + CHAR_FILE_SEP + "paste";
    private static String fileNonExistent = "NonExistent.txt";


    @Test
    void mergeFile_OnlyOneFile_ReturnsCorrectString() throws Exception {
        String expected = "A" + STRING_NEWLINE + "B" + STRING_NEWLINE + "C" + STRING_NEWLINE + "D" + STRING_NEWLINE;
        String actual = pasteApplication.mergeFile(false, folderName + CHAR_FILE_SEP + fileNameA);
        assertEquals(expected, actual);
    }
    
    @Test
    void mergeFile_MergeTwoEmptyFilesNotSerial_ReturnsEmptyString() throws Exception {
        String expected = "";
        assertDoesNotThrow(() -> {
            String actual = pasteApplication.mergeFile(false, folderName + CHAR_FILE_SEP + fileEmpty1,
                    folderName + CHAR_FILE_SEP + fileEmpty2);
            assertEquals(expected, actual);
        });
    }

    @Test
    void testMergeFile_MergeEmptyWithNonEmptyFileNotSerial_ReturnsNonEmptyString() {
        String expected = CHAR_TAB + "A" + STRING_NEWLINE + CHAR_TAB + "B" +
                STRING_NEWLINE + CHAR_TAB + "C" + STRING_NEWLINE + CHAR_TAB + "D" + STRING_NEWLINE;
        assertDoesNotThrow(() -> {
            String actual = pasteApplication.mergeFile(false, folderName + CHAR_FILE_SEP + fileEmpty1,
                    folderName + CHAR_FILE_SEP + fileNameA);
            assertEquals(expected, actual);
        });
    }

    @Test
    void testMergeFile_MergeNonEmptyWithEmptyFileNotSerial_ReturnsNonEmptyString() {
        String expected = "A" + CHAR_TAB + STRING_NEWLINE + "B" + CHAR_TAB + STRING_NEWLINE + "C" + CHAR_TAB +
                STRING_NEWLINE + "D" + CHAR_TAB + STRING_NEWLINE;
        assertDoesNotThrow(() -> {
            String actual = pasteApplication.mergeFile(false, folderName + CHAR_FILE_SEP + fileNameA,
                    folderName + CHAR_FILE_SEP + fileEmpty1);
            assertEquals(expected, actual);
        });
    }

    @Test
    void testMergeFile_MergeTwoNonEmptyFilesNotSerial_ReturnsNonEmptyString() {
        String expected = "A" + CHAR_TAB + "1" + STRING_NEWLINE +
                "B" + CHAR_TAB + "2" + STRING_NEWLINE +
                "C" + CHAR_TAB + "3" + STRING_NEWLINE +
                "D" + CHAR_TAB + "4" + STRING_NEWLINE;
        assertDoesNotThrow(() -> {
            String actual = pasteApplication.mergeFile(false, folderName + CHAR_FILE_SEP + fileNameA,
                    folderName + CHAR_FILE_SEP + fileNameB);
            System.out.println(actual.length());
            System.out.println(expected.length());
            assertEquals(expected, actual);
        });
    }

    @Test
    void testMergeFile_MergeTwoNonEmptyFilesIsSerial_ReturnsNonEmptyString() {
        String expected = "A" + CHAR_TAB + "B" + CHAR_TAB + "C" + CHAR_TAB + "D" + STRING_NEWLINE +
                "1" + CHAR_TAB + "2" + CHAR_TAB + "3" + CHAR_TAB + "4" + STRING_NEWLINE;
        assertDoesNotThrow(() -> {
            String actual = pasteApplication.mergeFile(true, folderName + CHAR_FILE_SEP + fileNameA,
                    folderName + CHAR_FILE_SEP + fileNameB);
            assertEquals(expected, actual);
        });
    }

    @Test
    void testMergeFile_MergeTwoNonEmptyFilesWithDiffLineCountNotSerial_ReturnsCorrectString() {
        String expected = "A" + CHAR_TAB + "1" + STRING_NEWLINE +
                "B" + CHAR_TAB + "2" + STRING_NEWLINE +
                "C" + CHAR_TAB + "3" + STRING_NEWLINE +
                "D" + CHAR_TAB + "4" + STRING_NEWLINE +
                CHAR_TAB + "5" + STRING_NEWLINE +
                CHAR_TAB + "6" + STRING_NEWLINE;
        assertDoesNotThrow(() -> {
            String actual = pasteApplication.mergeFile(false, folderName + CHAR_FILE_SEP + fileNameA,
                    folderName + CHAR_FILE_SEP + fileNameC);
            assertEquals(expected, actual);
        });
    }

    @Test
    void testMergeFile_MergeThreeNonEmptyFilesNotSerial_ReturnsCorrectString() {
        String expected = "A" + CHAR_TAB + "1" + CHAR_TAB + "1" + STRING_NEWLINE +
                "B" + CHAR_TAB + "2" + CHAR_TAB + "2" + STRING_NEWLINE +
                "C" + CHAR_TAB + "3" + CHAR_TAB + "3" + STRING_NEWLINE +
                "D" + CHAR_TAB + "4" + CHAR_TAB + "4" + STRING_NEWLINE +
                CHAR_TAB + CHAR_TAB + "5" + STRING_NEWLINE +
                CHAR_TAB + CHAR_TAB + "6" + STRING_NEWLINE;
        assertDoesNotThrow(() -> {
            String actual = pasteApplication.mergeFile(false, folderName + CHAR_FILE_SEP + fileNameA,
                    folderName + CHAR_FILE_SEP + fileNameB,
                    folderName + CHAR_FILE_SEP + fileNameC);
            assertEquals(expected, actual);
        });
    }

    @Test
    void testMergeStdin_OnlyOneStdinNotSerial_ReturnsCorrect() {
        String inputString = "A" + STRING_NEWLINE + "B" + STRING_NEWLINE + "C" + STRING_NEWLINE + "D" + STRING_NEWLINE;
        InputStream stdin = new ByteArrayInputStream(inputString.getBytes());
        String expected = "A" + STRING_NEWLINE + "B" + STRING_NEWLINE + "C" + STRING_NEWLINE + "D" + STRING_NEWLINE;
        assertDoesNotThrow(() -> {
            String actual = pasteApplication.mergeStdin(false, stdin);
            assertEquals(expected, actual);
        });
    }

    @Test
    void testMergeFileAndStdin_StdInAndOneFile_ReturnsCorrect() {
        String inputString = "A" + STRING_NEWLINE + "B" + STRING_NEWLINE + "C" + STRING_NEWLINE + "D" + STRING_NEWLINE;
        InputStream stdin = new ByteArrayInputStream(inputString.getBytes());
        String expected = "A" + CHAR_TAB + "1" + STRING_NEWLINE +
                "B" + CHAR_TAB + "2" + STRING_NEWLINE +
                "C" + CHAR_TAB + "3" + STRING_NEWLINE +
                "D" + CHAR_TAB + "4" + STRING_NEWLINE;
        assertDoesNotThrow(() -> {
            String actual = pasteApplication.mergeFileAndStdin(false, stdin, folderName +
                    CHAR_FILE_SEP + fileNameB);
            assertEquals(expected, actual);
        });
    }

    // If the FILE arg is -, paste reads from standard input.
    @Test
    void testRun_OnlyStdInWithDashFlag_ReturnsCorrect() {
        String[] args = {"-"};
        String inputString = "A" + STRING_NEWLINE + "B" + STRING_NEWLINE + "C" + STRING_NEWLINE + "D" + STRING_NEWLINE;
        InputStream stdin = new ByteArrayInputStream(inputString.getBytes());
        String expected = "A" + STRING_NEWLINE + "B" + STRING_NEWLINE + "C" + STRING_NEWLINE + "D" + STRING_NEWLINE;
        OutputStream outputStream = new ByteArrayOutputStream();
        assertDoesNotThrow(() -> {
            pasteApplication.run(args, stdin, outputStream);
            assertEquals(expected, outputStream.toString());
        });
    }

    @Test
    void testRun_WithOnlyFiles_ReturnsCorrect() {
        String[] args = {folderName + CHAR_FILE_SEP + fileNameA, folderName + CHAR_FILE_SEP + fileNameB};
        String expected = "A" + CHAR_TAB + "1" + STRING_NEWLINE +
                "B" + CHAR_TAB + "2" + STRING_NEWLINE +
                "C" + CHAR_TAB + "3" + STRING_NEWLINE +
                "D" + CHAR_TAB + "4" + STRING_NEWLINE;
        OutputStream outputStream = new ByteArrayOutputStream();
        assertDoesNotThrow(() -> {
            pasteApplication.run(args, null, outputStream);
            assertEquals(expected, outputStream.toString());
        });
    }

    @Test
    void testRun_WithStdinAndFiles_ReturnsCorrect() {
        String[] args = {"-", folderName + CHAR_FILE_SEP + fileNameB, folderName + CHAR_FILE_SEP + fileNameC};
        String inputString = "A" + STRING_NEWLINE + "B" + STRING_NEWLINE + "C" + STRING_NEWLINE + "D" + STRING_NEWLINE;
        InputStream stdin = new ByteArrayInputStream(inputString.getBytes());
        String expected = "A" + CHAR_TAB + "1" + CHAR_TAB + "1" + STRING_NEWLINE +
                "B" + CHAR_TAB + "2" + CHAR_TAB + "2" + STRING_NEWLINE +
                "C" + CHAR_TAB + "3" + CHAR_TAB + "3" + STRING_NEWLINE +
                "D" + CHAR_TAB + "4" + CHAR_TAB + "4" + STRING_NEWLINE +
                CHAR_TAB + CHAR_TAB + "5" + STRING_NEWLINE +
                CHAR_TAB + CHAR_TAB + "6" + STRING_NEWLINE;
        OutputStream outputStream = new ByteArrayOutputStream();
        assertDoesNotThrow(() -> {
            pasteApplication.run(args, stdin, outputStream);
            assertEquals(expected, outputStream.toString());
        });
    }

    // if this example is confusing, refer to project documentation example for paste
    @Test
    void testRun_WithStdinAndFileAndStdin_ReturnsCorrect() {
        String[] args = {"-", folderName + CHAR_FILE_SEP + fileNameB, "-"};
        String inputString = "A" + STRING_NEWLINE + "B" + STRING_NEWLINE + "C" + STRING_NEWLINE + "D" + STRING_NEWLINE;
        InputStream stdin = new ByteArrayInputStream(inputString.getBytes());
        String expected = "A" + CHAR_TAB + "1" + CHAR_TAB + "B" + STRING_NEWLINE +
                "C" + CHAR_TAB + "2" + CHAR_TAB + "D" + STRING_NEWLINE +
                CHAR_TAB + "3" + CHAR_TAB + STRING_NEWLINE +
                CHAR_TAB + "4" + CHAR_TAB + STRING_NEWLINE;
        OutputStream outputStream = new ByteArrayOutputStream();
        assertDoesNotThrow(() -> {
            pasteApplication.run(args, stdin, outputStream);
            assertEquals(expected, outputStream.toString());
        });
    }

    @Test
    void testRun_WithDirAsFileName_ThrowsException() {
        String[] args = {folderName};
        String expected = "paste: " + folderName + ":" + IS_DIRECTORY;
        OutputStream outputStream = new ByteArrayOutputStream();
        assertThrows(PasteException.class, () -> {
            pasteApplication.run(args, null, outputStream);
            assertEquals(expected, outputStream.toString());
        });
    }

    @Test
    void testRun_WithNonExistingFile_ThrowsException() {
        String[] args = {folderName + CHAR_FILE_SEP + fileNonExistent};
        OutputStream outputStream = new ByteArrayOutputStream();
        Throwable thrown = assertThrows(PasteException.class,
                () -> pasteApplication.run(args, System.in, outputStream));
        assertEquals(thrown.getMessage(),
                "paste: " + folderName + CHAR_FILE_SEP + fileNonExistent + ": " + ERR_FILE_DIR_NOT_FOUND);
    }

    @Test
    void testRun_WithNullArgs_ThrowsException() {
        OutputStream outputStream = new ByteArrayOutputStream();
        Throwable thrown = assertThrows(PasteException.class,
                () -> pasteApplication.run(null, System.in, outputStream));
        assertEquals("paste: " + ERR_NULL_ARGS, thrown.getMessage());
    }
}
