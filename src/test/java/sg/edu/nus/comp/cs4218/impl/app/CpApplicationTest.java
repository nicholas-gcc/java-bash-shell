package sg.edu.nus.comp.cs4218.impl.app;

import org.junit.jupiter.api.*;
import sg.edu.nus.comp.cs4218.impl.exception.CpException;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

public class CpApplicationTest {
    CpApplication cpApplication;

    static String inTestFileName = "input_test.txt";
    static String outTestFileName = "output_test.txt";
    static String inTestDirectory = "input";
    static String outTestDirectory = "output";
    static String fileContent = "Snowflakes fall gently from the sky," + System.lineSeparator() +
            "Blanketing the world in white." + System.lineSeparator() +
            "A winter wonderland appears," + System.lineSeparator() +
            "A magical and peaceful sight." + System.lineSeparator() +
            System.lineSeparator() +
            "The air is crisp, the ground is still," + System.lineSeparator() +
            "As snowflakes dance in the chill." + System.lineSeparator() +
            "The trees are dressed in snowy lace," + System.lineSeparator() +
            "A serene and wondrous place." + System.lineSeparator() +
            System.lineSeparator() +
            "Children play and build snowmen," + System.lineSeparator() +
            "Sledding down the hills again and again." + System.lineSeparator() +
            "Families gather by the fire," + System.lineSeparator() +
            "Enjoying warmth and holiday cheer." + System.lineSeparator() +
            System.lineSeparator() +
            "Oh, snow, you bring us joy and peace," + System.lineSeparator() +
            "A blanket of calm in winter's freeze." + System.lineSeparator() +
            "A wonder of nature, a sight to see," + System.lineSeparator() +
            "Snowflakes falling, free and light as can be.";

    boolean isContentEqual (String path1, String path2) throws IOException {
        Path file1 = new File(path1).toPath();
        Path file2 = new File(path2).toPath();

        byte[] first = Files.readAllBytes(file1);
        byte[] second = Files.readAllBytes(file1);
        return Arrays.equals(first, second);
    }
    void rewriteFileContent() throws IOException {
        File outFile = new File(outTestFileName);
        if (outFile.isFile()) {
            FileWriter inputWriter = null;
            try {
                inputWriter = new FileWriter(outTestFileName);
                inputWriter.write("new content");
            } catch (IOException ioException) {
                throw ioException;
            } finally {
                inputWriter.close();
            }
        }
    }
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
        File dirOutFile1 = new File(outTestDirectory + "/" + outTestFileName);
        File dirOutFile2 = new File(outTestDirectory + "/" + inTestFileName);
        File dirOutDirFile = new File(outTestDirectory + "/" + inTestDirectory + "/" + inTestFileName);
        File dirOutDir = new File(outTestDirectory + "/" + inTestDirectory);
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
        if (dirOutFile1.exists()) {
            dirOutFile1.delete();
        }
        if (dirOutFile2.exists()) {
            dirOutFile2.delete();
        }
        if (dirOutDirFile.exists()) {
            dirOutDirFile.delete();
        }
        if (dirOutDir.exists()) {
            dirOutDir.delete();
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
    void cp_FileToNonExistingFile_ShouldCpCorrectly() {
        assertDoesNotThrow(() -> {
            cpApplication.cpSrcFileToDestFile(false, inTestFileName, outTestFileName);
        });
        assertDoesNotThrow(() -> {
            assertTrue(isContentEqual(inTestFileName, outTestFileName));
        });

    }

    @Test
    void cp_FileToExistingFile_ShouldCpCorrectly() throws IOException {
        File outFile = new File(outTestFileName);
        assertTrue(outFile.isFile());
        rewriteFileContent();

        assertDoesNotThrow(() -> {
            cpApplication.cpSrcFileToDestFile(false, inTestFileName, outTestFileName);
            assertTrue(isContentEqual(inTestFileName, outTestFileName));
        });

    }

    @Test
    void cp_FileToExistingFolder_ShouldCpCorrectly() {
        File outDir = new File(outTestDirectory);
        if (!outDir.exists()) {
            outDir.mkdir();
        }
        assertTrue(outDir.exists());

        assertDoesNotThrow(() -> {
            cpApplication.cpFilesToFolder(false, outTestDirectory, inTestFileName);
            assertTrue(isContentEqual(outTestDirectory + "/" + inTestFileName, inTestFileName));
        });


        File file = new File(outTestDirectory + "/" + inTestFileName);
        file.delete();
        assertTrue(!file.exists());
        outDir.delete();
        assertTrue(!outDir.exists());
    }

    @Test
    void cp_FolderToExistingFolder_ShouldCpCorrectly() {
        File outDir = new File(outTestDirectory);
        if (!outDir.exists()) {
            outDir.mkdir();
        }
        assertTrue(outDir.exists());

        assertDoesNotThrow(() -> {
            cpApplication.cpFilesToFolder(true, outTestDirectory, inTestDirectory);
        });

        File outDirInDir = new File(outTestDirectory + "/" + inTestDirectory);
        assertTrue(outDirInDir.isDirectory());
        File outDirInDirInFile = new File(outTestDirectory + "/" + inTestDirectory + "/" + inTestFileName);
        assertTrue(outDirInDirInFile.isFile());
        assertDoesNotThrow(()->{
            assertTrue(isContentEqual(inTestDirectory + "/" + inTestFileName, outDirInDirInFile.getPath()));
        });
        outDirInDirInFile.delete();
        assertTrue(!outDirInDirInFile.exists());
        outDirInDir.delete();
        assertTrue(!outDirInDir.exists());
        outDir.delete();
        assertTrue(!outDir.exists());
    }

    @Test
    void cp_FolderToNonExistingFolderRecursively_ShouldCpCorrectly() {
        File outDir = new File(outTestDirectory);
        if(outDir.exists()) {
            outDir.delete();
        }
        assertTrue(!outDir.exists());

        assertDoesNotThrow(() -> {
            cpApplication.cpFilesToFolder(true, outTestDirectory, inTestDirectory);
        });

        File outDirInDir = new File(outTestDirectory + "/" + inTestDirectory);
        assertTrue(outDirInDir.isDirectory());
        File outDirInDirInFile = new File(outTestDirectory + "/" + inTestDirectory + "/" + inTestFileName);
        assertTrue(outDirInDirInFile.isFile());
        assertDoesNotThrow(() -> {
            assertTrue(isContentEqual(inTestDirectory + "/" + inTestFileName, outDirInDirInFile.getPath()));
        });
        outDirInDirInFile.delete();
        assertTrue(!outDirInDirInFile.exists());
        outDirInDir.delete();
        assertTrue(!outDirInDir.exists());
        outDir.delete();
        assertTrue(!outDir.exists());
    }

    @Test
    void cp_FolderToNonExistingFolderNonRecursively_ShouldThrowCpException() {
        File outDir = new File(outTestDirectory);
        if(outDir.exists()) {
            outDir.delete();
        }
        assertTrue(!outDir.exists());

        assertThrows(CpException.class, () -> {
            cpApplication.cpFilesToFolder(false, outTestDirectory, inTestDirectory);
        });
    }

    @Test
    void cp_FolderToExistingFile_ShouldThrowCpException() throws IOException {
        File outFile = new File(outTestFileName);
        if(!outFile.exists()) {
            outFile.createNewFile();
        }
        assertTrue(outFile.exists());

        assertThrows(CpException.class, () -> {
            cpApplication.cpFilesToFolder(true, outTestFileName, inTestDirectory);
        });
    }

    @Test
    void cp_wildcardToFolder_ShouldCpCorrectly (){
        String wildcard = inTestDirectory + "/*.txt";

        assertDoesNotThrow(() -> {
            cpApplication.cpFilesToFolder(true, outTestDirectory, wildcard);
        });
    }

}