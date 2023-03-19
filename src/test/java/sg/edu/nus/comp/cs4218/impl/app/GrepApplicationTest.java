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

public class GrepApplicationTest {
    private final GrepApplication grepApplication = new GrepApplication();
    private static final String FILE_NAME = "test.md";
    private static final String BEACON_TEXT1 = "Beacon looks like bacon,";
    private static final String BEACON_TEXT2 = "but beacon is not beacon";
    private static final String FILE_CONTENT = "In the deep expanse of the midnight sky," + System.lineSeparator() +
            "The stars above twinkle and shine." + System.lineSeparator() +
            "A cosmic dance that's never-ending," + System.lineSeparator() +
            "A symphony that's truly divine." + System.lineSeparator() +
            System.lineSeparator() +
            "Each star a miracle of light," + System.lineSeparator() +
            "A beacon of hope in the dark of night." + System.lineSeparator() +
            "A symbol of dreams and aspirations," + System.lineSeparator() +
            "A guide for explorations." + System.lineSeparator() +
            System.lineSeparator() +
            "They shine bright and steady," + System.lineSeparator() +
            "Like a promise of a future yet to be." + System.lineSeparator() +
            "A reminder of the infinite possibilities," + System.lineSeparator() +
            "And the beauty that's in our destiny." + System.lineSeparator() +
            System.lineSeparator() +
            "The stars above, a celestial wonder," + System.lineSeparator() +
            "A storybook of the universe to ponder." + System.lineSeparator() +
            "A timeless mystery that's forever told," + System.lineSeparator() +
            "A magic that never grows old." + System.lineSeparator() +
            System.lineSeparator() +
            "So when you look up at the sky," + System.lineSeparator() +
            "And see the stars shining high," + System.lineSeparator() +
            "Remember that they are there for you," + System.lineSeparator() +
            "Guiding your way, always true.";
    private static String fileName2 = "test2.md";
    private static String fileContent2 = "Twinkle twinkle little star, " + System.lineSeparator() +
            "How I wonder what you are." + System.lineSeparator() +
            "Up above the world so high, " + System.lineSeparator() +
            "Like a diamond in the sky.";
    @BeforeAll
    static void setUpTestFile() throws IOException {
        File testFile = new File(FILE_NAME);
        File testFile2 = new File(fileName2);
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
            writer2 = new FileWriter(fileName2);
            writer2.write(fileContent2);
        } catch (IOException ioException) {
            throw ioException;
        } finally {
            writer1.close();
            writer2.close();
        }
    }

    @AfterAll
    static void cleanUpFile() {
        File file = new File(FILE_NAME);
        File file2 = new File(fileName2);
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
        String correctResult = "A cosmic dance that's never-ending," + System.lineSeparator();
        assertEquals(correctResult, result);
    }

    @Test
    void grep_CaseInsensitiveFromFile_ShouldGrepCorrectly() throws Exception {
        String pattern = "and";
        String result = grepApplication.grepFromFiles(pattern, true, false, false, FILE_NAME);
        String correctResult = "The stars above twinkle and shine."  + System.lineSeparator() +
                "A symbol of dreams and aspirations," + System.lineSeparator() +
                "They shine bright and steady,"  + System.lineSeparator() +
                "And the beauty that's in our destiny." + System.lineSeparator() +
                "And see the stars shining high," + System.lineSeparator();
        assertEquals(correctResult, result);
    }

    @Test
    void grep_CountFromFile_ShouldGrepCorrectly() throws Exception {
        String pattern = "and";
        String result = grepApplication.grepFromFiles(pattern, false, true, false, FILE_NAME);
        String correctResult = "3" + System.lineSeparator();
        assertEquals(correctResult, result);
    }

    @Test
    void grep_WithPrefixFromFile_ShouldGrepCorrectly() throws Exception {
        String pattern = "and";
        String result = grepApplication.grepFromFiles(pattern, false, false, true, FILE_NAME);
        String correctResult = FILE_NAME + ": The stars above twinkle and shine." + System.lineSeparator() +
                FILE_NAME + ": A symbol of dreams and aspirations," + System.lineSeparator() +
                FILE_NAME + ": They shine bright and steady," + System.lineSeparator();
    }

    @Test
    void grep_NoTagFromMultipleFiles_ShouldGrepCorrectly() throws Exception {
        String pattern = "star";
        String result = grepApplication.grepFromFiles(pattern, false, false, false,
                FILE_NAME, fileName2);
        String correctResult = FILE_NAME + ": The stars above twinkle and shine." + System.lineSeparator() +
                FILE_NAME + ": Each star a miracle of light," + System.lineSeparator() +
                FILE_NAME + ": The stars above, a celestial wonder," + System.lineSeparator() +
                FILE_NAME + ": And see the stars shining high," + System.lineSeparator() +
                fileName2 + ": Twinkle twinkle little star, " + System.lineSeparator();
        assertEquals(correctResult, result);
    }

    @Test
    void grep_CountFromMultipleFiles_ShouldGrepCorrectly() throws Exception {
        String pattern = "star";
        String result = grepApplication.grepFromFiles(pattern, false, true, false,
                FILE_NAME, fileName2);
        String correctResult = FILE_NAME + ": 4" + System.lineSeparator() +
                fileName2 + ": 1" + System.lineSeparator();
        assertEquals(correctResult, result);
    }

    @Test
    void grep_NoTagFromStdin_ShouldGrepCorrectly() throws Exception  {
        String pattern = "pacon";
        String mockInput = "pacon looks like bacon";
        InputStream mockStd = new java.io.ByteArrayInputStream(mockInput.getBytes());
        String result = grepApplication.grepFromStdin(pattern, false, false, false, mockStd);
        String correctResult = mockInput + System.lineSeparator();
        assertEquals(correctResult, result);
    }

    @Test
    void grep_CaseInsensitiveFromStdin_ShouldGrepCorrectly() throws Exception  {
        String pattern = "ham";
        String mockInput = "ham looks like bacon," + System.lineSeparator()+"but ham is not bacon";
        InputStream mockStd = new java.io.ByteArrayInputStream(mockInput.getBytes());
        String result = grepApplication.grepFromStdin(pattern, true, false, false, mockStd);
        String correctResult = mockInput + System.lineSeparator();
        assertEquals(correctResult, result);
    }

    @Test
    void grep_CaseInsensitiveCountFromStdin_ShouldGrepCorrectly() throws Exception  {
        String pattern = "n";
        String mockInput = "Beaconn looks like bacon," + System.lineSeparator() + " but beacon is not beacon";
        InputStream mockStd = new java.io.ByteArrayInputStream(mockInput.getBytes());
        String result = grepApplication.grepFromStdin(pattern, true, true, false, mockStd);
        String correctResult = "2" + System.lineSeparator();
        assertEquals(correctResult, result);
    }

    @Test
    void grep_CaseInsensitiveCountWithPrefixFromStdin_ShouldGrepCorrectly() throws Exception  {
        String pattern = "ea";
        String mockInput = BEACON_TEXT1 + System.lineSeparator() + BEACON_TEXT2;
        InputStream mockStd = new java.io.ByteArrayInputStream(mockInput.getBytes());
        String result = grepApplication.grepFromStdin(pattern, true, true, true, mockStd);
        String correctResult = "standard input: " + "2";
        assertEquals(correctResult, result);
    }

    @Test
    void grep_NoTagFromFileAndStdin_ShouldGrepCorrectly() throws Exception  {
        String pattern = "beacon";
        String mockInput = BEACON_TEXT1 + System.lineSeparator() + BEACON_TEXT2;
        InputStream mockStd = new java.io.ByteArrayInputStream(mockInput.getBytes());
        String result = grepApplication.grepFromFileAndStdin(pattern, false, false, false,
                mockStd, FILE_NAME, "-");
        String correctResult = FILE_NAME + ": A beacon of hope in the dark of night." + System.lineSeparator() +
                "standard input: " + "but beacon is not beacon" + System.lineSeparator();
        assertEquals(correctResult, result);
    }

    @Test
    void grep_CountWithPrefixFromFileAndStdin_ShouldGrepCorrectly() throws Exception  {
        String pattern = "beacon";
        String mockInput = "Beaconnn looks like bacon," + System.lineSeparator()+ BEACON_TEXT2;
        InputStream mockStd = new java.io.ByteArrayInputStream(mockInput.getBytes());
        String result = grepApplication.grepFromFileAndStdin(pattern, false, true, true,
                mockStd, FILE_NAME, "-");
        String correctResult = FILE_NAME + ": 1" + System.lineSeparator() +
                "standard input: " + "1";
        assertEquals(correctResult, result);
    }

    @Test
    void grep_InvalidPatterFromFile_ShouldThrowException() throws Exception{
        String mockInput = BEACON_TEXT1 + System.lineSeparator() + BEACON_TEXT2;
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
