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
public class PairwisePastePipeCmdIT {
    static final String CWD = System.getProperty("user.dir");
    static final String TESTING_PATH = CHAR_FILE_SEP + "assets" + CHAR_FILE_SEP + "it" + CHAR_FILE_SEP + "pipe" + CHAR_FILE_SEP + "paste";
    static final String ALP_FILE = "alp.txt";
    static final String ALP_TEXT1 = "a";
    static final String ALP_TEXT2 = "b";
    static final String ALP_TEXT3 = "c";
    static final String NUM_FILE = "num.txt";
    static final String NUM_TEXT1 = "1";
    static final String NUM_TEXT2 = "2";
    static final String NUM_TEXT3 = "3";
    static final String NEW_FILE = "new.txt";
    static final String NEW_TEXT = "this is new text";
    static final String PASTE_CMD = "paste";
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
    void parseAndEvaluate_PastePipeToWc_OutputsCorrectly() throws FileNotFoundException, AbstractApplicationException, ShellException {
        CallCommand pasteCommand = buildCallCommand(PASTE_CMD, ALP_FILE, NUM_FILE);
        CallCommand wcCommand = buildCallCommand("wc");

        PipeCommand pipeCommand = buildPipeCommand(List.of(new CallCommand[]{pasteCommand, wcCommand}));
        pipeCommand.evaluate(inputStream, outputStream);
        String expectedResult = WC_SPACING + "3" + WC_SPACING+ "6" + StringUtils.multiplyChar(CHAR_SPACE, 6) + "15" + STRING_NEWLINE;
        assertEquals(expectedResult, outputStream.toString());
    }

    @Test
    void parseAndEvaluate_PastePipeToGrep_OutputsCorrectly() throws Exception {
        // Create new file and write 2 lines of new text to it
        FileSystemUtils.createEmptyFile(NEW_FILE);
        FileSystemUtils.writeStrToFile(false, NEW_TEXT + STRING_NEWLINE + NEW_TEXT, NEW_FILE);

        CallCommand pasteCommand = buildCallCommand(PASTE_CMD, ALP_FILE, NUM_FILE, NEW_FILE);
        // Grep only first two lines
        CallCommand grepCommand = buildCallCommand("grep", "this is");

        PipeCommand pipeCommand = buildPipeCommand(List.of(new CallCommand[]{pasteCommand, grepCommand}));
        pipeCommand.evaluate(inputStream, outputStream);
        String expectedResult = ALP_TEXT1 + CHAR_TAB + NUM_TEXT1 + CHAR_TAB + NEW_TEXT + STRING_NEWLINE
                + ALP_TEXT2 + CHAR_TAB + NUM_TEXT2 + CHAR_TAB + NEW_TEXT + STRING_NEWLINE;
        assertEquals(expectedResult, outputStream.toString());
    }

    @Test
    void parseAndEvaluate_PastePipeToCut_OutputsCorrectly() throws FileNotFoundException, AbstractApplicationException, ShellException {
        CallCommand pasteCommand = buildCallCommand(PASTE_CMD, ALP_FILE, NUM_FILE);
        CallCommand cutCommand = buildCallCommand("cut", "-b", "3");

        PipeCommand pipeCommand = buildPipeCommand(List.of(new CallCommand[]{pasteCommand, cutCommand}));
        pipeCommand.evaluate(inputStream, outputStream);
        String expectedResult = NUM_TEXT1 + STRING_NEWLINE + NUM_TEXT2 + STRING_NEWLINE + NUM_TEXT3+ STRING_NEWLINE;
        assertEquals(expectedResult, outputStream.toString());
    }

    @Test
    void parseAndEvaluate_PastePipeToPaste_OutputsCorrectly() throws FileNotFoundException, AbstractApplicationException, ShellException {
        CallCommand pasteCommand1 = buildCallCommand(PASTE_CMD, ALP_FILE, NUM_FILE);
        CallCommand pasteCommand2 = buildCallCommand(PASTE_CMD, ALP_FILE, NUM_FILE, "-");

        PipeCommand pipeCommand = buildPipeCommand(List.of(new CallCommand[]{pasteCommand1, pasteCommand2}));
        pipeCommand.evaluate(inputStream, outputStream);
        String expectedResult = ALP_TEXT1 + CHAR_TAB + NUM_TEXT1 + CHAR_TAB + ALP_TEXT1 + CHAR_TAB + NUM_TEXT1 + STRING_NEWLINE
                + ALP_TEXT2 + CHAR_TAB + NUM_TEXT2 + CHAR_TAB + ALP_TEXT2 + CHAR_TAB + NUM_TEXT2 + STRING_NEWLINE
                + ALP_TEXT3 + CHAR_TAB + NUM_TEXT3 + CHAR_TAB + ALP_TEXT3 + CHAR_TAB + NUM_TEXT3 + STRING_NEWLINE;
        assertEquals(expectedResult, outputStream.toString());
    }

    @Test
    void parseAndEvaluate_PastePipeToUniq_OutputsCorrectly() throws FileNotFoundException, AbstractApplicationException, ShellException {
        // Serialize content with serial option
        CallCommand pasteCommand = buildCallCommand(PASTE_CMD, ALP_FILE, ALP_FILE, NUM_FILE, NUM_FILE, NUM_FILE, "-s");
        CallCommand uniqCommand = buildCallCommand("uniq");

        PipeCommand pipeCommand = buildPipeCommand(List.of(new CallCommand[]{pasteCommand, uniqCommand}));
        pipeCommand.evaluate(inputStream, outputStream);
        String expectedResult = ALP_TEXT1 + CHAR_TAB + ALP_TEXT2 + CHAR_TAB + ALP_TEXT3 + STRING_NEWLINE
                + NUM_TEXT1 + CHAR_TAB + NUM_TEXT2 + CHAR_TAB + NUM_TEXT3 + STRING_NEWLINE;
        assertEquals(expectedResult, outputStream.toString());
    }

    @Test
    void parseAndEvaluate_PastePipeToSort_OutputsCorrectly() throws Exception {
        // Serialize content with serial option
        CallCommand pasteCommand = buildCallCommand(PASTE_CMD, ALP_FILE, NUM_FILE, ALP_FILE, NUM_FILE, "-s");
        CallCommand sortCommand = buildCallCommand("sort");

        PipeCommand pipeCommand = buildPipeCommand(List.of(new CallCommand[]{pasteCommand, sortCommand}));
        pipeCommand.evaluate(inputStream, outputStream);
        // Numbers will be sorted first
        String expectedResult = NUM_TEXT1 + CHAR_TAB + NUM_TEXT2 + CHAR_TAB + NUM_TEXT3 + STRING_NEWLINE
                + NUM_TEXT1 + CHAR_TAB + NUM_TEXT2 + CHAR_TAB + NUM_TEXT3 + STRING_NEWLINE
                + ALP_TEXT1 + CHAR_TAB + ALP_TEXT2 + CHAR_TAB + ALP_TEXT3 + STRING_NEWLINE
                + ALP_TEXT1 + CHAR_TAB + ALP_TEXT2 + CHAR_TAB + ALP_TEXT3 + STRING_NEWLINE;
        assertEquals(expectedResult, outputStream.toString());
    }

    @Test
    void parseAndEvaluate_PastePipeToTee_OutputsCorrectly() throws Exception {
        // Create new file and write 1 line of new text to it
        FileSystemUtils.createEmptyFile(NEW_FILE);
        FileSystemUtils.writeStrToFile(false, NEW_TEXT, NEW_FILE);

        CallCommand pasteCommand = buildCallCommand(PASTE_CMD, ALP_FILE, NUM_FILE);
        CallCommand teeCommand = buildCallCommand("tee", "-a",  NEW_FILE);

        PipeCommand pipeCommand = buildPipeCommand(List.of(new CallCommand[]{pasteCommand, teeCommand}));
        pipeCommand.evaluate(inputStream, outputStream);
        String expectedResult = NEW_TEXT + ALP_TEXT1 + CHAR_TAB + NUM_TEXT1 + STRING_NEWLINE
                + ALP_TEXT2 + CHAR_TAB + NUM_TEXT2 + STRING_NEWLINE
                + ALP_TEXT3 + CHAR_TAB + NUM_TEXT3 + STRING_NEWLINE;
        assertEquals(expectedResult, FileSystemUtils.readFileContent(NEW_FILE));
    }

    @Test
    void parseAndEvaluate_PastePipeToCat_OutputsCorrectly() throws Exception {
        // Create new file and write 1 line of new text to it
        FileSystemUtils.createEmptyFile(NEW_FILE);
        FileSystemUtils.writeStrToFile(false, NEW_TEXT, NEW_FILE);

        CallCommand pasteCommand = buildCallCommand(PASTE_CMD, ALP_FILE, NUM_FILE);
        CallCommand catCommand = buildCallCommand("cat", NEW_FILE, "-");

        PipeCommand pipeCommand = buildPipeCommand(List.of(new CallCommand[]{pasteCommand, catCommand}));
        pipeCommand.evaluate(inputStream, outputStream);
        String expectedResult = NEW_TEXT + STRING_NEWLINE
                + ALP_TEXT1 + CHAR_TAB + NUM_TEXT1 + STRING_NEWLINE
                + ALP_TEXT2 + CHAR_TAB + NUM_TEXT2 + STRING_NEWLINE
                + ALP_TEXT3 + CHAR_TAB + NUM_TEXT3 + STRING_NEWLINE;
        assertEquals(expectedResult, outputStream.toString());
    }
}
