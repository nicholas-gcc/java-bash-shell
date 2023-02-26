package sg.edu.nus.comp.cs4218.impl.app;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Disabled;
import sg.edu.nus.comp.cs4218.exception.SortException;


import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
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
    @Disabled
    void sortFromFiles_ValidTextFile_CorrectOutput() throws Exception {
        String input = "abc\nzzz\nccc\nbbb\naaa";
        String expectedOutput = "aaa\nbbb\nccc\nzzz";
        Path inputFile = createTempFileWithContent(input);
        String[] fileNames = new String[]{inputFile.toString()};

        String result = sortApp.sortFromFiles(false, false, false, fileNames);

        assertEquals(expectedOutput, result);
    }

    @Test
    @Disabled
    void sortFromFiles_InvalidFile_ShouldThrow() {
        String[] fileNames = new String[]{"invalid-file"};

        assertThrows(SortException.class, () -> {
            sortApp.sortFromFiles(false, false, false, fileNames);
        });
    }

    @Test
    @Disabled
    void sortFromFiles_InputIsDirectory_ShouldThrow() {
        String[] fileNames = new String[]{tempDir.toString()};

        assertThrows(SortException.class, () -> {
            sortApp.sortFromFiles(false, false, false, fileNames);
        });
    }

    @Test
    @Disabled
    void sortFromStdin_ValidInput_CorrectOutput() throws Exception {
        String input = "abc\nzzz\nccc\nbbb\naaa";
        String expectedOutput = "aaa\nbbb\nccc\nzzz";
        InputStream inputStream = new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8));

        String result = sortApp.sortFromStdin(false, false, false, inputStream);

        assertEquals(expectedOutput, result);
    }

    @Test
    @Disabled
    void sortFromStdin_EmptyInput_CorrectOutput() throws Exception {
        String input = "";
        InputStream inputStream = new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8));

        String result = sortApp.sortFromStdin(false, false, false, inputStream);

        assertEquals("", result);
    }

    @Test
    @Disabled
    void sortFromStdin_NullInput_ShouldThrow() {
        assertThrows(SortException.class, () -> {
            sortApp.sortFromStdin(false, false, false, null);
        });
    }

    private Path createTempFileWithContent(String content) throws IOException {
        Path inputFile = Files.createTempFile(tempDir, "input", ".txt");
        Files.write(inputFile, content.getBytes(StandardCharsets.UTF_8));
        return inputFile;
    }

}
