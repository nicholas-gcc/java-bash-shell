package sg.edu.nus.comp.cs4218.impl.app;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledOnOs;
import org.junit.jupiter.api.condition.OS;
import sg.edu.nus.comp.cs4218.Environment;
import sg.edu.nus.comp.cs4218.exception.ShellException;
import sg.edu.nus.comp.cs4218.exception.WcException;
import sg.edu.nus.comp.cs4218.impl.util.IOUtils;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.ERR_NULL_STREAMS;
import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.ERR_WRITE_STREAM;
import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.ERR_GENERAL;
import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.ERR_FILE_DIR_NOT_FOUND;
import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.ERR_IS_DIR;
import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.ERR_NO_PERM;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.STRING_NEWLINE;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.CHAR_FILE_SEP;

public class WcApplicationTest {

    WcApplication wcApplication;
    InputStream stdin;
    OutputStream stdout;
    static final String TESTING_PATH = CHAR_FILE_SEP + "assets" + CHAR_FILE_SEP + "app" + CHAR_FILE_SEP + "wc";
    static final String CWD = System.getProperty("user.dir");
    private static final String WC_EX_PREFIX = "wc: ";
    private static final String WC_TEST_1_FILE = "wc_test1.txt";
    private static final String WC_TEST_1_RESULT = "       0       1       5 wc_test1.txt";
    private static final String WC_MULTI_SENT = "multi-sentence.txt";
    private static final String WC_MULTI_PARA = "multi-para.txt";

    @BeforeEach
    void setup() {
        wcApplication = new WcApplication();
        stdin = System.in;
        stdout = new ByteArrayOutputStream();
        Environment.currentDirectory += TESTING_PATH;
    }
    
    @AfterEach
    void reset() {
        Environment.currentDirectory = CWD;
    }

    @Test
    void wc_RunWithNullStdout_ThrowsWcException() {
        String [] array = {};
        stdout = null;
        WcException wcException = assertThrows(WcException.class, () -> wcApplication.run(array, stdin, stdout));
        assertEquals(WC_EX_PREFIX + ERR_NULL_STREAMS, wcException.getMessage());
    }

    @Test
    void wc_RunWithOneFile_ShouldWcCorrectly() throws WcException {
        String [] array = {WC_TEST_1_FILE};
        String expected = WC_TEST_1_RESULT + STRING_NEWLINE;
        wcApplication.run(array, stdin, stdout);
        assertEquals(expected, stdout.toString());
    }

    @Test
    void wc_RunWithMultipleFiles_ShouldWcCorrectly() throws WcException {
        String [] array = {WC_TEST_1_FILE, "wc_test2.txt"};
        String expected = WC_TEST_1_RESULT + STRING_NEWLINE +
                "       0       1       5 wc_test2.txt" + STRING_NEWLINE +
                "       0       2      10 total" + STRING_NEWLINE;
        wcApplication.run(array, stdin, stdout);
        assertEquals(expected, stdout.toString());
    }

    @Test
    void wc_RunWithEmptyFile_ShouldWcCorrectly() throws WcException {
        String [] array = {"empty.txt"};
        String expected = "       0       0       0 empty.txt" + STRING_NEWLINE;
        wcApplication.run(array, stdin, stdout);
        assertEquals(expected, stdout.toString());
    }

    @Test
    @EnabledOnOs({OS.MAC})
    void wc_RunWithMultiSentenceFileOnMac_ShouldWcCorrectly() throws WcException {
        String [] array = {WC_MULTI_SENT};
        String expected = "       2      55     287 multi-sentence.txt" + STRING_NEWLINE;
        wcApplication.run(array, stdin, stdout);
        assertEquals(expected, stdout.toString());
    }

    @Test
    @EnabledOnOs({OS.WINDOWS})
    void wc_RunWithMultiSentenceFileOnWindows_ShouldWcCorrectly() throws WcException {
        String [] array = {WC_MULTI_SENT};
        String expected = "       2      55     289 multi-sentence.txt" + STRING_NEWLINE;
        wcApplication.run(array, stdin, stdout);
        assertEquals(expected, stdout.toString());
    }

    @Test
    @EnabledOnOs({OS.MAC})
    void wc_RunWithMultiParaFileOnMac_ShouldWcCorrectly() throws WcException {
        String [] array = {WC_MULTI_PARA};
        String expected = "      12     220    1234 multi-para.txt" + STRING_NEWLINE;
        wcApplication.run(array, stdin, stdout);
        assertEquals(expected, stdout.toString());
    }

    @Test
    @EnabledOnOs({OS.WINDOWS})
    void wc_RunWithMultiParaFileOnWindows_ShouldWcCorrectly() throws WcException {
        String [] array = {WC_MULTI_PARA};
        String expected = "      12     220    1246 multi-para.txt" + STRING_NEWLINE;
        wcApplication.run(array, stdin, stdout);
        assertEquals(expected, stdout.toString());
    }

    @Test
    void wc_RunWithStdin_ShouldWcCorrectly() throws FileNotFoundException, WcException {
        String [] array = {};
        stdin = new FileInputStream(Environment.currentDirectory + CHAR_FILE_SEP + "wc_test1.txt");
        wcApplication.run(array, stdin, stdout);
    }

    @Test
    void wc_RunWithByteArgument_ShouldWcCorrectly() throws WcException {
        String [] array = {"-c", WC_TEST_1_FILE};
        String expected = "       5 wc_test1.txt" + STRING_NEWLINE;
        wcApplication.run(array, stdin, stdout);
        assertEquals(expected, stdout.toString());
    }

    @Test
    void wc_RunWithLineArgument_ShouldWcCorrectly() throws WcException {
        String [] array = {"-l", WC_TEST_1_FILE};
        String expected = "       0 wc_test1.txt" + STRING_NEWLINE;
        wcApplication.run(array, stdin, stdout);
        assertEquals(expected, stdout.toString());
    }

    @Test
    void wc_RunWithWordsArgument_ShouldWcCorrectly() throws WcException {
        String [] array = {"-w", WC_TEST_1_FILE};
        String expected = "       1 wc_test1.txt" + STRING_NEWLINE;
        wcApplication.run(array, stdin, stdout);
        assertEquals(expected, stdout.toString());
    }

    @Test
    void wc_RunWithLineByteArgument_ShouldWcCorrectly() throws WcException {
        String [] array = {"-l", "-c", WC_TEST_1_FILE};
        String expected = "       0       5 wc_test1.txt" + STRING_NEWLINE;
        wcApplication.run(array, stdin, stdout);
        assertEquals(expected, stdout.toString());
    }

    @Test
    void wc_RunWithByteWordArgument_ShouldWcCorrectly() throws WcException {
        String [] array = {"-c", "-w", WC_TEST_1_FILE};
        String expected = "       1       5 wc_test1.txt" + STRING_NEWLINE;
        wcApplication.run(array, stdin, stdout);
        assertEquals(expected, stdout.toString());
    }

    @Test
    void wc_RunWithNullStdin_ThrowsWcException() {
        String [] array = {};
        stdin = null;
        WcException wcException = assertThrows(WcException.class, () -> wcApplication.run(array, stdin, stdout));
        assertEquals(WC_EX_PREFIX + ERR_NULL_STREAMS, wcException.getMessage());
    }

    @Test
    void wc_RunWithClosedStdout_ThrowsWcException() throws FileNotFoundException, ShellException {
        String [] array = {"output.txt"};
        stdout = new FileOutputStream("output.txt");
        IOUtils.closeOutputStream(stdout);
        WcException wcException = assertThrows(WcException.class,() -> wcApplication.run(array, stdin, stdout));
        assertEquals(WC_EX_PREFIX + ERR_WRITE_STREAM, wcException.getMessage());
        File file = new File("output.txt");
        file.delete();
    }

    @Test
    void wc_CountFromFilesNull_ThrowsException() {
        Exception exception = assertThrows(Exception.class, () ->
                wcApplication.countFromFiles(true, true, true, null)
        );
        assertEquals(ERR_GENERAL, exception.getMessage());
    }

    @Test
    void wc_CountFromFilesNonExistingFile_ShowsFileNotFound() throws Exception {
        String result = wcApplication.countFromFiles(true, true, true, "blah");
        assertEquals(WC_EX_PREFIX + ERR_FILE_DIR_NOT_FOUND, result);
    }

    @Test
    void wc_CountFromFilesDirectory_ShowsDirectory() throws Exception {
        String result = wcApplication.countFromFiles(true, true, true, "test_dir");
        assertEquals(WC_EX_PREFIX + ERR_IS_DIR, result);
    }

    @Test
    @EnabledOnOs({OS.MAC})
//    Mac and windows uses different file permissions, this method only works on mac
    void wc_CountFromFilesUnreadable_ShowsUnreadable() throws Exception {
        File unreadableFile = new File(Environment.currentDirectory + File.separator + "unreadable.txt");
        unreadableFile.setReadable(false);
        String result = wcApplication.countFromFiles(true, true, true, "unreadable.txt");
        assertEquals(WC_EX_PREFIX + ERR_NO_PERM, result);
        unreadableFile.setReadable(true);
    }

    @Test
    void wc_CountFromFilesValidFile_CountsCorrectly() throws Exception {
        String expected = WC_TEST_1_RESULT;
        String result = wcApplication.countFromFiles(true, true, true,
                WC_TEST_1_FILE);
        assertEquals(expected, result);
    }

    @Test
    void wc_CountFromFilesMultipleValidFiles_CountsCorrectly() throws Exception {
        String expected = WC_TEST_1_RESULT + STRING_NEWLINE +
                "       0       1       5 wc_test2.txt" + STRING_NEWLINE +
                "       0       2      10 total";
        String result = wcApplication.countFromFiles(true, true, true,
                new String[] {"wc_test1.txt", "wc_test2.txt"});
        assertEquals(expected, result);
    }

    @Test
    void wc_CountFromFilesEmptyFile_CountsCorrectly() throws Exception {
        String expected = "       0       0       0 empty.txt";
        String result = wcApplication.countFromFiles(true, true, true,
                "empty.txt");
        assertEquals(expected, result);
    }

    @Test
    @EnabledOnOs({OS.MAC})
    void wc_CountFromFilesMultiSentenceFileOnMac_CountsCorrectly() throws Exception {
        String expected = "       2      55     287 multi-sentence.txt";
        String result = wcApplication.countFromFiles(true, true, true,
                WC_MULTI_SENT);
        assertEquals(expected, result);
    }

    @Test
    @EnabledOnOs({OS.WINDOWS})
    void wc_CountFromFilesMultiSentenceFileOnWindows_CountsCorrectly() throws Exception {
        String expected = "       2      55     289 multi-sentence.txt";
        String result = wcApplication.countFromFiles(true, true, true,
                WC_MULTI_SENT);
        assertEquals(expected, result);
    }

    @Test
    @EnabledOnOs({OS.MAC})
    void wc_CountFromFilesMultiParaFileOnMac_CountsCorrectly() throws Exception {
        String expected = "      12     220    1234 multi-para.txt";
        String result = wcApplication.countFromFiles(true, true, true,
                WC_MULTI_PARA);
        assertEquals(expected, result);
    }

    @Test
    @EnabledOnOs({OS.WINDOWS})
    void wc_CountFromFilesMultiParaFileOnWindows_CountsCorrectly() throws Exception {
        String expected = "      12     220    1246 multi-para.txt";
        String result = wcApplication.countFromFiles(true, true, true,
                WC_MULTI_PARA);
        assertEquals(expected, result);
    }

    @Test
    void wc_CountFromStdinNullStdin_ThrowsException() {
        Exception exception = assertThrows(Exception.class, () ->
                wcApplication.countFromStdin(true, true, true, null)
        );
        assertEquals(ERR_NULL_STREAMS, exception.getMessage());
    }

    @Test
    void wc_CountFromStdinValidFile_CountsCorrectly() throws Exception {
        String expected = "       0       1       5";
        stdin = new FileInputStream(Environment.currentDirectory + File.separator + WC_TEST_1_FILE);
        String result = wcApplication.countFromStdin(true, true, true, stdin);
        assertEquals(expected, result);
    }

    @Test
    void wc_CountFromStdinEmptyFile_CountsCorrectly() throws Exception {
        String expected = "       0       0       0";
        stdin = new FileInputStream(Environment.currentDirectory + File.separator + "empty.txt");
        String result = wcApplication.countFromStdin(true, true, true, stdin);
        assertEquals(expected, result);
    }

    @Test
    @EnabledOnOs({OS.MAC})
    void wc_CountFromStdinMultiSentenceFileOnMac_CountsCorrectly() throws Exception {
        String expected = "       2      55     287";
        stdin = new FileInputStream(Environment.currentDirectory + File.separator + WC_MULTI_SENT);
        String result = wcApplication.countFromStdin(true, true, true, stdin);
        assertEquals(expected, result);
    }

    @Test
    @EnabledOnOs({OS.WINDOWS})
    void wc_CountFromStdinMultiSentenceFileOnWindows_CountsCorrectly() throws Exception {
        String expected = "       2      55     289";
        stdin = new FileInputStream(Environment.currentDirectory + File.separator + WC_MULTI_SENT);
        String result = wcApplication.countFromStdin(true, true, true, stdin);
        assertEquals(expected, result);
    }

    @Test
    @EnabledOnOs({OS.MAC})
    void wc_CountFromStdinMultiParaFileOnMac_CountsCorrectly() throws Exception {
        String expected = "      12     220    1234";
        stdin = new FileInputStream(Environment.currentDirectory + File.separator + WC_MULTI_PARA);
        String result = wcApplication.countFromStdin(true, true, true, stdin);
        assertEquals(expected, result);
    }

    @Test
    @EnabledOnOs({OS.WINDOWS})
    void wc_CountFromStdinMultiParaFileOnWindows_CountsCorrectly() throws Exception {
        String expected = "      12     220    1246";
        stdin = new FileInputStream(Environment.currentDirectory + File.separator + WC_MULTI_PARA);
        String result = wcApplication.countFromStdin(true, true, true, stdin);
        assertEquals(expected, result);
    }

    @Test
    void wc_GetCountReportNullInput_ThrowsException() {
        Exception exception = assertThrows(Exception.class, () ->
                wcApplication.getCountReport(null)
        );
        assertEquals(ERR_NULL_STREAMS, exception.getMessage());
    }

    @Test void wc_GetCountReportValidInput_GetsCorrectly() throws Exception {
        stdin = new FileInputStream(Environment.currentDirectory + File.separator + WC_TEST_1_FILE);
        wcApplication.getCountReport(stdin);
    }
}
