package sg.edu.nus.comp.cs4218.impl.app;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import sg.edu.nus.comp.cs4218.exception.CatException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.ERR_FILE_NOT_FOUND;
import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.ERR_IS_DIR;
import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.ERR_NULL_ARGS;
import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.ERR_NULL_STREAMS;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.CHAR_FILE_SEP;

public class CatApplicationTest {
    private static final String FILE_NAME_A = "A.txt";
    private static final String FILE_NAME_B = "B.txt";
    private static final String FILE_NAME_C = "C.txt";

    private static final String FILE_DOES_NOT_EXIST = "NonExistent.txt";
    private static final String TEST_FOLDER = "assets" + CHAR_FILE_SEP + "app" + CHAR_FILE_SEP + "cat";
    private static final String CD_EX_PREFIX = "cd: ";

    private CatApplication catApplication = new CatApplication();

    @Test
    @Disabled
    void testCatFiles_FileDoesNotExist_ThrowsException() {
        Throwable thrown = assertThrows(CatException.class,
                () -> catApplication.catFiles(false, FILE_DOES_NOT_EXIST));
        assertEquals(CD_EX_PREFIX + ERR_FILE_NOT_FOUND, thrown.getMessage());
    }

    @Test
    @Disabled
    void testCatFiles_IsDirectory_ThrowsException() {
        Throwable thrown = assertThrows(CatException.class,
                () -> catApplication.catFiles(false, TEST_FOLDER));
        assertEquals(CD_EX_PREFIX + ERR_IS_DIR, thrown.getMessage());
    }

    @Test
    @Disabled
    void testCatFiles_NullFileNames_ThrowsException() {
        Throwable thrown = assertThrows(CatException.class,
                () -> catApplication.catFiles(false, null));
        assertEquals(CD_EX_PREFIX + ERR_NULL_ARGS, thrown.getMessage());
    }

    @Test
    @Disabled
    void testCatFiles_FileNamesContainNull_ThrowsException() {
        Throwable thrown = assertThrows(CatException.class,
                () -> catApplication.catFiles(false, FILE_NAME_A, null));
        assertEquals(CD_EX_PREFIX + ERR_NULL_ARGS, thrown.getMessage());
    }

    @Test
    @Disabled
    void testCatFiles_NullIsLineNumber_ThrowsException() {
        Throwable thrown = assertThrows(CatException.class,
                () -> catApplication.catFiles(null, FILE_NAME_A));
        assertEquals(CD_EX_PREFIX + ERR_NULL_ARGS, thrown.getMessage());
    }

    @Test
    @Disabled
    void testCatFiles_ValidArgs_ReturnsCorrectOutput() {
        String expected = "This is file A." + System.lineSeparator();
        assertDoesNotThrow(() -> {
            String actual = catApplication.catFiles(false, TEST_FOLDER + CHAR_FILE_SEP + FILE_NAME_A);
            assertEquals(expected, actual);
        });
    }

    @Test
    @Disabled
    void testCatStdin_NullStdin_ThrowsException() {
        Throwable thrown = assertThrows(CatException.class,
                () -> catApplication.catStdin(false, null));
        assertEquals(CD_EX_PREFIX + ERR_NULL_STREAMS, thrown.getMessage());
    }

    @Test
    @Disabled
    void testCatStdin_NullIsLineNumber_ThrowsException() {
        Throwable thrown = assertThrows(CatException.class,
                () -> catApplication.catStdin(null, System.in));
        assertEquals(CD_EX_PREFIX + ERR_NULL_ARGS, thrown.getMessage());
    }

    @Test
    @Disabled
    void testCatStdin_Valid_ReturnsCorrectOutput() {
        String expected = "This is file A." + System.lineSeparator();
        assertDoesNotThrow(() -> {
            String actual = catApplication.catStdin(false, new ByteArrayInputStream(expected.getBytes()));
            assertEquals(expected, actual);
        });
    }

    @Test
    @Disabled
    void testCatFileAndStdin_NullStdin_ThrowsException() {
        Throwable thrown = assertThrows(CatException.class,
                () -> catApplication.catFileAndStdin(false, null, FILE_NAME_A));
        assertEquals(CD_EX_PREFIX + ERR_NULL_STREAMS, thrown.getMessage());
    }

    @Test
    @Disabled
    void testCatFileAndStdin_NullFileNames_ThrowsException() {
        Throwable thrown = assertThrows(CatException.class,
                () -> catApplication.catFileAndStdin(false, System.in, null));
        assertEquals(CD_EX_PREFIX + ERR_NULL_ARGS, thrown.getMessage());
    }

    @Test
    @Disabled
    void testCatFileAndStdin_FileNamesContainNull_ThrowsException() {
        Throwable thrown = assertThrows(CatException.class,
                () -> catApplication.catFileAndStdin(false, System.in, FILE_NAME_A, null));
        assertEquals(CD_EX_PREFIX + ERR_NULL_ARGS, thrown.getMessage());
    }

    @Test
    @Disabled
    void testCatFileAndStdin_NullIsLineNumber_ThrowsException() {
        Throwable thrown = assertThrows(CatException.class,
                () -> catApplication.catFileAndStdin(null, System.in, FILE_NAME_A));
        assertEquals(CD_EX_PREFIX + ERR_NULL_ARGS, thrown.getMessage());
    }

    @Test
    @Disabled
    void testCatFileAndStdin_Valid_ReturnsCorrectOutput() {
        String expected = "This is file A." + System.lineSeparator();
        assertDoesNotThrow(() -> {
            String actual = catApplication.catFileAndStdin(false,
                    new ByteArrayInputStream(expected.getBytes()),
                    TEST_FOLDER + CHAR_FILE_SEP + FILE_NAME_A);
            assertEquals(expected, actual);
        });
    }

    @Test
    @Disabled
    void testRun_NullOutputStream_ThrowsException() {
        Throwable thrown = assertThrows(CatException.class, () -> catApplication.run(new String[]{FILE_NAME_A},
                System.in, null));
        assertEquals(CD_EX_PREFIX + ERR_NULL_STREAMS, thrown.getMessage());
    }

    @Test
    @Disabled
    void testRun_NullInputStream_ThrowsException() {
        Throwable thrown = assertThrows(CatException.class, () -> catApplication.run(new String[]{FILE_NAME_A},
                null, System.out));
        assertEquals(CD_EX_PREFIX + ERR_NULL_STREAMS, thrown.getMessage());
    }

    @Test
    @Disabled
    void testRun_NullArgs_ThrowsException() {
        Throwable thrown = assertThrows(CatException.class, () -> catApplication.run(null,
                System.in, System.out));
        assertEquals(CD_EX_PREFIX + ERR_NULL_ARGS, thrown.getMessage());
    }

    @Test
    @Disabled
    void testRun_ArgsContainNull_ThrowsException() {
        Throwable thrown = assertThrows(CatException.class, () -> catApplication.run(new String[]{FILE_NAME_A, null},
                System.in, System.out));
        assertEquals(CD_EX_PREFIX + ERR_NULL_ARGS, thrown.getMessage());
    }

    @Test
    @Disabled
    void testRun_ValidArgsOneFile_ReturnsCorrectOutput() {
        String expected = "This is file A." + System.lineSeparator();
        OutputStream outputStream = new ByteArrayOutputStream();
        assertDoesNotThrow(() -> {
            catApplication.run(new String[]{TEST_FOLDER + CHAR_FILE_SEP + FILE_NAME_A},
                    System.in,
                    outputStream);
            assertEquals(expected, outputStream.toString());
        });
    }

    @Test
    @Disabled
    void testRun_ValidArgsFileAWithLineNo_ReturnsCorrectOutput() {
        String expected = "1 This is file A." + System.lineSeparator();
        OutputStream outputStream = new ByteArrayOutputStream();
        assertDoesNotThrow(() -> {
            catApplication.run(new String[]{"-n", TEST_FOLDER + CHAR_FILE_SEP + FILE_NAME_A},
                    System.in,
                    outputStream);
            assertEquals(expected, outputStream.toString());
        });
    }

    @Test
    @Disabled
    void testRun_ValidArgsFileBWithLineNo_ReturnsCorrectOutput() {
        String expected = "1 This is file B." + System.lineSeparator() +
                "2 It has two lines." + System.lineSeparator();
        OutputStream outputStream = new ByteArrayOutputStream();
        assertDoesNotThrow(() -> {
            catApplication.run(new String[]{"-n", TEST_FOLDER + CHAR_FILE_SEP + FILE_NAME_B},
                    System.in,
                    outputStream);
            assertEquals(expected, outputStream.toString());
        });
    }

    @Test
    @Disabled
    void testRun_ValidArgsFileAWithLineNoAndFileBWithLineNo_ReturnsCorrectOutput() {
        String expected = "1 This is file A." + System.lineSeparator() +
                "2 This is file B." + System.lineSeparator() +
                "3 It has two lines." + System.lineSeparator();
        OutputStream outputStream = new ByteArrayOutputStream();
        assertDoesNotThrow(() -> {
            catApplication.run(new String[]{"-n", TEST_FOLDER + CHAR_FILE_SEP + FILE_NAME_A,
                    TEST_FOLDER + CHAR_FILE_SEP + FILE_NAME_B},
                    System.in,
                    outputStream);
            assertEquals(expected, outputStream.toString());
        });
    }

}
