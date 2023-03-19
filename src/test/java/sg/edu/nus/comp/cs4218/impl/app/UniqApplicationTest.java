package sg.edu.nus.comp.cs4218.impl.app;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import sg.edu.nus.comp.cs4218.exception.AbstractApplicationException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.STRING_NEWLINE;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

public class UniqApplicationTest {

    private static final String HELLO_WORLD_STR = "Hello World";
    private static final String ALICE_STR = "Alice";
    private static final String BOB_STR = "Bob";

    UniqApplication uniqApplication = new UniqApplication();
    static String fileContent = HELLO_WORLD_STR + STRING_NEWLINE +
            HELLO_WORLD_STR + STRING_NEWLINE +
            HELLO_WORLD_STR + STRING_NEWLINE +
            HELLO_WORLD_STR + STRING_NEWLINE +
            HELLO_WORLD_STR + STRING_NEWLINE +
            ALICE_STR + STRING_NEWLINE +
            ALICE_STR + STRING_NEWLINE +
            BOB_STR + STRING_NEWLINE +
            ALICE_STR + STRING_NEWLINE +
            BOB_STR + STRING_NEWLINE +
            BOB_STR;
    String noTagResult = HELLO_WORLD_STR + STRING_NEWLINE +
            ALICE_STR + STRING_NEWLINE +
            BOB_STR + STRING_NEWLINE +
            ALICE_STR + STRING_NEWLINE +
            BOB_STR + STRING_NEWLINE;
    String countTagResult = "5 Hello World" + STRING_NEWLINE +
            "2 Alice" + STRING_NEWLINE +
            "1 Bob" + STRING_NEWLINE +
            "1 Alice" + STRING_NEWLINE +
            "2 Bob" + STRING_NEWLINE;

    String repeatedTagResult = HELLO_WORLD_STR + STRING_NEWLINE +
            ALICE_STR + STRING_NEWLINE +
            BOB_STR + STRING_NEWLINE;

    String allRepeatedResult = HELLO_WORLD_STR + STRING_NEWLINE +
            HELLO_WORLD_STR + STRING_NEWLINE +
            HELLO_WORLD_STR + STRING_NEWLINE +
            HELLO_WORLD_STR + STRING_NEWLINE +
            HELLO_WORLD_STR + STRING_NEWLINE +
            ALICE_STR + STRING_NEWLINE +
            ALICE_STR + STRING_NEWLINE +
            BOB_STR + STRING_NEWLINE +
            BOB_STR + STRING_NEWLINE;

    String countRepeatResult = "5 Hello World" + STRING_NEWLINE +
            "2 Alice" + STRING_NEWLINE +
            "2 Bob" + STRING_NEWLINE;

    String allTagsResult = "1 Hello World" + STRING_NEWLINE +
            "2 Hello World" + STRING_NEWLINE +
            "3 Hello World" + STRING_NEWLINE +
            "4 Hello World" + STRING_NEWLINE +
            "5 Hello World" + STRING_NEWLINE +
            "1 Alice" + STRING_NEWLINE +
            "2 Alice" + STRING_NEWLINE +
            "1 Bob" + STRING_NEWLINE +
            "2 Bob" + STRING_NEWLINE;
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
    void uniq_FromFileNoTag_ShouldCompleteProperly() throws Exception {
        assertEquals(noTagResult,
                uniqApplication.uniqFromFile(false, false, false,
                        inputFileName, outputFileName));
    }

    @Test
    void uniq_FromStdinNoTag_ShouldCompleteProperly() throws Exception {
        assertEquals(noTagResult,
                uniqApplication.uniqFromStdin(false, false, false,
                        mockStdIn, outputFileName));
    }

    @Test
    void uniq_FromFileTagCount_ShouldCompleteProperly() throws Exception {
        assertEquals(countTagResult,
                uniqApplication.uniqFromFile(true, false, false,
                        inputFileName, outputFileName));
    }

    @Test
    void uniq_FromStdinTagCount_ShouldCompleteProperly() throws Exception {
        assertEquals(countTagResult,
                uniqApplication.uniqFromStdin(true, false, false,
                        mockStdIn, outputFileName));
    }

    @Test
    void uniq_FromFileTagRepeated_ShouldCompleteProperly() throws Exception {
        assertEquals(repeatedTagResult,
                uniqApplication.uniqFromFile(false, true, false,
                        inputFileName, outputFileName));
    }

    @Test
    void uniq_FromStdinTagRepeated_ShouldCompleteProperly() throws Exception {
        assertEquals(repeatedTagResult,
                uniqApplication.uniqFromStdin(false, true, false,
                        mockStdIn, outputFileName));
    }
    @Test
    void uniq_FromFileTagAllRepeated_ShouldCompleteProperly() throws Exception {
        assertEquals(allRepeatedResult,
                uniqApplication.uniqFromFile(false, false, true,
                        inputFileName, outputFileName));
    }

    @Test
    void uniq_FromStdinTagAllRepeated_ShouldCompleteProperly() throws Exception {
        assertEquals(allRepeatedResult,
                uniqApplication.uniqFromStdin(false, false, true,
                        mockStdIn, outputFileName));
    }

    @Test
    void uniq_FromFileTagCountRepeated_ShouldCompleteProperly() throws Exception {
        assertEquals(countRepeatResult,
                uniqApplication.uniqFromFile(true, true, false,
                        inputFileName, outputFileName));
    }

    @Test
    void uniq_FromStdinTagCountRepeated_ShouldCompleteProperly() throws Exception {
        assertEquals(countRepeatResult,
                uniqApplication.uniqFromStdin(true, true, false,
                        mockStdIn, outputFileName));
    }

    @Test
    void uniq_FromFileAllTags_ShouldCompleteProperly() throws Exception {
        assertEquals(allTagsResult,
                uniqApplication.uniqFromFile(true, true, true,
                        inputFileName, outputFileName));
    }

    @Test
    void uniq_FromStdinAllTag_ShouldCompleteProperly() throws Exception {
        assertEquals(allTagsResult,
                uniqApplication.uniqFromStdin(true, true, true,
                        mockStdIn, outputFileName));
    }

    @Test
    void uniq_runWithArgsAllTags_ShouldCompleteProperly() throws AbstractApplicationException, IOException {
        uniqApplication.run(
                new String[]{"-c", "-d", "-D", inputFileName},
                mockStdIn,
                mockStdOut);
        assertEquals(allTagsResult, mockStdOut.toString());
        mockStdOut.close();

        uniqApplication.run(
                new String[]{"-c", "-d", "-D", inputFileName, outputFileName},
                mockStdIn,
                mockStdOut);
        assertEquals(allTagsResult, Files.readString(Path.of(outputFileName)));

    mockStdOut = new ByteArrayOutputStream();
        uniqApplication.run(
                new String[]{"-c", "-d", "-D"},
                mockStdIn,
                mockStdOut);
        assertEquals(allTagsResult, mockStdOut.toString());
        mockStdOut.close();
    }
}
