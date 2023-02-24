package sg.edu.nus.comp.cs4218.impl.app;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sg.edu.nus.comp.cs4218.Environment;
import sg.edu.nus.comp.cs4218.exception.MvException;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class MvApplicationTest {

    private static final String BASE_URL = Environment.currentDirectory;
    private static final String FLAG_IS_NO_OVERWRITE = "-n";

    private static final Path CWD = Paths.get(BASE_URL);
    private Path tempFileA;
    private Path tempFileB;
    private Path tempDirA;
    private Path tempDirB;

    private InputStream inputStream;
    private ByteArrayOutputStream outputStream;

    private MvApplication mvApplication;

    @BeforeEach
    void setUp() throws IOException {
        tempFileA = Files.createTempFile(CWD, "stubA", ".txt");
        tempFileB = Files.createTempFile(CWD, "stubB", ".txt");
        tempDirA = Files.createTempDirectory(CWD, "stubDirA");
        tempDirB = Files.createTempDirectory(CWD, "stubDirB");

        mvApplication = new MvApplication();
        inputStream = new ByteArrayInputStream(new byte[0]);
        outputStream = new ByteArrayOutputStream();
    }

    @AfterEach
    void tearDown() throws IOException {
        delete(tempFileA, false);
        delete(tempFileB, false);
        delete(tempDirA, true);
        delete(tempDirB, true);
    }

    @Test
    void run_AllNullParameters_ThrowsException() {
        String expected = "mv: Null arguments";
        Throwable err = assertThrows(MvException.class, () -> mvApplication.run(null, null, null));
        assertEquals(expected, err.getMessage());
    }

    @Test
    void run_nullInputStream_ShouldNotFail() {
        String[] args = {getFileName(tempFileA), getFileName(tempFileB)};
        assertDoesNotThrow(() -> mvApplication.run(args, null, outputStream));

    }

    @Test
    void run_nullOutputStream_ThrowsException() {
        String expected = "mv: OutputStream cannot be null";
        String[] args = {};
        Throwable err = assertThrows(MvException.class, () -> mvApplication.run(args, inputStream, null));
        assertEquals(expected, err.getMessage());
    }

    @Test
    void run_WithInvalidFlags_ThrowsException() {
        String expected = "mv: illegal option -- x";
        String[] args = {"-x", getFileName(tempFileB)};
        Throwable err = assertThrows(MvException.class, () -> mvApplication.run(args, inputStream, outputStream));
        assertEquals(expected, err.getMessage());
    }


    @Test
    void run_WithMissingSourceFile_ThrowsException() {
        String expected = "mv: Missing Argument";
        String[] args = {};
        Throwable err = assertThrows(MvException.class, () -> mvApplication.run(args, inputStream, outputStream));
        assertEquals(expected, err.getMessage());
    }


    @Test
    void run_RenameFileOverride_ShouldNotThrow() {
        String[] args = {getFileName(tempFileA), getFileName(tempFileB)};
        assertDoesNotThrow(() -> mvApplication.run(args, inputStream, outputStream));
    }


    @Test
    void run_MoveFileIntoDirectoryOverride_ShouldNotThrow() {
        String[] args = {getFileName(tempFileA), getFileName(tempDirA)};
        assertDoesNotThrow(() -> mvApplication.run(args, inputStream, outputStream));
    }

    @Test
    void run_MoveFileIntoDirectoryNoOverride_ShouldNotThrow() {
        String[] args = {FLAG_IS_NO_OVERWRITE, getFileName(tempFileA), getFileName(tempDirA)};
        assertDoesNotThrow(() -> mvApplication.run(args, inputStream, outputStream));
    }

    @Test
    void run_MoveTwoFilesIntoDirectoryOverride() {
        String[] args = {getFileName(tempFileA), getFileName(tempFileB), getFileName(tempDirA)};
        assertDoesNotThrow(() -> mvApplication.run(args, inputStream, outputStream));
    }


    @Test
    void run_MoveFolderToAnother_ShouldNotThrow() {
        String[] args = {getFileName(tempDirA), getFileName(tempDirB)};
        assertDoesNotThrow(() -> mvApplication.run(args, inputStream, outputStream));
    }

    private String getFileName(Path path) {
        return path.getFileName().toString();
    }

    // recursive filepath delete https://stackoverflow.com/questions/35988192/java-nio-most-concise-recursive-directory-delete
    private void delete(Path path, boolean isFolder) throws IOException {
        if (isFolder) {
            if (path.toFile().exists()) {
                try (Stream<Path> walk = Files.walk(path)) {
                    walk.sorted(Comparator.reverseOrder())
                            .map(Path::toFile)
                            .forEach(File::delete);
                }
            }
        } else {
            path.toFile().delete();
        }
    }
}
