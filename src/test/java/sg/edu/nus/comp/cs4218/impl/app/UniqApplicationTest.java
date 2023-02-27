package sg.edu.nus.comp.cs4218.impl.app;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

public class UniqApplicationTest {
    UniqApplication uniqApplication = new UniqApplication();
    static String fileContent = "Hello World" + System.lineSeparator() + //NOPMD - suppressed AvoidDuplicateLiterals -
                                                                         // repetition is necessary to testing
            "Hello World" + System.lineSeparator() +
            "Hello World" + System.lineSeparator() +
            "Hello World" + System.lineSeparator() +
            "Hello World" + System.lineSeparator() +
            "Alice" + System.lineSeparator() + //NOPMD - suppressed AvoidDuplicateLiterals - repetition is necessary to testing
            "Alice" + System.lineSeparator() + //NOPMD - suppressed AvoidDuplicateLiterals - repetition is necessary to testing
            "Bob" + System.lineSeparator() + //NOPMD - suppressed AvoidDuplicateLiterals - repetition is necessary to testing
            "Alice" + System.lineSeparator() +
            "Bob" + System.lineSeparator() +
            "Bob";
    String noTagResult = "Hello World" + System.lineSeparator() +
            "Alice" + System.lineSeparator() +
            "Bob" + System.lineSeparator() +
            "Alice" + System.lineSeparator() +
            "Bob" + System.lineSeparator();
    String countTagResult = "5 Hello World" + System.lineSeparator() +
            "2 Alice" + System.lineSeparator() +
            "1 Bob" + System.lineSeparator() +
            "1 Alice" + System.lineSeparator() +
            "2 Bob" + System.lineSeparator();

    String repeatedTagResult = "Hello World" + System.lineSeparator() +
            "Alice" + System.lineSeparator() +
            "Bob" + System.lineSeparator();

    String allRepeatedResult = "Hello World" + System.lineSeparator() +
            "Hello World" + System.lineSeparator() +
            "Hello World" + System.lineSeparator() +
            "Hello World" + System.lineSeparator() +
            "Hello World" + System.lineSeparator() +
            "Alice" + System.lineSeparator() +
            "Alice" + System.lineSeparator() +
            "Bob" + System.lineSeparator() +
            "Bob" + System.lineSeparator();

    String countRepeatResult = "5 Hello World" + System.lineSeparator() +
            "2 Alice" + System.lineSeparator() +
            "2 Bob" + System.lineSeparator();

    String allTagsResult = "1 Hello World" + System.lineSeparator() +
            "2 Hello World" + System.lineSeparator() +
            "3 Hello World" + System.lineSeparator() +
            "4 Hello World" + System.lineSeparator() +
            "5 Hello World" + System.lineSeparator() +
            "1 Alice" + System.lineSeparator() +
            "2 Alice" + System.lineSeparator() +
            "1 Bob" + System.lineSeparator() +
            "2 Bob" + System.lineSeparator();
    InputStream mockStdIn = new java.io.ByteArrayInputStream(fileContent.getBytes());
    ByteArrayOutputStream mockStdOut = new ByteArrayOutputStream();
    static String inputFileName = "test.txt";
    static String outputFileName = "testOut.txt";

    @BeforeAll
    static void setUpFile() throws IOException {
        File inputFile = new File(inputFileName);
        if(!inputFile.exists()) {
            inputFile.createNewFile();
            FileWriter inputWriter = null;
            try {
                inputWriter = new FileWriter(inputFile);
                inputWriter.write(fileContent);
            } catch (IOException ioException) {
                throw ioException;
            } finally {
                inputWriter.close();
            }
        }
    }

    @AfterAll
    static void cleanUpFile() {
        File inputFile = new File(inputFileName);
        File outputFile = new File(outputFileName);
        if (inputFile.exists()) {
            inputFile.delete();
        }
        if (outputFile.exists()) {
            outputFile.delete();
        }
    }

    @Test
    void uniq_FromFileNoTag_ShouldCompleteProperly() {
        assertDoesNotThrow(() -> {
            assertEquals(noTagResult,
                    uniqApplication.uniqFromFile(false, false, false,
                            inputFileName, outputFileName));
        });
    }

    @Test
    void uniq_FromStdinNoTag_ShouldCompleteProperly() {
        assertDoesNotThrow(() -> {
            assertEquals(noTagResult,
                    uniqApplication.uniqFromStdin(false, false, false,
                            mockStdIn, outputFileName));
        });
    }

    @Test
    void uniq_FromFileTagCount_ShouldCompleteProperly() {
        assertDoesNotThrow(() -> {
            assertEquals(countTagResult,
                    uniqApplication.uniqFromFile(true, false, false,
                            inputFileName, outputFileName));
        });
    }

    @Test
    void uniq_FromStdinTagCount_ShouldCompleteProperly() {
        assertDoesNotThrow(() -> {
            assertEquals(countTagResult,
                    uniqApplication.uniqFromStdin(true, false, false,
                            mockStdIn, outputFileName));
        });
    }

    @Test
    void uniq_FromFileTagRepeated_ShouldCompleteProperly() {
        assertDoesNotThrow(() -> {
            assertEquals(repeatedTagResult,
                    uniqApplication.uniqFromFile(false, true, false,
                            inputFileName, outputFileName));
        });
    }

    @Test
    void uniq_FromStdinTagRepeated_ShouldCompleteProperly() {
        assertDoesNotThrow(() -> {
            assertEquals(repeatedTagResult,
                    uniqApplication.uniqFromStdin(false, true, false,
                            mockStdIn, outputFileName));
        });
    }
    @Test
    void uniq_FromFileTagAllRepeated_ShouldCompleteProperly() {
        assertDoesNotThrow(() -> {
            assertEquals(allRepeatedResult,
                    uniqApplication.uniqFromFile(false, false, true,
                            inputFileName, outputFileName));
        });
    }

    @Test
    void uniq_FromStdinTagAllRepeated_ShouldCompleteProperly() {
        assertDoesNotThrow(() -> {
            assertEquals(allRepeatedResult,
                    uniqApplication.uniqFromStdin(false, false, true,
                            mockStdIn, outputFileName));
        });
    }

    @Test
    void uniq_FromFileTagCountRepeated_ShouldCompleteProperly() {
        assertDoesNotThrow(() -> {
            assertEquals(countRepeatResult,
                    uniqApplication.uniqFromFile(true, true, false,
                            inputFileName, outputFileName));
        });
    }

    @Test
    void uniq_FromStdinTagCountRepeated_ShouldCompleteProperly() {
        assertDoesNotThrow(() -> {
            assertEquals(countRepeatResult,
                    uniqApplication.uniqFromStdin(true, true, false,
                            mockStdIn, outputFileName));
        });
    }

    @Test
    void uniq_FromFileAllTags_ShouldCompleteProperly() {
        assertDoesNotThrow(() -> {
            assertEquals(allTagsResult,
                    uniqApplication.uniqFromFile(true, true, true,
                            inputFileName, outputFileName));
        });
    }

    @Test
    void uniq_FromStdinAllTag_ShouldCompleteProperly() {
        assertDoesNotThrow(() -> {
            assertEquals(allTagsResult,
                    uniqApplication.uniqFromStdin(true, true, true,
                            mockStdIn, outputFileName));
        });
    }

    @Test
    void uniq_runWithArgsAllTags_ShouldCompleteProperly() {
        assertDoesNotThrow(() -> {
            uniqApplication.run(
                    new String[]{"-c", "-d", "-D", inputFileName},
                    mockStdIn,
                    mockStdOut);
            assertEquals(allTagsResult, new String(mockStdOut.toByteArray()));
            mockStdOut.close();
        });

        assertDoesNotThrow(() -> {
            uniqApplication.run(
                    new String[]{"-c", "-d", "-D", inputFileName, outputFileName},
                    mockStdIn,
                    mockStdOut);
            assertEquals(allTagsResult, Files.readString(Path.of(outputFileName)));
        });

        mockStdOut = new ByteArrayOutputStream();
        assertDoesNotThrow(() -> {
            uniqApplication.run(
                    new String[]{"-c", "-d", "-D"},
                    mockStdIn,
                    mockStdOut);
            assertEquals(allTagsResult, new String(mockStdOut.toByteArray()));
            mockStdOut.close();
        });
    }
}
