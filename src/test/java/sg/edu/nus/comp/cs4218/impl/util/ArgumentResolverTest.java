package sg.edu.nus.comp.cs4218.impl.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledOnOs;
import org.junit.jupiter.api.condition.OS;
import sg.edu.nus.comp.cs4218.Environment;
import sg.edu.nus.comp.cs4218.exception.ShellException;

import java.io.File;
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
    private static final Path PATH_FILES = CWD.resolve("assets" + File.separator + "app"
            + File.separator + "ls");
    private static final String ECHO_CMD = "echo";
    private static final String HELLO_STR = "hello";

    ApplicationRunner applicationRunner;
    ArgumentResolver argumentResolver;

    @BeforeEach
    void setUp() throws IOException {
        this.applicationRunner = new ApplicationRunner();
        this.argumentResolver = new ArgumentResolver();
    }

    @Test
    @DisabledOnOs(OS.WINDOWS)
    void parseArgument_GlobSingleAsteriskInCurrDirectory_CorrectArgTokens() {
        List<String> args = Arrays.asList("ls", PATH_FILES + File.separator + "*");
        List<String> expectedTokens = Arrays.asList("ls", PATH_FILES + File.separator + "abc.txt",
                PATH_FILES + File.separator + "testDir1",
                PATH_FILES + File.separator + "testDir2",
                PATH_FILES + File.separator + "testDir3");
        assertDoesNotThrow(() -> {
            List<String> actualTokens = argumentResolver.parseArguments(args);
            assertEquals(expectedTokens, actualTokens);
        });
    }

    @Test
    @DisabledOnOs(OS.WINDOWS)
    void parseArgument_GlobMatchSpecificDirectory_CorrectArgTokens() {
        List<String> args = Arrays.asList("ls", PATH_FILES + File.separator + "testDir1*");
        List<String> expectedTokens = Arrays.asList("ls", PATH_FILES + File.separator + "testDir1");
        assertDoesNotThrow(() -> {
            List<String> actualTokens = argumentResolver.parseArguments(args);
            assertEquals(expectedTokens, actualTokens);
        });
    }

    @Test
    @DisabledOnOs(OS.WINDOWS)
    void parseArgument_GlobMatchMultipleDirectory_CorrectArgTokens() {
        List<String> args = Arrays.asList("ls", PATH_FILES + File.separator + "testD*");
        List<String> expectedTokens = Arrays.asList("ls", PATH_FILES + File.separator + "testDir1",
                PATH_FILES + File.separator + "testDir2",
                PATH_FILES + File.separator + "testDir3");
        assertDoesNotThrow(() -> {
            List<String> actualTokens = argumentResolver.parseArguments(args);
            assertEquals(expectedTokens, actualTokens);
        });
    }

    @Test
    void parseArgument_CommandSubstitution_ReturnsCommandWithCorrectArgTokens() {
        List<String> args = Arrays.asList(ECHO_CMD, "`echo hello`");
        List<String> expectedTokens = Arrays.asList(ECHO_CMD, "hello");
        assertDoesNotThrow(() -> {
            List<String> actualTokens = argumentResolver.parseArguments(args);
            assertEquals(expectedTokens, actualTokens);
        });
    }

    @Test
    void parseArgument_CommandSubstitutionWithSingleQuote_ReturnsCommandWithCorrectArgTokens() {
        List<String> args = Arrays.asList(ECHO_CMD, "`echo 'hello world'`");

        // From project documentation: Other characters (including quotes) are not interpreted as special characters
        List<String> expectedTokens = Arrays.asList(ECHO_CMD, "hello", "world");
        assertDoesNotThrow(() -> {
            List<String> actualTokens = argumentResolver.parseArguments(args);
            assertEquals(expectedTokens, actualTokens);
        });
    }

    @Test
    void parseArgument_CommandSubstitutionWithMixedQuotes_ReturnsCommandWithCorrectArgTokens() {
        List<String> args = Arrays.asList(ECHO_CMD, "`echo \"‘quote is not interpreted as special character’\"`");

        // From proj documentation: Other characters (including quotes) are not interpreted as special characters
        List<String> expectedTokens = Arrays.asList(ECHO_CMD, "‘quote", "is", "not", "interpreted", "as", "special", "character’");
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
        List<String> args = Arrays.asList(ECHO_CMD, "`hello world`");
        assertThrows(ShellException.class, () -> {
            argumentResolver.parseArguments(args);
        });
    }

    @Test
    void parseArgument_CommandSubstitutionContainsNewline_ThrowsShellException() {
        List<String> args = Arrays.asList(ECHO_CMD, "`echo hello" + System.lineSeparator() + "`");
        assertThrows(ShellException.class, () -> {
            argumentResolver.parseArguments(args);
        });
    }


    @Test
    void resolveOneArgument_CommandSubstitution_ReturnsCommandWithCorrectArgTokens() {
        String args = "`echo hello`";
        List<String> expectedTokens = Arrays.asList(HELLO_STR);
        assertDoesNotThrow(() -> {
            List<String> actualTokens = argumentResolver.resolveOneArgument(args);
            assertEquals(expectedTokens, actualTokens);
        });
    }

    @Test
    void resolveOneArgument_CommandSubstitutionNoQuoteMultipleTokens_ReturnsCommandWithCorrectArgTokens() {
        String args = "`echo hello world`";
        List<String> expectedTokens = Arrays.asList(HELLO_STR, "world");
        assertDoesNotThrow(() -> {
            List<String> actualTokens = argumentResolver.resolveOneArgument(args);
            assertEquals(expectedTokens, actualTokens);
        });
    }

    @Test
    void resolveOneArgument_CommandSubstitutionWithSingleQuote_ReturnsCommandWithCorrectArgTokens() {
        String args = "`echo 'hello world'`";

        // note: resolveOneArgument should split up 'hello world'. parseArguments will combine them into one single 'hello world' token
        List<String> expectedTokens = Arrays.asList(HELLO_STR, "world");
        assertDoesNotThrow(() -> {
            List<String> actualTokens = argumentResolver.resolveOneArgument(args);
            assertEquals(expectedTokens, actualTokens);
        });
    }
}
