package sg.edu.nus.comp.cs4218.integration;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import sg.edu.nus.comp.cs4218.Environment;
import sg.edu.nus.comp.cs4218.Shell;
import sg.edu.nus.comp.cs4218.impl.ShellImpl;
import sg.edu.nus.comp.cs4218.impl.util.FileSystemUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.CHAR_FILE_SEP;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.STRING_NEWLINE;

public class SequenceCommandIT {
    static final String CWD = System.getProperty("user.dir");
    static final String TESTING_PATH = CHAR_FILE_SEP + "assets" + CHAR_FILE_SEP + "it" + CHAR_FILE_SEP + "sequence";
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
    Shell shell = new ShellImpl();

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

    

}
