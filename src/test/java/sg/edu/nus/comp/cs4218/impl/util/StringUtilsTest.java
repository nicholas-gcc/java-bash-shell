package sg.edu.nus.comp.cs4218.impl.util;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class StringUtilsTest {

    static final String OS_PROPERTY_NAME = "os.name";
    static final String CURRENT_OS = System.getProperty(OS_PROPERTY_NAME);
    static final String NUM_TEXT_FILE = "123.txt";
    static final String ALP_RTF_FILE = "abc.rtf";
    static final String NUM_RTF_FILE = "123.rtf";

    @AfterEach
    void reset() {
        System.setProperty(OS_PROPERTY_NAME, CURRENT_OS);

    }

    @Test
    void fileSeparator_WindowsOS_ReturnsCorrectFileSeparator() {
        System.setProperty(OS_PROPERTY_NAME, "Windows");
        assertEquals("\\" + File.separator, StringUtils.fileSeparator());
    }

    @Test
    void fileSeparator_MacOS_ReturnsCorrectFileSeparator() {
        System.setProperty(OS_PROPERTY_NAME, "Mac");
        assertEquals(File.separator, StringUtils.fileSeparator());
    }

    @Test
    void fileSeparator_LinuxOS_ReturnsCorrectFileSeparator() {
        System.setProperty(OS_PROPERTY_NAME, "Linux");
        assertEquals(File.separator, StringUtils.fileSeparator());
    }

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
    void multiplyChar_ZeroAlp_ReturnsCorrectString() {
        assertEquals(StringUtils.multiplyChar('a', 0), "");
    }

    @Test
    void multiplyChar_OneAlp_ReturnsCorrectString() {
        assertEquals(StringUtils.multiplyChar('a', 1), "a");
    }

    @Test
    void multiplyChar_TenAlps_ReturnsCorrectString() {
        assertEquals(StringUtils.multiplyChar('a', 10), "aaaaaaaaaa");
    }

    @Test
    void multiplyChar_OneSymbol_ReturnsCorrectString() {
        assertEquals(StringUtils.multiplyChar('*', 1), "*");
    }

    @Test
    void multiplyChar_TenSymbol_ReturnsCorrectString() {
        assertEquals(StringUtils.multiplyChar('*', 10), "**********");
    }

    @Test
    void multiplyChar_FiveSpecialChar_ReturnsCorrectString() {
        assertEquals(StringUtils.multiplyChar('\\', 5), "\\\\\\\\\\");
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

    @Test
    void isNumber_StrWithOnlyNums_ReturnsTrue() {
        assertTrue(StringUtils.isNumber("012345"));
    }

    @Test
    void isNumber_EmptyStr_ReturnsFalse() {
        assertFalse(StringUtils.isNumber(""));
    }

    @Test
    void isNumber_StrWithNonNums_ReturnsFalse() {
        assertFalse(StringUtils.isNumber("abc*"));
    }

    @Test
    void isNumber_StrWithNumsAndNonNums_ReturnsFalse() {
        assertFalse(StringUtils.isNumber("a123"));
    }

    @Test
    void isNumber_DecimalNumStr_ReturnsFalse() {
        assertFalse(StringUtils.isNumber("1.1"));
    }

    @Test
    void sortFilenamesByExt_ListOfFilenames_FilesAreSortedByExtension() {
        List<String> fileNames = Arrays.asList(NUM_TEXT_FILE, ALP_RTF_FILE, NUM_RTF_FILE);
        List<String> expectedList = Arrays.asList(ALP_RTF_FILE, NUM_RTF_FILE, NUM_TEXT_FILE);
        StringUtils.sortFilenamesByExt(fileNames);
        assertEquals(expectedList, fileNames);
    }

    @Test
    void sortFilenamesByExt_ListOfFilenamesAndDir_FilesAreSortedByExtension() {
        List<String> fileNames = Arrays.asList(NUM_TEXT_FILE, ALP_RTF_FILE, "dir2", NUM_RTF_FILE, "dir1");
        List<String> expectedList = Arrays.asList("dir1", "dir2", ALP_RTF_FILE, NUM_RTF_FILE, NUM_TEXT_FILE);
        StringUtils.sortFilenamesByExt(fileNames);
        assertEquals(expectedList, fileNames);
    }
}
