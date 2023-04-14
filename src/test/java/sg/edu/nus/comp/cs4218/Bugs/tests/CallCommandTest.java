package sg.edu.nus.comp.cs4218.Bugs.tests;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import sg.edu.nus.comp.cs4218.Environment;
import sg.edu.nus.comp.cs4218.impl.cmd.CallCommand;
import sg.edu.nus.comp.cs4218.impl.util.ApplicationRunner;
import sg.edu.nus.comp.cs4218.impl.util.ArgumentResolver;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static sg.edu.nus.comp.cs4218.Bugs.utils.StringUtils.CHAR_FILE_SEP;
import static sg.edu.nus.comp.cs4218.Bugs.utils.StringUtils.STRING_NEWLINE;

@SuppressWarnings("PMD.LongVariable")
public class CallCommandTest {
    static final String CWD = System.getProperty("user.dir");
    static final String TESTING_PATH = CHAR_FILE_SEP + "src" + CHAR_FILE_SEP + "test" + CHAR_FILE_SEP
            + "java" + CHAR_FILE_SEP + "sg" + CHAR_FILE_SEP + "edu" + CHAR_FILE_SEP + "nus" + CHAR_FILE_SEP + "comp"
            + CHAR_FILE_SEP + "cs4218" + CHAR_FILE_SEP + "Bugs" + CHAR_FILE_SEP + "assets" + CHAR_FILE_SEP + "app"
            + CHAR_FILE_SEP + "ls";
    static final String TEST_DIR_1 = "testDir1";
    static final String TEST_DIR_2 = "testDir2";
    static final String TEST_DIR_3 = "testDir3";
    static final String TEST_DIR_1_1 = "testDir1-1";
    static final String TEST_DIR_1_2 = "testDir1-2";
    static final String ALP_TEXTFILE1 = "abc.txt";
    static final String ALP_TEXTFILE2 = "abc.rtf";
    static final String NUM_TEXTFILE = "123.txt";
    InputStream inputStream;
    OutputStream outputStream;
    private CallCommand buildCallCommand(String... args) {
        ApplicationRunner applicationRunner = new ApplicationRunner();
        ArgumentResolver argumentResolver = new ArgumentResolver();
        return new CallCommand(List.of(args), applicationRunner, argumentResolver);
    }


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

    @Test
    @DisplayName("Rebuttal 30")
    void evaluate_LsExistingDirWithGlobbingArgs_OutputsContentCorrectly() throws Exception {
        String existingDirWithAsterisk = TEST_DIR_1 + CHAR_FILE_SEP + "*";
        String[] args = {"ls", existingDirWithAsterisk};
        CallCommand callCommand = buildCallCommand(args);
        callCommand.evaluate(inputStream, outputStream);
        String expectedOutput = TEST_DIR_1 + CHAR_FILE_SEP + ALP_TEXTFILE1 + STRING_NEWLINE + STRING_NEWLINE
                + TEST_DIR_1 + CHAR_FILE_SEP + TEST_DIR_1_1  + ":" + STRING_NEWLINE
                + ALP_TEXTFILE1 + STRING_NEWLINE + STRING_NEWLINE
                + TEST_DIR_1 + CHAR_FILE_SEP + TEST_DIR_1_2 + ":" + STRING_NEWLINE
                + ALP_TEXTFILE1 + STRING_NEWLINE;
        assertEquals(expectedOutput, outputStream.toString());
    }

    @Test
    @DisplayName("Rebuttal 31")
    void evaluate_LsNestedGlobbingArgs_OutputsContentCorrectly() throws Exception {
        String asteriskWithAsterisk = "*/*";
        String[] args = {"ls", asteriskWithAsterisk};
        CallCommand callCommand = buildCallCommand(args);
        callCommand.evaluate(inputStream, outputStream);
        String expectedOutput = TEST_DIR_1 + CHAR_FILE_SEP  + ALP_TEXTFILE1 + STRING_NEWLINE
                + TEST_DIR_2 + CHAR_FILE_SEP + ALP_TEXTFILE1 + STRING_NEWLINE
                + TEST_DIR_3 + CHAR_FILE_SEP + NUM_TEXTFILE + STRING_NEWLINE
                + TEST_DIR_3 + CHAR_FILE_SEP + ALP_TEXTFILE2 + STRING_NEWLINE + STRING_NEWLINE
                + TEST_DIR_1 + CHAR_FILE_SEP + TEST_DIR_1_1 + ":" + STRING_NEWLINE
                + ALP_TEXTFILE1 + STRING_NEWLINE + STRING_NEWLINE
                + TEST_DIR_1 + CHAR_FILE_SEP + TEST_DIR_1_2 + ":" + STRING_NEWLINE
                + ALP_TEXTFILE1 + STRING_NEWLINE;
        assertEquals(expectedOutput, outputStream.toString());
    }
}
