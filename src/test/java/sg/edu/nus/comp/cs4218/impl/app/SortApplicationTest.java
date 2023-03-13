package sg.edu.nus.comp.cs4218.impl.app;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sg.edu.nus.comp.cs4218.exception.SortException;


import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SortApplicationTest {

    private SortApplication sortApp;
    private Path tempDir;

    @BeforeEach
    void setUp() throws IOException {
        sortApp = new SortApplication();
        tempDir = Files.createTempDirectory("sort-application-test");
    }

    @Test
    void sortFromFiles_ValidTextFile_CorrectOutput() throws Exception {
        String input = "abc\nzzz\nccc\nbbb\naaa";
        String expectedOutput = "aaa\nabc\nbbb\nccc\nzzz";
        Path inputFile = createTempFileWithContent(input);
        String[] fileNames = new String[]{inputFile.toString()};

        String result = sortApp.sortFromFiles(false, false, false, fileNames);

        assertEquals(expectedOutput, result);
    }

    @Test
    void sortFromFiles_InvalidFile_ShouldThrow() {
        String[] fileNames = new String[]{"invalid-file"};

        assertThrows(SortException.class, () -> {
            sortApp.sortFromFiles(false, false, false, fileNames);
        });
    }

    @Test
    void sortFromFiles_InputIsDirectory_ShouldThrow() {
        String[] fileNames = new String[]{tempDir.toString()};

        assertThrows(SortException.class, () -> {
            sortApp.sortFromFiles(false, false, false, fileNames);
        });
    }

    @Test
    void sortFromStdin_ValidInput_CorrectOutput() throws Exception {
        String input = "abc\nzzz\nccc\nbbb\naaa";
        String expectedOutput = "aaa\nabc\nbbb\nccc\nzzz";
        InputStream inputStream = new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8));

        String result = sortApp.sortFromStdin(false, false, false, inputStream);

        assertEquals(expectedOutput, result);
    }

    @Test
    void sortFromStdin_ValidInputReverse_CorrectOutput() throws Exception {
        String input = "abc\nzzz\nccc\nbbb\naaa";
        String expectedOutput = "zzz\nccc\nbbb\nabc\naaa";
        InputStream inputStream = new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8));

        String result = sortApp.sortFromStdin(false, true, false, inputStream);

        assertEquals(expectedOutput, result);
    }

    @Test
    void sortFromStdin_EmptyInput_CorrectOutput() throws Exception {
        String input = "";
        InputStream inputStream = new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8));

        String result = sortApp.sortFromStdin(false, false, false, inputStream);

        assertEquals("", result);
    }

    @Test
    void sortFromStdin_NullInput_ShouldThrow() {
        assertThrows(SortException.class, () -> {
            sortApp.sortFromStdin(false, false, false, null);
        });
    }

    @Test
    void run_ValidInputFilesAndOutputStream_CorrectOutput() throws Exception {
        // should combine the items of two files and sort them after merging
        String input1 = "abc\nzzz\nccc\nbbb\naaa";
        String input2 = "def\nefg\nbcd\naaa";
        String expectedOutput = "aaa\naaa\nabc\nbbb\nbcd\nccc\ndef\nefg\nzzz";

        // create the files
        InputStream inputStream = new ByteArrayInputStream(input1.getBytes(StandardCharsets.UTF_8));
        Path inputFile1 = createTempFileWithContent(input1);
        Path inputFile2 = createTempFileWithContent(input2);
        Path outputFile = Files.createTempFile(tempDir, "output", ".txt");
        OutputStream outputStream = Files.newOutputStream(outputFile);

        sortApp.run(new String[]{"-n", "-f", inputFile1.toString(), inputFile2.toString()}, inputStream, outputStream);
        String result = new String(Files.readAllBytes(outputFile));

        assertEquals(expectedOutput + System.lineSeparator(), result);
    }

    @Test
    void run_ValidInputFilesAndOutputStreamReverse_CorrectOutput() throws Exception {
        // should combine the items of two files and sort them after merging
        String input1 = "abc\nzzz\nccc\nbbb\naaa";
        String input2 = "def\nefg\nbcd\naaa";
        String expectedOutput = "zzz\nefg\ndef\nccc\nbcd\nbbb\nabc\naaa\naaa";

        // create the files
        InputStream inputStream = new ByteArrayInputStream(input1.getBytes(StandardCharsets.UTF_8));
        Path inputFile1 = createTempFileWithContent(input1);
        Path inputFile2 = createTempFileWithContent(input2);
        Path outputFile = Files.createTempFile(tempDir, "output", ".txt");
        OutputStream outputStream = Files.newOutputStream(outputFile);

        sortApp.run(new String[]{"-n", "-r", "-f", inputFile1.toString(), inputFile2.toString()}, inputStream, outputStream);
        String result = new String(Files.readAllBytes(outputFile));

        assertEquals(expectedOutput + System.lineSeparator(), result);
    }

    @Test
    void run_NullArgs_ShouldThrow() {
        assertThrows(SortException.class, () -> {
            sortApp.run(null, null, null);
        });
    }

    @Test
    void run_NullOutputStream_ShouldThrow() {
        assertThrows(SortException.class, () -> {
            sortApp.run(new String[]{}, null, null);
        });
    }

    @Test
    void run_InvalidArguments_ShouldThrow() throws IOException {
        String[] invalidArgs = new String[]{"-invalid", "filename.txt"};
        Path outputFile = Files.createTempFile(tempDir, "output", ".txt");
        assertThrows(SortException.class, () -> {
            sortApp.run(invalidArgs, null, Files.newOutputStream(outputFile));
        });
    }

    private Path createTempFileWithContent(String content) throws IOException {
        Path inputFile = Files.createTempFile(tempDir, "input", ".txt");
        Files.write(inputFile, content.getBytes(StandardCharsets.UTF_8));
        return inputFile;
    }

}
