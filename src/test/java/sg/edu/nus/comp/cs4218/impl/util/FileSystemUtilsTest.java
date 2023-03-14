package sg.edu.nus.comp.cs4218.impl.util;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sg.edu.nus.comp.cs4218.Environment;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.CHAR_FILE_SEP;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.STRING_NEWLINE;

public class FileSystemUtilsTest { //NOPMD
    static final String CWD = System.getProperty("user.dir");
    static final String TESTING_PATH = CHAR_FILE_SEP + "assets" + CHAR_FILE_SEP + "util" + CHAR_FILE_SEP + "FileSystemUtils";
    static final String SAMPLE_FILE = "sample.txt";
    static final String FAKE_FILE = "fake.txt";
    static final String SAMPLE_DIR = "sample";
    static final String FAKE_DIR = "fake";
    static final String NEW_FILE = "new.txt";
    static final String NEW_DIR = "new";
    static final String NEW_SUB_DIR = "newsubdir";


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
    void createEmptyFile_NonExistingFile_CreateNewFile() {
        String absolutePath = Environment.currentDirectory + CHAR_FILE_SEP + NEW_FILE;
        File file = new File(absolutePath);
        assertDoesNotThrow(() -> {
            FileSystemUtils.createEmptyFile(NEW_FILE);
            assertTrue(file.exists());
            file.delete();
        });
    }

    @Test
    void createEmptyFile_ExistingFile_ThrowException() {
        String expectedMessage = String.format("File or directory %s already exist", SAMPLE_FILE);
        try {
            FileSystemUtils.createEmptyFile(SAMPLE_FILE);
        } catch (Exception e) {
            assertEquals(expectedMessage, e.getMessage());
        }
    }

    @Test
    void createEmptyDir_NonExistingDir_CreateNewDir() {
        String absolutePath = Environment.currentDirectory + CHAR_FILE_SEP + NEW_DIR;
        File file = new File(absolutePath);
        assertDoesNotThrow(() -> {
            FileSystemUtils.createEmptyDir(NEW_DIR);
            assertTrue(file.exists());
            file.delete();
        });
    }

    @Test
    void createEmptyDir_ExistingDir_ThrowException() {
        String expectedMessage = String.format("File or directory %s already exist", SAMPLE_DIR);
        try {
            FileSystemUtils.createEmptyDir(SAMPLE_DIR);
        } catch (Exception e) {
            assertEquals(expectedMessage, e.getMessage());
        }
    }

    @Test
    void deleteFileOrDir_ExistingFile_DeletesFile() {
        String absolutePath = Environment.currentDirectory + CHAR_FILE_SEP + NEW_FILE;
        File file = new File(absolutePath);
        assertDoesNotThrow(() -> {
            file.createNewFile();
            FileSystemUtils.deleteFileOrDir(NEW_FILE);
            assertFalse(file.exists());
        });
    }

    @Test
    void deleteFileOrDir_NonExistingFile_ThrowsException() {
        String expectedMessage = String.format("File or directory %s does not exist", FAKE_FILE); //NOPMD
        try {
            FileSystemUtils.deleteFileOrDir(FAKE_FILE);
        } catch (Exception e) {
            assertEquals(expectedMessage, e.getMessage());
        }
    }

    @Test
    void deleteFileOrDir_ExistingDir_DeletesDir() {
        String absolutePath = Environment.currentDirectory + CHAR_FILE_SEP + NEW_DIR;
        File file = new File(absolutePath);
        assertDoesNotThrow(() -> {
            file.mkdir();
            FileSystemUtils.deleteFileOrDir(NEW_DIR);
            assertFalse(file.exists());
        });
    }

    @Test
    void deleteFileOrDir_NonExistingDir_ThrowsException() {
        String expectedMessage = String.format("File or directory %s does not exist", FAKE_DIR);
        try {
            FileSystemUtils.deleteFileOrDir(FAKE_DIR);
        } catch (Exception e) {
            assertEquals(expectedMessage, e.getMessage());
        }
    }

    @Test
    void readFileContent_ExistingFile_ReturnFilesContent() {
        String expectedResult = "This is a sample text." + STRING_NEWLINE + "This is the second line.";
        assertDoesNotThrow(() -> {
            String actualResult = FileSystemUtils.readFileContent(SAMPLE_FILE);
            assertEquals(expectedResult, actualResult);
        });
    }

    @Test
    void readFileContent_NonExistingFilename_ThrowsException() {
        String expectedMessage = String.format("File or directory %s does not exist", FAKE_FILE);
        try {
            FileSystemUtils.readFileContent(FAKE_FILE);
        } catch (Exception e) {
            assertEquals(expectedMessage, e.getMessage());
        }
    }

    @Test
    void readFileContent_ExistingDir_ThrowsException() {
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
    void writeStrToFile_WritingToExistingFile_WritesStringToFile() {
        String absolutePath = Environment.currentDirectory + CHAR_FILE_SEP + NEW_FILE;
        File file = new File(absolutePath);
        String textToAppend = "This is a text to write to the new file." + STRING_NEWLINE + "This is the second line.";
        boolean isAppend = false;
        assertDoesNotThrow(() -> {
            file.createNewFile();
            FileSystemUtils.writeStrToFile(isAppend, textToAppend, NEW_FILE);
            assertEquals(textToAppend, Files.readString(Paths.get(absolutePath)));
            file.delete();
        });
    }

    @Test
    void writeStrToFile_AppendToExistingFile_AppendsStringToFile() {
        String absolutePath = Environment.currentDirectory + CHAR_FILE_SEP + NEW_FILE;
        File file = new File(absolutePath);
        String textToWrite = "Initial text" + STRING_NEWLINE + "Testing";
        String textToAppend = "This is a text to append to the new file." + STRING_NEWLINE + "This is the second line.";
        boolean isAppend = true;
        assertDoesNotThrow(() -> {
            file.createNewFile();
            Path path = Paths.get(absolutePath);
            Files.write(path, textToWrite.getBytes(), StandardOpenOption.APPEND);

            FileSystemUtils.writeStrToFile(isAppend, textToAppend, NEW_FILE);
            assertEquals(textToWrite + textToAppend, Files.readString(path));
            file.delete();
        });
    }

    @Test
    void isDir_ExistingDir_ReturnsTrue() {
        assertDoesNotThrow(() -> assertTrue(FileSystemUtils.isDir(SAMPLE_DIR)));
    }

    @Test
    void isDir_ExistingFile_ReturnsFalse() {
        assertDoesNotThrow(() -> assertFalse(FileSystemUtils.isDir(SAMPLE_FILE)));
    }

    @Test
    void isDir_NonExistingDir_ThrowsException() {
        String expectedMessage = String.format("File or directory %s does not exist", FAKE_DIR);
        try {
            FileSystemUtils.isDir(FAKE_DIR);
        } catch (Exception e) {
            assertEquals(expectedMessage, e.getMessage());
        }
    }

    @Test
    void isEmptyDir_ExistingEmptyDir_ReturnsTrue() {
        String absolutePath = Environment.currentDirectory + CHAR_FILE_SEP + NEW_DIR;
        File file = new File(absolutePath);
        assertDoesNotThrow(() -> {
            file.mkdir();
            assertTrue(FileSystemUtils.isEmptyDir(NEW_DIR));
            file.delete();
        });
    }

    @Test
    void isEmptyDir_ExistingNonEmptyDir_ReturnsFalse() {
        assertDoesNotThrow(() -> assertFalse(FileSystemUtils.isEmptyDir(SAMPLE_DIR)));
    }

    @Test
    void isEmptyDir_ExistingFile_ReturnsFalse() {
        assertDoesNotThrow(() -> assertFalse(FileSystemUtils.isEmptyDir(SAMPLE_FILE)));
    }

    @Test
    void isEmptyDir_NonExistingDir_ThrowsException() {
        String expectedMessage = String.format("File or directory %s does not exist", FAKE_DIR);
        try {
            FileSystemUtils.isEmptyDir(FAKE_DIR);
        } catch (Exception e) {
            assertEquals(expectedMessage, e.getMessage());
        }
    }

    @Test
    void isSubDir_ExistingDirWithSubDir_ReturnsTrue() {
        String newDirPath = Environment.currentDirectory + CHAR_FILE_SEP + NEW_DIR;
        String newSubDirPath = newDirPath + CHAR_FILE_SEP + NEW_SUB_DIR;

        File newDir = new File(newDirPath);
        File newSubDir = new File(newSubDirPath);

        assertDoesNotThrow(() -> {
            newDir.mkdir();
            newSubDir.mkdir();
            assertTrue(FileSystemUtils.isSubDir(NEW_DIR, NEW_DIR + CHAR_FILE_SEP + NEW_SUB_DIR));
            newSubDir.delete();
            newDir.delete();
        });
    }

    @Test
    void isSubDir_TwoSeparateExistingDir_ReturnsFalse() {
        String newDirPath = Environment.currentDirectory + CHAR_FILE_SEP + NEW_DIR;
        File newDir1 = new File(newDirPath);
        assertDoesNotThrow(() -> {
            newDir1.mkdir();
            assertFalse(FileSystemUtils.isSubDir(NEW_DIR, SAMPLE_DIR));
            newDir1.delete();
        });
    }

    @Test
    void isSubDir_ExistingDirAndExistingFile_ReturnsFalse() {
        assertDoesNotThrow(() -> {
            assertFalse(FileSystemUtils.isSubDir(SAMPLE_FILE, SAMPLE_DIR));
        });
    }

    @Test
    void isFileInFolder_ExistingDirWithFile_ReturnsTrue() {
        String newDirPath = Environment.currentDirectory + CHAR_FILE_SEP + NEW_DIR;
        String newFilePath = newDirPath + CHAR_FILE_SEP + NEW_FILE;

        File newDir = new File(newDirPath);
        File newFile = new File(newFilePath);

        assertDoesNotThrow(() -> {
            newDir.mkdir();
            newFile.createNewFile();
            assertTrue(FileSystemUtils.isFileInFolder(NEW_DIR + CHAR_FILE_SEP + NEW_FILE, NEW_DIR));
            newFile.delete();
            newDir.delete();
        });
    }

    @Test
    void isFileInFolder_SeparateExistingFileAndDir_ReturnsFalse() {
        String newDirPath = Environment.currentDirectory + CHAR_FILE_SEP + NEW_DIR;

        File newDir = new File(newDirPath);

        assertDoesNotThrow(() -> {
            newDir.mkdir();
            assertFalse(FileSystemUtils.isFileInFolder(SAMPLE_FILE, NEW_DIR));
            newDir.delete();
        });
    }

    @Test
    void isFileInFolder_NonExistingFileAndExistingDir_ReturnsFalse() {
        assertDoesNotThrow(() -> {
            assertFalse(FileSystemUtils.isFileInFolder(SAMPLE_FILE, FAKE_DIR));
        });
    }

    @Test
    void isFileInFolder_ExistingFileAndNonExistingDir_ReturnsFalse() {
        assertDoesNotThrow(() -> {
            assertFalse(FileSystemUtils.isFileInFolder(FAKE_DIR, SAMPLE_DIR));
        });
    }

    @Test
    void getFilesInFolder_ExistingDirWithSubDirAndFile_ReturnsFileNames() {
        String newDirPath = Environment.currentDirectory + CHAR_FILE_SEP + NEW_DIR;
        String newSubDirPath = newDirPath + CHAR_FILE_SEP + NEW_SUB_DIR;
        String newFilePath = newDirPath + CHAR_FILE_SEP + NEW_FILE;

        File newDir = new File(newDirPath);
        File newSubDir = new File(newSubDirPath);
        File newFile = new File(newFilePath);

        assertDoesNotThrow(() -> {
            newDir.mkdir();
            newSubDir.mkdir();
            newFile.createNewFile();
            String[] expectedFilenames = {NEW_FILE, NEW_SUB_DIR};
            // Used Hashset so that order does not matter
            assertEquals(new HashSet<>(List.of(expectedFilenames)), new HashSet<>(List.of(FileSystemUtils.getFilesInFolder(NEW_DIR))));

            for (int i = 0; i < expectedFilenames.length; i++) {
            }
            newFile.delete();
            newSubDir.delete();
            newDir.delete();
        });
    }

    @Test
    void getFilesInFolder_ExistingEmptyDir_ReturnsFileNames() {
        String newDirPath = Environment.currentDirectory + CHAR_FILE_SEP + NEW_DIR;
        File newDir = new File(newDirPath);
        assertDoesNotThrow(() -> {
            newDir.mkdir();
            assertEquals(0, FileSystemUtils.getFilesInFolder(NEW_DIR).length);
            newDir.delete();
        });
    }

    @Test
    void getFilesInFolder_NonExistingDir_ThrowsException() {
        String expectedMessage = String.format("File or directory %s does not exist", FAKE_DIR);
        try {
            FileSystemUtils.getFilesInFolder(FAKE_DIR);
        } catch (Exception e) {
            assertEquals(expectedMessage, e.getMessage());
        }
    }

    @Test
    void getFilesInFolder_ExistingFile_ThrowsException() {
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
}
