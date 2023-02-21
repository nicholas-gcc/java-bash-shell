package sg.edu.nus.comp.cs4218.impl.app;

import org.junit.jupiter.api.Test;
import sg.edu.nus.comp.cs4218.exception.CutException;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.ERR_NULL_STREAMS;

public class CutApplicationTest {
    private static final String CUT_EXCEPTION_PREFIX = "cut: ";
    private final CutApplication cutApp = new CutApplication();

    @Test
    void testCut_FirstByteFromStdin_ReturnsCorrectOutput() {
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
    void testCut_SecondByteFromStdin_ReturnsCorrectOutput() {
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
    void testCut_FirstByteAndEighthByteFromStdin_ReturnsCorrectOutput() {
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
    void testCut_RangeFirstByteToEigthByteFromStdin_ReturnsCorrectOutput() {
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
    void testCut_FromNullStream_ReturnsException() {
        List<int[]> ranges = List.of(new int[]{1, 1});
        Throwable thrown = assertThrows(CutException.class, () -> {
            cutApp.cutFromStdin(false, true, ranges, null);
        });
        assertEquals(CUT_EXCEPTION_PREFIX + ERR_NULL_STREAMS, thrown.getMessage());
    }
}
