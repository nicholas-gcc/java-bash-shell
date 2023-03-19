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
public class PairwiseUniqPipeCmdIT {
    static final String CWD = System.getProperty("user.dir");
    static final String TESTING_PATH = CHAR_FILE_SEP + "assets" + CHAR_FILE_SEP + "it" + CHAR_FILE_SEP + "pipe" + CHAR_FILE_SEP + "uniq";
    static final String SAMPLE_FILE = "sample.txt";
    static final String APPLE_STR = "apple";
    static final String DOG_STR = "dog";
    static final String BANANA_STR = "banana";
    static final String CAT_STR = "cat";
    static final String NEW_FILE = "new.txt";
    static final String NEW_TEXT = "this is new text";
    static final String UNIQ_CMD = "uniq";
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
    void parseAndEvaluate_UniqPipeToWc_OutputsCorrectly() throws FileNotFoundException, AbstractApplicationException, ShellException {
        CallCommand uniqCommand = buildCallCommand(UNIQ_CMD, SAMPLE_FILE);
        CallCommand wcCommand = buildCallCommand("wc");

        PipeCommand pipeCommand = buildPipeCommand(List.of(new CallCommand[]{uniqCommand, wcCommand}));
        pipeCommand.evaluate(inputStream, outputStream);
        String expectedResult = WC_SPACING + "4" + WC_SPACING+ "4" + StringUtils.multiplyChar(CHAR_SPACE, 6) + "25" + STRING_NEWLINE;
        assertEquals(expectedResult, outputStream.toString());
    }

    @Test
    void parseAndEvaluate_UniqPipeToGrep_OutputsCorrectly() throws FileNotFoundException, AbstractApplicationException, ShellException {
        CallCommand uniqCommand = buildCallCommand(UNIQ_CMD, SAMPLE_FILE);
        CallCommand grepCommand = buildCallCommand("grep", BANANA_STR);

        PipeCommand pipeCommand = buildPipeCommand(List.of(new CallCommand[]{uniqCommand, grepCommand}));
        pipeCommand.evaluate(inputStream, outputStream);
        String expectedResult = BANANA_STR + STRING_NEWLINE;
        assertEquals(expectedResult, outputStream.toString());
    }

    @Test
    void parseAndEvaluate_UniqPipeToCut_OutputsCorrectly() throws FileNotFoundException, AbstractApplicationException, ShellException {
        CallCommand uniqCommand = buildCallCommand(UNIQ_CMD, SAMPLE_FILE);
        CallCommand cutCommand = buildCallCommand("cut", "-b", "1-3");

        PipeCommand pipeCommand = buildPipeCommand(List.of(new CallCommand[]{uniqCommand, cutCommand}));
        pipeCommand.evaluate(inputStream, outputStream);
        String expectedResult = "app" + STRING_NEWLINE + DOG_STR + STRING_NEWLINE + "ban" + STRING_NEWLINE + CAT_STR + STRING_NEWLINE;
        assertEquals(expectedResult, outputStream.toString());
    }

    @Test
    void parseAndEvaluate_UniqPipeToPaste_OutputsCorrectly() throws Exception {
        // Create new file and write 2 lines of new text to it
        FileSystemUtils.createEmptyFile(NEW_FILE);
        FileSystemUtils.writeStrToFile(false, NEW_TEXT + STRING_NEWLINE + NEW_TEXT, NEW_FILE);

        CallCommand uniqCommand = buildCallCommand(UNIQ_CMD, SAMPLE_FILE);
        CallCommand pasteCommand = buildCallCommand("paste", NEW_FILE, "-");

        PipeCommand pipeCommand = buildPipeCommand(List.of(new CallCommand[]{uniqCommand, pasteCommand}));
        pipeCommand.evaluate(inputStream, outputStream);
        String expectedResult = NEW_TEXT + CHAR_TAB + APPLE_STR + STRING_NEWLINE
                + NEW_TEXT + CHAR_TAB + DOG_STR + STRING_NEWLINE
                + CHAR_TAB + BANANA_STR + STRING_NEWLINE
                + CHAR_TAB + CAT_STR + STRING_NEWLINE;
        assertEquals(expectedResult, outputStream.toString());
    }

    @Test
    void parseAndEvaluate_UniqPipeToUniq_OutputsCorrectly() throws FileNotFoundException, AbstractApplicationException, ShellException {
        CallCommand uniqCommand1 = buildCallCommand(UNIQ_CMD, SAMPLE_FILE);
        CallCommand uniqCommand2 = buildCallCommand(UNIQ_CMD);

        PipeCommand pipeCommand = buildPipeCommand(List.of(new CallCommand[]{uniqCommand1, uniqCommand2}));
        pipeCommand.evaluate(inputStream, outputStream);
        String expectedResult = APPLE_STR + STRING_NEWLINE + DOG_STR + STRING_NEWLINE + BANANA_STR + STRING_NEWLINE + CAT_STR + STRING_NEWLINE;
        assertEquals(expectedResult, outputStream.toString());
    }

    @Test
    void parseAndEvaluate_UniqPipeToSort_OutputsCorrectly() throws Exception {
        CallCommand uniqCommand = buildCallCommand(UNIQ_CMD, SAMPLE_FILE);
        CallCommand sortCommand = buildCallCommand("sort");

        PipeCommand pipeCommand = buildPipeCommand(List.of(new CallCommand[]{uniqCommand, sortCommand}));
        pipeCommand.evaluate(inputStream, outputStream);
        String expectedResult = APPLE_STR + STRING_NEWLINE + BANANA_STR + STRING_NEWLINE + CAT_STR + STRING_NEWLINE + DOG_STR + STRING_NEWLINE;
        assertEquals(expectedResult, outputStream.toString());
    }

    @Test
    void parseAndEvaluate_UniqPipeToTee_OutputsCorrectly() throws Exception {
        // Create new file and write 1 line of new text to it
        FileSystemUtils.createEmptyFile(NEW_FILE);
        FileSystemUtils.writeStrToFile(false, NEW_TEXT, NEW_FILE);

        CallCommand uniqCommand = buildCallCommand(UNIQ_CMD, SAMPLE_FILE);
        CallCommand teeCommand = buildCallCommand("tee", "-a",  NEW_FILE);

        PipeCommand pipeCommand = buildPipeCommand(List.of(new CallCommand[]{uniqCommand, teeCommand}));
        pipeCommand.evaluate(inputStream, outputStream);
        String expectedResult = NEW_TEXT + APPLE_STR + STRING_NEWLINE
                + DOG_STR + STRING_NEWLINE
                + BANANA_STR + STRING_NEWLINE
                + CAT_STR + STRING_NEWLINE;
        assertEquals(expectedResult, FileSystemUtils.readFileContent(NEW_FILE));
    }

    @Test
    void parseAndEvaluate_UniqPipeToCat_OutputsCorrectly() throws Exception {
        // Create new file and write 1 line of new text to it
        FileSystemUtils.createEmptyFile(NEW_FILE);
        FileSystemUtils.writeStrToFile(false, NEW_TEXT, NEW_FILE);

        CallCommand uniqCommand = buildCallCommand(UNIQ_CMD, SAMPLE_FILE);
        CallCommand catCommand = buildCallCommand("cat", NEW_FILE, "-");

        PipeCommand pipeCommand = buildPipeCommand(List.of(new CallCommand[]{uniqCommand, catCommand}));
        pipeCommand.evaluate(inputStream, outputStream);
        String expectedResult = NEW_TEXT + STRING_NEWLINE
                + APPLE_STR + STRING_NEWLINE
                + DOG_STR + STRING_NEWLINE
                + BANANA_STR + STRING_NEWLINE
                + CAT_STR + STRING_NEWLINE;
        assertEquals(expectedResult, outputStream.toString());
    }

}
