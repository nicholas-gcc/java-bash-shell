package sg.edu.nus.comp.cs4218.integration;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sg.edu.nus.comp.cs4218.Environment;
import sg.edu.nus.comp.cs4218.Shell;
import sg.edu.nus.comp.cs4218.exception.AbstractApplicationException;
import sg.edu.nus.comp.cs4218.exception.ShellException;
import sg.edu.nus.comp.cs4218.impl.ShellImpl;
import sg.edu.nus.comp.cs4218.impl.util.FileSystemUtils;
import sg.edu.nus.comp.cs4218.impl.util.IOUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.CHAR_FILE_SEP;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.CHAR_TAB;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.STRING_NEWLINE;

public class SequenceCommandIntegrationTest {
    static final String CWD = System.getProperty("user.dir");
    static final String TESTING_PATH = "assets" + CHAR_FILE_SEP + "it" + CHAR_FILE_SEP + "sequence";
    static final String SAMPLE_FILE = "sample.txt";

    static final String DELETE_FILE = "delete_me.txt";
    static final String CONTENT_LINE1 = "This is a sample text.";
    static final String CONTENT_LINE2 =  "This is the second line.";
    static final String SAMPLE_CONTENT =  CONTENT_LINE1 + STRING_NEWLINE + CONTENT_LINE2;
    static final String NEW_FILE = "new.txt";
    static final String NEW_FILE1 = "new1.txt";
    static final String NEW_FILE2 = "new2.txt";
    static final String NEW_CONTENT_LINE1 = "This is new content.";
    static final String NEW_CONTENT_LINE2 =  "This is the second line of new content.";
    static final String NEW_CONTENT = NEW_CONTENT_LINE1 + STRING_NEWLINE + NEW_CONTENT_LINE2;
    static final String DIR = "dir";

    static final String CP_COMMAND = "cp ";


    InputStream inputStream;
    OutputStream outputStream;
    Shell shell = new ShellImpl();

    @BeforeEach
    void setup() {
        Environment.currentDirectory += CHAR_FILE_SEP + TESTING_PATH;
        inputStream = System.in;
        outputStream = new ByteArrayOutputStream();
    }

//    @AfterEach
//    void reset() throws IOException {
//        Environment.currentDirectory = CWD;
//        inputStream.close();
//        outputStream.close();
//    }

    @AfterEach
    void deleteFiles() throws Exception {

        if (FileSystemUtils.fileOrDirExist(NEW_FILE)) {
            FileSystemUtils.deleteFileOrDir(NEW_FILE);
        }
        if (FileSystemUtils.fileOrDirExist(NEW_FILE1)) {
            FileSystemUtils.deleteFileOrDir(NEW_FILE1);
        }
        if (FileSystemUtils.fileOrDirExist(NEW_FILE2)) {
            FileSystemUtils.deleteFileOrDir(NEW_FILE2);
        }
        if (FileSystemUtils.fileOrDirExist(DIR + CHAR_FILE_SEP + NEW_FILE2)) {
            FileSystemUtils.deleteFileOrDir(DIR + CHAR_FILE_SEP + NEW_FILE2);
        }
        Environment.currentDirectory = CWD;
        inputStream.close();
        outputStream.close();
    }

    @Test
    void parseAndEvaluate_cdAndCat_shouldPrintFileContent() throws FileNotFoundException, AbstractApplicationException, ShellException {
        Environment.currentDirectory = CWD;
        String command = "cd " + TESTING_PATH + "; cat " + SAMPLE_FILE;
        shell.parseAndEvaluate(command, outputStream);
        assertEquals(SAMPLE_CONTENT + STRING_NEWLINE, outputStream.toString());
    }

    @Test
    void parseAndEvaluate_CdAndCp_shouldPrintFileContent() throws Exception {
        Environment.currentDirectory = CWD;
        String command = "cd " + TESTING_PATH + "; cp " + SAMPLE_FILE + " " + NEW_FILE;
        shell.parseAndEvaluate(command, outputStream);
        String output = FileSystemUtils.readFileContent(NEW_FILE);
        assertEquals(SAMPLE_CONTENT, output);
    }

    @Test
    void parseAndEvaluate_CpAndCutC_shouldPrintCutContent() throws Exception {
        String command = CP_COMMAND + SAMPLE_FILE + " " + NEW_FILE + "; cut -c 6-7 " + NEW_FILE;
        shell.parseAndEvaluate(command, outputStream);
        String expected = "is" + STRING_NEWLINE + "is" + STRING_NEWLINE;
        assertEquals(expected, outputStream.toString());
    }

    @Test
    void parseAndEvaluate_CpAndCutB_shouldPrintCutContent() throws Exception {
        String command = CP_COMMAND + SAMPLE_FILE + " " + NEW_FILE + "; cut -b 2 " + NEW_FILE;
        shell.parseAndEvaluate(command, outputStream);
        String expected = "h" + STRING_NEWLINE + "h" + STRING_NEWLINE;
        assertEquals(expected, outputStream.toString());
    }

    @Test
    void parseAndEvaluate_CdAndLs_shouldPrintFiles() throws Exception {
        Environment.currentDirectory = CWD;
        String command = "cd " + TESTING_PATH + "; ls ";
        shell.parseAndEvaluate(command, outputStream);
        String expected = DELETE_FILE + STRING_NEWLINE
                + DIR + STRING_NEWLINE
                + SAMPLE_FILE + STRING_NEWLINE;
        assertEquals(expected, outputStream.toString());
    }

    @Test
    void parseAndEvaluate_CpAndGrep_shouldGrepProperly() throws Exception {
        String command = CP_COMMAND + SAMPLE_FILE + " " + NEW_FILE + "; grep sample " + NEW_FILE;
        shell.parseAndEvaluate(command, outputStream);
        String expected = CONTENT_LINE1 + STRING_NEWLINE;
        assertEquals(expected, outputStream.toString());

        command = CP_COMMAND + SAMPLE_FILE + " " + NEW_FILE + "; grep -H sample " + NEW_FILE;
        shell.parseAndEvaluate(command, outputStream);
        expected += NEW_FILE + ": " + CONTENT_LINE1 + STRING_NEWLINE;
        assertEquals(expected, outputStream.toString());
    }

    @Test
    void parseAndEvaluate_MvAndCdAndLs_shouldMoveCorrectly() throws Exception {
        FileSystemUtils.createEmptyFile(NEW_FILE);

        String command = "mv " + NEW_FILE + " " + DIR + "; ls " + DIR;
        shell.parseAndEvaluate(command, outputStream);
        String expected = DIR + ":"+ STRING_NEWLINE + DELETE_FILE + STRING_NEWLINE + NEW_FILE + STRING_NEWLINE;
        assertEquals(expected, outputStream.toString());

        command = "cd dir; mv new.txt ..;ls";
        shell.parseAndEvaluate(command, outputStream);
        expected += DELETE_FILE + STRING_NEWLINE;
        assertEquals(expected, outputStream.toString());

        Environment.currentDirectory = CWD + CHAR_FILE_SEP + TESTING_PATH;;
    }

    @Test
    void parseAndEvaluate_PasteSAndUniq_shouldPrintCorrectly() throws Exception {
        FileSystemUtils.createEmptyFile(NEW_FILE1);
        FileSystemUtils.writeStrToFile(false, NEW_CONTENT, NEW_FILE1);

        FileSystemUtils.createEmptyFile(NEW_FILE2);

        String command = "paste -s " + NEW_FILE1 + " " + SAMPLE_FILE + " > " + NEW_FILE2 + "; uniq " + NEW_FILE2;
        shell.parseAndEvaluate(command, outputStream);
        String expected = NEW_CONTENT_LINE1 + CHAR_TAB + NEW_CONTENT_LINE2 + STRING_NEWLINE
                + CONTENT_LINE1 + CHAR_TAB + CONTENT_LINE2 + STRING_NEWLINE;
        assertEquals(expected, outputStream.toString());
    }

    @Test
    void parseAndEvaluate_MvRmAndLs_ShouldRunCorrectly() throws Exception {
        FileSystemUtils.createEmptyFile(NEW_FILE1);
        FileSystemUtils.writeStrToFile(false, NEW_CONTENT, NEW_FILE1);

        String command = "mv " + NEW_FILE1 + " " + NEW_FILE2 + "; ls" +  "; rm " + NEW_FILE2 + "; ls";
        shell.parseAndEvaluate(command, outputStream);
        String expected = DELETE_FILE + STRING_NEWLINE
                + DIR + STRING_NEWLINE
                + NEW_FILE2 + STRING_NEWLINE
                + SAMPLE_FILE + STRING_NEWLINE
                + DELETE_FILE + STRING_NEWLINE
                + DIR + STRING_NEWLINE
                + SAMPLE_FILE + STRING_NEWLINE;
        assertEquals(expected, outputStream.toString());
    }

   @Test
   void parseAndEvaluate_PasteAndSort_ShouldSortProperly() throws Exception {
       FileSystemUtils.createEmptyFile(NEW_FILE1);
       FileSystemUtils.writeStrToFile(false, NEW_CONTENT, NEW_FILE1);

       FileSystemUtils.createEmptyFile(NEW_FILE2);

       String command = "paste " + NEW_FILE1 + " " + SAMPLE_FILE + " > " + NEW_FILE2 + "; sort " + NEW_FILE2;
       shell.parseAndEvaluate(command, outputStream);
       String expected = NEW_CONTENT_LINE1 + CHAR_TAB + CONTENT_LINE1 + STRING_NEWLINE
       + NEW_CONTENT_LINE2 + CHAR_TAB + CONTENT_LINE2 + STRING_NEWLINE;
       assertEquals(expected, outputStream.toString());
   }

    @Test
    void parseAndEvaluate_EchoAndWc_ShouldRunProperly() throws Exception {
        FileSystemUtils.createEmptyFile(NEW_FILE);

        String command = "echo \"" + CONTENT_LINE1 + "\" > " + NEW_FILE +
                "; wc -l " + NEW_FILE + "; wc -w " + NEW_FILE;
        shell.parseAndEvaluate(command, outputStream);
        String expected = "       1 " + NEW_FILE + STRING_NEWLINE + "       5 " + NEW_FILE + STRING_NEWLINE;
        assertEquals(expected, outputStream.toString());
    }

    @Test
    void parseAndEvaluate_TeeAndUniq_ShouldRunProperly() throws Exception {
        FileSystemUtils.createEmptyFile(NEW_FILE);

        String input = CONTENT_LINE1 + STRING_NEWLINE +
                CONTENT_LINE1 + STRING_NEWLINE +
                CONTENT_LINE2 + STRING_NEWLINE +
                CONTENT_LINE2 + STRING_NEWLINE +
                NEW_CONTENT_LINE1 + STRING_NEWLINE +
                CONTENT_LINE2 + STRING_NEWLINE +
                NEW_CONTENT_LINE1 + STRING_NEWLINE;
        String command = "tee " + NEW_FILE + "; uniq " + NEW_FILE;
        inputStream = new ByteArrayInputStream(input.getBytes());
        System.setIn(inputStream);
        shell.parseAndEvaluate(command, outputStream);

        String uniqOutput = CONTENT_LINE1 + STRING_NEWLINE +
                CONTENT_LINE2 + STRING_NEWLINE +
                NEW_CONTENT_LINE1 + STRING_NEWLINE +
                CONTENT_LINE2 + STRING_NEWLINE +
                NEW_CONTENT_LINE1 + STRING_NEWLINE;
        assertEquals(input + uniqOutput, outputStream.toString());
    }
}
