package sg.edu.nus.comp.cs4218.impl.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sg.edu.nus.comp.cs4218.exception.ShellException;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;


public class ArgumentResolverTest {
    ApplicationRunner applicationRunner;
    ArgumentResolver argumentResolver;


    @BeforeEach
    void setUp() {
        this.applicationRunner = new ApplicationRunner();
        this.argumentResolver = new ArgumentResolver();

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
