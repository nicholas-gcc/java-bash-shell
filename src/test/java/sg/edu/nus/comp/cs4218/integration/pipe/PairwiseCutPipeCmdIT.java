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
public class PairwiseCutPipeCmdIT {
    static final String CWD = System.getProperty("user.dir");
    static final String TESTING_PATH = CHAR_FILE_SEP + "assets" + CHAR_FILE_SEP + "it" + CHAR_FILE_SEP + "pipe" + CHAR_FILE_SEP + "cut";
    static final String SAMPLE_FILE = "sample.txt";
    static final String NEW_FILE = "new.txt";
    static final String NEW_TEXT = "this is new text";
    static final String A3_STR = "a3";
    static final String B1_STR = "b1";
    static final String C2_STR = "c2";
    static final String CUT_CMD = "cut";
    static final String BYTE_OPTION = "-b";
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
    void parseAndEvaluate_CutPipeToWc_OutputsCorrectly() throws FileNotFoundException, AbstractApplicationException, ShellException {
        CallCommand cutCommand = buildCallCommand(CUT_CMD, BYTE_OPTION, "1-8", SAMPLE_FILE);
        CallCommand wcCommand = buildCallCommand("wc");

        PipeCommand pipeCommand = buildPipeCommand(List.of(new CallCommand[]{cutCommand, wcCommand}));
        pipeCommand.evaluate(inputStream, outputStream);
        String expectedResult = WC_SPACING + "2" + WC_SPACING+ "4" + StringUtils.multiplyChar(CHAR_SPACE, 6) + "20" + STRING_NEWLINE;
        assertEquals(expectedResult, outputStream.toString());
    }

    @Test
    void parseAndEvaluate_CutPipeToGrep_OutputsCorrectly() throws FileNotFoundException, AbstractApplicationException, ShellException {
        CallCommand cutCommand = buildCallCommand(CUT_CMD, BYTE_OPTION, "1-9", SAMPLE_FILE);
        // Should grep only the first line
        CallCommand wcCommand = buildCallCommand("grep", "a");

        PipeCommand pipeCommand = buildPipeCommand(List.of(new CallCommand[]{cutCommand, wcCommand}));
        pipeCommand.evaluate(inputStream, outputStream);
        String expectedResult = "This is a" + STRING_NEWLINE;
        assertEquals(expectedResult, outputStream.toString());
    }

    @Test
    void parseAndEvaluate_CutPipeToCut_OutputsCorrectly() throws FileNotFoundException, AbstractApplicationException, ShellException {
        CallCommand cutCommand1 = buildCallCommand(CUT_CMD, BYTE_OPTION, "2-9", SAMPLE_FILE);
        CallCommand cutCommand2 = buildCallCommand(CUT_CMD, BYTE_OPTION, "5-6", "-");

        PipeCommand pipeCommand = buildPipeCommand(List.of(new CallCommand[]{cutCommand1, cutCommand2}));
        pipeCommand.evaluate(inputStream, outputStream);
        String expectedResult = "is" + STRING_NEWLINE + "is" + STRING_NEWLINE;
        assertEquals(expectedResult, outputStream.toString());
    }

    @Test
    void parseAndEvaluate_CutPipeToPaste_OutputsCorrectly() throws Exception {
        // Create new file and write 1 line of new text to it
        FileSystemUtils.createEmptyFile(NEW_FILE);
        FileSystemUtils.writeStrToFile(false, NEW_TEXT, NEW_FILE);

        CallCommand cutCommand = buildCallCommand(CUT_CMD, BYTE_OPTION, "1-9", SAMPLE_FILE);
        CallCommand pasteCommand = buildCallCommand("paste", NEW_FILE, "-");

        PipeCommand pipeCommand = buildPipeCommand(List.of(new CallCommand[]{cutCommand, pasteCommand}));
        pipeCommand.evaluate(inputStream, outputStream);
        String expectedResult = NEW_TEXT + CHAR_TAB + "This is a" + STRING_NEWLINE + CHAR_TAB + "This is t" + STRING_NEWLINE;
        assertEquals(expectedResult, outputStream.toString());
    }

    @Test
    void parseAndEvaluate_CutPipeToUniq_OutputsCorrectly() throws FileNotFoundException, AbstractApplicationException, ShellException {
        CallCommand cutCommand = buildCallCommand(CUT_CMD, BYTE_OPTION, "1-7", SAMPLE_FILE);
        CallCommand uniqCommand = buildCallCommand("uniq");

        PipeCommand pipeCommand = buildPipeCommand(List.of(new CallCommand[]{cutCommand, uniqCommand}));
        pipeCommand.evaluate(inputStream, outputStream);

        String expectedResult = "This is" + STRING_NEWLINE;
        assertEquals(expectedResult, outputStream.toString());
    }

    @Test
    void parseAndEvaluate_CutPipeToSort_OutputsCorrectly() throws Exception {
        // Create new file and write "a3", "b1" and "c2" to it
        FileSystemUtils.createEmptyFile(NEW_FILE);
        FileSystemUtils.writeStrToFile(false, A3_STR + STRING_NEWLINE + B1_STR + STRING_NEWLINE + C2_STR, NEW_FILE);
        CallCommand cutCommand = buildCallCommand(CUT_CMD, BYTE_OPTION, "2", NEW_FILE);
        CallCommand sortCommand = buildCallCommand("sort");

        PipeCommand pipeCommand = buildPipeCommand(List.of(new CallCommand[]{cutCommand, sortCommand}));
        pipeCommand.evaluate(inputStream, outputStream);

        String expectedResult = "1" + STRING_NEWLINE + "2" + STRING_NEWLINE + "3" + STRING_NEWLINE;
        assertEquals(expectedResult, outputStream.toString());
    }

    @Test
    void parseAndEvaluate_CutPipeToTee_OutputsCorrectly() throws Exception {
        // Create new file and write 1 line of new text to it
        FileSystemUtils.createEmptyFile(NEW_FILE);
        FileSystemUtils.writeStrToFile(false, NEW_TEXT, NEW_FILE);

        CallCommand cutCommand = buildCallCommand(CUT_CMD, BYTE_OPTION, "9", SAMPLE_FILE);
        CallCommand teeCommand = buildCallCommand("tee", "-a",  NEW_FILE);

        PipeCommand pipeCommand = buildPipeCommand(List.of(new CallCommand[]{cutCommand, teeCommand}));
        pipeCommand.evaluate(inputStream, outputStream);
        String expectedResult = NEW_TEXT + "a" + STRING_NEWLINE + "t" + STRING_NEWLINE;
        assertEquals(expectedResult, FileSystemUtils.readFileContent(NEW_FILE));
    }

    @Test
    void parseAndEvaluate_CutPipeToCat_OutputsCorrectly() throws Exception {
        // Create new file and write 1 line of new text to it
        FileSystemUtils.createEmptyFile(NEW_FILE);
        FileSystemUtils.writeStrToFile(false, NEW_TEXT, NEW_FILE);

        CallCommand grepCommand = buildCallCommand(CUT_CMD, BYTE_OPTION, "9", SAMPLE_FILE);
        CallCommand catCommand = buildCallCommand("cat", NEW_FILE, "-");

        PipeCommand pipeCommand = buildPipeCommand(List.of(new CallCommand[]{grepCommand, catCommand}));
        pipeCommand.evaluate(inputStream, outputStream);
        String expectedResult = NEW_TEXT + STRING_NEWLINE + "a" + STRING_NEWLINE + "t" + STRING_NEWLINE;
        assertEquals(expectedResult, outputStream.toString());
    }

}
