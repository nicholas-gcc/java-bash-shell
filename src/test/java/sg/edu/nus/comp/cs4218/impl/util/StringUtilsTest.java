package sg.edu.nus.comp.cs4218.impl.util;

import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class StringUtilsTest {
    @Test
    void isBlank_EmptyString_ReturnsTrue() {

        assertTrue(StringUtils.isBlank(""));
    }

    @Test
    void isBlank_NullValue_ReturnsTrue() {

        assertTrue(StringUtils.isBlank(null));
    }

    @Test
    void isBlank_WhiteSpacesOnlyString_ReturnsTrue() {

        assertTrue(StringUtils.isBlank("  "));
    }

    @Test
    void isBlank_NonEmptyString_ReturnsFalse() {
        assertFalse(StringUtils.isBlank("string"));
    }

    @Test
    void isBlank_NonEmptyStringWithWhiteSpaces_ReturnsFalse() {
        assertFalse(StringUtils.isBlank("I have whitespaces."));
    }

    @Test
    void tokenize_StringWithAllWhiteSpaces_TokensMatchesEmptyArray() {
        String toBeTokenize = "  ";
        String[] expectedTokens = new String[0];

        String[] actualTokens = StringUtils.tokenize(toBeTokenize);
        assertTrue(Arrays.equals(actualTokens, expectedTokens));
    }

    @Test
    void tokenize_StringWithNoWhiteSpaces_TokensMatchesExpectedTokens() {
        String toBeTokenize = "string";
        String[] expectedTokens = {"string"};

        String[] actualTokens = StringUtils.tokenize(toBeTokenize);
        assertTrue(Arrays.equals(actualTokens, expectedTokens));
    }

    @Test
    void tokenize_StringWithOneWhiteSpaces_TokensMatchesExpectedTokens() {
        String toBeTokenize = "string1 string2";
        String[] expectedTokens = {"string1", "string2"};

        String[] actualTokens = StringUtils.tokenize(toBeTokenize);
        assertTrue(Arrays.equals(actualTokens, expectedTokens));
    }

    @Test
    void tokenize_ComplexString_TokensMatchesExpectedTokens() {
        String toBeTokenize = " string1   string2 string3 string4  ";
        String[] expectedTokens = {"string1", "string2", "string3", "string4"};

        String[] actualTokens = StringUtils.tokenize(toBeTokenize);
        assertTrue(Arrays.equals(actualTokens, expectedTokens));
    }
}