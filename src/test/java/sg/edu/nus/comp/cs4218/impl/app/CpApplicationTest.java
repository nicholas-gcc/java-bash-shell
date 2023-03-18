package sg.edu.nus.comp.cs4218.impl.app;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sg.edu.nus.comp.cs4218.Environment;
import sg.edu.nus.comp.cs4218.exception.CpException;
import sg.edu.nus.comp.cs4218.impl.util.FileSystemUtils;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

public class CpApplicationTest {
    private static final String BASE_URL = Environment.currentDirectory;
    private static final String FLAG_IS_RECURSIVE = "-r";

    private static final Path CWD = Paths.get(BASE_URL);
    private Path tempFileA;
    private Path tempFileB;
    private Path tempDirA;
    private Path tempDirB;
    private Path tempDirC;
    private Path tempFileC;
    private Path tempFileD;

    private InputStream inputStream;
    private ByteArrayOutputStream outputStream;

    private CpApplication cpApplication;
    private final static String STUB_A_TEXT = "This is stubA.txt";
    private final static String STUB_B_TEXT = "This is stubB.txt";
    private final static String STUB_C_TEXT = "This is stubC.txt";
    private final static String STUB_D_TEXT = "This is stubD.txt";
    private final static String TXT_EXTENSION = ".txt";


    @BeforeEach
    void setUp() throws IOException {
        /*
         * stubDirA
         *   - stubA.txt
         * stubDirB
         *   - stubDirC
         *       - stubC.txt
         *   - stubB.txt
         * stubD.txt
         * */
        tempDirA = Files.createTempDirectory(CWD, "stubDirA");
        tempFileA = Files.createTempFile(tempDirA, "stubA", TXT_EXTENSION);
        Files.writeString(tempFileA, STUB_A_TEXT);
        tempDirB = Files.createTempDirectory(CWD, "stubDirB");
        tempDirC = Files.createTempDirectory(tempDirB, "stubDirC");
        tempFileC = Files.createTempFile(tempDirC, "stubC", TXT_EXTENSION);
        Files.writeString(tempFileC, STUB_C_TEXT);
        tempFileB = Files.createTempFile(tempDirB, "stubB", TXT_EXTENSION);
        Files.writeString(tempFileB, STUB_B_TEXT);
        tempFileD = Files.createTempFile(CWD, "stubD", TXT_EXTENSION);
        Files.writeString(tempFileD, STUB_D_TEXT);

        cpApplication = new CpApplication();
        inputStream = new ByteArrayInputStream(new byte[0]);
        outputStream = new ByteArrayOutputStream();
    }

    @AfterEach
    void tearDown() throws IOException {
        delete(tempFileA, false);
        delete(tempFileB, false);
        delete(tempDirA, true);
        delete(tempDirB, true);
        delete(tempFileD, false);
    }

    @Test
    void run_NullArgs_ThrowsException() {
        String expected = "cp: Null arguments";
        Throwable err = assertThrows(CpException.class, () -> cpApplication.run(null, null, null));
        assertEquals(expected, err.getMessage());
    }

    @Test
    void run_WithInvalidFlags_ThrowsException() {
        String expected = "cp: illegal option -- x";
        String[] args = {"-x", getFileName(tempFileB)};
        Throwable err = assertThrows(CpException.class, () -> cpApplication.run(args, inputStream, outputStream));
        assertEquals(expected, err.getMessage());
    }

    @Test
    void run_WithMissingSourceFile_ThrowsException() {
        String expected = "cp: Missing Argument";
        String[] args = {};
        Throwable err = assertThrows(CpException.class, () -> cpApplication.run(args, inputStream, outputStream));
        assertEquals(expected, err.getMessage());
    }

    @Test
    void run_MoveFileToDirectory_Success() throws IOException {
        String[] args = {getFileName(tempFileD), getFileName(tempDirA)};

        // check newly copied file exists
        assertDoesNotThrow(() -> cpApplication.run(args, inputStream, outputStream));
        Path copiedFile = tempDirA.resolve(tempFileD.getFileName());
        assertTrue(Files.exists(copiedFile));

        // check file contents are copied over
        String expectedContent = STUB_D_TEXT;
        String actualContent = Files.readString(copiedFile);
        assertEquals(expectedContent, actualContent);
    }

    @Test
    void run_CopyFileToAnotherFile_Success() throws IOException {
        String[] args = {getFileName(tempFileD), tempFileA.toString()};

        // check newly copied file exists
        assertDoesNotThrow(() -> cpApplication.run(args, inputStream, outputStream));
        Path copiedFile = CWD.resolve(tempFileD.getFileName());
        Path overwrittenFile = tempDirA.resolve(tempFileA.getFileName());
        assertTrue(Files.exists(copiedFile) && Files.exists(overwrittenFile));

        // check file contents are copied over correctly
        String expectedContent = STUB_D_TEXT;
        String actualContent = Files.readString(overwrittenFile);
        assertEquals(expectedContent, actualContent);
    }

    @Test
    void run_CopyMultipleFilesIntoDir_Success() throws IOException {
        // copy contents of tempFileD and tempFileB to tempDirA
        String[] args = {getFileName(tempFileD), tempFileB.toString(), getFileName(tempDirA)};

        // check newly copied file exists
        assertDoesNotThrow(() -> cpApplication.run(args, inputStream, outputStream));
        Path copiedFileD = tempDirA.resolve(tempFileD.getFileName());
        Path copiedFileA = tempDirA.resolve(tempFileB.getFileName());
        assertTrue(Files.exists(copiedFileD) && Files.exists(copiedFileA));

        // check file contents are copied over
        String expectedContentD = STUB_D_TEXT;
        String actualContentD = Files.readString(copiedFileD);
        assertEquals(expectedContentD, actualContentD);

        String expectedContentA = STUB_B_TEXT;
        String actualContentA = Files.readString(copiedFileA);
        assertEquals(expectedContentA, actualContentA);
    }

    @Test
    void run_CopyDirIntoDirWithoutRecursiveFlag_ThrowsException() {
        String expected = "cp: This is a directory: " + getFileName(tempDirA);
        String[] args = { getFileName(tempDirA), getFileName(tempDirB) };
        Throwable err = assertThrows(CpException.class, () -> cpApplication.run(args, inputStream, outputStream));
        assertEquals(expected, err.getMessage());
    }

    @Test
    void run_CopyDirRecursivelyIntoChild_ThrowsException() {
        /**
         * Follows the behaviour from Unix - error will say file is too long due to unlimited recursion
         * example: copy dirB into dirC when dirC is a child of dirB
         * dirB
         *  - dirC
         *  - other files...
         */
        String[] args = { getFileName(tempDirB), tempDirC.toString() };
        assertThrows(CpException.class, () -> cpApplication.run(args, inputStream, outputStream));
    }

    @Test
    void run_RecursiveCopyDirIntoAnotherDir_Success() throws IOException {
        String[] args = {FLAG_IS_RECURSIVE, getFileName(tempDirB), getFileName(tempDirA) };

        assertDoesNotThrow(() -> cpApplication.run(args, inputStream, outputStream));

        // check dirB is copied to dirA
        Path copiedDirB = tempDirA.resolve(tempDirB.getFileName());

        // check contents of dirB are also copied to dirA
        Path copiedFileB = copiedDirB.resolve(tempFileB.getFileName()); // check B.txt is inside newly copied dirB
        Path copiedDirC = copiedDirB.resolve(tempDirC.getFileName()); // check we copied dirC nested in dirB
        Path copiedFileC = copiedDirC.resolve(tempFileC.getFileName()); // check we coped C.txt nested in dirC
        assertTrue(Files.exists(copiedDirB) && Files.exists(copiedFileB) && Files.exists(copiedDirC) &&
                Files.exists(copiedFileC));

        // check file contents are copied over
        String expectedContentB = STUB_B_TEXT;
        String actualContentB = Files.readString(copiedFileB);
        assertEquals(expectedContentB, actualContentB);

        String expectedContentC = STUB_C_TEXT;
        String actualContentC = Files.readString(copiedFileC);
        assertEquals(expectedContentC, actualContentC);
    }

    @Test
    void run_CopyDirAndFileIntoAnotherDir_Success() throws IOException {
        String[] args = {FLAG_IS_RECURSIVE, getFileName(tempDirB), getFileName(tempFileD), getFileName(tempDirA) };

        assertDoesNotThrow(() -> cpApplication.run(args, inputStream, outputStream));

        // check dirB is copied to dirA
        Path copiedDirB = tempDirA.resolve(tempDirB.getFileName());

        // check D.txt is copied to dirA
        Path copiedFileD = tempDirA.resolve(tempFileD.getFileName());

        // check contents of dirB are also copied to dirA
        Path copiedFileB = copiedDirB.resolve(tempFileB.getFileName()); // check B.txt is inside newly copied dirB
        Path copiedDirC = copiedDirB.resolve(tempDirC.getFileName()); // check we copied dirC nested in dirB
        Path copiedFileC = copiedDirC.resolve(tempFileC.getFileName()); // check we coped C.txt nested in dirC
        assertTrue(Files.exists(copiedDirB) && Files.exists(copiedFileB) && Files.exists(copiedDirC) &&
                Files.exists(copiedFileC) && Files.exists(copiedFileD));

        // check file contents are copied over
        String expectedContentB = STUB_B_TEXT;
        String actualContentB = Files.readString(copiedFileB);
        assertEquals(expectedContentB, actualContentB);

        String expectedContentC = STUB_C_TEXT;
        String actualContentC = Files.readString(copiedFileC);
        assertEquals(expectedContentC, actualContentC);

        String expectedContentD = STUB_D_TEXT;
        String actualContentD = Files.readString(copiedFileD);
        assertEquals(expectedContentD, actualContentD);
    }

    @Test
    void run_CopyDirIntoNonExistentDir_Success() throws IOException {
        String newDir = "newDir";
        String[] args = {FLAG_IS_RECURSIVE, getFileName(tempDirB), newDir };

        assertDoesNotThrow(() -> cpApplication.run(args, inputStream, outputStream));

        // check newDir is created
        assertTrue(Files.exists(Path.of(newDir)));

        Path newlyCreatedDir = FileSystemUtils.resolvePath(newDir);

        // check dirB is copied to dirA
        Path copiedDirB = newlyCreatedDir.resolve(tempDirB.getFileName());

        // check contents of dirB are also copied to dirA
        Path copiedFileB = copiedDirB.resolve(tempFileB.getFileName()); // check B.txt is inside newly copied dirB
        Path copiedDirC = copiedDirB.resolve(tempDirC.getFileName()); // check we copied dirC nested in dirB
        Path copiedFileC = copiedDirC.resolve(tempFileC.getFileName()); // check we coped C.txt nested in dirC
        assertTrue(Files.exists(copiedDirB) && Files.exists(copiedFileB) && Files.exists(copiedDirC) &&
                Files.exists(copiedFileC));

        // check file contents are copied over
        String expectedContentB = STUB_B_TEXT;
        String actualContentB = Files.readString(copiedFileB);
        assertEquals(expectedContentB, actualContentB);

        String expectedContentC = STUB_C_TEXT;
        String actualContentC = Files.readString(copiedFileC);
        assertEquals(expectedContentC, actualContentC);

        delete(newlyCreatedDir, true);
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
