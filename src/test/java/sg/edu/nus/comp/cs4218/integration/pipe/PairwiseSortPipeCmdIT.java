package sg.edu.nus.comp.cs4218.integration.pipe;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import sg.edu.nus.comp.cs4218.Environment;
import sg.edu.nus.comp.cs4218.exception.AbstractApplicationException;
import sg.edu.nus.comp.cs4218.exception.ShellException;
import sg.edu.nus.comp.cs4218.impl.cmd.CallCommand;
import sg.edu.nus.comp.cs4218.impl.cmd.PipeCommand;
import sg.edu.nus.comp.cs4218.impl.util.ApplicationRunner;
import sg.edu.nus.comp.cs4218.impl.util.ArgumentResolver;
import sg.edu.nus.comp.cs4218.impl.util.FileSystemUtils;
import sg.edu.nus.comp.cs4218.impl.util.StringUtils;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.CHAR_FILE_SEP;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.CHAR_SPACE;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.CHAR_TAB;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.STRING_NEWLINE;

@SuppressWarnings({"PMD.ClassNamingConventions"})
public class PairwiseSortPipeCmdIT {
    static final String CWD = System.getProperty("user.dir");
    static final String TESTING_PATH = CHAR_FILE_SEP + "assets" + CHAR_FILE_SEP + "it" + CHAR_FILE_SEP + "pipe" + CHAR_FILE_SEP + "sort";
    static final String SAMPLE_FILE = "sample.txt";
    static final String A_STR = "a";
    static final String A1_STR = "a1";
    static final String A2_STR = "a2";
    static final String B_STR = "b";
    static final String C_STR = "c";
    static final String NEW_FILE = "new.txt";
    static final String NEW_TEXT = "this is new text";
    static final String SORT_CMD = "sort";
    static final String WC_SPACING = StringUtils.multiplyChar(CHAR_SPACE, 7);

    InputStream inputStream;
    OutputStream outputStream;
    @BeforeEach
    void setup() {
        Environment.currentDirectory += TESTING_PATH;
        inputStream = System.in;
        outputStream = new ByteArrayOutputStream();
    }

    @AfterEach
    void reset() throws IOException {
        Environment.currentDirectory = CWD;
        inputStream.close();
        outputStream.close();
    }

    @AfterEach
    void deleteFiles() throws Exception {
        if (FileSystemUtils.fileOrDirExist(NEW_FILE)) {
            FileSystemUtils.deleteFileOrDir(NEW_FILE);
        }
    }


    private CallCommand buildCallCommand(String... args) {
        ApplicationRunner applicationRunner = new ApplicationRunner();
        ArgumentResolver argumentResolver = new ArgumentResolver();
        return new CallCommand(List.of(args), applicationRunner, argumentResolver);
    }

    private PipeCommand buildPipeCommand(List<CallCommand> callCommands) {
        return new PipeCommand(callCommands);
    }

    @Test
    void parseAndEvaluate_SortPipeToWc_OutputsCorrectly() throws FileNotFoundException, AbstractApplicationException, ShellException {
        CallCommand sortCommand = buildCallCommand(SORT_CMD, SAMPLE_FILE);
        CallCommand wcCommand = buildCallCommand("wc");

        PipeCommand pipeCommand = buildPipeCommand(List.of(new CallCommand[]{sortCommand, wcCommand}));
        pipeCommand.evaluate(inputStream, outputStream);
        String expectedResult = WC_SPACING + "5" + WC_SPACING+ "5" + StringUtils.multiplyChar(CHAR_SPACE, 6) + "18" + STRING_NEWLINE;
        assertEquals(expectedResult, outputStream.toString());
    }

    @Test
    void parseAndEvaluate_SortPipeToGrep_OutputsCorrectly() throws FileNotFoundException, AbstractApplicationException, ShellException {
        CallCommand sortCommand = buildCallCommand(SORT_CMD, SAMPLE_FILE);
        CallCommand grepCommand = buildCallCommand("grep", "a");

        PipeCommand pipeCommand = buildPipeCommand(List.of(new CallCommand[]{sortCommand, grepCommand}));
        pipeCommand.evaluate(inputStream, outputStream);
        String expectedResult = A1_STR + STRING_NEWLINE + A1_STR + STRING_NEWLINE + A2_STR + STRING_NEWLINE;
        assertEquals(expectedResult, outputStream.toString());
    }

    @Test
    void parseAndEvaluate_SortPipeToCut_OutputsCorrectly() throws FileNotFoundException, AbstractApplicationException, ShellException {
        CallCommand sortCommand = buildCallCommand(SORT_CMD, SAMPLE_FILE);
        CallCommand cutCommand = buildCallCommand("cut", "-b", "1");

        PipeCommand pipeCommand = buildPipeCommand(List.of(new CallCommand[]{sortCommand, cutCommand}));
        pipeCommand.evaluate(inputStream, outputStream);
        String expectedResult = A_STR + STRING_NEWLINE + A_STR + STRING_NEWLINE + A_STR + STRING_NEWLINE + B_STR + STRING_NEWLINE + C_STR + STRING_NEWLINE;
        assertEquals(expectedResult, outputStream.toString());
    }

    @Test
    void parseAndEvaluate_SortPipeToPaste_OutputsCorrectly() throws Exception {
        // Create new file and write 2 lines of new text to it
        FileSystemUtils.createEmptyFile(NEW_FILE);
        FileSystemUtils.writeStrToFile(false, NEW_TEXT + STRING_NEWLINE + NEW_TEXT, NEW_FILE);

        CallCommand sortCommand = buildCallCommand(SORT_CMD, SAMPLE_FILE);
        CallCommand pasteCommand = buildCallCommand("paste", NEW_FILE, "-");

        PipeCommand pipeCommand = buildPipeCommand(List.of(new CallCommand[]{sortCommand, pasteCommand}));
        pipeCommand.evaluate(inputStream, outputStream);
        String expectedResult = NEW_TEXT + CHAR_TAB + A1_STR + STRING_NEWLINE
                + NEW_TEXT + CHAR_TAB + A1_STR + STRING_NEWLINE
                + CHAR_TAB + A2_STR + STRING_NEWLINE
                + CHAR_TAB + B_STR + STRING_NEWLINE
                + CHAR_TAB + C_STR + STRING_NEWLINE;
        assertEquals(expectedResult, outputStream.toString());
    }

    @Test
    void parseAndEvaluate_SortPipeToUniq_OutputsCorrectly() throws FileNotFoundException, AbstractApplicationException, ShellException {
        CallCommand sortCommand = buildCallCommand(SORT_CMD, SAMPLE_FILE);
        CallCommand uniqCommand = buildCallCommand("uniq");

        PipeCommand pipeCommand = buildPipeCommand(List.of(new CallCommand[]{sortCommand, uniqCommand}));
        pipeCommand.evaluate(inputStream, outputStream);
        String expectedResult = A1_STR + STRING_NEWLINE + A2_STR + STRING_NEWLINE + B_STR + STRING_NEWLINE + C_STR + STRING_NEWLINE;
        assertEquals(expectedResult, outputStream.toString());
    }

    @Test
    void parseAndEvaluate_SortPipeToSort_OutputsCorrectly() throws Exception {
        CallCommand sortCommand1 = buildCallCommand(SORT_CMD, SAMPLE_FILE);
        CallCommand sortCommand2 = buildCallCommand(SORT_CMD, SAMPLE_FILE, "-");

        PipeCommand pipeCommand = buildPipeCommand(List.of(new CallCommand[]{sortCommand1, sortCommand2}));
        pipeCommand.evaluate(inputStream, outputStream);
        String expectedResult = A1_STR + STRING_NEWLINE
                + A1_STR + STRING_NEWLINE
                + A1_STR + STRING_NEWLINE
                + A1_STR + STRING_NEWLINE
                + A2_STR + STRING_NEWLINE
                + A2_STR + STRING_NEWLINE
                + B_STR + STRING_NEWLINE
                + B_STR + STRING_NEWLINE
                + C_STR + STRING_NEWLINE
                + C_STR + STRING_NEWLINE;
        assertEquals(expectedResult, outputStream.toString());
    }

    @Test
    void parseAndEvaluate_SortPipeToTee_OutputsCorrectly() throws Exception {
        // Create new file and write 1 line of new text to it
        FileSystemUtils.createEmptyFile(NEW_FILE);
        FileSystemUtils.writeStrToFile(false, NEW_TEXT, NEW_FILE);

        CallCommand sortCommand = buildCallCommand(SORT_CMD, SAMPLE_FILE);
        CallCommand teeCommand = buildCallCommand("tee", "-a",  NEW_FILE);

        PipeCommand pipeCommand = buildPipeCommand(List.of(new CallCommand[]{sortCommand, teeCommand}));
        pipeCommand.evaluate(inputStream, outputStream);
        String expectedResult = NEW_TEXT + A1_STR + STRING_NEWLINE
                + A1_STR + STRING_NEWLINE
                + A2_STR + STRING_NEWLINE
                + B_STR + STRING_NEWLINE
                + C_STR + STRING_NEWLINE;
        assertEquals(expectedResult, FileSystemUtils.readFileContent(NEW_FILE));
    }

    @Test
    void parseAndEvaluate_SortPipeToCat_OutputsCorrectly() throws Exception {
        // Create new file and write 1 line of new text to it
        FileSystemUtils.createEmptyFile(NEW_FILE);
        FileSystemUtils.writeStrToFile(false, NEW_TEXT, NEW_FILE);

        CallCommand sortCommand = buildCallCommand(SORT_CMD, SAMPLE_FILE);
        CallCommand catCommand = buildCallCommand("cat", NEW_FILE, "-");

        PipeCommand pipeCommand = buildPipeCommand(List.of(new CallCommand[]{sortCommand, catCommand}));
        pipeCommand.evaluate(inputStream, outputStream);
        String expectedResult = NEW_TEXT + STRING_NEWLINE
                + A1_STR + STRING_NEWLINE
                + A1_STR + STRING_NEWLINE
                + A2_STR + STRING_NEWLINE
                + B_STR + STRING_NEWLINE
                + C_STR + STRING_NEWLINE;
        assertEquals(expectedResult, outputStream.toString());
    }
}
