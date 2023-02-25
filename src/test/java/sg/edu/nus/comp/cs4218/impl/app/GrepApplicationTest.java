package sg.edu.nus.comp.cs4218.impl.app;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.*;

public class GrepApplicationTest {
    private GrepApplication grepApplication = new GrepApplication();
    private static final String fileName = "test.md";
    private static final String fileConent = "In the deep expanse of the midnight sky,\n" +
            "The stars above twinkle and shine.\n" +
            "A cosmic dance that's never-ending,\n" +
            "A symphony that's truly divine.\n" +
            "\n" +
            "Each star a miracle of light,\n" +
            "A beacon of hope in the dark of night.\n" +
            "A symbol of dreams and aspirations,\n" +
            "A guide for explorations.\n" +
            "\n" +
            "They shine bright and steady,\n" +
            "Like a promise of a future yet to be.\n" +
            "A reminder of the infinite possibilities,\n" +
            "And the beauty that's in our destiny.\n" +
            "\n" +
            "The stars above, a celestial wonder,\n" +
            "A storybook of the universe to ponder.\n" +
            "A timeless mystery that's forever told,\n" +
            "A magic that never grows old.\n" +
            "\n" +
            "So when you look up at the sky,\n" +
            "And see the stars shining high,\n" +
            "Remember that they are there for you,\n" +
            "Guiding your way, always true.";
    private static final String fileName_2 = "test2.md";
    private static final String fileContent_2 = "Twinkle twinkle little star, \n" + "How I wonder what you are.\n" +
            "Up above the world so high, \n" + "Like a diamond in the sky.";
    @BeforeAll
    static void setUpTestFile() throws IOException {
        File testFile = new File(fileName);
        File testFile2 = new File(fileName_2);
        if (!testFile.exists()) {
            testFile.createNewFile();
        }
        if (!testFile2.exists()) {
            testFile2.createNewFile();
        }
        FileWriter writer1 = null;
        FileWriter writer2 = null;
        try {
            writer1 = new FileWriter(fileName);
            writer1.write(fileConent);
            writer2 = new FileWriter(fileName_2);
            writer2.write(fileContent_2);
        } catch (IOException ioException) {
            throw ioException;
        } finally {
            writer1.close();
            writer2.close();
        }
    }

    @AfterAll
    static void cleanUpFile() {
        File file = new File(fileName);
        if (file.exists()) {
            file.delete();
        }
    }

    @Test
    void grep_NoTagFromFile_ShouldGrepCorrectly() throws Exception {
        String pattern = "cosmic";
        String result = grepApplication.grepFromFiles(pattern, false, false, false, fileName);
        String correctResult = "A cosmic dance that's never-ending,\n";
        assertEquals(correctResult, result);
    }

    @Test
    void grep_CaseInsensitiveFromFile_ShouldGrepCorrectly() throws Exception {
        String pattern = "and";
        String result = grepApplication.grepFromFiles(pattern, true, false, false, fileName);
        String correctResult = "The stars above twinkle and shine.\n" + "A symbol of dreams and aspirations,\n" + "They shine bright and steady,\n" +
                "And the beauty that's in our destiny.\n" + "And see the stars shining high,\n";
        assertEquals(correctResult, result);
    }

    @Test
    void grep_CountFromFile_ShouldGrepCorrectly() throws Exception {
        String pattern = "and";
        String result = grepApplication.grepFromFiles(pattern, false, true, false, fileName);
        String correctResult = "3\n";
        assertEquals(correctResult, result);
    }

    @Test
    void grep_WithPrefixFromFile_ShouldGrepCorrectly() throws Exception {
        String pattern = "and";
        String result = grepApplication.grepFromFiles(pattern, false, false, true, fileName);
        String correctResult = fileName + ": The stars above twinkle and shine.\n" +
                fileName + ": A symbol of dreams and aspirations,\n" +
                fileName + ": They shine bright and steady,\n";
    }

    @Test
    void grep_NoTagFromMultipleFiles_ShouldGrepCorrectly() throws Exception {
        String pattern = "star";
        String result = grepApplication.grepFromFiles(pattern, false, false, false,
                fileName, fileName_2);
        String correctResult = fileName + ": The stars above twinkle and shine.\n" +
                fileName + ": Each star a miracle of light,\n" +
                fileName + ": The stars above, a celestial wonder,\n" +
                fileName + ": And see the stars shining high,\n" +
                fileName_2 + ": Twinkle twinkle little star, \n";
        assertEquals(correctResult, result);
    }

    @Test
    void grep_CountFromMultipleFiles_ShouldGrepCorrectly() throws Exception {
        String pattern = "star";
        String result = grepApplication.grepFromFiles(pattern, false, true, false,
                fileName, fileName_2);
        String correctResult = fileName + ": 4\n" +
                fileName_2 + ": 1\n";
        assertEquals(correctResult, result);
    }

    @Test
    void grep_NoTagFromStdin_ShouldGrepCorrectly() throws Exception  {
        String pattern = "beacon";
        String mockInput = "beacon looks like bacon";
        InputStream mockStd = new java.io.ByteArrayInputStream(mockInput.getBytes());
        String result = grepApplication.grepFromStdin(pattern, false, false, false, mockStd);
        String correctResult = "beacon looks like bacon\n";
        assertEquals(correctResult, result);
    }
}
