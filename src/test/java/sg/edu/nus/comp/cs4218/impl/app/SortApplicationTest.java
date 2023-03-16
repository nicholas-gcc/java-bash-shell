package sg.edu.nus.comp.cs4218.impl.app;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sg.edu.nus.comp.cs4218.exception.SortException;
import sg.edu.nus.comp.cs4218.impl.util.IOUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.ERR_FILE_NOT_FOUND;
import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.ERR_INVALID_FLAG;
import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.ERR_IS_DIR;
import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.ERR_NULL_ARGS;
import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.ERR_NULL_STREAMS;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.STRING_NEWLINE;
@SuppressWarnings("PMD.CloseResource")
class SortApplicationTest {

    private static final String ABC_STRING = "abc";
    private static final String AAA_STRING = "aaa";
    private static final String BBB_STRING = "bbb";
    private static final String BCD_STRING = "bcd";
    private static final String CCC_STRING = "ccc";
    private static final String DEF_STRING = "def";
    private static final String EFG_STRING = "efg";
    private static final String ZZZ_STRING = "zzz";
    private static final String TXT_FILE_EXT = ".txt";
    private static final String SORT_ERR_PREFIX = "sort: ";
    private SortApplication sortApp;
    private Path tempDir;

    @BeforeEach
    void setUp() throws IOException {
        sortApp = new SortApplication();
        tempDir = Files.createTempDirectory("sort-application-test");
    }

    @Test
    void sortFromFiles_ValidTextFile_CorrectOutput() throws Exception {
        String input = ABC_STRING + STRING_NEWLINE + ZZZ_STRING + STRING_NEWLINE
                + CCC_STRING + STRING_NEWLINE + BBB_STRING + STRING_NEWLINE + AAA_STRING;

        String expectedOutput = AAA_STRING + STRING_NEWLINE + ABC_STRING + STRING_NEWLINE +
                BBB_STRING + STRING_NEWLINE + CCC_STRING + STRING_NEWLINE + ZZZ_STRING;

        Path inputFile = createTempFileWithContent(input);
        String[] fileNames = new String[]{inputFile.toString()};

        String result = sortApp.sortFromFiles(false, false, false, fileNames);

        assertEquals(expectedOutput, result);
    }

    @Test
    void sortFromFiles_InvalidFile_ShouldThrow() {
        String[] fileNames = new String[]{"invalid-file"};

        Throwable err = assertThrows(SortException.class, () -> {
            sortApp.sortFromFiles(false, false, false, fileNames);
        });

        assertEquals(SORT_ERR_PREFIX + ERR_FILE_NOT_FOUND, err.getMessage());

    }

    @Test
    void sortFromFiles_InputIsDirectory_ShouldThrow() {
        String[] fileNames = new String[]{tempDir.toString()};

        Throwable err = assertThrows(SortException.class, () -> {
            sortApp.sortFromFiles(false, false, false, fileNames);
        });

        assertEquals(SORT_ERR_PREFIX + ERR_IS_DIR, err.getMessage());
    }

    @Test
    void sortFromStdin_ValidInput_CorrectOutput() throws Exception {
        String input = ABC_STRING + STRING_NEWLINE + ZZZ_STRING + STRING_NEWLINE
                + CCC_STRING + STRING_NEWLINE + BBB_STRING + STRING_NEWLINE + AAA_STRING;

        String expectedOutput = AAA_STRING + STRING_NEWLINE + ABC_STRING + STRING_NEWLINE +
                BBB_STRING + STRING_NEWLINE + CCC_STRING + STRING_NEWLINE + ZZZ_STRING;

        InputStream inputStream = new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8));

        String result = sortApp.sortFromStdin(false, false, false, inputStream);

        assertEquals(expectedOutput, result);
    }

    @Test
    void sortFromStdin_ValidInputReverse_CorrectOutput() throws Exception {
        String input = ABC_STRING + STRING_NEWLINE + ZZZ_STRING + STRING_NEWLINE
                + CCC_STRING + STRING_NEWLINE + BBB_STRING + STRING_NEWLINE + AAA_STRING;

        String expectedOutput = ZZZ_STRING + STRING_NEWLINE + CCC_STRING + STRING_NEWLINE
                + BBB_STRING + STRING_NEWLINE + ABC_STRING + STRING_NEWLINE + AAA_STRING;

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
        Throwable err = assertThrows(SortException.class, () -> {
            sortApp.sortFromStdin(false, false, false, null);
        });

        assertEquals(SORT_ERR_PREFIX + ERR_NULL_STREAMS, err.getMessage());

    }

    @Test
    void run_ValidInputFilesAndOutputStream_CorrectOutput() throws Exception {
        // should combine the items of two files and sort them after merging
        String input1 = ABC_STRING + STRING_NEWLINE + ZZZ_STRING + STRING_NEWLINE
                + CCC_STRING + STRING_NEWLINE + BBB_STRING + STRING_NEWLINE + AAA_STRING;

        String input2 = DEF_STRING + STRING_NEWLINE + EFG_STRING + STRING_NEWLINE
                + BCD_STRING + STRING_NEWLINE + AAA_STRING;

        String expectedOutput = AAA_STRING + STRING_NEWLINE + AAA_STRING + STRING_NEWLINE
                + ABC_STRING + STRING_NEWLINE + BBB_STRING + STRING_NEWLINE + BCD_STRING +
                STRING_NEWLINE + CCC_STRING + STRING_NEWLINE + DEF_STRING + STRING_NEWLINE
                + EFG_STRING + STRING_NEWLINE + ZZZ_STRING;

        // create the files
        InputStream inputStream = new ByteArrayInputStream(input1.getBytes(StandardCharsets.UTF_8));
        Path inputFile1 = createTempFileWithContent(input1);
        Path inputFile2 = createTempFileWithContent(input2);
        Path outputFile = Files.createTempFile(tempDir, "output", TXT_FILE_EXT);
        OutputStream outputStream = Files.newOutputStream(outputFile);

        sortApp.run(new String[]{"-n", "-f", inputFile1.toString(), inputFile2.toString()}, inputStream, outputStream);
        String result = new String(Files.readAllBytes(outputFile));

        assertEquals(expectedOutput + STRING_NEWLINE, result);

        IOUtils.closeOutputStream(outputStream);
    }

    @Test
    void run_ValidInputFilesAndOutputStreamReverse_CorrectOutput() throws Exception {
        String input1 = ABC_STRING + STRING_NEWLINE + ZZZ_STRING + STRING_NEWLINE
                + CCC_STRING + STRING_NEWLINE + BBB_STRING + STRING_NEWLINE + AAA_STRING;

        String input2 = DEF_STRING + STRING_NEWLINE + EFG_STRING + STRING_NEWLINE
                + BCD_STRING + STRING_NEWLINE + AAA_STRING;

        String expectedOutput = ZZZ_STRING + STRING_NEWLINE + EFG_STRING + STRING_NEWLINE
                + DEF_STRING + STRING_NEWLINE + CCC_STRING + STRING_NEWLINE + BCD_STRING + STRING_NEWLINE
                + BBB_STRING + STRING_NEWLINE + ABC_STRING + STRING_NEWLINE + AAA_STRING + STRING_NEWLINE + AAA_STRING;

        // create the files
        InputStream inputStream = new ByteArrayInputStream(input1.getBytes(StandardCharsets.UTF_8));
        Path inputFile1 = createTempFileWithContent(input1);
        Path inputFile2 = createTempFileWithContent(input2);
        Path outputFile = Files.createTempFile(tempDir, "output", TXT_FILE_EXT);
        OutputStream outputStream = Files.newOutputStream(outputFile);

        sortApp.run(new String[]{"-n", "-r", "-f", inputFile1.toString(), inputFile2.toString()}, inputStream, outputStream);
        String result = new String(Files.readAllBytes(outputFile));

        assertEquals(expectedOutput + STRING_NEWLINE, result);

        IOUtils.closeOutputStream(outputStream);
    }

    @Test
    void run_NullArgs_ShouldThrow() {
        Throwable err = assertThrows(SortException.class, () -> {
            sortApp.run(null, null, null);
        });

        assertEquals(SORT_ERR_PREFIX + ERR_NULL_ARGS, err.getMessage());

    }

    @Test
    void run_NullOutputStream_ShouldThrow() {
        Throwable err = assertThrows(SortException.class, () -> {
            sortApp.sortFromStdin(false, false, false, null);
        });

        assertEquals(SORT_ERR_PREFIX + ERR_NULL_STREAMS, err.getMessage());
    }

    @Test
    void run_InvalidArguments_ShouldThrow() throws IOException {
        String[] invalidArgs = new String[]{"-invalid", "filename.txt"};
        Path outputFile = Files.createTempFile(tempDir, "output", TXT_FILE_EXT);
        Throwable err = assertThrows(SortException.class, () -> {
            sortApp.run(invalidArgs, null, Files.newOutputStream(outputFile));
        });

        assertEquals(SORT_ERR_PREFIX + ERR_INVALID_FLAG + ": " + "-invalid", err.getMessage());
    }

    private Path createTempFileWithContent(String content) throws IOException {
        Path inputFile = Files.createTempFile(tempDir, "input", TXT_FILE_EXT);
        Files.write(inputFile, content.getBytes(StandardCharsets.UTF_8));
        return inputFile;
    }

}
