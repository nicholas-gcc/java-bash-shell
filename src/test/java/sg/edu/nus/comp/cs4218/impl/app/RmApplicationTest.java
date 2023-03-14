package sg.edu.nus.comp.cs4218.impl.app;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sg.edu.nus.comp.cs4218.Environment;
import sg.edu.nus.comp.cs4218.exception.RmException;
import sg.edu.nus.comp.cs4218.impl.util.FileSystemUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.CHAR_FILE_SEP;
import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.ERR_NO_FILE_ARGS;
import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.ERR_NO_OSTREAM;
import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.ERR_NULL_ARGS;
import static sg.edu.nus.comp.cs4218.impl.parser.ArgsParser.ILLEGAL_FLAG_MSG;

public class RmApplicationTest {
    RmApplication rmApplication;
    OutputStream outputStream;
    static final String CWD = System.getProperty("user.dir");
    static final String TESTING_PATH = CHAR_FILE_SEP + "assets" + CHAR_FILE_SEP + "app" + CHAR_FILE_SEP + "rm";
    static final String RECURSIVE_ARG = "-r";
    static final String RM_EMPTY_DIR_ARG = "-d";

    static final String EMPTY_DIR_NAME = "empty";
    static final String DIR_NAME1 = "dir1";
    static final String DIR_NAME2 = "dir2";
    static final String SUB_DIR_NAME1 = "subDir1";
    static final String SUB_DIR_NAME2 = "subDir2";
    static final String TEXT_FILE_NAME1 = "text1.txt";
    static final String TEXT_FILE_NAME2 = "text2.txt";
    static final String FAKE_FILE = "fakefile.txt";
    static final String FAKE_DIR = "fakedir";
    static final String ERROR_INITIALS = "rm: ";

    @BeforeEach
    void setup() {
        rmApplication = new RmApplication();
    }

    @BeforeEach
    void setCurrentDirectory() {
        Environment.currentDirectory += TESTING_PATH;
        outputStream = new ByteArrayOutputStream();
    }

    @AfterEach
    void reset() throws IOException {
        Environment.currentDirectory = CWD;
        outputStream.close();
    }

    @Test
    void run_NullArguments_ThrowsRmException(){
        RmException exception = assertThrows(RmException.class, () -> {
            rmApplication.run(null, System.in, outputStream);
        });
        String expectedMessage =  ERROR_INITIALS + ERR_NULL_ARGS;
        assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    void run_NullOutputStream_ThrowsRmException(){
        String[] args = {TEXT_FILE_NAME1};
        RmException exception = assertThrows(RmException.class, () -> {
            rmApplication.run(args, System.in, null);
        });
        String expectedMessage =  ERROR_INITIALS + ERR_NO_OSTREAM;
        assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    void run_OnlyInvalidArgs_ThrowsRmException() {
        String[] args = {"-b", "-c", TEXT_FILE_NAME1};
        RmException exception = assertThrows(RmException.class, () -> {
            rmApplication.run(args, System.in, outputStream);
        });
        String expectedMessage =  String.format("rm: %s", ILLEGAL_FLAG_MSG + 'b');
        assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    void run_InvalidAndValidArgs_ThrowsRmException() {
        String[] args = {"-r", "-d", "-R", "-D", TEXT_FILE_NAME1};
        RmException exception = assertThrows(RmException.class, () -> {
            rmApplication.run(args, System.in, outputStream);
        });
        String expectedMessage =  String.format("rm: %s", ILLEGAL_FLAG_MSG + 'R');
        assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    void run_ValidArgs_DoesNotThrowException() {
        String[] args = {"-r", "-d", TEXT_FILE_NAME1};
        assertDoesNotThrow(() -> {
            FileSystemUtils.createEmptyFile(TEXT_FILE_NAME1);
            rmApplication.run(args, System.in, outputStream);
        });
    }

    @Test
    void run_ValidArgsWithNoFiles_ThrowsRmException() {
        String[] args = {"-r", "-d"};
        RmException exception = assertThrows(RmException.class, () -> {
            rmApplication.run(args, System.in, outputStream);
        });
        String expectedMessage =  String.format("rm: %s", ERR_NO_FILE_ARGS);
        assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    void run_OneFileArgs_CorrectOutputStream() {
        String[] args = {TEXT_FILE_NAME1};
        assertDoesNotThrow(() -> {
            FileSystemUtils.createEmptyFile(TEXT_FILE_NAME1);
            rmApplication.run(args, System.in, outputStream);
            assertFalse(FileSystemUtils.fileOrDirExist(TEXT_FILE_NAME1));
        });
    }

    @Test
    void run_NonExistingFileArgs_CorrectOutputStream() {
        String[] args = {FAKE_FILE};
        RmException exception = assertThrows(RmException.class, () -> {
            rmApplication.run(args, System.in, outputStream);
        });
        String expectedMessage =  String.format("rm: File or directory %s does not exist", FAKE_FILE);
        assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    void run_MultipleFilesArgs_CorrectOutputStream() {
        String[] args = {TEXT_FILE_NAME1, TEXT_FILE_NAME2};
        assertDoesNotThrow(() -> {
            FileSystemUtils.createEmptyFile(TEXT_FILE_NAME1);
            FileSystemUtils.createEmptyFile(TEXT_FILE_NAME2);
            rmApplication.run(args, System.in, outputStream);
            assertFalse(FileSystemUtils.fileOrDirExist(TEXT_FILE_NAME1));
            assertFalse(FileSystemUtils.fileOrDirExist(TEXT_FILE_NAME2));
        });
    }

    @Test
    void run_DirWithoutEmptyDirArg_ThrowsRmException() {
        String[] args = {EMPTY_DIR_NAME};
        RmException exception = assertThrows(RmException.class, () -> {
            FileSystemUtils.createEmptyDir(EMPTY_DIR_NAME);
            rmApplication.run(args, System.in, outputStream);
        });
        assertDoesNotThrow(() -> FileSystemUtils.deleteFileOrDir(EMPTY_DIR_NAME));

        String expectedMessage = String.format(String.format("rm: Cannot remove %s: Is a directory", EMPTY_DIR_NAME));
        assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    void run_OneDirWithRecursiveArgs_CorrectOutputStream() {
        String[] args = {DIR_NAME1, RECURSIVE_ARG};
        assertDoesNotThrow(() -> {
            FileSystemUtils.createEmptyDir(DIR_NAME1);
            String prevDir = Environment.currentDirectory;
            // Moves current working directory to the directory created
            Environment.currentDirectory += CHAR_FILE_SEP + DIR_NAME1;
            FileSystemUtils.createEmptyDir(SUB_DIR_NAME1);
            // Reset current working directory to working directory prior to moving
            Environment.currentDirectory = prevDir;

            rmApplication.run(args, System.in, outputStream);
            assertFalse(FileSystemUtils.fileOrDirExist(DIR_NAME1));
        });
    }

    @Test
    void run_MultipleDirsWithRecursiveArgs_CorrectOutputStream() {
        String[] args = {DIR_NAME1, DIR_NAME2, RECURSIVE_ARG};
        assertDoesNotThrow(() -> {
            FileSystemUtils.createEmptyDir(DIR_NAME1);
            String prevDir = Environment.currentDirectory;
            // Moves current working directory to the directory created
            Environment.currentDirectory += CHAR_FILE_SEP + DIR_NAME1;
            FileSystemUtils.createEmptyDir(SUB_DIR_NAME1);
            // Reset current working directory to working directory prior to moving
            Environment.currentDirectory = prevDir;

            FileSystemUtils.createEmptyDir(DIR_NAME2);
            // Moves current working directory to the directory created
            Environment.currentDirectory += CHAR_FILE_SEP + DIR_NAME2;
            FileSystemUtils.createEmptyDir(SUB_DIR_NAME2);
            // Reset current working directory to working directory prior to moving
            Environment.currentDirectory = prevDir;

            rmApplication.run(args, System.in, outputStream);
            assertFalse(FileSystemUtils.fileOrDirExist(DIR_NAME1));
            assertFalse(FileSystemUtils.fileOrDirExist(DIR_NAME2));
        });
    }

    @Test
    void run_OneDirOneNonExistingFileWithRecursiveArgs_ThrowsRmException() {
        String[] args = {DIR_NAME1, FAKE_FILE, RECURSIVE_ARG};
        RmException exception = assertThrows(RmException.class, () -> {
            FileSystemUtils.createEmptyDir(DIR_NAME1);
            String prevDir = Environment.currentDirectory;
            // Moves current working directory to the directory created
            Environment.currentDirectory += CHAR_FILE_SEP + DIR_NAME1;
            FileSystemUtils.createEmptyDir(SUB_DIR_NAME1);
            // Reset current working directory to working directory prior to moving
            Environment.currentDirectory = prevDir;
            rmApplication.run(args, System.in, outputStream);
        });
        String expectedMessage =  String.format("rm: File or directory %s does not exist", FAKE_FILE);
        assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    void run_NonExistingDirWithRecursiveArgs_ThrowsRmException() {
        String[] args = {FAKE_DIR, RECURSIVE_ARG};
         RmException exception = assertThrows(RmException.class, () -> {
            rmApplication.run(args, System.in, outputStream);
        });

        String expectedMessage =  String.format("rm: File or directory %s does not exist", FAKE_DIR);
        assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    void run_OneDirWithIsEmptyDirArgs_CorrectOutputStream() {
        String[] args = {DIR_NAME1, RM_EMPTY_DIR_ARG};
        assertDoesNotThrow(() -> {
            FileSystemUtils.createEmptyDir(DIR_NAME1);
            rmApplication.run(args, System.in, outputStream);
            assertFalse(FileSystemUtils.fileOrDirExist(DIR_NAME1));
        });
    }

    @Test
    void run_MultipleDirsWithIsEmptyDirArgs_CorrectOutputStream() {
        String[] args = {DIR_NAME1, DIR_NAME2, RM_EMPTY_DIR_ARG};
        assertDoesNotThrow(() -> {
            FileSystemUtils.createEmptyDir(DIR_NAME1);
            FileSystemUtils.createEmptyDir(DIR_NAME2);
            rmApplication.run(args, System.in, outputStream);
            assertFalse(FileSystemUtils.fileOrDirExist(DIR_NAME1));
            assertFalse(FileSystemUtils.fileOrDirExist(DIR_NAME2));
        });
    }

    @Test
    void run_MultipleDirsAndFilesWithIsEmptyDirArgs_CorrectOutputStream() {
        String[] args = {DIR_NAME1, DIR_NAME2, TEXT_FILE_NAME1, TEXT_FILE_NAME2, RM_EMPTY_DIR_ARG};
        assertDoesNotThrow(() -> {
            FileSystemUtils.createEmptyDir(DIR_NAME1);
            FileSystemUtils.createEmptyDir(DIR_NAME2);
            FileSystemUtils.createEmptyFile(TEXT_FILE_NAME1);
            FileSystemUtils.createEmptyFile(TEXT_FILE_NAME2);
            rmApplication.run(args, System.in, outputStream);
            assertFalse(FileSystemUtils.fileOrDirExist(DIR_NAME1));
            assertFalse(FileSystemUtils.fileOrDirExist(DIR_NAME2));
        });
    }

    @Test
    void run_NonEmptyDirIsEmptyDirArgs_ThrowRmException() {
        String[] args = {DIR_NAME1, RM_EMPTY_DIR_ARG};
        String relativeTestPath = "." + CHAR_FILE_SEP + DIR_NAME1 + CHAR_FILE_SEP + TEXT_FILE_NAME1;
        RmException exception = assertThrows(RmException.class, () -> {
            FileSystemUtils.createEmptyDir(DIR_NAME1);
            FileSystemUtils.createEmptyFile(relativeTestPath);
            rmApplication.run(args, System.in, outputStream);
        });
        assertDoesNotThrow(() -> {
            FileSystemUtils.deleteFileOrDir(relativeTestPath);
            FileSystemUtils.deleteFileOrDir(DIR_NAME1);
        });
        String expectedMessage = String.format("rm: Cannot remove %s: directory is not empty", DIR_NAME1);
        assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    void run_MultipleDirWithRecursiveAndIsEmptyDirArgs_CorrectOutputStream() {
        String[] args = {DIR_NAME1, DIR_NAME2, RECURSIVE_ARG, RM_EMPTY_DIR_ARG};
        assertDoesNotThrow(() -> {
            FileSystemUtils.createEmptyDir(DIR_NAME1);
            String prevDir = Environment.currentDirectory;
            // Moves current working directory to the directory created
            Environment.currentDirectory += CHAR_FILE_SEP + DIR_NAME1;
            FileSystemUtils.createEmptyDir(SUB_DIR_NAME1);
            // Reset current working directory to working directory prior to moving
            Environment.currentDirectory = prevDir;

            // Create empty directory
            FileSystemUtils.createEmptyDir(DIR_NAME2);

            rmApplication.run(args, System.in, outputStream);
            assertFalse(FileSystemUtils.fileOrDirExist(DIR_NAME1));
            assertFalse(FileSystemUtils.fileOrDirExist(DIR_NAME2));
        });
    }

    @Test
    void remove_TwoFiles_RemovesFiles() {
        boolean isEmptyFolder = false;
        boolean isRecursive = false;
        assertDoesNotThrow(() -> {
            FileSystemUtils.createEmptyFile(TEXT_FILE_NAME1);
            FileSystemUtils.createEmptyFile(TEXT_FILE_NAME2);

            rmApplication.remove(isEmptyFolder, isRecursive, TEXT_FILE_NAME1, TEXT_FILE_NAME2);
            assertFalse(FileSystemUtils.fileOrDirExist(TEXT_FILE_NAME1));
            assertFalse(FileSystemUtils.fileOrDirExist(TEXT_FILE_NAME2));
        });
    }

    @Test
    void remove_OneFileOneNonExistingFile_ThrowRmException() {
        boolean isEmptyFolder = false;
        boolean isRecursive = false;
        assertThrows(RmException.class, () -> {
            FileSystemUtils.createEmptyFile(TEXT_FILE_NAME1);
            rmApplication.remove(isEmptyFolder, isRecursive, TEXT_FILE_NAME1, FAKE_FILE);
        });
    }

    @Test
    void remove_OneFileOneDir_ThrowRmException() {
        boolean isEmptyFolder = false;
        boolean isRecursive = false;
        RmException exception = assertThrows(RmException.class, () -> {
            FileSystemUtils.createEmptyFile(TEXT_FILE_NAME1);
            FileSystemUtils.createEmptyDir(DIR_NAME1);
            rmApplication.remove(isEmptyFolder, isRecursive, TEXT_FILE_NAME1, DIR_NAME1);
        });

        assertDoesNotThrow(() -> {
            FileSystemUtils.deleteFileOrDir(DIR_NAME1);
        });

        String expectedMessage = String.format("rm: Cannot remove %s: Is a directory", DIR_NAME1);
        assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    void remove_OneFileOneDirIsEmptyFolderArg_RemovesFiles() {
        boolean isEmptyFolder = true;
        boolean isRecursive = false;
        assertDoesNotThrow(() -> {
            FileSystemUtils.createEmptyFile(TEXT_FILE_NAME1);
            FileSystemUtils.createEmptyDir(DIR_NAME1);
            rmApplication.remove(isEmptyFolder, isRecursive, TEXT_FILE_NAME1, DIR_NAME1);
        });
    }

    @Test
    void remove_OneDirRecursive_RemovesFiles() {
        boolean isEmptyFolder = false;
        boolean isRecursive = true;
        assertDoesNotThrow(() -> {
            FileSystemUtils.createEmptyDir(DIR_NAME1);
            String prevDir = Environment.currentDirectory;
            // Moves current working directory to the directory created
            Environment.currentDirectory += CHAR_FILE_SEP + DIR_NAME1;
            FileSystemUtils.createEmptyDir(SUB_DIR_NAME1);
            // Reset current working directory to working directory prior to moving
            Environment.currentDirectory = prevDir;

            rmApplication.remove(isEmptyFolder, isRecursive, DIR_NAME1);
            assertFalse(FileSystemUtils.fileOrDirExist(DIR_NAME1));
        });
    }
}
