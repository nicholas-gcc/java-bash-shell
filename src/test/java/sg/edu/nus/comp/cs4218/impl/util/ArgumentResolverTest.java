package sg.edu.nus.comp.cs4218.impl.util;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sg.edu.nus.comp.cs4218.Environment;
import sg.edu.nus.comp.cs4218.exception.AbstractApplicationException;
import sg.edu.nus.comp.cs4218.exception.ShellException;

import java.io.ByteArrayOutputStream;
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
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.CHAR_FILE_SEP;


public class ArgumentResolverTest {
    static final String CWD = System.getProperty("user.dir");
    static final String TESTING_PATH = CHAR_FILE_SEP + "assets" + CHAR_FILE_SEP + "app" + CHAR_FILE_SEP + "ls";

    private static final String ECHO_CMD = "echo";
    private static final String HELLO_STR = "hello";

    ApplicationRunner applicationRunner;
    ArgumentResolver argumentResolver;

    @BeforeEach
    void setUp() {
        this.applicationRunner = new ApplicationRunner();
        this.argumentResolver = new ArgumentResolver();
    }

    @BeforeEach
    void setCurrentDirectory() {
        Environment.currentDirectory += TESTING_PATH;
    }

    @AfterEach
    void resetCurrentDirectory()  {
        Environment.currentDirectory = this.CWD;
    }

    @Test
    void parseArgument_GlobSingleAsteriskInCurrDirectory_CorrectArgTokens() throws FileNotFoundException, AbstractApplicationException, ShellException {
        List<String> args = Arrays.asList("ls", "*");
        List<String> expectedTokens = Arrays.asList("ls", Environment.currentDirectory + CHAR_FILE_SEP + "abc.txt",
                Environment.currentDirectory + CHAR_FILE_SEP + "testDir1" + CHAR_FILE_SEP,
                Environment.currentDirectory + CHAR_FILE_SEP + "testDir2" + CHAR_FILE_SEP,
                Environment.currentDirectory + CHAR_FILE_SEP + "testDir3" + CHAR_FILE_SEP);

        List<String> actualTokens = argumentResolver.parseArguments(args);
        assertEquals(expectedTokens, actualTokens);
    }

    @Test
    void parseArgument_GlobMatchSpecificDirectory_CorrectArgTokens() throws FileNotFoundException, AbstractApplicationException, ShellException {
        List<String> args = Arrays.asList("ls", "testDir1*");
        List<String> expectedTokens = Arrays.asList("ls",  Environment.currentDirectory + CHAR_FILE_SEP + "testDir1" + CHAR_FILE_SEP);

        List<String> actualTokens = argumentResolver.parseArguments(args);
        assertEquals(expectedTokens, actualTokens);

    }

    @Test
    void parseArgument_GlobMatchMultipleDirectory_CorrectArgTokens() throws FileNotFoundException, AbstractApplicationException, ShellException {
        List<String> args = Arrays.asList("ls", "testD*");
        List<String> expectedTokens = Arrays.asList("ls", Environment.currentDirectory + CHAR_FILE_SEP + "testDir1" + CHAR_FILE_SEP,
                Environment.currentDirectory + CHAR_FILE_SEP + "testDir2" + CHAR_FILE_SEP,
                Environment.currentDirectory + CHAR_FILE_SEP + "testDir3" + CHAR_FILE_SEP);

        List<String> actualTokens = argumentResolver.parseArguments(args);
        assertEquals(expectedTokens, actualTokens);
    }

    @Test
    void parseArgument_GlobMatchRegularFile_CorrectArgTokens() throws FileNotFoundException, AbstractApplicationException, ShellException {
        // In this unit test, * is a prefix to the pattern instead of postfix
        List<String> args = Arrays.asList("ls", "*.txt");
        List<String> expectedTokens = Arrays.asList("ls", Environment.currentDirectory + CHAR_FILE_SEP + "abc.txt");
        List<String> actualTokens = argumentResolver.parseArguments(args);
        assertEquals(expectedTokens, actualTokens);
    }

    @Test
    void parseArgument_CommandSubstitution_ReturnsCommandWithCorrectArgTokens() throws FileNotFoundException, AbstractApplicationException, ShellException {
        List<String> args = Arrays.asList(ECHO_CMD, "`echo hello`");
        List<String> expectedTokens = Arrays.asList(ECHO_CMD, HELLO_STR);
        List<String> actualTokens = argumentResolver.parseArguments(args);
        assertEquals(expectedTokens, actualTokens);
    }

    @Test
    void parseArgument_CommandSubstitutionWithSingleQuote_ReturnsCommandWithCorrectArgTokens() throws FileNotFoundException, AbstractApplicationException, ShellException {
        List<String> args = Arrays.asList(ECHO_CMD, "`echo 'hello world'`");

        // From project documentation: Other characters (including quotes) are not interpreted as special characters
        List<String> expectedTokens = Arrays.asList(ECHO_CMD, HELLO_STR, "world");
        List<String> actualTokens = argumentResolver.parseArguments(args);
        assertEquals(expectedTokens, actualTokens);
    }

    @Test
    void parseArgument_CommandSubstitutionWithMixedQuotes_ReturnsCommandWithCorrectArgTokens() throws FileNotFoundException, AbstractApplicationException, ShellException {
        List<String> args = Arrays.asList(ECHO_CMD, "`echo \"‘quote is not interpreted as special character’\"`");

        // From proj documentation: Other characters (including quotes) are not interpreted as special characters
        List<String> expectedTokens = Arrays.asList(ECHO_CMD, "‘quote", "is", "not", "interpreted", "as", "special", "character’");
        List<String> actualTokens = argumentResolver.parseArguments(args);
        assertEquals(expectedTokens, actualTokens);
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
    void resolveOneArgument_CommandSubstitutionNoQuoteMultipleTokens_ReturnsCommandWithCorrectArgTokens() throws FileNotFoundException, AbstractApplicationException, ShellException {
        String args = "`echo hello world`";
        List<String> expectedTokens = Arrays.asList(HELLO_STR, "world");
        List<String> actualTokens = argumentResolver.resolveOneArgument(args);
        assertEquals(expectedTokens, actualTokens);
    }

    @Test
    void resolveOneArgument_CommandSubstitutionWithSingleQuote_ReturnsCommandWithCorrectArgTokens() throws FileNotFoundException, AbstractApplicationException, ShellException {
        String args = "`echo 'hello world'`";

        // note: resolveOneArgument should split up 'hello world'. parseArguments will combine them into one single 'hello world' token
        List<String> expectedTokens = Arrays.asList(HELLO_STR, "world");
        List<String> actualTokens = argumentResolver.resolveOneArgument(args);
        assertEquals(expectedTokens, actualTokens);
    }
}
