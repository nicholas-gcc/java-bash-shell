package sg.edu.nus.comp.cs4218.impl.util;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sg.edu.nus.comp.cs4218.Environment;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.CHAR_ASTERISK;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.CHAR_FILE_SEP;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.STRING_NEWLINE;

@SuppressWarnings({"PMD.GodClass", "PMD.LongVariable"})
public class FileSystemUtilsTest {
    static final String CWD = System.getProperty("user.dir");
    static final String TESTING_PATH = CHAR_FILE_SEP + "assets" + CHAR_FILE_SEP + "util" + CHAR_FILE_SEP + "FileSystemUtils";
    static final String SAMPLE_FILE = "sample.txt";
    static final String FAKE_FILE = "fake.txt";
    static final String SAMPLE_DIR = "sample";
    static final String NESTED_SAMPLE_DIR = "nestedSample";
    static final String NESTED_NESTED_SAMPLE_DIR = "nestedNestedSample";
    static final String FAKE_DIR = "fake";
    static final String NEW_FILE = "new.txt";
    static final String NEW_DIR = "new";
    static final String NEW_SUB_DIR = "newsubdir";
    static final String NON_EXIST_ERR = "File or directory %s does not exist";

    @BeforeEach
    void setCurrentDirectory() {
        Environment.currentDirectory += TESTING_PATH;
    }

    @AfterEach
    void reset() {
        Environment.currentDirectory = CWD;
    }

    @Test
    void getAbsolutePathName_Filename_ReturnCorrectPath() {
        String expectedPath = Environment.currentDirectory + CHAR_FILE_SEP + SAMPLE_FILE;
        assertEquals(expectedPath, FileSystemUtils.getAbsolutePathName(SAMPLE_FILE));
    }

    @Test
    void fileOrDirExist_ExistingFile_ReturnTrue() {
        assertTrue(FileSystemUtils.fileOrDirExist(SAMPLE_FILE));
    }

    @Test
    void fileOrDirExist_NonExistentFile_ReturnFalse() {
        assertFalse(FileSystemUtils.fileOrDirExist(FAKE_FILE));
    }

    @Test
    void fileOrDirExist_ExistingDir_ReturnTrue() {
        assertTrue(FileSystemUtils.fileOrDirExist(SAMPLE_DIR));
    }

    @Test
    void fileOrDirExist_NonExistentDir_ReturnFalse() {
        assertFalse(FileSystemUtils.fileOrDirExist(FAKE_DIR));
    }

    @Test
    void fileOrDirExist_NestedNonExistentDir_ReturnFalse() {
        assertFalse(FileSystemUtils.fileOrDirExist(SAMPLE_DIR + CHAR_FILE_SEP + FAKE_DIR));
    }

    @Test
    void isExistingFilesInPath_SimpleExistingDir_ReturnTrue() {
        assertTrue(FileSystemUtils.isExistingFilesInPath(SAMPLE_DIR));
    }

    @Test
    void isExistingFilesInPath_SimpleExistingFile_ReturnTrue() {
        assertTrue(FileSystemUtils.isExistingFilesInPath(SAMPLE_FILE));
    }

    @Test
    void isExistingFilesInPath_NestedExistingDir_ReturnTrue() {
        assertTrue(FileSystemUtils.isExistingFilesInPath(SAMPLE_DIR + CHAR_FILE_SEP + NESTED_SAMPLE_DIR));
    }

    @Test
    void isExistingFilesInPath_NestedExistingFile_ReturnTrue() {
        assertTrue(FileSystemUtils.isExistingFilesInPath(SAMPLE_DIR + CHAR_FILE_SEP + SAMPLE_FILE));
    }

    @Test
    void isExistingFilesInPath_NestedNestedExistingDir_ReturnTrue() {
        assertTrue(FileSystemUtils.isExistingFilesInPath(SAMPLE_DIR + CHAR_FILE_SEP + NESTED_SAMPLE_DIR + CHAR_FILE_SEP + NESTED_NESTED_SAMPLE_DIR));
    }

    @Test
    void isExistingFilesInPath_NestedNestedExistingFile_ReturnTrue() {
        assertTrue(FileSystemUtils.isExistingFilesInPath(SAMPLE_DIR + CHAR_FILE_SEP + NESTED_SAMPLE_DIR + CHAR_FILE_SEP + SAMPLE_FILE));
    }

    @Test
    void isExistingFilesInPath_NestedNestedNestedExistingFile_ReturnTrue() {
        assertTrue(FileSystemUtils.isExistingFilesInPath(SAMPLE_DIR + CHAR_FILE_SEP + NESTED_SAMPLE_DIR + CHAR_FILE_SEP + NESTED_NESTED_SAMPLE_DIR + CHAR_FILE_SEP + SAMPLE_FILE));
    }

    @Test
    void isExistingFilesInPath_NonExistingDir_ReturnFalse() {
        assertFalse(FileSystemUtils.isExistingFilesInPath(FAKE_DIR));
    }

    @Test
    void isExistingFilesInPath_NonExistingFile_ReturnFalse() {
        assertFalse(FileSystemUtils.isExistingFilesInPath(FAKE_FILE));
    }

    @Test
    void isExistingFilesInPath_NestedNonExistingDir_ReturnFalse() {
        assertFalse(FileSystemUtils.isExistingFilesInPath(SAMPLE_DIR + CHAR_FILE_SEP + FAKE_DIR));
    }

    @Test
    void isExistingFilesInPath_NestedNonExistingFile_ReturnFalse() {
        assertFalse(FileSystemUtils.isExistingFilesInPath(SAMPLE_DIR + CHAR_FILE_SEP + FAKE_FILE));
    }

    @Test
    void isExistingFilesInPath_NestedNestedNonExistingDir_ReturnFalse() {
        assertFalse(FileSystemUtils.isExistingFilesInPath(SAMPLE_DIR + CHAR_FILE_SEP + NESTED_SAMPLE_DIR + CHAR_FILE_SEP + FAKE_DIR));
    }

    @Test
    void isExistingFilesInPath_NestedNestedNonExistingFile_ReturnFalse() {
        assertFalse(FileSystemUtils.isExistingFilesInPath(SAMPLE_DIR + CHAR_FILE_SEP + NESTED_SAMPLE_DIR + CHAR_FILE_SEP + FAKE_FILE));
    }

    @Test
    void isExistingFilesInPath_NestedNestedNestedNonExistingDir_ReturnFalse() {
        assertFalse(FileSystemUtils.isExistingFilesInPath(SAMPLE_DIR + CHAR_FILE_SEP + NESTED_SAMPLE_DIR + CHAR_FILE_SEP + NESTED_NESTED_SAMPLE_DIR + CHAR_FILE_SEP + FAKE_FILE));
    }

    @Test
    void isExistingFilesInPath_NonExistingDirInMiddleOfNestedPath_ReturnFalse() {
        assertFalse(FileSystemUtils.isExistingFilesInPath(SAMPLE_DIR + CHAR_FILE_SEP + FAKE_DIR + CHAR_FILE_SEP + NESTED_NESTED_SAMPLE_DIR + CHAR_FILE_SEP + SAMPLE_FILE));
    }

    @Test
    void isExistingFilesInPath_FileNameWithReservedCharAtTheEnd_ReturnFalse() {
        assertFalse(FileSystemUtils.isExistingFilesInPath(SAMPLE_DIR + CHAR_FILE_SEP + NESTED_SAMPLE_DIR + CHAR_FILE_SEP + NESTED_NESTED_SAMPLE_DIR + CHAR_FILE_SEP + SAMPLE_FILE + CHAR_ASTERISK));
    }

    @Test
    void isExistingFilesInPath_FileNameWithReservedCharinTheMiddle_ReturnFalse() {
        assertFalse(FileSystemUtils.isExistingFilesInPath(SAMPLE_DIR + CHAR_FILE_SEP + NESTED_SAMPLE_DIR + CHAR_FILE_SEP + CHAR_ASTERISK + NESTED_NESTED_SAMPLE_DIR + CHAR_FILE_SEP + SAMPLE_FILE ));
    }

    @Test
    void isValidDirsInPath_SimpleExistingDir_ReturnTrue() throws Exception {
        assertTrue(FileSystemUtils.isValidDirsInPath(SAMPLE_DIR));
    }

    @Test
    void isValidDirsInPath_SimpleExistingFile_ReturnTrue() throws Exception {
        assertTrue(FileSystemUtils.isValidDirsInPath(SAMPLE_FILE));
    }

    @Test
    void isValidDirsInPath_NestedExistingDir_ReturnTrue() throws Exception {
        assertTrue(FileSystemUtils.isValidDirsInPath(SAMPLE_DIR + CHAR_FILE_SEP + NESTED_SAMPLE_DIR));
    }

    @Test
    void isValidDirsInPath_NestedExistingFile_ReturnTrue() throws Exception {
        assertTrue(FileSystemUtils.isValidDirsInPath(SAMPLE_DIR + CHAR_FILE_SEP + SAMPLE_FILE));
    }

    @Test
    void isValidDirsInPath_NestedNestedExistingDir_ReturnTrue() throws Exception {
        assertTrue(FileSystemUtils.isValidDirsInPath(SAMPLE_DIR + CHAR_FILE_SEP + NESTED_SAMPLE_DIR + CHAR_FILE_SEP + NESTED_NESTED_SAMPLE_DIR));
    }

    @Test
    void isValidDirsInPath_NestedNestedExistingFile_ReturnTrue() throws Exception {
        assertTrue(FileSystemUtils.isValidDirsInPath(SAMPLE_DIR + CHAR_FILE_SEP + NESTED_SAMPLE_DIR + CHAR_FILE_SEP + SAMPLE_FILE));
    }

    @Test
    void isValidDirsInPath_NestedNestedNestedExistingFile_ReturnTrue() throws Exception {
        assertTrue(FileSystemUtils.isValidDirsInPath(SAMPLE_DIR + CHAR_FILE_SEP + NESTED_SAMPLE_DIR + CHAR_FILE_SEP + NESTED_NESTED_SAMPLE_DIR + CHAR_FILE_SEP + SAMPLE_FILE));
    }

    @Test
    void isValidDirsInPath_FileAtStartOfNestedPath_ReturnFalse() throws Exception {
        assertFalse(FileSystemUtils.isValidDirsInPath(SAMPLE_FILE + CHAR_FILE_SEP + NESTED_SAMPLE_DIR + CHAR_FILE_SEP + NESTED_NESTED_SAMPLE_DIR + CHAR_FILE_SEP + SAMPLE_FILE));
    }

    @Test
    void isValidDirsInPath_FileInMiddleOfNestedPath_ReturnFalse() throws Exception {
        assertFalse(FileSystemUtils.isValidDirsInPath(SAMPLE_DIR + CHAR_FILE_SEP + SAMPLE_FILE + CHAR_FILE_SEP + NESTED_NESTED_SAMPLE_DIR + CHAR_FILE_SEP + SAMPLE_FILE));
    }

    @Test
    void createEmptyFile_NonExistingFile_CreateNewFile() throws Exception {
        String absolutePath = Environment.currentDirectory + CHAR_FILE_SEP + NEW_FILE;
        File file = new File(absolutePath);
        FileSystemUtils.createEmptyFile(NEW_FILE);
        assertTrue(file.exists());
        file.delete();
    }

    @Test
    void createEmptyFile_ExistingFile_ThrowExceptionWithCorrectMessage() {
        String expectedMessage = String.format("File or directory %s already exist", SAMPLE_FILE);
        try {
            FileSystemUtils.createEmptyFile(SAMPLE_FILE);
        } catch (Exception e) {
            assertEquals(expectedMessage, e.getMessage());
        }
    }

    @Test
    void createEmptyDir_NonExistingDir_CreateNewDir()  throws Exception {
        String absolutePath = Environment.currentDirectory + CHAR_FILE_SEP + NEW_DIR;
        File file = new File(absolutePath);
        FileSystemUtils.createEmptyDir(NEW_DIR);
        assertTrue(file.exists());
        file.delete();

    }

    @Test
    void createEmptyDir_ExistingDir_ThrowExceptionWithCorrectMessage() {
        String expectedMessage = String.format("File or directory %s already exist", SAMPLE_DIR);
        try {
            FileSystemUtils.createEmptyDir(SAMPLE_DIR);
        } catch (Exception e) {
            assertEquals(expectedMessage, e.getMessage());
        }
    }

    @Test
    void deleteFileOrDir_ExistingFile_DeletesFile()  throws Exception {
        String absolutePath = Environment.currentDirectory + CHAR_FILE_SEP + NEW_FILE;
        File file = new File(absolutePath);
        file.createNewFile();
        FileSystemUtils.deleteFileOrDir(NEW_FILE);
        assertFalse(file.exists());

    }

    @Test
    void deleteFileOrDir_NonExistingFile_ThrowExceptionWithCorrectMessage() {
        String expectedMessage = String.format(NON_EXIST_ERR, FAKE_FILE);
        try {
            FileSystemUtils.deleteFileOrDir(FAKE_FILE);
        } catch (Exception e) {
            assertEquals(expectedMessage, e.getMessage());
        }
    }

    @Test
    void deleteFileOrDir_ExistingDir_DeletesDir() throws Exception {
        String absolutePath = Environment.currentDirectory + CHAR_FILE_SEP + NEW_DIR;
        File file = new File(absolutePath);
        file.mkdir();
        FileSystemUtils.deleteFileOrDir(NEW_DIR);
        assertFalse(file.exists());

    }

    @Test
    void deleteFileOrDir_NonExistingDir_ThrowExceptionWithCorrectMessage() {
        String expectedMessage = String.format(NON_EXIST_ERR, FAKE_DIR);
        try {
            FileSystemUtils.deleteFileOrDir(FAKE_DIR);
        } catch (Exception e) {
            assertEquals(expectedMessage, e.getMessage());
        }
    }

    @Test
    void readFileContent_ExistingFile_ReturnFilesContent() throws Exception {
        String expectedResult = "This is a sample text." + STRING_NEWLINE + "This is the second line.";
        String actualResult = FileSystemUtils.readFileContent(SAMPLE_FILE);
        assertEquals(expectedResult, actualResult);

    }

    @Test
    void readFileContent_NonExistingFilename_ThrowExceptionWithCorrectMessage() {
        String expectedMessage = String.format(NON_EXIST_ERR, FAKE_FILE);
        try {
            FileSystemUtils.readFileContent(FAKE_FILE);
        } catch (Exception e) {
            assertEquals(expectedMessage, e.getMessage());
        }
    }

    @Test
    void readFileContent_ExistingDir_ThrowExceptionWithCorrectMessage() {
        String expectedMessage = String.format("Failed to read file %s", SAMPLE_DIR);
        try {
            FileSystemUtils.readFileContent(SAMPLE_DIR);
        } catch (Exception e) {
            assertEquals(expectedMessage, e.getMessage());
        }
    }
    @Test
    void writeStrToFile_NonExistingFile_ThrowsException() {
        String expectedMessage = String.format("File or directory %s does not exist", FAKE_FILE);
        String notRelevant = "";
        boolean isAppend = false;
        try {
            FileSystemUtils.writeStrToFile(isAppend, notRelevant, FAKE_FILE);
        } catch (Exception e) {
            assertEquals(expectedMessage, e.getMessage());
        }
    }

    @Test
    void wrtieStrToFile_ExistingDir_ThrowsException() {
        String expectedMessage = String.format("Failed to write to file %s", SAMPLE_DIR);
        String notRelevant = "";
        boolean isAppend = false;
        try {
            FileSystemUtils.writeStrToFile(isAppend, notRelevant, SAMPLE_DIR);
        } catch (Exception e) {
            assertEquals(expectedMessage, e.getMessage());
        }
    }

    @Test
    void writeStrToFile_WritingToExistingFile_WritesStringToFile() throws Exception {
        String absolutePath = Environment.currentDirectory + CHAR_FILE_SEP + NEW_FILE;
        File file = new File(absolutePath);
        String textToAppend = "This is a text to write to the new file." + STRING_NEWLINE + "This is the second line.";
        boolean isAppend = false;
        file.createNewFile();
        FileSystemUtils.writeStrToFile(isAppend, textToAppend, NEW_FILE);
        assertEquals(textToAppend, Files.readString(Paths.get(absolutePath)));
        file.delete();
    }

    @Test
    void writeStrToFile_AppendToExistingFile_AppendsStringToFile() throws Exception {
        String absolutePath = Environment.currentDirectory + CHAR_FILE_SEP + NEW_FILE;
        File file = new File(absolutePath);
        String textToWrite = "Initial text" + STRING_NEWLINE + "Testing";
        String textToAppend = "This is a text to append to the new file." + STRING_NEWLINE + "This is the second line.";
        boolean isAppend = true;

        // Creates file and writes initial text to the file
        file.createNewFile();
        Path path = Paths.get(absolutePath);
        Files.write(path, textToWrite.getBytes(), StandardOpenOption.APPEND);

        // Appends new text to the file
        FileSystemUtils.writeStrToFile(isAppend, textToAppend, NEW_FILE);

        assertEquals(textToWrite + textToAppend, Files.readString(path));
        file.delete();
    }

    @Test
    void isDir_ExistingDir_ReturnsTrue() throws Exception {
        assertTrue(FileSystemUtils.isDir(SAMPLE_DIR));
    }

    @Test
    void isDir_ExistingFile_ReturnsFalse() throws Exception {
        assertFalse(FileSystemUtils.isDir(SAMPLE_FILE));
    }

    @Test
    void isDir_NonExistingDir_ThrowExceptionWithCorrectMessage() {
        String expectedMessage = String.format(NON_EXIST_ERR, FAKE_DIR);
        try {
            FileSystemUtils.isDir(FAKE_DIR);
        } catch (Exception e) {
            assertEquals(expectedMessage, e.getMessage());
        }
    }

    @Test
    void isEmptyDir_ExistingEmptyDir_ReturnsTrue() throws Exception {
        String absolutePath = Environment.currentDirectory + CHAR_FILE_SEP + NEW_DIR;
        File file = new File(absolutePath);
        file.mkdir();
        assertTrue(FileSystemUtils.isEmptyDir(NEW_DIR));
        file.delete();
    }

    @Test
    void isEmptyDir_ExistingNonEmptyDir_ReturnsFalse() throws Exception {
        assertFalse(FileSystemUtils.isEmptyDir(SAMPLE_DIR));
    }

    @Test
    void isEmptyDir_ExistingFile_ReturnsFalse() throws Exception {
        assertFalse(FileSystemUtils.isEmptyDir(SAMPLE_FILE));
    }

    @Test
    void isEmptyDir_NonExistingDir_ThrowExceptionWithCorrectMessage() {
        String expectedMessage = String.format(NON_EXIST_ERR, FAKE_DIR);
        try {
            FileSystemUtils.isEmptyDir(FAKE_DIR);
        } catch (Exception e) {
            assertEquals(expectedMessage, e.getMessage());
        }
    }

    @Test
    void isSubDir_ExistingDirWithSubDir_ReturnsTrue() throws Exception {
        String newDirPath = Environment.currentDirectory + CHAR_FILE_SEP + NEW_DIR;
        String newSubDirPath = newDirPath + CHAR_FILE_SEP + NEW_SUB_DIR;

        File newDir = new File(newDirPath);
        File newSubDir = new File(newSubDirPath);

        newDir.mkdir();
        newSubDir.mkdir();
        assertTrue(FileSystemUtils.isSubDir(NEW_DIR, NEW_DIR + CHAR_FILE_SEP + NEW_SUB_DIR));
        newSubDir.delete();
        newDir.delete();
    }

    @Test
    void isSubDir_TwoSeparateExistingDir_ReturnsFalse() {
        String newDirPath = Environment.currentDirectory + CHAR_FILE_SEP + NEW_DIR;
        File newDir1 = new File(newDirPath);
        newDir1.mkdir();
        assertFalse(FileSystemUtils.isSubDir(NEW_DIR, SAMPLE_DIR));
        newDir1.delete();
    }

    @Test
    void isSubDir_ExistingDirAndExistingFile_ReturnsFalse() {
        assertFalse(FileSystemUtils.isSubDir(SAMPLE_FILE, SAMPLE_DIR));

    }

    @Test
    void isFileInFolder_ExistingDirWithFile_ReturnsTrue() throws IOException {
        String newDirPath = Environment.currentDirectory + CHAR_FILE_SEP + NEW_DIR;
        String newFilePath = newDirPath + CHAR_FILE_SEP + NEW_FILE;

        File newDir = new File(newDirPath);
        File newFile = new File(newFilePath);

        newDir.mkdir();
        newFile.createNewFile();
        assertTrue(FileSystemUtils.isFileInFolder(NEW_DIR + CHAR_FILE_SEP + NEW_FILE, NEW_DIR));
        newFile.delete();
        newDir.delete();
    }

    @Test
    void isFileInFolder_SeparateExistingFileAndDir_ReturnsFalse() {
        String newDirPath = Environment.currentDirectory + CHAR_FILE_SEP + NEW_DIR;

        File newDir = new File(newDirPath);

        newDir.mkdir();
        assertFalse(FileSystemUtils.isFileInFolder(SAMPLE_FILE, NEW_DIR));
        newDir.delete();
    }

    @Test
    void isFileInFolder_NonExistingFileAndExistingDir_ReturnsFalse() {
        assertFalse(FileSystemUtils.isFileInFolder(SAMPLE_FILE, FAKE_DIR));
    }

    @Test
    void isFileInFolder_ExistingFileAndNonExistingDir_ReturnsFalse() {
        assertFalse(FileSystemUtils.isFileInFolder(FAKE_DIR, SAMPLE_DIR));
    }

    @Test
    void getFilesInFolder_ExistingDirWithSubDirAndFile_ReturnsFileNames() throws Exception {
        String newDirPath = Environment.currentDirectory + CHAR_FILE_SEP + NEW_DIR;
        String newSubDirPath = newDirPath + CHAR_FILE_SEP + NEW_SUB_DIR;
        String newFilePath = newDirPath + CHAR_FILE_SEP + NEW_FILE;

        File newDir = new File(newDirPath);
        File newSubDir = new File(newSubDirPath);
        File newFile = new File(newFilePath);

        newDir.mkdir();
        newSubDir.mkdir();
        newFile.createNewFile();
        String[] expectedFilenames = {NEW_FILE, NEW_SUB_DIR};
        // Used Hashset so that order does not matter
        assertEquals(new HashSet<>(List.of(expectedFilenames)), new HashSet<>(List.of(FileSystemUtils.getFilesInFolder(NEW_DIR))));
        newFile.delete();
        newSubDir.delete();
        newDir.delete();
    }

    @Test
    void getFilesInFolder_ExistingEmptyDir_ReturnsFileNames() throws Exception {
        String newDirPath = Environment.currentDirectory + CHAR_FILE_SEP + NEW_DIR;
        File newDir = new File(newDirPath);
        newDir.mkdir();
        assertEquals(0, FileSystemUtils.getFilesInFolder(NEW_DIR).length);
        newDir.delete();
    }

    @Test
    void getFilesInFolder_NonExistingDir_ThrowExceptionWithCorrectMessage() {
        String expectedMessage = String.format(NON_EXIST_ERR, FAKE_DIR);
        try {
            FileSystemUtils.getFilesInFolder(FAKE_DIR);
        } catch (Exception e) {
            assertEquals(expectedMessage, e.getMessage());
        }
    }

    @Test
    void getFilesInFolder_ExistingFile_ThrowExceptionWithCorrectMessage() {
        String expectedMessage = String.format("%s is not a directory", SAMPLE_FILE);
        try {
            FileSystemUtils.getFilesInFolder(SAMPLE_DIR);
        } catch (Exception e) {
            assertEquals(expectedMessage, e.getMessage());
        }
    }

    @Test
    void joinPath_SingleFileName_ReturnsCorrectPath() {
        assertEquals(NEW_FILE + CHAR_FILE_SEP, FileSystemUtils.joinPath(NEW_FILE));
    }

    @Test
    void joinPath_MultipleFileNames_ReturnsCorrectPath() {
        assertEquals(NEW_DIR +  CHAR_FILE_SEP + NEW_SUB_DIR
                + CHAR_FILE_SEP + NEW_FILE + CHAR_FILE_SEP, FileSystemUtils.joinPath(NEW_DIR, NEW_SUB_DIR, NEW_FILE));
    }

    @Test
    void joinPath_NoFileName_ReturnsCorrectPath() {
        assertEquals("" + CHAR_FILE_SEP, FileSystemUtils.joinPath());
    }

    @Test
    void resolvePath_Dirname_ReturnsCorrectPath() {
        Path path = FileSystemUtils.resolvePath(SAMPLE_DIR);
        assertEquals(Paths.get(Environment.currentDirectory).resolve(SAMPLE_DIR).toString(), path.toString());
    }

    @Test
    void resolvePath_Filename_ReturnsCorrectPath() {
        Path path = FileSystemUtils.resolvePath(SAMPLE_FILE);
        assertEquals(Paths.get(Environment.currentDirectory).resolve(SAMPLE_FILE).toString(), path.toString());
    }

    @Test
    void getRelativeToCwd_ExistingDir_ReturnsCorrectRelativePath() {
        Path path = Paths.get(Environment.currentDirectory).resolve(SAMPLE_DIR);
        assertEquals(SAMPLE_DIR, FileSystemUtils.getRelativeToCwd(path).toString());
    }

    @Test
    void getRelativeToCwd_ExistingFile_ReturnsCorrectRelativePath() {
        Path path = Paths.get(Environment.currentDirectory).resolve(SAMPLE_FILE);
        assertEquals(SAMPLE_FILE, FileSystemUtils.getRelativeToCwd(path).toString());
    }
}
