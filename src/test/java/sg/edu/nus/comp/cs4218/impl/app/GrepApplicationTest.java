package sg.edu.nus.comp.cs4218.impl.app;

import org.junit.jupiter.api.BeforeAll;

import java.io.File;
import java.io.IOException;

public class GrepApplicationTest {
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
    @BeforeAll
    void setUpTestFile() throws IOException {
        File testFile = new File(fileName);
        if (!testFile.exists()) {
            testFile.createNewFile();
        }
        
    }
}
