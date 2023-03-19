package sg.edu.nus.comp.cs4218.impl.app;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.STRING_NEWLINE;

public class GrepApplicationTest {
    private final GrepApplication grepApplication = new GrepApplication();
    private static final String FILE_NAME = "test.md";
    private static final String BEACON_TEXT1 = "Beacon looks like bacon,";
    private static final String BEACON_TEXT2 = "but beacon is not beacon";
    private static final String FILE_CONTENT = "In the deep expanse of the midnight sky," + STRING_NEWLINE +
            "The stars above twinkle and shine." + STRING_NEWLINE +
            "A cosmic dance that's never-ending," + STRING_NEWLINE +
            "A symphony that's truly divine." + STRING_NEWLINE +
            STRING_NEWLINE +
            "Each star a miracle of light," + STRING_NEWLINE +
            "A beacon of hope in the dark of night." + STRING_NEWLINE +
            "A symbol of dreams and aspirations," + STRING_NEWLINE +
            "A guide for explorations." + STRING_NEWLINE +
            STRING_NEWLINE +
            "They shine bright and steady," + STRING_NEWLINE +
            "Like a promise of a future yet to be." + STRING_NEWLINE +
            "A reminder of the infinite possibilities," + STRING_NEWLINE +
            "And the beauty that's in our destiny." + STRING_NEWLINE +
            STRING_NEWLINE +
            "The stars above, a celestial wonder," + STRING_NEWLINE +
            "A storybook of the universe to ponder." + STRING_NEWLINE +
            "A timeless mystery that's forever told," + STRING_NEWLINE +
            "A magic that never grows old." + STRING_NEWLINE +
            STRING_NEWLINE +
            "So when you look up at the sky," + STRING_NEWLINE +
            "And see the stars shining high," + STRING_NEWLINE +
            "Remember that they are there for you," + STRING_NEWLINE +
            "Guiding your way, always true.";
    private static final String FILE_NAME_2 = "test2.md";
    private static final String FILE_CONTENT_2 = "Twinkle twinkle little star, " + STRING_NEWLINE +
            "How I wonder what you are." + STRING_NEWLINE +
            "Up above the world so high, " + STRING_NEWLINE +
            "Like a diamond in the sky.";
    @BeforeAll
    static void setUpTestFile() throws IOException {
        File testFile = new File(FILE_NAME);
        File testFile2 = new File(FILE_NAME_2);
        if (!testFile.exists()) {
            testFile.createNewFile();
        }
        if (!testFile2.exists()) {
            testFile2.createNewFile();
        }
        FileWriter writer1 = null;
        FileWriter writer2 = null;
        try {
            writer1 = new FileWriter(FILE_NAME);
            writer1.write(FILE_CONTENT);
            writer2 = new FileWriter(FILE_NAME_2);
            writer2.write(FILE_CONTENT_2);
        } finally {
            writer1.close();
            writer2.close();
        }
    }

    @AfterAll
    static void cleanUpFile() {
        File file = new File(FILE_NAME);
        File file2 = new File(FILE_NAME_2);
        if (file.exists()) {
            file.delete();
        }
        if (file2.exists()) {
            file2.delete();
        }
    }

    @Test
    void grep_NoTagFromFile_ShouldGrepCorrectly() throws Exception {
        String pattern = "cosmic";
        String result = grepApplication.grepFromFiles(pattern, false, false, false, FILE_NAME);
        String correctResult = "A cosmic dance that's never-ending," + STRING_NEWLINE;
        assertEquals(correctResult, result);
    }

    @Test
    void grep_CaseInsensitiveFromFile_ShouldGrepCorrectly() throws Exception {
        String pattern = "and";
        String result = grepApplication.grepFromFiles(pattern, true, false, false, FILE_NAME);
        String correctResult = "The stars above twinkle and shine."  + STRING_NEWLINE +
                "A symbol of dreams and aspirations," + STRING_NEWLINE +
                "They shine bright and steady,"  + STRING_NEWLINE +
                "And the beauty that's in our destiny." + STRING_NEWLINE +
                "And see the stars shining high," + STRING_NEWLINE;
        assertEquals(correctResult, result);
    }

    @Test
    void grep_CountFromFile_ShouldGrepCorrectly() throws Exception {
        String pattern = "and";
        String result = grepApplication.grepFromFiles(pattern, false, true, false, FILE_NAME);
        String correctResult = "3" + STRING_NEWLINE;
        assertEquals(correctResult, result);
    }

    @Test
    void grep_WithPrefixFromFile_ShouldGrepCorrectly() throws Exception {
        String pattern = "and";
        String result = grepApplication.grepFromFiles(pattern, false, false, true, FILE_NAME);
        String correctResult = FILE_NAME + ": The stars above twinkle and shine." + STRING_NEWLINE +
                FILE_NAME + ": A symbol of dreams and aspirations," + STRING_NEWLINE +
                FILE_NAME + ": They shine bright and steady," + STRING_NEWLINE;
    }

    @Test
    void grep_NoTagFromMultipleFiles_ShouldGrepCorrectly() throws Exception {
        String pattern = "star";
        String result = grepApplication.grepFromFiles(pattern, false, false, false,
                FILE_NAME, FILE_NAME_2);
        String correctResult = FILE_NAME + ": The stars above twinkle and shine." + STRING_NEWLINE +
                FILE_NAME + ": Each star a miracle of light," + STRING_NEWLINE +
                FILE_NAME + ": The stars above, a celestial wonder," + STRING_NEWLINE +
                FILE_NAME + ": And see the stars shining high," + STRING_NEWLINE +
                FILE_NAME_2 + ": Twinkle twinkle little star, " + STRING_NEWLINE;
        assertEquals(correctResult, result);
    }

    @Test
    void grep_CountFromMultipleFiles_ShouldGrepCorrectly() throws Exception {
        String pattern = "star";
        String result = grepApplication.grepFromFiles(pattern, false, true, false,
                FILE_NAME, FILE_NAME_2);
        String correctResult = FILE_NAME + ": 4" + STRING_NEWLINE +
                FILE_NAME_2 + ": 1" + STRING_NEWLINE;
        assertEquals(correctResult, result);
    }

    @Test
    void grep_NoTagFromStdin_ShouldGrepCorrectly() throws Exception  {
        String pattern = "pacon";
        String mockInput = "pacon looks like bacon";
        InputStream mockStd = new java.io.ByteArrayInputStream(mockInput.getBytes());
        String result = grepApplication.grepFromStdin(pattern, false, false, false, mockStd);
        String correctResult = mockInput + STRING_NEWLINE;
        assertEquals(correctResult, result);
    }

    @Test
    void grep_CaseInsensitiveFromStdin_ShouldGrepCorrectly() throws Exception  {
        String pattern = "ham";
        String mockInput = "ham looks like bacon," + STRING_NEWLINE+"but ham is not bacon";
        InputStream mockStd = new java.io.ByteArrayInputStream(mockInput.getBytes());
        String result = grepApplication.grepFromStdin(pattern, true, false, false, mockStd);
        String correctResult = mockInput + STRING_NEWLINE;
        assertEquals(correctResult, result);
    }

    @Test
    void grep_CaseInsensitiveCountFromStdin_ShouldGrepCorrectly() throws Exception  {
        String pattern = "n";
        String mockInput = "Beaconn looks like bacon," + STRING_NEWLINE + " but beacon is not beacon";
        InputStream mockStd = new java.io.ByteArrayInputStream(mockInput.getBytes());
        String result = grepApplication.grepFromStdin(pattern, true, true, false, mockStd);
        String correctResult = "2" + STRING_NEWLINE;
        assertEquals(correctResult, result);
    }

    @Test
    void grep_CaseInsensitiveCountWithPrefixFromStdin_ShouldGrepCorrectly() throws Exception  {
        String pattern = "ea";
        String mockInput = BEACON_TEXT1 + STRING_NEWLINE + BEACON_TEXT2;
        InputStream mockStd = new java.io.ByteArrayInputStream(mockInput.getBytes());
        String result = grepApplication.grepFromStdin(pattern, true, true, true, mockStd);
        String correctResult = "standard input: " + "2";
        assertEquals(correctResult, result);
    }

    @Test
    void grep_NoTagFromFileAndStdin_ShouldGrepCorrectly() throws Exception  {
        String pattern = "beacon";
        String mockInput = BEACON_TEXT1 + STRING_NEWLINE + BEACON_TEXT2;
        InputStream mockStd = new java.io.ByteArrayInputStream(mockInput.getBytes());
        String result = grepApplication.grepFromFileAndStdin(pattern, false, false, false,
                mockStd, FILE_NAME, "-");
        String correctResult = FILE_NAME + ": A beacon of hope in the dark of night." + STRING_NEWLINE +
                "standard input: " + "but beacon is not beacon" + STRING_NEWLINE;
        assertEquals(correctResult, result);
    }

    @Test
    void grep_CountWithPrefixFromFileAndStdin_ShouldGrepCorrectly() throws Exception  {
        String pattern = "beacon";
        String mockInput = "Beaconnn looks like bacon," + STRING_NEWLINE+ BEACON_TEXT2;
        InputStream mockStd = new java.io.ByteArrayInputStream(mockInput.getBytes());
        String result = grepApplication.grepFromFileAndStdin(pattern, false, true, true,
                mockStd, FILE_NAME, "-");
        String correctResult = FILE_NAME + ": 1" + STRING_NEWLINE +
                "standard input: " + "1";
        assertEquals(correctResult, result);
    }

    @Test
    void grep_InvalidPatterFromFile_ShouldThrowException() throws Exception{
        String mockInput = BEACON_TEXT1 + STRING_NEWLINE + BEACON_TEXT2;
        InputStream mockStd = new java.io.ByteArrayInputStream(mockInput.getBytes());
        OutputStream mockOut = new ByteArrayOutputStream();

        String[] args = {"([abc", FILE_NAME};
        assertThrows(Exception.class, () -> grepApplication.run(args, mockStd, mockOut ));

        args[0] = "";
        assertThrows(Exception.class, () -> grepApplication.run(args, mockStd, mockOut ));

        args[0] = null;
        assertThrows(Exception.class, () -> grepApplication.run(args, mockStd, mockOut ));
    }
}
