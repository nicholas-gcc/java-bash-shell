package sg.edu.nus.comp.cs4218.impl.app;

import org.junit.jupiter.api.Test;
import sg.edu.nus.comp.cs4218.exception.CutException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.*;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.CHAR_FILE_SEP;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.STRING_NEWLINE;

public class CutApplicationTest {
    private static final String CUT_EX_PREFIX = "cut: ";
    private static final String SAMPLE_SENTENCE = "Today is Tuesday";
    private static final String SAMPLE_WORD = "baz";

    private static final String FOLDER_FILEPATH = "assets" + CHAR_FILE_SEP + "app" + CHAR_FILE_SEP + "cut";
    private static final String ONE_SENTENCE_FILE = "one-sentence.txt";
    private static final String MUL_SENTENCE_FILE = "multi-sentence.txt";

    private static final String ONE_WORD_FILE = "one-word.txt";

    private static final String NON_EXISTENT_FILE = "non-existent.txt";

    private final CutApplication cutApp = new CutApplication();

    @Test
    void testCutFromStdin_FirstByte_ReturnsCorrectOutput() {
        InputStream inputStream = new ByteArrayInputStream(SAMPLE_WORD.getBytes());
        String expected = "b";
        List<int[]> ranges = List.of(new int[]{1, 1});
        assertDoesNotThrow(() -> {
            String actual = cutApp.cutFromStdin(false, true, ranges, inputStream);
            assertEquals(expected, actual);
        });
    }

    @Test
    void testCutFromStdin_SecondByte_ReturnsCorrectOutput() {
        InputStream inputStream = new ByteArrayInputStream(SAMPLE_WORD.getBytes());
        String expected = "a";
        List<int[]> ranges = List.of(new int[]{2, 2});
        assertDoesNotThrow(() -> {
            String actual = cutApp.cutFromStdin(false, true, ranges, inputStream);
            assertEquals(expected, actual);
        });
    }

    @Test
    void testCutFromStdin_FirstByteAndEighthByte_ReturnsCorrectOutput() {
        InputStream inputStream = new ByteArrayInputStream(SAMPLE_SENTENCE.getBytes());
        String expected = "Ts";
        List<int[]> ranges = List.of(new int[]{1, 1}, new int[]{8, 8});
        assertDoesNotThrow(() -> {
            String actual = cutApp.cutFromStdin(false, true, ranges, inputStream);
            assertEquals(expected, actual);
        });
    }

    @Test
    void testCutFromStdin_RangeFirstByteToEighthByte_ReturnsCorrectOutput() {
        InputStream inputStream = new ByteArrayInputStream(SAMPLE_SENTENCE.getBytes());
        String expected = "Today is";//NOPMD
        List<int[]> ranges = List.of(new int[]{1, 8});
        assertDoesNotThrow(() -> {
            String actual = cutApp.cutFromStdin(false, true, ranges, inputStream);
            assertEquals(expected, actual);
        });
    }
    @Test
    void testCutFromStdin_FromNullStream_ReturnsException() {
        List<int[]> ranges = List.of(new int[]{1, 1});
        Throwable thrown = assertThrows(CutException.class, () -> {
            cutApp.cutFromStdin(false, true, ranges, null);
        });
        assertEquals(CUT_EX_PREFIX + ERR_NULL_STREAMS, thrown.getMessage());
    }

    @Test
    void testCutFromFiles_NonExistentFile_ThrowsException() {
        List<int[]> ranges = List.of(new int[]{1, 1});
        String filepath = FOLDER_FILEPATH + CHAR_FILE_SEP + NON_EXISTENT_FILE;
        Throwable thrown = assertThrows(CutException.class, () -> {
            cutApp.cutFromFiles(false, true, ranges, filepath);
        });
        assertEquals(CUT_EX_PREFIX + "No such file or directory", thrown.getMessage());
    }

    @Test
    void testCurFromFiles_Directory_ThrowsException() {
        List<int[]> ranges = List.of(new int[]{1, 1});
        String filepath = FOLDER_FILEPATH;
        Throwable thrown = assertThrows(CutException.class, () -> {
            cutApp.cutFromFiles(false, true, ranges, filepath);
        });
        assertEquals(CUT_EX_PREFIX + ERR_IS_DIR, thrown.getMessage());
    }

    @Test
    void testCutFromFiles_FirstByteOfOneWordFile_ReturnsCorrectOutput() {
        String expected = "b";
        List<int[]> ranges = List.of(new int[]{1, 1});
        String filepath = FOLDER_FILEPATH + CHAR_FILE_SEP + ONE_WORD_FILE;
        assertDoesNotThrow(() -> {
            String actual = cutApp.cutFromFiles(false, true, ranges, filepath);
            assertEquals(expected, actual);
        });
    }

    @Test
    void testCutFromFiles_FirstByteOfOneSentenceFile_ReturnsCorrectOutput() {
        String expected = "T";
        List<int[]> ranges = List.of(new int[]{1, 1});
        String filepath = FOLDER_FILEPATH + CHAR_FILE_SEP + ONE_SENTENCE_FILE;
        assertDoesNotThrow(() -> {
            String actual = cutApp.cutFromFiles(false, true, ranges, filepath);
            assertEquals(expected, actual);
        });
    }

    @Test
    void testCutFromFile_RangeFirstByteToEighthByteOfOneSentenceFile_ReturnsCorrectOutput() {
        String expected = "Today is";
        List<int[]> ranges = List.of(new int[]{1, 8});
        String filepath = FOLDER_FILEPATH + CHAR_FILE_SEP + ONE_SENTENCE_FILE;
        assertDoesNotThrow(() -> {
            String actual = cutApp.cutFromFiles(false, true, ranges, filepath);
            assertEquals(expected, actual);
        });
    }

    // no newline at end of output because this is only a single file. At end of each file the run
    // method of CutApplication will add a newline
    @Test
    void testCutFromFile_RangeFirstByteToEighthByteOfMultiSentenceFile_ReturnsCorrectOutput() {
        String expected = "Good mor" + STRING_NEWLINE + "This is " + STRING_NEWLINE + "I hope y";
        List<int[]> ranges = List.of(new int[]{1, 8});
        String filepath = FOLDER_FILEPATH + CHAR_FILE_SEP + MUL_SENTENCE_FILE;
        assertDoesNotThrow(() -> {
            String actual = cutApp.cutFromFiles(false, true, ranges, filepath);
            assertEquals(expected, actual);
        });
    }

    @Test
    void testRun_WithNullArgs_ReturnsException() {
        Throwable thrown = assertThrows(CutException.class, () -> {
            cutApp.run(null, null, null);
        });
        assertEquals(CUT_EX_PREFIX + ERR_NULL_ARGS, thrown.getMessage());
    }

    // TODO: Address nested exception messages (currently ends up as cut: cut: SOME_ERROR as the exceptions are nested)
//    @Test
//    void testRun_WithNullInputStreamFromStdin_ReturnsException() {
//        OutputStream outputStream = new ByteArrayOutputStream();
//        Throwable thrown = assertThrows(CutException.class, () -> {
//            cutApp.run(new String[]{"-b", "1"}, null, outputStream);
//        });
//        assertEquals(CUT_EX_PREFIX + ERR_NULL_STREAMS, thrown.getMessage());
//    }

    @Test
    void testRun_WithNullOutputStreamFromStdin_ReturnsException() {
        Throwable thrown = assertThrows(CutException.class, () -> {
            cutApp.run(new String[]{"-b", "1"}, System.in, null);
        });
        assertEquals(CUT_EX_PREFIX + ERR_NULL_STREAMS, thrown.getMessage());
    }

    @Test
    void testRun_RangeFromStdin_ReturnsCorrectOutput() {
        InputStream inputStream = new ByteArrayInputStream(SAMPLE_SENTENCE.getBytes());
        String expected = "Today is" + STRING_NEWLINE;
        assertDoesNotThrow(() -> {
            OutputStream outputStream = new ByteArrayOutputStream();
            cutApp.run(new String[]{"-b", "1-8"}, inputStream, outputStream);
            assertEquals(expected, outputStream.toString());
        });
    }

    @Test
    void testRun_SingleNumberFromOneWordFile_ReturnsCorrectOutput() {
        String filepath = FOLDER_FILEPATH + CHAR_FILE_SEP + ONE_WORD_FILE;
        String expected = "a" + STRING_NEWLINE;
        assertDoesNotThrow(() -> {
            OutputStream outputStream = new ByteArrayOutputStream();
            cutApp.run(new String[]{"-b", "2", filepath}, null, outputStream);
            assertEquals(expected, outputStream.toString());
        });
    }

    @Test
    void testRun_RangeFromOneSentenceFile_ReturnsCorrectOutput() {
        String filepath = FOLDER_FILEPATH + CHAR_FILE_SEP + ONE_SENTENCE_FILE;
        String expected = "Today is" + STRING_NEWLINE;
        assertDoesNotThrow(() -> {
            OutputStream outputStream = new ByteArrayOutputStream();
            cutApp.run(new String[]{"-b", "1-8", filepath}, null, outputStream);
            assertEquals(expected, outputStream.toString());
        });
    }

    @Test
    void testRun_RangeFromMultiSentenceFile_ReturnsCorrectOutput() {
        String filepath = FOLDER_FILEPATH + CHAR_FILE_SEP + MUL_SENTENCE_FILE;
        String expected = "Good mor" + STRING_NEWLINE + "This is " + STRING_NEWLINE + "I hope y" + STRING_NEWLINE;
        assertDoesNotThrow(() -> {
            OutputStream outputStream = new ByteArrayOutputStream();
            cutApp.run(new String[]{"-b", "1-8", filepath}, null, outputStream);
            assertEquals(expected, outputStream.toString());
        });
    }

    @Test
    void testRun_SingleNumberFromStdin_ReturnsCorrectOutput() {
        InputStream inputStream = new ByteArrayInputStream(SAMPLE_SENTENCE.getBytes());
        String expected = "T" + STRING_NEWLINE;
        assertDoesNotThrow(() -> {
            OutputStream outputStream = new ByteArrayOutputStream();
            cutApp.run(new String[]{"-b", "1"}, inputStream, outputStream);
            assertEquals(expected, outputStream.toString());
        });
    }

    @Test
    void testRun_MixOfRangeAndSingleNumberFromStdin_ReturnsCorrectOutput() {
        InputStream inputStream = new ByteArrayInputStream(SAMPLE_SENTENCE.getBytes());
        String expected = "Today isT" + STRING_NEWLINE;
        assertDoesNotThrow(() -> {
            OutputStream outputStream = new ByteArrayOutputStream();
            cutApp.run(new String[]{"-b", "1-8,10"}, inputStream, outputStream);
            assertEquals(expected, outputStream.toString());
        });
    }
}
