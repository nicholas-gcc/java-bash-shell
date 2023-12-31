package sg.edu.nus.comp.cs4218.impl.app;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import sg.edu.nus.comp.cs4218.exception.CatException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.ERR_FILE_DIR_NOT_FOUND;
import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.ERR_FILE_NOT_FOUND;
import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.ERR_IS_DIR;
import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.ERR_NO_FILE_ARGS;
import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.ERR_NO_OSTREAM;
import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.ERR_NULL_ARGS;
import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.ERR_NULL_STREAMS;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.CHAR_FILE_SEP;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.CHAR_TAB;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.STRING_NEWLINE;

public class CatApplicationTest {
    private static final String FILE_NAME_A = "A.txt";
    private static final String FILE_NAME_B = "B.txt";
    private static final String FILE_NAME_C = "C.txt";
    private static final String NON_EXISTENT_TXT = "NonExistent.txt";
    private static final String TEST_FOLDER = "assets" + CHAR_FILE_SEP + "app" + CHAR_FILE_SEP + "cat";
    private static final String CAT_EX_PREFIX = "cat: ";
    private static final String FILE_A_STR = "This is file A.";

    private final CatApplication catApplication = new CatApplication();

    @Test
    void catFiles_FileDoesNotExist_ThrowsException() {
        Throwable thrown = assertThrows(CatException.class,
                () -> catApplication.catFiles(false, NON_EXISTENT_TXT));
        assertEquals(CAT_EX_PREFIX + NON_EXISTENT_TXT + ": " + ERR_FILE_NOT_FOUND, thrown.getMessage());
    }

    @Test
    void catFiles_IsDirectory_ThrowsException() {
        Throwable thrown = assertThrows(CatException.class,
                () -> catApplication.catFiles(false, TEST_FOLDER));
        assertEquals(CAT_EX_PREFIX + TEST_FOLDER + ": " + ERR_IS_DIR , thrown.getMessage());
    }

    @Test
    void catFiles_NullFileNames_ThrowsException() {
        Throwable thrown = assertThrows(CatException.class,
                () -> catApplication.catFiles(false, null));
        assertEquals(CAT_EX_PREFIX + ERR_NO_FILE_ARGS, thrown.getMessage());
    }

    @Test
    void catFiles_FileNamesContainNull_ThrowsException() {
        Throwable thrown = assertThrows(CatException.class,
                () -> catApplication.catFiles(false, TEST_FOLDER + CHAR_FILE_SEP + FILE_NAME_A, null));
        assertEquals(CAT_EX_PREFIX + ERR_NULL_ARGS, thrown.getMessage());
    }

    @Test
    void catFiles_NullIsLineNumber_ThrowsException() {
        Throwable thrown = assertThrows(CatException.class,
                () -> catApplication.catFiles(null, FILE_NAME_A));
        assertEquals(CAT_EX_PREFIX + ERR_NULL_ARGS, thrown.getMessage());
    }

    @Test
    void catFiles_SingleLineFile_ReturnsCorrectOutput() throws CatException {
        String expected = FILE_A_STR;
        String actual = catApplication.catFiles(false,
                TEST_FOLDER + CHAR_FILE_SEP + FILE_NAME_A);
        assertEquals(expected, actual);
    }

    @Test
    void catFiles_MultiLineFile_ReturnsCorrectOutput() throws CatException {
        String expected = "This is file B." + STRING_NEWLINE +
                "It has two lines.";
        String actual = catApplication.catFiles(false,
                TEST_FOLDER + CHAR_FILE_SEP + FILE_NAME_B);
        assertEquals(expected, actual);
    }

    @Test
    void catFiles_MultiLineFileWithLineNo_ReturnsCorrectOutput() throws CatException {
        String actual = catApplication.catFiles(true,
                TEST_FOLDER + CHAR_FILE_SEP + FILE_NAME_C);
        String expected = CHAR_TAB + "1 This is file C." + STRING_NEWLINE +
                CHAR_TAB + "2 It has many lines." + STRING_NEWLINE +
                CHAR_TAB + "3 " + STRING_NEWLINE +
                CHAR_TAB + "4 This is another line." + STRING_NEWLINE +
                CHAR_TAB + "5 " + STRING_NEWLINE +
                CHAR_TAB + "6 This is the last line.";
        assertEquals(expected, actual);
    }

    @Test
    void catStdin_NullStdin_ThrowsException() {
        Throwable thrown = assertThrows(CatException.class,
                () -> catApplication.catStdin(false, null));
        assertEquals(CAT_EX_PREFIX + ERR_NULL_STREAMS, thrown.getMessage());
    }

    @Test
    void catStdin_NullIsLineNumber_ThrowsException() {
        Throwable thrown = assertThrows(CatException.class,
                () -> catApplication.catStdin(null, System.in));
        assertEquals(CAT_EX_PREFIX + ERR_NULL_ARGS, thrown.getMessage());
    }

    @Test
    void catStdin_ValidWithoutLineNo_ReturnsCorrectOutput() {
        String expected = FILE_A_STR + STRING_NEWLINE;
        assertDoesNotThrow(() -> {
            String actual = catApplication.catStdin(false, new ByteArrayInputStream(expected.getBytes()));
            assertEquals(expected, actual);
        });
    }

    @Test
    void catStdin_ValidWithLineNo_ReturnsCorrectOutput() {
        String input = FILE_A_STR + STRING_NEWLINE;
        assertDoesNotThrow(() -> {
            String actual = catApplication.catStdin(true, new ByteArrayInputStream(input.getBytes()));
            String expected = "1 This is file A." + STRING_NEWLINE;
            assertEquals(expected, actual);
        });
    }

    @Test
    void catStdin_ValidWithMultipleLineNo_ReturnsCorrectOutput() {
        String input = "This is the first sentence." + STRING_NEWLINE +
                "This is the second sentence." + STRING_NEWLINE +
                "This is the third sentence." + STRING_NEWLINE;
        assertDoesNotThrow(() -> {
            String actual = catApplication.catStdin(true, new ByteArrayInputStream(input.getBytes()));
            String expected = "1 This is the first sentence." + STRING_NEWLINE +
                    "2 This is the second sentence." + STRING_NEWLINE +
                    "3 This is the third sentence." + STRING_NEWLINE;
            assertEquals(expected, actual);
        });
    }

    @Test
    void catFileAndStdin_NullFileNames_ThrowsException() {
        Throwable thrown = assertThrows(CatException.class,
                () -> catApplication.catFileAndStdin(false, System.in, null));
        assertEquals(CAT_EX_PREFIX + ERR_NO_FILE_ARGS, thrown.getMessage());
    }

    @Test
    void catFileAndStdin_isLineNumberNull_ThrowsException() {
        Throwable thrown = assertThrows(CatException.class,
                () -> catApplication.catFileAndStdin(null, System.in,
                        TEST_FOLDER + CHAR_FILE_SEP + FILE_NAME_A));
        assertEquals(CAT_EX_PREFIX + ERR_NULL_ARGS, thrown.getMessage());
    }
    @Test
    void catFileAndStdin_NullStdin_ThrowsException() {
        Throwable thrown = assertThrows(CatException.class,
                () -> catApplication.catFileAndStdin(false, null,
                        TEST_FOLDER + CHAR_FILE_SEP + FILE_NAME_A));
        assertEquals(CAT_EX_PREFIX + ERR_NULL_STREAMS, thrown.getMessage());
    }

    @Test
    void catFileAndStdin_FileNamesContainNull_ThrowsException() {
        Throwable thrown = assertThrows(CatException.class,
                () -> catApplication.catFileAndStdin(false, System.in,
                        TEST_FOLDER + CHAR_FILE_SEP + FILE_NAME_A, null));
        assertEquals(CAT_EX_PREFIX + ERR_NULL_ARGS, thrown.getMessage());
    }

    @Test
    void catFileAndStdin_FileNotFound_ThrowsException() {
        Throwable thrown = assertThrows(CatException.class,
                () -> catApplication.catFileAndStdin(false, System.in,
                        TEST_FOLDER + CHAR_FILE_SEP + NON_EXISTENT_TXT, null));
        assertEquals(CAT_EX_PREFIX + ERR_FILE_DIR_NOT_FOUND +
                ": " + TEST_FOLDER + CHAR_FILE_SEP + NON_EXISTENT_TXT + " does not exist.", thrown.getMessage());
    }

    // We have 1 sentence from stdin and 1 from file
    @Test
    void catFileAndStdin_ValidArgs_ReturnsCorrectOutput() throws CatException {
        String stdinString = "This is from stdin." + STRING_NEWLINE;
        String expected = FILE_A_STR + STRING_NEWLINE +
                "This is from stdin." + STRING_NEWLINE;
        String actual = catApplication.catFileAndStdin(false,
                new ByteArrayInputStream(stdinString.getBytes()),
                TEST_FOLDER + CHAR_FILE_SEP + FILE_NAME_A);
        assertEquals(expected, actual);
    }

    @Test
    void run_NullOutputStream_ThrowsException() {
        Throwable thrown = assertThrows(CatException.class, () -> catApplication.run(new String[]{FILE_NAME_A},
                System.in, null));
        assertEquals(CAT_EX_PREFIX + ERR_NO_OSTREAM, thrown.getMessage());
    }

    @Test
    void run_NullArgs_ThrowsException() {
        Throwable thrown = assertThrows(CatException.class, () -> catApplication.run(null,
                System.in, System.out));
        assertEquals(CAT_EX_PREFIX + ERR_NULL_ARGS, thrown.getMessage());
    }

    @Test
    void run_ArgsContainNull_ThrowsException() {
        Throwable thrown = assertThrows(CatException.class, () -> catApplication.run(new String[]{FILE_NAME_A, null},
                System.in, System.out));
        assertEquals(CAT_EX_PREFIX + ERR_NULL_ARGS, thrown.getMessage());
    }

    @Test
    void run_ValidArgsOneFile_ReturnsCorrectOutput() throws CatException {
        OutputStream outputStream = new ByteArrayOutputStream();
        catApplication.run(new String[]{TEST_FOLDER + CHAR_FILE_SEP + FILE_NAME_A},
                System.in,
                outputStream);
        assertEquals(FILE_A_STR, outputStream.toString());
    }

    @Test
    void run_ValidArgsFileAWithLineNo_ReturnsCorrectOutput() throws CatException {
        String expected = CHAR_TAB + "1 This is file A.";
        OutputStream outputStream = new ByteArrayOutputStream();
        catApplication.run(new String[]{"-n", TEST_FOLDER + CHAR_FILE_SEP + FILE_NAME_A},
                System.in,
                outputStream);
        assertEquals(expected, outputStream.toString());
    }

    @Test
    void run_ValidArgsFileBWithLineNo_ReturnsCorrectOutput() throws CatException {
        String expected = CHAR_TAB + "1 This is file B." + STRING_NEWLINE +
                CHAR_TAB + "2 It has two lines.";
        OutputStream outputStream = new ByteArrayOutputStream();
        catApplication.run(new String[]{"-n", TEST_FOLDER + CHAR_FILE_SEP + FILE_NAME_B},
                System.in,
                outputStream);
        assertEquals(expected, outputStream.toString());
    }

    @Test
    void run_ValidArgsFileAWithLineNoAndFileBWithLineNo_ReturnsCorrectOutput() throws CatException {
        String expected = CHAR_TAB + "1 This is file A.This is file B." + STRING_NEWLINE +
                CHAR_TAB + "2 It has two lines.";
        OutputStream outputStream = new ByteArrayOutputStream();
        catApplication.run(new String[]{"-n", TEST_FOLDER + CHAR_FILE_SEP + FILE_NAME_A,
                TEST_FOLDER + CHAR_FILE_SEP + FILE_NAME_B},
                System.in,
                outputStream);
        assertEquals(expected, outputStream.toString());
    }

    @Test
    void run_FileNotFound_ThrowsFileNotFoundCatException() {
        Throwable thrown = assertThrows(CatException.class,
                () -> catApplication.run(new String[]{"RANDOM"}, System.in, System.out));
        assertEquals(CAT_EX_PREFIX + "RANDOM: " + ERR_FILE_NOT_FOUND, thrown.getMessage());
    }

    @Test
    void run_StdinAndFileNamesAreNull_ThrowsException() throws CatException {
        InputStream inputStream = new ByteArrayInputStream("HI".getBytes());
        OutputStream outputStream = new ByteArrayOutputStream();
        catApplication.run(new String[]{}, inputStream, outputStream);
        assertEquals("HI", outputStream.toString());
    }

    @Test
    void run_FileIsDirectory_ThrowsException() {
        Throwable thrown = assertThrows(CatException.class,
                () -> catApplication.run(new String[]{TEST_FOLDER}, System.in, System.out));
        assertEquals(CAT_EX_PREFIX + TEST_FOLDER + ": " + ERR_IS_DIR, thrown.getMessage());
    }

    @Test
    @DisplayName("Test cat for 2 files without newlines")
    void run_TwoFilesWithoutNewLines_ReturnsOneLine() throws CatException {
        String expected = "This is file A.This is file A.";
        OutputStream outputStream = new ByteArrayOutputStream();
        catApplication.run(new String[]{TEST_FOLDER + CHAR_FILE_SEP + FILE_NAME_A,
                TEST_FOLDER + CHAR_FILE_SEP + FILE_NAME_A}, System.in, outputStream);
        assertEquals(expected, outputStream.toString());
    }
}
