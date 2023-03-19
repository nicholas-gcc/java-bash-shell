package sg.edu.nus.comp.cs4218.impl.app;

import org.junit.jupiter.api.Test;
import sg.edu.nus.comp.cs4218.exception.CutException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
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
    private static final String TDY_STR = "Today is";

    private final CutApplication cutApp = new CutApplication();

    @Test
    void testCutFromStdin_FirstByte_ReturnsCorrectOutput() throws CutException {
        InputStream inputStream = new ByteArrayInputStream(SAMPLE_WORD.getBytes());
        String expected = "b" + STRING_NEWLINE;
        List<int[]> ranges = List.of(new int[]{1, 1});
        String actual = cutApp.cutFromStdin(false, true, ranges, inputStream);
        assertEquals(expected, actual);
    }

    @Test
    void testCutFromStdin_SecondByte_ReturnsCorrectOutput() throws CutException {
        InputStream inputStream = new ByteArrayInputStream(SAMPLE_WORD.getBytes());
        String expected = "a" + STRING_NEWLINE;
        List<int[]> ranges = List.of(new int[]{2, 2});
        String actual = cutApp.cutFromStdin(false, true, ranges, inputStream);
        assertEquals(expected, actual);
    }

    @Test
    void testCutFromStdin_FirstByteAndEighthByte_ReturnsCorrectOutput() throws CutException {
        InputStream inputStream = new ByteArrayInputStream(SAMPLE_SENTENCE.getBytes());
        String expected = "Ts" + STRING_NEWLINE;
        List<int[]> ranges = List.of(new int[]{1, 1}, new int[]{8, 8});
        String actual = cutApp.cutFromStdin(false, true, ranges, inputStream);
        assertEquals(expected, actual);
    }

    @Test
    void cutFromStdin_RangeFirstByteToEighthByte_ReturnsCorrectOutput() throws CutException {
        InputStream inputStream = new ByteArrayInputStream(SAMPLE_SENTENCE.getBytes());
        String expected = "Today is" + STRING_NEWLINE;
        List<int[]> ranges = List.of(new int[]{1, 8});
        String actual = cutApp.cutFromStdin(false, true, ranges, inputStream);
        assertEquals(expected, actual);
    }
    @Test
    void cutFromStdin_FromNullStream_ReturnsException() {
        List<int[]> ranges = List.of(new int[]{1, 1});
        Throwable thrown = assertThrows(CutException.class, () -> {
            cutApp.cutFromStdin(false, true, ranges, null);
        });
        assertEquals(CUT_EX_PREFIX + ERR_NULL_STREAMS, thrown.getMessage());
    }

    @Test
    void cutFromFiles_NonExistentFile_ThrowsException() {
        List<int[]> ranges = List.of(new int[]{1, 1});
        String filepath = FOLDER_FILEPATH + CHAR_FILE_SEP + NON_EXISTENT_FILE;
        Throwable thrown = assertThrows(CutException.class, () -> {
            cutApp.cutFromFiles(false, true, ranges, filepath);
        });
        assertEquals(CUT_EX_PREFIX + "No such file or directory", thrown.getMessage());
    }

    @Test
    void cutFromFiles_Directory_ThrowsException() {
        List<int[]> ranges = List.of(new int[]{1, 1});
        Throwable thrown = assertThrows(CutException.class, () -> {
            cutApp.cutFromFiles(false, true, ranges, FOLDER_FILEPATH);
        });
        assertEquals(CUT_EX_PREFIX + ERR_IS_DIR, thrown.getMessage());
    }

    @Test
    void cutFromFiles_FirstByteOfOneWordFile_ReturnsCorrectOutput() throws CutException {
        String expected = "b" + STRING_NEWLINE;
        List<int[]> ranges = List.of(new int[]{1, 1});
        String filepath = FOLDER_FILEPATH + CHAR_FILE_SEP + ONE_WORD_FILE;
        String actual = cutApp.cutFromFiles(false, true, ranges, filepath);
        assertEquals(expected, actual);
    }

    @Test
    void cutFromFiles_FirstByteOfOneSentenceFile_ReturnsCorrectOutput() throws CutException {
        String expected = "T" + STRING_NEWLINE;
        List<int[]> ranges = List.of(new int[]{1, 1});
        String filepath = FOLDER_FILEPATH + CHAR_FILE_SEP + ONE_SENTENCE_FILE;
        String actual = cutApp.cutFromFiles(false, true, ranges, filepath);
        assertEquals(expected, actual);
    }

    @Test
    void cutFromFiles_RangeFirstByteToEighthByteOfOneSentenceFile_ReturnsCorrectOutput() throws CutException {
        String expected = TDY_STR + STRING_NEWLINE;
        List<int[]> ranges = List.of(new int[]{1, 8});
        String filepath = FOLDER_FILEPATH + CHAR_FILE_SEP + ONE_SENTENCE_FILE;
        String actual = cutApp.cutFromFiles(false, true, ranges, filepath);
        assertEquals(expected, actual);
    }

    // no newline at end of output because this is only a single file. At end of each file the run
    // method of CutApplication will add a newline
    @Test
    void cutFromFiles_RangeFirstByteToEighthByteOfMultiSentenceFile_ReturnsCorrectOutput() throws CutException {
        String expected = "Good mor" + STRING_NEWLINE + "This is " + STRING_NEWLINE + "I hope y" + STRING_NEWLINE;
        List<int[]> ranges = List.of(new int[]{1, 8});
        String filepath = FOLDER_FILEPATH + CHAR_FILE_SEP + MUL_SENTENCE_FILE;
        String actual = cutApp.cutFromFiles(false, true, ranges, filepath);
        assertEquals(expected, actual);
    }

    @Test
    void cutFromFiles_fileNameIsNull_ReturnsNullArgsException() {
        List<int[]> ranges = List.of(new int[]{1, 8});
        Throwable thrown = assertThrows(CutException.class,
                () -> cutApp.cutFromFiles(false, true, ranges, null));
        assertEquals(CUT_EX_PREFIX + ERR_NULL_ARGS, thrown.getMessage());
    }

    @Test
    void run_WithNullArgs_ReturnsException() {
        Throwable thrown = assertThrows(CutException.class, () -> {
            cutApp.run(null, null, null);
        });
        assertEquals(CUT_EX_PREFIX + ERR_NULL_ARGS, thrown.getMessage());
    }

    @Test
    void run_WithNullInputStreamFromStdin_ReturnsException() {
        OutputStream outputStream = new ByteArrayOutputStream();
        Throwable thrown = assertThrows(CutException.class, () -> {
            cutApp.run(new String[]{"-b", "1"}, null, outputStream);
        });
        assertEquals(CUT_EX_PREFIX + ERR_NULL_STREAMS, thrown.getMessage());
    }

    @Test
    void run_WithNullOutputStreamFromStdin_ReturnsException() {
        Throwable thrown = assertThrows(CutException.class, () -> {
            cutApp.run(new String[]{"-b", "1"}, System.in, null);
        });
        assertEquals(CUT_EX_PREFIX + ERR_NULL_STREAMS, thrown.getMessage());
    }

    @Test
    void run_RangeFromStdin_ReturnsCorrectOutput() throws CutException {
        InputStream inputStream = new ByteArrayInputStream(SAMPLE_SENTENCE.getBytes());
        String expected = TDY_STR + STRING_NEWLINE;
        OutputStream outputStream = new ByteArrayOutputStream();
        cutApp.run(new String[]{"-b", "1-8"}, inputStream, outputStream);
        assertEquals(expected, outputStream.toString());
    }

    @Test
    void run_SingleNumberFromOneWordFile_ReturnsCorrectOutput() throws CutException {
        String filepath = FOLDER_FILEPATH + CHAR_FILE_SEP + ONE_WORD_FILE;
        String expected = "a" + STRING_NEWLINE;
        OutputStream outputStream = new ByteArrayOutputStream();
        cutApp.run(new String[]{"-b", "2", filepath}, null, outputStream);
        assertEquals(expected, outputStream.toString());
    }

    @Test
    void run_RangeFromOneSentenceFile_ReturnsCorrectOutput() throws CutException {
        String filepath = FOLDER_FILEPATH + CHAR_FILE_SEP + ONE_SENTENCE_FILE;
        String expected = TDY_STR + STRING_NEWLINE;
        OutputStream outputStream = new ByteArrayOutputStream();
        cutApp.run(new String[]{"-b", "1-8", filepath}, null, outputStream);
        assertEquals(expected, outputStream.toString());
    }

    @Test
    void run_RangeFromMultiSentenceFile_ReturnsCorrectOutput() throws CutException {
        String filepath = FOLDER_FILEPATH + CHAR_FILE_SEP + MUL_SENTENCE_FILE;
        String expected = "Good mor" + STRING_NEWLINE + "This is " + STRING_NEWLINE + "I hope y" + STRING_NEWLINE;
        OutputStream outputStream = new ByteArrayOutputStream();
        cutApp.run(new String[]{"-b", "1-8", filepath}, null, outputStream);
        assertEquals(expected, outputStream.toString());
    }

    @Test
    void run_SingleNumberFromStdin_ReturnsCorrectOutput() throws CutException {
        InputStream inputStream = new ByteArrayInputStream(SAMPLE_SENTENCE.getBytes());
        String expected = "T" + STRING_NEWLINE;
        OutputStream outputStream = new ByteArrayOutputStream();
        cutApp.run(new String[]{"-b", "1"}, inputStream, outputStream);
        assertEquals(expected, outputStream.toString());
    }

    @Test
    void run_MixOfRangeAndSingleNumberFromStdin_ReturnsCorrectOutput() throws CutException {
        InputStream inputStream = new ByteArrayInputStream(SAMPLE_SENTENCE.getBytes());
        String expected = "Today isT" + STRING_NEWLINE;
        OutputStream outputStream = new ByteArrayOutputStream();
        cutApp.run(new String[]{"-b", "1-8,10"}, inputStream, outputStream);
        assertEquals(expected, outputStream.toString());
    }

    @Test
    void run_InvalidArgs_ReturnsException() {
        Throwable thrown = assertThrows(CutException.class,
                () -> cutApp.run(new String[]{"-b", "???"}, System.in, System.out));
        assertEquals(CUT_EX_PREFIX + "Invalid indexes provided", thrown.getMessage());
    }
}
