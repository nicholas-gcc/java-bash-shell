package sg.edu.nus.comp.cs4218.impl.util;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledOnOs;
import org.junit.jupiter.api.condition.OS;
import sg.edu.nus.comp.cs4218.Environment;
import sg.edu.nus.comp.cs4218.exception.AbstractApplicationException;
import sg.edu.nus.comp.cs4218.exception.ShellException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;


public class ArgumentResolverTest {
    private static final String BASE_DIR = Environment.currentDirectory;

    private static final Path CWD = Paths.get(BASE_DIR);
    private static final Path PATH_TO_TEST_FILES = CWD.resolve("assets" + File.separator + "app"
            + File.separator + "ls");
    ApplicationRunner applicationRunner;
    ArgumentResolver argumentResolver;

    @BeforeEach
    void setUp() throws IOException {
        this.applicationRunner = new ApplicationRunner();
        this.argumentResolver = new ArgumentResolver();
    }

    @Test
    void parseArgument_GlobSingleAsteriskInCurrDirectory_CorrectArgTokens() throws FileNotFoundException, AbstractApplicationException, ShellException {
        List<String> args = Arrays.asList("ls", PATH_TO_TEST_FILES + File.separator + "*");
        List<String> expectedTokens = Arrays.asList("ls", PATH_TO_TEST_FILES + File.separator + "abc.txt",
                PATH_TO_TEST_FILES + File.separator + "testDir1",
                PATH_TO_TEST_FILES + File.separator + "testDir2",
                PATH_TO_TEST_FILES + File.separator + "testDir3");

        List<String> actualTokens = argumentResolver.parseArguments(args);
        assertEquals(expectedTokens, actualTokens);
    }

    @Test
    void parseArgument_GlobMatchSpecificDirectory_CorrectArgTokens() throws FileNotFoundException, AbstractApplicationException, ShellException {
        List<String> args = Arrays.asList("ls", PATH_TO_TEST_FILES + File.separator + "testDir1*");
        List<String> expectedTokens = Arrays.asList("ls", PATH_TO_TEST_FILES + File.separator + "testDir1");

        List<String> actualTokens = argumentResolver.parseArguments(args);
        assertEquals(expectedTokens, actualTokens);

    }

    @Test
    void parseArgument_GlobMatchMultipleDirectory_CorrectArgTokens() throws FileNotFoundException, AbstractApplicationException, ShellException {
        List<String> args = Arrays.asList("ls", PATH_TO_TEST_FILES + File.separator + "testD*");
        List<String> expectedTokens = Arrays.asList("ls", PATH_TO_TEST_FILES + File.separator + "testDir1",
                PATH_TO_TEST_FILES + File.separator + "testDir2",
                PATH_TO_TEST_FILES + File.separator + "testDir3");

        List<String> actualTokens = argumentResolver.parseArguments(args);
        assertEquals(expectedTokens, actualTokens);
    }

    @Test
    void parseArgument_GlobMatchRegularFile_CorrectArgTokens() {
        // In this unit test, * is a prefix to the pattern instead of postfix
        List<String> args = Arrays.asList("ls", PATH_TO_TEST_FILES + File.separator + "*.txt");
        List<String> expectedTokens = Arrays.asList("ls", PATH_TO_TEST_FILES + File.separator + "abc.txt");
        assertDoesNotThrow(() -> {
            List<String> actualTokens = argumentResolver.parseArguments(args);
            assertEquals(expectedTokens, actualTokens);
        });
    }

    @Test
    void parseArgument_CommandSubstitution_ReturnsCommandWithCorrectArgTokens() {
        List<String> args = Arrays.asList("echo", "`echo hello`");
        List<String> expectedTokens = Arrays.asList("echo", "hello");
        assertDoesNotThrow(() -> {
            List<String> actualTokens = argumentResolver.parseArguments(args);
            assertEquals(expectedTokens, actualTokens);
        });
    }

    @Test
    void parseArgument_CommandSubstitutionWithSingleQuote_ReturnsCommandWithCorrectArgTokens() {
        List<String> args = Arrays.asList("echo", "`echo 'hello world'`");

        // From project documentation: Other characters (including quotes) are not interpreted as special characters
        List<String> expectedTokens = Arrays.asList("echo", "hello", "world");
        assertDoesNotThrow(() -> {
            List<String> actualTokens = argumentResolver.parseArguments(args);
            assertEquals(expectedTokens, actualTokens);
        });
    }

    @Test
    void parseArgument_CommandSubstitutionWithMixedQuotes_ReturnsCommandWithCorrectArgTokens() {
        List<String> args = Arrays.asList("echo", "`echo \"‘quote is not interpreted as special character’\"`");

        // From proj documentation: Other characters (including quotes) are not interpreted as special characters
        List<String> expectedTokens = Arrays.asList("echo", "‘quote", "is", "not", "interpreted", "as", "special", "character’");
        assertDoesNotThrow(() -> {
            List<String> actualTokens = argumentResolver.parseArguments(args);
            assertEquals(expectedTokens, actualTokens);
        });
    }

    @Test
    void parseArgument_CommandSubstitution_ReturnsCommandWithNoNewline() {
        List<String> args = Arrays.asList("`ls`");
        assertDoesNotThrow(() -> {
            // output without command substitution: file1\n file2\n file3...
            // with command substitution: file1 file2 file3
            List<String> actualTokens = argumentResolver.parseArguments(args);

            // no newlines -> newlineIndex should be -1
            int newlineIndex = actualTokens.get(0).indexOf('\n');
            assertEquals(newlineIndex, -1);
        });
    }

    @Test
    void parseArgument_InvalidCommandSubstitution_ThrowsShellException() {
        List<String> args = Arrays.asList("echo", "`hello world`");
        assertThrows(ShellException.class, () -> {
            argumentResolver.parseArguments(args);
        });
    }

    @Test
    void parseArgument_CommandSubstitutionContainsNewline_ThrowsShellException() {
        List<String> args = Arrays.asList("echo", "`echo hello" + System.lineSeparator() + "`");
        assertThrows(ShellException.class, () -> {
            argumentResolver.parseArguments(args);
        });
    }


    @Test
    void resolveOneArgument_CommandSubstitution_ReturnsCommandWithCorrectArgTokens() {
        String args = "`echo hello`";
        List<String> expectedTokens = Arrays.asList("hello");
        assertDoesNotThrow(() -> {
            List<String> actualTokens = argumentResolver.resolveOneArgument(args);
            assertEquals(expectedTokens, actualTokens);
        });
    }

    @Test
    void resolveOneArgument_CommandSubstitutionNoQuoteMultipleTokens_ReturnsCommandWithCorrectArgTokens() {
        String args = "`echo hello world`";
        List<String> expectedTokens = Arrays.asList("hello", "world");
        assertDoesNotThrow(() -> {
            List<String> actualTokens = argumentResolver.resolveOneArgument(args);
            assertEquals(expectedTokens, actualTokens);
        });
    }

    @Test
    void resolveOneArgument_CommandSubstitutionWithSingleQuote_ReturnsCommandWithCorrectArgTokens() {
        String args = "`echo 'hello world'`";

        // note: resolveOneArgument should split up 'hello world'. parseArguments will combine them into one single 'hello world' token
        List<String> expectedTokens = Arrays.asList("hello", "world");
        assertDoesNotThrow(() -> {
            List<String> actualTokens = argumentResolver.resolveOneArgument(args);
            assertEquals(expectedTokens, actualTokens);
        });
    }
}
