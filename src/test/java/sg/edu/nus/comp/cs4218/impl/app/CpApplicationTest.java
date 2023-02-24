package sg.edu.nus.comp.cs4218.impl.app;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sg.edu.nus.comp.cs4218.impl.exception.CpException;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

public class CpApplicationTest {
    CpApplication cpApplication;

    static String inTestFileName = "input_test.txt";
    static String outTestFileName = "output_test.txt";
    static String inTestDirectory = "input";
    static String outTestDirectory = "output";
    static String fileContent = "Snowflakes fall gently from the sky,\n" +
            "Blanketing the world in white.\n" +
            "A winter wonderland appears,\n" +
            "A magical and peaceful sight.\n" +
            "\n" +
            "The air is crisp, the ground is still,\n" +
            "As snowflakes dance in the chill.\n" +
            "The trees are dressed in snowy lace,\n" +
            "A serene and wondrous place.\n" +
            "\n" +
            "Children play and build snowmen,\n" +
            "Sledding down the hills again and again.\n" +
            "Families gather by the fire,\n" +
            "Enjoying warmth and holiday cheer.\n" +
            "\n" +
            "Oh, snow, you bring us joy and peace,\n" +
            "A blanket of calm in winter's freeze.\n" +
            "A wonder of nature, a sight to see,\n" +
            "Snowflakes falling, free and light as can be.";

    @BeforeAll
    static void populateFiles() throws IOException {
        File inputTestFile = new File (inTestFileName);

        if(!inputTestFile.exists()) {
            inputTestFile.createNewFile();
            FileWriter inputWriter = null;
            try {
                inputWriter = new FileWriter(inTestFileName);
                inputWriter.write(fileContent);
            } catch (IOException ioException) {
                throw ioException;
            } finally {
                inputWriter.close();
            }
        }

        File inDir = new File(inTestDirectory);
        if (!inDir.exists()) {
            inDir.mkdir();
        }

        String dirFileName = inTestDirectory + "/" + inTestFileName;
        File dirFile =  new File(dirFileName);

        if(!dirFile.exists()) {
            dirFile.createNewFile();
            FileWriter inputWriter = null;
            try {
                inputWriter = new FileWriter(dirFileName);
                inputWriter.write(fileContent);
            } catch (IOException ioException) {
                throw ioException;
            } finally {
                inputWriter.close();
            }
        }
    }

    @AfterAll
    static void deleteFiles() {
        File inTestFile = new File(inTestFileName);
        File dirTestFile = new File(inTestDirectory + "/" + inTestFileName);
        File outTestFile = new File(outTestFileName);
        File dirOutFile = new File(outTestDirectory);
        File inDir = new File(inTestDirectory);
        File outDir = new File(outTestDirectory);

        if (inTestFile.exists()) {
            inTestFile.delete();
        }
        if (dirTestFile.exists()) {
            dirTestFile.delete();
        }
        if (outTestFile.exists()) {
            outTestFile.delete();
        }
        if (dirOutFile.exists()) {
            dirOutFile.delete();
        }
        if (inDir.exists()) {
            inDir.delete();
        }
        if (outDir.exists()) {
            outDir.delete();
        }
    }

    @BeforeEach
    void setup() {
        cpApplication = new CpApplication();

    }

    @Test
    void cp_FileToUnexistingFile_ShouldCpCorrectly() throws CpException {
        cpApplication.run
        String a = "1";
        assertEquals("1", a);
    }
}
