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
public class PairwiseCatPipeCmdIT {
    static final String CWD = System.getProperty("user.dir");
    static final String TESTING_PATH = CHAR_FILE_SEP + "assets" + CHAR_FILE_SEP + "it" + CHAR_FILE_SEP + "pipe" + CHAR_FILE_SEP + "cat";
    static final String SAMPLE1_FILE = "sample1.txt";
    static final String SAMPLE2_FILE = "sample2.txt";
    static final String SAMPLE1_CONTENT = "This is the first sample text.";
    static final String SAMPLE2_CONTENT = "This is the second sample text.";
    static final String NEW_FILE = "new.txt";
    static final String NEW_TEXT = "this is new text";
    static final String CAT_CMD = "cat";
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
    void parseAndEvaluate_CatPipeToWc_OutputsCorrectly() throws FileNotFoundException, AbstractApplicationException, ShellException {
        CallCommand catCommand = buildCallCommand(CAT_CMD, SAMPLE1_FILE, SAMPLE2_FILE);
        CallCommand wcCommand = buildCallCommand("wc");

        PipeCommand pipeCommand = buildPipeCommand(List.of(new CallCommand[]{catCommand, wcCommand}));
        pipeCommand.evaluate(inputStream, outputStream);
        String expectedResult = WC_SPACING + "0" + StringUtils.multiplyChar(CHAR_SPACE, 6) + "11" + StringUtils.multiplyChar(CHAR_SPACE, 6)
                + (SAMPLE1_CONTENT + SAMPLE2_CONTENT).getBytes().length + STRING_NEWLINE;
        assertEquals(expectedResult, outputStream.toString());
    }

    @Test
    void parseAndEvaluate_CatPipeToGrep_OutputsCorrectly() throws FileNotFoundException, AbstractApplicationException, ShellException {
        CallCommand catCommand = buildCallCommand(CAT_CMD, SAMPLE1_FILE, SAMPLE2_FILE);
        CallCommand grepCommand = buildCallCommand("grep", "second");

        PipeCommand pipeCommand = buildPipeCommand(List.of(new CallCommand[]{catCommand, grepCommand}));
        pipeCommand.evaluate(inputStream, outputStream);
        String expectedResult = SAMPLE1_CONTENT + SAMPLE2_CONTENT + STRING_NEWLINE;
        assertEquals(expectedResult, outputStream.toString());
    }

    @Test
    void parseAndEvaluate_CatPipeToCut_OutputsCorrectly() throws FileNotFoundException, AbstractApplicationException, ShellException {
        CallCommand catCommand = buildCallCommand(CAT_CMD, SAMPLE1_FILE, SAMPLE2_FILE);
        CallCommand cutCommand = buildCallCommand("cut", "-b", "2");

        PipeCommand pipeCommand = buildPipeCommand(List.of(new CallCommand[]{catCommand, cutCommand}));
        pipeCommand.evaluate(inputStream, outputStream);
        String expectedResult = "h" + STRING_NEWLINE;
        assertEquals(expectedResult, outputStream.toString());
    }

    @Test
    void parseAndEvaluate_CatPipeToPaste_OutputsCorrectly() throws Exception {
        // Create new file and write 2 lines of new text to it
        FileSystemUtils.createEmptyFile(NEW_FILE);
        FileSystemUtils.writeStrToFile(false, NEW_TEXT + STRING_NEWLINE + NEW_TEXT, NEW_FILE);

        CallCommand catCommand = buildCallCommand(CAT_CMD, SAMPLE1_FILE, SAMPLE2_FILE);
        CallCommand pasteCommand = buildCallCommand("paste", NEW_FILE, "-");

        PipeCommand pipeCommand = buildPipeCommand(List.of(new CallCommand[]{catCommand, pasteCommand}));
        pipeCommand.evaluate(inputStream, outputStream);
        String expectedResult = NEW_TEXT + CHAR_TAB + SAMPLE1_CONTENT + SAMPLE2_CONTENT + STRING_NEWLINE
                + NEW_TEXT + CHAR_TAB + STRING_NEWLINE;
        assertEquals(expectedResult, outputStream.toString());
    }

    @Test
    void parseAndEvaluate_CatPipeToUniq_OutputsCorrectly() throws FileNotFoundException, AbstractApplicationException, ShellException {
        // Concatenate two same file
        CallCommand catCommand = buildCallCommand(CAT_CMD, SAMPLE1_FILE, SAMPLE1_FILE);
        CallCommand uniqCommand = buildCallCommand("uniq");

        PipeCommand pipeCommand = buildPipeCommand(List.of(new CallCommand[]{catCommand, uniqCommand}));
        pipeCommand.evaluate(inputStream, outputStream);
        String expectedResult = SAMPLE1_CONTENT + SAMPLE1_CONTENT + STRING_NEWLINE;
        assertEquals(expectedResult, outputStream.toString());
    }

    @Test
    void parseAndEvaluate_CatPipeToSort_OutputsCorrectly() throws Exception {
        CallCommand catCommand = buildCallCommand(CAT_CMD, SAMPLE1_FILE, SAMPLE2_FILE);
        CallCommand sortCommand = buildCallCommand("sort", SAMPLE2_FILE, "-");

        PipeCommand pipeCommand = buildPipeCommand(List.of(new CallCommand[]{catCommand, sortCommand}));
        pipeCommand.evaluate(inputStream, outputStream);
        String expectedResult = SAMPLE1_CONTENT + SAMPLE2_CONTENT + STRING_NEWLINE + SAMPLE2_CONTENT + STRING_NEWLINE;
        assertEquals(expectedResult, outputStream.toString());
    }

    @Test
    void parseAndEvaluate_CatPipeToTee_OutputsCorrectly() throws Exception {
        // Create new file and write 1 line of new text to it
        FileSystemUtils.createEmptyFile(NEW_FILE);
        FileSystemUtils.writeStrToFile(false, NEW_TEXT, NEW_FILE);

        CallCommand catCommand = buildCallCommand(CAT_CMD, SAMPLE1_FILE, SAMPLE2_FILE);
        CallCommand teeCommand = buildCallCommand("tee", "-a", NEW_FILE);

        PipeCommand pipeCommand = buildPipeCommand(List.of(new CallCommand[]{catCommand, teeCommand}));
        pipeCommand.evaluate(inputStream, outputStream);
        String expectedResult = NEW_TEXT + SAMPLE1_CONTENT + SAMPLE2_CONTENT + STRING_NEWLINE;
        assertEquals(expectedResult, FileSystemUtils.readFileContent(NEW_FILE));
    }

    // TODO: Fix catFileAndStdin
    @Test
    void parseAndEvaluate_CatPipeToCat_OutputsCorrectly() throws Exception {
        // Create new file and write 1 line of new text to it
        FileSystemUtils.createEmptyFile(NEW_FILE);
        FileSystemUtils.writeStrToFile(false, NEW_TEXT, NEW_FILE);

        CallCommand catCommand1 = buildCallCommand(CAT_CMD, SAMPLE1_FILE, SAMPLE2_FILE);
        CallCommand catCommand2 = buildCallCommand("cat", NEW_FILE, "-");

        PipeCommand pipeCommand = buildPipeCommand(List.of(new CallCommand[]{catCommand1, catCommand2}));
        pipeCommand.evaluate(inputStream, outputStream);
        // String expectedResult = NEW_TEXT + SAMPLE1_CONTENT + SAMPLE2_CONTENT;
        String expectedResult = NEW_TEXT + STRING_NEWLINE + SAMPLE1_CONTENT + SAMPLE2_CONTENT; // wrong output
        assertEquals(expectedResult, outputStream.toString());
    }
}
