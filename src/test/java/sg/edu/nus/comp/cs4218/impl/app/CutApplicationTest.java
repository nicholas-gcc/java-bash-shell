package sg.edu.nus.comp.cs4218.impl.app;

import org.junit.jupiter.api.Test;
import sg.edu.nus.comp.cs4218.exception.CutException;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.ERR_NULL_ARGS;
import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.ERR_NULL_STREAMS;

public class CutApplicationTest {
    private static final String CUT_EX_PREFIX = "cut: ";
    private final CutApplication cutApp = new CutApplication();

    @Test
    void testCutFromStdin_FirstByte_ReturnsCorrectOutput() {
        String input = "baz";
        InputStream inputStream = new ByteArrayInputStream(input.getBytes());
        String expected = "b";
        List<int[]> ranges = List.of(new int[]{1, 1});
        assertDoesNotThrow(() -> {
            String actual = cutApp.cutFromStdin(false, true, ranges, inputStream);
            assertEquals(expected, actual);
        });
    }

    @Test
    void testCutFromStdin_SecondByte_ReturnsCorrectOutput() {
        String input = "baz";
        InputStream inputStream = new ByteArrayInputStream(input.getBytes());
        String expected = "a";
        List<int[]> ranges = List.of(new int[]{2, 2});
        assertDoesNotThrow(() -> {
            String actual = cutApp.cutFromStdin(false, true, ranges, inputStream);
            assertEquals(expected, actual);
        });
    }

    @Test
    void testCutFromStdin_FirstByteAndEighthByte_ReturnsCorrectOutput() {
        String input = "Today is Tuesday";
        InputStream inputStream = new ByteArrayInputStream(input.getBytes());
        String expected = "Ts";
        List<int[]> ranges = List.of(new int[]{1, 1}, new int[]{8, 8});
        assertDoesNotThrow(() -> {
            String actual = cutApp.cutFromStdin(false, true, ranges, inputStream);
            assertEquals(expected, actual);
        });
    }

    @Test
    void testCutFromStdin_RangeFirstByteToEighthByte_ReturnsCorrectOutput() {
        String input = "Today is Tuesday";
        InputStream inputStream = new ByteArrayInputStream(input.getBytes());
        String expected = "Today is";
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
    void testRun_WithNullArgs_ReturnsException() {
        Throwable thrown = assertThrows(CutException.class, () -> {
            cutApp.run(null, null, null);
        });
        assertEquals(CUT_EX_PREFIX + ERR_NULL_ARGS, thrown.getMessage());
    }

    @Test
    void testRun_WithNullInputStream_ReturnsException() {
        Throwable thrown = assertThrows(CutException.class, () -> {
            cutApp.run(new String[]{"-b", "1"}, null, null);
        });
        assertEquals(CUT_EX_PREFIX + ERR_NULL_STREAMS, thrown.getMessage());
    }

    @Test
    void testRun_WithNullOutputStream_ReturnsException() {
        Throwable thrown = assertThrows(CutException.class, () -> {
            cutApp.run(new String[]{"-b", "1"}, System.in, null);
        });
        assertEquals(CUT_EX_PREFIX + ERR_NULL_STREAMS, thrown.getMessage());
    }

    @Test
    void testRun_Range_ReturnsCorrectOutput() {
        String input = "Today is Tuesday";
        InputStream inputStream = new ByteArrayInputStream(input.getBytes());
        String expected = "Today is";
        List<int[]> ranges = List.of(new int[]{1, 8});
        assertDoesNotThrow(() -> cutApp.run(new String[]{"-b", "1-8"}, inputStream, System.out));
    }

    @Test
    void testRun_SingleNumber_DoesNotThrowException() {
        String input = "Today is Tuesday";
        InputStream inputStream = new ByteArrayInputStream(input.getBytes());
        assertDoesNotThrow(() -> cutApp.run(new String[]{"-b", "1"}, inputStream, System.out));
    }

    void testRun_MixOfRangeAndSingleNumber_DoesNotThrowException() {
        String input = "Today is Tuesday";
        InputStream inputStream = new ByteArrayInputStream(input.getBytes());
        assertDoesNotThrow(() -> cutApp.run(new String[]{"-b", "1-8,10"}, inputStream, System.out));
    }

}
