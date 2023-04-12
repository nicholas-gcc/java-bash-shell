package sg.edu.nus.comp.cs4218.integration.pipe;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static sg.edu.nus.comp.cs4218.impl.util.FileSystemUtils.fileOrDirExist;
import static sg.edu.nus.comp.cs4218.impl.util.FileSystemUtils.getAbsolutePathName;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.CHAR_FILE_SEP;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.CHAR_SPACE;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.CHAR_TAB;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.STRING_NEWLINE;

@SuppressWarnings({"PMD.ClassNamingConventions"})
public class PairwiseTeePipeCmdIT {
    static final String CWD = System.getProperty("user.dir");
    static final String TESTING_PATH = CHAR_FILE_SEP + "assets" + CHAR_FILE_SEP + "it" + CHAR_FILE_SEP + "pipe" + CHAR_FILE_SEP + "tee";
    static final String SAMPLE_LINE1 = "This is sample text." ;
    static final String SAMPLE_LINE2 = "This is second line.";
    static final String SAMPLE_TEXT = SAMPLE_LINE1 + STRING_NEWLINE + SAMPLE_LINE2;
    static final String NEW_FILE = "new.txt";
    static final String NEW_TEXT = "This is new text";
    static final String TEE_CMD = "tee";
    static final String WC_SPACING = StringUtils.multiplyChar(CHAR_SPACE, 7);
    InputStream inputStream;
    OutputStream outputStream;

    // Method isolated to this file, originally from FileSystemUtils
    public static void createEmptyFile(String filename) throws Exception {
        String absolutePath = getAbsolutePathName(filename);
        if (fileOrDirExist(absolutePath)) {
            return;
        }

        File file = new File(absolutePath);

        if (!file.createNewFile()) {
            throw new Exception("Unable to create file: " + filename);
        }
    }
    @BeforeEach
    void setup() {
        Environment.currentDirectory += TESTING_PATH;
        // Create input stream with sample text
        inputStream = new ByteArrayInputStream(SAMPLE_TEXT.getBytes());
        outputStream = new ByteArrayOutputStream();
    }


    @AfterEach
    void handleAfterEach() throws Exception {
        Environment.currentDirectory = CWD;
        inputStream.close();
        outputStream.close();
        if (fileOrDirExist(NEW_FILE)) {
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
    void parseAndEvaluate_TeePipeToWc_OutputsCorrectly() throws FileNotFoundException, AbstractApplicationException, ShellException {
        CallCommand teeCommand = buildCallCommand(TEE_CMD);
        CallCommand wcCommand = buildCallCommand("wc");

        PipeCommand pipeCommand = buildPipeCommand(List.of(new CallCommand[]{teeCommand, wcCommand}));
        pipeCommand.evaluate(inputStream, outputStream);
        String expectedResult = WC_SPACING + "2" + WC_SPACING+ "8" + StringUtils.multiplyChar(CHAR_SPACE, 6) + "44" + STRING_NEWLINE;
        assertEquals(expectedResult, outputStream.toString());
    }

    @Test
    void parseAndEvaluate_TeePipeToGrep_OutputsCorrectly() throws FileNotFoundException, AbstractApplicationException, ShellException {
        CallCommand teeCommand = buildCallCommand(TEE_CMD);
        CallCommand grepCommand = buildCallCommand("grep", "second");

        PipeCommand pipeCommand = buildPipeCommand(List.of(new CallCommand[]{teeCommand, grepCommand}));
        pipeCommand.evaluate(inputStream, outputStream);
        String expectedResult = SAMPLE_LINE2 + STRING_NEWLINE;
        assertEquals(expectedResult, outputStream.toString());
    }

    @Test
    void parseAndEvaluate_TeePipeToCut_OutputsCorrectly() throws FileNotFoundException, AbstractApplicationException, ShellException {
        CallCommand teeCommand = buildCallCommand(TEE_CMD);
        CallCommand cutCommand = buildCallCommand("cut", "-b", "9-14");

        PipeCommand pipeCommand = buildPipeCommand(List.of(new CallCommand[]{teeCommand, cutCommand}));
        pipeCommand.evaluate(inputStream, outputStream);
        String expectedResult = "sample" + STRING_NEWLINE + "second" + STRING_NEWLINE;
        assertEquals(expectedResult, outputStream.toString());
    }

    @Test
    void parseAndEvaluate_TeePipeToPaste_OutputsCorrectly() throws Exception {
        // Create new file and write 2 lines of new text to it
        createEmptyFile(NEW_FILE);
        FileSystemUtils.writeStrToFile(false, NEW_TEXT + STRING_NEWLINE + NEW_TEXT, NEW_FILE);

        CallCommand teeCommand = buildCallCommand(TEE_CMD);
        CallCommand cutCommand = buildCallCommand("paste", NEW_FILE, "-");

        PipeCommand pipeCommand = buildPipeCommand(List.of(new CallCommand[]{teeCommand, cutCommand}));
        pipeCommand.evaluate(inputStream, outputStream);
        String expectedResult = NEW_TEXT + CHAR_TAB + SAMPLE_LINE1 + STRING_NEWLINE
                + NEW_TEXT + CHAR_TAB + SAMPLE_LINE2 + STRING_NEWLINE;
        assertEquals(expectedResult, outputStream.toString());
    }

    @Test
    void parseAndEvaluate_TeePipeToUniq_OutputsCorrectly() throws FileNotFoundException, AbstractApplicationException, ShellException {
        CallCommand teeCommand = buildCallCommand(TEE_CMD);
        CallCommand cutCommand = buildCallCommand("uniq");

        PipeCommand pipeCommand = buildPipeCommand(List.of(new CallCommand[]{teeCommand, cutCommand}));
        pipeCommand.evaluate(inputStream, outputStream);
        String expectedResult = SAMPLE_TEXT + STRING_NEWLINE;
        assertEquals(expectedResult, outputStream.toString());
    }

    @Test
    void parseAndEvaluate_TeePipeToSort_OutputsCorrectly() throws Exception {
        // Create new file and write 1 line of new text to it
        createEmptyFile(NEW_FILE);
        FileSystemUtils.writeStrToFile(false, NEW_TEXT, NEW_FILE);

        CallCommand teeCommand = buildCallCommand(TEE_CMD);
        CallCommand sortCommand = buildCallCommand("sort", NEW_FILE, "-");

        PipeCommand pipeCommand = buildPipeCommand(List.of(new CallCommand[]{teeCommand, sortCommand}));
        pipeCommand.evaluate(inputStream, outputStream);
        String expectedResult = NEW_TEXT + STRING_NEWLINE + SAMPLE_TEXT + STRING_NEWLINE;
        assertEquals(expectedResult, outputStream.toString());

    }

    @Test
    void parseAndEvaluate_TeePipeToTee_OutputsCorrectly() throws Exception {
        // Create new file and write 1 line of new text to it
        createEmptyFile(NEW_FILE);
        FileSystemUtils.writeStrToFile(false, NEW_TEXT, NEW_FILE);

        CallCommand teeCommand1 = buildCallCommand(TEE_CMD);
        CallCommand teeCommand2 = buildCallCommand(TEE_CMD, "-a",  NEW_FILE);

        PipeCommand pipeCommand = buildPipeCommand(List.of(new CallCommand[]{teeCommand1, teeCommand2}));
        pipeCommand.evaluate(inputStream, outputStream);
        String expectedResult = NEW_TEXT + SAMPLE_TEXT + STRING_NEWLINE;
        assertEquals(expectedResult, FileSystemUtils.readFileContent(NEW_FILE));
    }

    @Test
    void parseAndEvaluate_TeePipeToCat_OutputsCorrectly() throws Exception {
        // Create new file and write 1 line of new text to it
        createEmptyFile(NEW_FILE);
        FileSystemUtils.writeStrToFile(false, NEW_TEXT, NEW_FILE);

        CallCommand teeCommand = buildCallCommand(TEE_CMD);
        CallCommand catCommand = buildCallCommand("cat", NEW_FILE, "-");

        PipeCommand pipeCommand = buildPipeCommand(List.of(new CallCommand[]{teeCommand, catCommand}));
        pipeCommand.evaluate(inputStream, outputStream);
        String expectedResult = NEW_TEXT + STRING_NEWLINE + SAMPLE_TEXT + STRING_NEWLINE;;
        assertEquals(expectedResult, outputStream.toString());
    }
}
