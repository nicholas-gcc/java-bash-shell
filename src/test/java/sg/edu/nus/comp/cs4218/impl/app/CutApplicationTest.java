package sg.edu.nus.comp.cs4218.impl.app;

import org.junit.jupiter.api.Test;
import sg.edu.nus.comp.cs4218.exception.CutException;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.ERR_NULL_STREAMS;

public class CutApplicationTest {
    private static final String cutExceptionPrefix = "cut: ";
    private final CutApplication cutApp = new CutApplication();

    @Test
    void testCutFirstByteFromStdin() {
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
    void testCutSecondByteFromStdin() {
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
    void testCutFirstByteAndEighthByteFromStdin() {
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
    void testCutRangeFirstByteToEigthByteFromStdin() {
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
    void testCutFromNullStream() {
        List<int[]> ranges = List.of(new int[]{1, 1});
        Throwable thrown = assertThrows(CutException.class, () -> {
            cutApp.cutFromStdin(false, true, ranges, null);
        });
        assertEquals(cutExceptionPrefix + ERR_NULL_STREAMS, thrown.getMessage());
    }
}
