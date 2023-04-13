package sg.edu.nus.comp.cs4218.integration;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sg.edu.nus.comp.cs4218.Environment;
import sg.edu.nus.comp.cs4218.impl.cmd.CallCommand;
import sg.edu.nus.comp.cs4218.impl.util.ApplicationRunner;
import sg.edu.nus.comp.cs4218.impl.util.ArgumentResolver;
import sg.edu.nus.comp.cs4218.impl.util.FileSystemUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.CHAR_FILE_SEP;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.CHAR_TAB;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.STRING_NEWLINE;

@SuppressWarnings("PMD.ClassNamingConventions")
public class CallCommandIT {
    static final String CWD = System.getProperty("user.dir");
    static final String TESTING_PATH = CHAR_FILE_SEP + "assets" + CHAR_FILE_SEP + "it" + CHAR_FILE_SEP + "call";
    static final String SAMPLE_FILE = "sample.txt";
    static final String CONTENT_LINE1 = "This is a sample text.";
    static final String CONTENT_LINE2 =  "This is the second line.";
    static final String SAMPLE_CONTENT =  CONTENT_LINE1 + STRING_NEWLINE + CONTENT_LINE2;
    static final String NEW_FILE1 = "new1.txt";
    static final String NEW_FILE2 = "new2.txt";
    static final String NEW_CONTENT_LINE1 = "This is new content.";
    static final String NEW_CONTENT_LINE2 =  "This is the second line of new content.";
    static final String NEW_CONTENT = NEW_CONTENT_LINE1 + STRING_NEWLINE + NEW_CONTENT_LINE2;
    static final String DIR = "dir";


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
        if (FileSystemUtils.fileOrDirExist(NEW_FILE1)) {
            FileSystemUtils.deleteFileOrDir(NEW_FILE1);
        }
        if (FileSystemUtils.fileOrDirExist(NEW_FILE2)) {
            FileSystemUtils.deleteFileOrDir(NEW_FILE2);
        }
        if (FileSystemUtils.fileOrDirExist(DIR + CHAR_FILE_SEP + NEW_FILE2)) {
            FileSystemUtils.deleteFileOrDir(DIR + CHAR_FILE_SEP + NEW_FILE2);
        }
    }



    private CallCommand buildCallCommand(String... args) {
        ApplicationRunner applicationRunner = new ApplicationRunner();
        ArgumentResolver argumentResolver = new ArgumentResolver();
        return new CallCommand(List.of(args), applicationRunner, argumentResolver);
    }

    @Test
    void evaluate_EchoRedirectToFile_AddsLinesToFile() throws Exception {
        FileSystemUtils.createEmptyFile(NEW_FILE1);
        String[] args = {"echo", NEW_CONTENT, ">", NEW_FILE1};
        CallCommand callCommand = buildCallCommand(args);
        callCommand.evaluate(inputStream, outputStream);
        String output = FileSystemUtils.readFileContent(NEW_FILE1);
        assertEquals(NEW_CONTENT + STRING_NEWLINE, output);
    }

    @Test
    void evaluate_PasteWithFileStdInRedirectFromFileToFile_AddsLinesToFile() throws Exception {
        FileSystemUtils.createEmptyFile(NEW_FILE1);
        FileSystemUtils.writeStrToFile(false, NEW_CONTENT, NEW_FILE1);

        FileSystemUtils.createEmptyFile(NEW_FILE2);

        String[] args = {"paste", NEW_FILE1, "-", "<", SAMPLE_FILE, ">", NEW_FILE2};
        CallCommand callCommand = buildCallCommand(args);
        callCommand.evaluate(inputStream, outputStream);
        String output = FileSystemUtils.readFileContent(NEW_FILE2);
        assertEquals(NEW_CONTENT_LINE1 + CHAR_TAB + CONTENT_LINE1 + STRING_NEWLINE
                        + NEW_CONTENT_LINE2 + CHAR_TAB + CONTENT_LINE2 + STRING_NEWLINE, output);
    }

    @Test
    void evaluate_CatWithGlobbing_OutputsCorrectContentToOutputStream() throws Exception {
        // Creates file for concatenation
        FileSystemUtils.createEmptyFile(NEW_FILE1);
        FileSystemUtils.writeStrToFile(false, NEW_CONTENT, NEW_FILE1);

        String[] args = {"cat", "*.txt"};
        CallCommand callCommand = buildCallCommand(args);
        callCommand.evaluate(inputStream, outputStream);
        String output = outputStream.toString();
        assertEquals(NEW_CONTENT + SAMPLE_CONTENT, output);
    }

    @Test
    void evaluate_CatWithGlobbingAndRedirectToFileInDir_AddsCorrectContentToFile() throws Exception {
        // Creates file for concatenation
        FileSystemUtils.createEmptyFile(NEW_FILE1);
        FileSystemUtils.writeStrToFile(false, NEW_CONTENT, NEW_FILE1);

        // Creates file to be redirected to
        FileSystemUtils.createEmptyFile(DIR + CHAR_FILE_SEP + NEW_FILE2);

        String[] args = {"cat", "*.txt", ">" , DIR + CHAR_FILE_SEP + NEW_FILE2};

        CallCommand callCommand = buildCallCommand(args);
        callCommand.evaluate(inputStream, outputStream);
        String output = FileSystemUtils.readFileContent(DIR + CHAR_FILE_SEP + NEW_FILE2);
        assertEquals(NEW_CONTENT + SAMPLE_CONTENT, output);
    }
}
