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
public class PairwiseEchoPipeCmdIT {
    static final String CWD = System.getProperty("user.dir");
    static final String TESTING_PATH = CHAR_FILE_SEP + "assets" + CHAR_FILE_SEP + "it" + CHAR_FILE_SEP + "pipe" + CHAR_FILE_SEP + "echo";
    static final String SAMPLE_TEXT1 = "hello world";
    static final String SAMPLE_TEXT2 =  "hello, nice to meet you";
    static final String NEW_FILE = "new.txt";
    static final String NEW_TEXT = "this is new text";
    static final String ECHO_CMD = "echo";
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
    void parseAndEvaluate_EchoPipeToWc_OutputsCorrectly() throws FileNotFoundException, AbstractApplicationException, ShellException {
        CallCommand echoCommand = buildCallCommand(ECHO_CMD, SAMPLE_TEXT1);
        CallCommand wcCommand = buildCallCommand("wc");

        PipeCommand pipeCommand = buildPipeCommand(List.of(new CallCommand[]{echoCommand, wcCommand}));
        pipeCommand.evaluate(inputStream, outputStream);
        String expectedResult = WC_SPACING + "1" + WC_SPACING + "2" + StringUtils.multiplyChar(CHAR_SPACE, 6) + (SAMPLE_TEXT1 + STRING_NEWLINE).getBytes().length + STRING_NEWLINE;
        assertEquals(expectedResult, outputStream.toString());
    }

    @Test
    void parseAndEvaluate_EchoPipeToGrep_OutputsCorrectly() throws FileNotFoundException, AbstractApplicationException, ShellException {
        CallCommand echoCommand = buildCallCommand(ECHO_CMD, SAMPLE_TEXT1 + STRING_NEWLINE + SAMPLE_TEXT2);
        CallCommand grepCommand = buildCallCommand("grep", "hello");

        PipeCommand pipeCommand = buildPipeCommand(List.of(new CallCommand[]{echoCommand, grepCommand}));
        pipeCommand.evaluate(inputStream, outputStream);
        String expectedResult = SAMPLE_TEXT1 + STRING_NEWLINE + SAMPLE_TEXT2 + STRING_NEWLINE;
        assertEquals(expectedResult, outputStream.toString());
    }

    @Test
    void parseAndEvaluate_EchoPipeToCut_OutputsCorrectly() throws FileNotFoundException, AbstractApplicationException, ShellException {
        CallCommand echoCommand = buildCallCommand(ECHO_CMD, SAMPLE_TEXT1);
        CallCommand cutCommand = buildCallCommand("cut", "-b", "5-10");

        PipeCommand pipeCommand = buildPipeCommand(List.of(new CallCommand[]{echoCommand, cutCommand}));
        pipeCommand.evaluate(inputStream, outputStream);
        String expectedResult = "o worl" + STRING_NEWLINE;
        assertEquals(expectedResult, outputStream.toString());
    }

    @Test
    void parseAndEvaluate_EchoPipeToPaste_OutputsCorrectly() throws Exception {
        // Create new file and write 1 line of new text to it
        FileSystemUtils.createEmptyFile(NEW_FILE);
        FileSystemUtils.writeStrToFile(false, NEW_TEXT, NEW_FILE);

        CallCommand echoCommand = buildCallCommand(ECHO_CMD, SAMPLE_TEXT1);
        CallCommand pasteCommand = buildCallCommand("paste", NEW_FILE, "-");

        PipeCommand pipeCommand = buildPipeCommand(List.of(new CallCommand[]{echoCommand, pasteCommand}));
        pipeCommand.evaluate(inputStream, outputStream);
        String expectedResult = NEW_TEXT + CHAR_TAB + SAMPLE_TEXT1 + STRING_NEWLINE;
        assertEquals(expectedResult, outputStream.toString());
    }

    @Test
    void parseAndEvaluate_EchoPipeToUniq_OutputsCorrectly() throws Exception {
        // Create new file and write 1 line of new text to it
        FileSystemUtils.createEmptyFile(NEW_FILE);
        FileSystemUtils.writeStrToFile(false, NEW_TEXT, NEW_FILE);

        CallCommand echoCommand = buildCallCommand(ECHO_CMD, SAMPLE_TEXT1 + STRING_NEWLINE + SAMPLE_TEXT1 + STRING_NEWLINE
                + SAMPLE_TEXT2 + STRING_NEWLINE + SAMPLE_TEXT2);
        CallCommand uniqCommand = buildCallCommand("uniq");

        PipeCommand pipeCommand = buildPipeCommand(List.of(new CallCommand[]{echoCommand, uniqCommand}));
        pipeCommand.evaluate(inputStream, outputStream);
        String expectedResult = SAMPLE_TEXT1 + STRING_NEWLINE + SAMPLE_TEXT2 + STRING_NEWLINE;
        assertEquals(expectedResult, outputStream.toString());
    }

    @Test
    void parseAndEvaluate_EchoPipeToSort_OutputsCorrectly() throws Exception {
        // Create new file and write 1 line of new text to it
        FileSystemUtils.createEmptyFile(NEW_FILE);
        FileSystemUtils.writeStrToFile(false, NEW_TEXT, NEW_FILE);

        // echos sample text 2 first then sample text 1
        CallCommand echoCommand = buildCallCommand(ECHO_CMD, SAMPLE_TEXT2 + STRING_NEWLINE + SAMPLE_TEXT1);
        CallCommand sortCommand = buildCallCommand("sort");

        PipeCommand pipeCommand = buildPipeCommand(List.of(new CallCommand[]{echoCommand, sortCommand}));
        pipeCommand.evaluate(inputStream, outputStream);

        // text 1 appears first
        String expectedResult = SAMPLE_TEXT1 + STRING_NEWLINE + SAMPLE_TEXT2 + STRING_NEWLINE;
        assertEquals(expectedResult, outputStream.toString());
    }

    @Test
    void parseAndEvaluate_EchoPipeToTee_OutputsCorrectly() throws Exception {
        // Create new file and write 1 line of new text to it
        FileSystemUtils.createEmptyFile(NEW_FILE);
        FileSystemUtils.writeStrToFile(false, NEW_TEXT, NEW_FILE);

        CallCommand echoCommand = buildCallCommand(ECHO_CMD, SAMPLE_TEXT1);
        CallCommand teeCommand = buildCallCommand("tee", "-a",  NEW_FILE);

        PipeCommand pipeCommand = buildPipeCommand(List.of(new CallCommand[]{echoCommand, teeCommand}));
        pipeCommand.evaluate(inputStream, outputStream);
        String expectedResult = NEW_TEXT + SAMPLE_TEXT1 + STRING_NEWLINE;
        assertEquals(expectedResult, FileSystemUtils.readFileContent(NEW_FILE));
    }

    // TODO: Fix catFilesAndStdin
    @Test
    void parseAndEvaluate_EchoPipeToCat_OutputsCorrectly() throws Exception {
        // Create new file and write 1 line of new text to it
        FileSystemUtils.createEmptyFile(NEW_FILE);
        FileSystemUtils.writeStrToFile(false, NEW_TEXT, NEW_FILE);

        CallCommand echoCommand = buildCallCommand(ECHO_CMD, SAMPLE_TEXT1);
        CallCommand catCommand = buildCallCommand("cat", NEW_FILE, "-");

        PipeCommand pipeCommand = buildPipeCommand(List.of(new CallCommand[]{echoCommand, catCommand}));
        pipeCommand.evaluate(inputStream, outputStream);
        // String expectedResult = NEW_TEXT + SAMPLE_TEXT1;
        String expectedResult = NEW_TEXT + STRING_NEWLINE + SAMPLE_TEXT1;
        assertEquals(expectedResult, outputStream.toString());
    }
}
