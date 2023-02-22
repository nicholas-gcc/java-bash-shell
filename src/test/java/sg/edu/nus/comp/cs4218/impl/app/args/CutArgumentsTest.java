package sg.edu.nus.comp.cs4218.impl.app.args;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class CutArgumentsTest {
    private final CutArguments cutArgs = new CutArguments();

    @Test
    void testParse_NullArgs_ThrowsNullPtrException() {
        assertThrows(NullPointerException.class, () -> cutArgs.parse(null));
    }

    @Test
    void testParse_EmptyArgs_ThrowsIllegalArgException() {
        assertThrows(IllegalArgumentException.class, cutArgs::parse);
    }

    @Test
    void testParse_TooManyArgs_ThrowsIllegalArgException() {
        assertThrows(IllegalArgumentException.class, () -> cutArgs.parse("1", "2", "3"));
    }

    @Test
    void testParse_InvalidFlag_ThrowsIllegalArgException() {
        assertThrows(IllegalArgumentException.class, () -> cutArgs.parse("-a"));
    }

    @Test
    void testParse_InvalidRange_ThrowsIllegalArgException() {
        assertThrows(IllegalArgumentException.class, () -> cutArgs.parse("-c", "a"));
    }

    @Test
    void testParse_TooLittleArgs_ThrowsIllegalArgException() {
        assertThrows(IllegalArgumentException.class, () -> cutArgs.parse("-c"));
    }

    @Test
    void testParse_CutByCharSingleNumber_DoesNotThrowException() {
        assertDoesNotThrow(() -> cutArgs.parse("-c", "1"));
    }

    @Test
    void testParse_CutByByteSingleNumber_DoesNotThrowException() {
        assertDoesNotThrow(() -> cutArgs.parse("-b", "1"));
    }

    @Test
    void testParse_CutByCharRange_DoesNotThrowException() {
        assertDoesNotThrow(() -> cutArgs.parse("-c", "1-2"));
    }

    @Test
    void testParse_CutByByteRange_DoesNotThrowException() {
        assertDoesNotThrow(() -> cutArgs.parse("-b", "1-2"));
    }

}
