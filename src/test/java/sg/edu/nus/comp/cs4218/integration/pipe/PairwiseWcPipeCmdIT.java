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
public class PairwiseWcPipeCmdIT {
    static final String CWD = System.getProperty("user.dir");
    static final String TESTING_PATH = CHAR_FILE_SEP + "assets" + CHAR_FILE_SEP + "it" + CHAR_FILE_SEP + "pipe" + CHAR_FILE_SEP + "wc";
    static final String SAMPLE_FILE = "sample.txt";
    static final String NEW_FILE = "new.txt";
    static final String NEW_TEXT = "this is new text";
    static final String WC_CMD = "wc";
    static final String WC_SPACING = StringUtils.multiplyChar(CHAR_SPACE, 7);
    static final String TOTAL_STR = "total";

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
    void parseAndEvaluate_WcPipeToWc_OutputsCorrectly() throws FileNotFoundException, AbstractApplicationException, ShellException {
        CallCommand wcCommand1 = buildCallCommand(WC_CMD, SAMPLE_FILE);
        CallCommand wcCommand2 = buildCallCommand(WC_CMD);

        PipeCommand pipeCommand = buildPipeCommand(List.of(new CallCommand[]{wcCommand1, wcCommand2}));
        pipeCommand.evaluate(inputStream, outputStream);
        String expectedResult = WC_SPACING + "1" + WC_SPACING + "4" + StringUtils.multiplyChar(CHAR_SPACE, 6) + "37" + STRING_NEWLINE;
        assertEquals(expectedResult, outputStream.toString());
    }

    @Test
    void parseAndEvaluate_WcPipeToGrep_OutputsCorrectly() throws Exception {
        // Create new file and write 1 line of new text to it
        FileSystemUtils.createEmptyFile(NEW_FILE);
        FileSystemUtils.writeStrToFile(false, NEW_TEXT, NEW_FILE);

        CallCommand wcCommand = buildCallCommand(WC_CMD, SAMPLE_FILE, NEW_FILE);
        CallCommand grepCommand = buildCallCommand("grep", TOTAL_STR);

        PipeCommand pipeCommand = buildPipeCommand(List.of(new CallCommand[]{wcCommand, grepCommand}));
        pipeCommand.evaluate(inputStream, outputStream);
        String expectedResult = WC_SPACING + "1" + StringUtils.multiplyChar(CHAR_SPACE, 6)
                + "14" + StringUtils.multiplyChar(CHAR_SPACE, 6) + "64" + CHAR_SPACE + TOTAL_STR + STRING_NEWLINE;

        assertEquals(expectedResult, outputStream.toString());
    }

    @Test
    void parseAndEvaluate_WcPipeToCut_OutputsCorrectly() throws FileNotFoundException, AbstractApplicationException, ShellException {
        CallCommand wcCommand = buildCallCommand(WC_CMD, SAMPLE_FILE);
        CallCommand cutCommand = buildCallCommand("cut", "-b", "15-16");

        PipeCommand pipeCommand = buildPipeCommand(List.of(new CallCommand[]{wcCommand, cutCommand}));
        pipeCommand.evaluate(inputStream, outputStream);
        String expectedResult = "10" + STRING_NEWLINE;
        assertEquals(expectedResult, outputStream.toString());
    }

    @Test
    void parseAndEvaluate_WcPipeToPaste_OutputsCorrectly() throws Exception {
        // Create new file and write 1 line of new text to it
        FileSystemUtils.createEmptyFile(NEW_FILE);
        FileSystemUtils.writeStrToFile(false, NEW_TEXT, NEW_FILE);

        CallCommand wcCommand = buildCallCommand(WC_CMD, SAMPLE_FILE);
        CallCommand pasteCommand = buildCallCommand("paste", NEW_FILE, "-");

        PipeCommand pipeCommand = buildPipeCommand(List.of(new CallCommand[]{wcCommand, pasteCommand}));
        pipeCommand.evaluate(inputStream, outputStream);
        String expectedResult = NEW_TEXT + CHAR_TAB + "1" + StringUtils.multiplyChar(CHAR_SPACE, 6)
                + "10" + StringUtils.multiplyChar(CHAR_SPACE, 6) + "48" + CHAR_SPACE + SAMPLE_FILE + STRING_NEWLINE;
        assertEquals(expectedResult, outputStream.toString());
    }

    @Test
    void parseAndEvaluate_WcPipeToUniq_OutputsCorrectly() throws FileNotFoundException, AbstractApplicationException, ShellException {
        CallCommand wcCommand = buildCallCommand(WC_CMD, SAMPLE_FILE, SAMPLE_FILE);
        CallCommand uniqCommand = buildCallCommand("uniq");

        PipeCommand pipeCommand = buildPipeCommand(List.of(new CallCommand[]{wcCommand, uniqCommand}));
        pipeCommand.evaluate(inputStream, outputStream);
        String expectedResult = WC_SPACING  + "1" + StringUtils.multiplyChar(CHAR_SPACE, 6)
                + "10" + StringUtils.multiplyChar(CHAR_SPACE, 6) + "48" + CHAR_SPACE + SAMPLE_FILE + STRING_NEWLINE
                + WC_SPACING + "2" + StringUtils.multiplyChar(CHAR_SPACE, 6)
                + "20" + StringUtils.multiplyChar(CHAR_SPACE, 6) + "96" + CHAR_SPACE + TOTAL_STR + STRING_NEWLINE;
        assertEquals(expectedResult, outputStream.toString());
    }

    @Test
    void parseAndEvaluate_WcPipeToSort_OutputsCorrectly() throws Exception {
        FileSystemUtils.createEmptyFile(NEW_FILE);
        FileSystemUtils.writeStrToFile(false, NEW_TEXT, NEW_FILE);

        CallCommand wcCommand = buildCallCommand(WC_CMD, SAMPLE_FILE, NEW_FILE);
        CallCommand sortCommand = buildCallCommand("sort");

        PipeCommand pipeCommand = buildPipeCommand(List.of(new CallCommand[]{wcCommand, sortCommand}));
        pipeCommand.evaluate(inputStream, outputStream);
        String expectedResult = WC_SPACING  + "0" + WC_SPACING + "4" + StringUtils.multiplyChar(CHAR_SPACE, 6) + "16" + CHAR_SPACE + NEW_FILE + STRING_NEWLINE
                + WC_SPACING  + "1" + StringUtils.multiplyChar(CHAR_SPACE, 6) + "10" + StringUtils.multiplyChar(CHAR_SPACE, 6) + "48" + CHAR_SPACE + SAMPLE_FILE + STRING_NEWLINE
                + WC_SPACING + "1" + StringUtils.multiplyChar(CHAR_SPACE, 6) + 14 + StringUtils.multiplyChar(CHAR_SPACE, 6) + 64 + CHAR_SPACE + TOTAL_STR + STRING_NEWLINE;
        assertEquals(expectedResult, outputStream.toString());
    }

    @Test
    void parseAndEvaluate_WcPipeToTee_OutputsCorrectly() throws Exception {
        // Create new file and write 1 line of new text to it
        FileSystemUtils.createEmptyFile(NEW_FILE);
        FileSystemUtils.writeStrToFile(false, NEW_TEXT, NEW_FILE);

        CallCommand echoCommand = buildCallCommand(WC_CMD, SAMPLE_FILE);
        CallCommand teeCommand = buildCallCommand("tee", "-a",  NEW_FILE);

        PipeCommand pipeCommand = buildPipeCommand(List.of(new CallCommand[]{echoCommand, teeCommand}));
        pipeCommand.evaluate(inputStream, outputStream);
        String expectedResult = NEW_TEXT
                + WC_SPACING  + "1" + StringUtils.multiplyChar(CHAR_SPACE, 6) + "10" + StringUtils.multiplyChar(CHAR_SPACE, 6) + "48" + CHAR_SPACE + SAMPLE_FILE + STRING_NEWLINE;
        assertEquals(expectedResult, FileSystemUtils.readFileContent(NEW_FILE));
    }

    @Test
    void parseAndEvaluate_WcPipeToCat_OutputsCorrectly() throws Exception {
        // Create new file and write 1 line of new text to it
        FileSystemUtils.createEmptyFile(NEW_FILE);
        FileSystemUtils.writeStrToFile(false, NEW_TEXT, NEW_FILE);

        CallCommand echoCommand = buildCallCommand(WC_CMD, SAMPLE_FILE);
        CallCommand catCommand = buildCallCommand("cat", NEW_FILE, "-");

        PipeCommand pipeCommand = buildPipeCommand(List.of(new CallCommand[]{echoCommand, catCommand}));
        pipeCommand.evaluate(inputStream, outputStream);
        String expectedResult = NEW_TEXT + STRING_NEWLINE
                + WC_SPACING  + "1" + StringUtils.multiplyChar(CHAR_SPACE, 6) + "10" + StringUtils.multiplyChar(CHAR_SPACE, 6) + "48" + CHAR_SPACE + SAMPLE_FILE + STRING_NEWLINE;
        assertEquals(expectedResult, outputStream.toString());
    }
}
