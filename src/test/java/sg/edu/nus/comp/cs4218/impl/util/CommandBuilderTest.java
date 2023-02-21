package sg.edu.nus.comp.cs4218.impl.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import sg.edu.nus.comp.cs4218.Command;
import sg.edu.nus.comp.cs4218.exception.ShellException;
import sg.edu.nus.comp.cs4218.impl.cmd.CallCommand;
import sg.edu.nus.comp.cs4218.impl.cmd.PipeCommand;
import sg.edu.nus.comp.cs4218.impl.cmd.SequenceCommand;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class CommandBuilderTest {
    ApplicationRunner applicationRunner;

    @BeforeEach
    void setUp() {
        this.applicationRunner = new ApplicationRunner();
    }

    @Test
    void parseCommand_SimpleCallArgs_ReturnsCallCommand() {
        String args = "echo hello";
        try {
            Command command = CommandBuilder.parseCommand(args, applicationRunner);
            assertEquals(command.getClass(), CallCommand.class);
        } catch (ShellException ignored) {

        }
    }

    @Test
    void parseCommand_ComplexCallArgs_ReturnsCallCommand() {
        String args = "grep “Interesting String” < text1.txt > result.txt";
        try {
            Command command = CommandBuilder.parseCommand(args, applicationRunner);
            assertEquals(command.getClass(), CallCommand.class);
        } catch (ShellException ignored) {

        }
    }

    @Test
    void parseCommand_ComplexSymbolArgs_ReturnsCallCommand() {
        String args = "echo ‡…™©~Œ{}[]\\^_':,+*&%$#=";
        try {
            Command command = CommandBuilder.parseCommand(args, applicationRunner);
            assertEquals(command.getClass(), CallCommand.class);
        } catch (ShellException ignored) {

        }
    }

    @Test
    void parseCommand_CallArgsWithPipeOperator_ReturnsPipeCommand() {
        String args = "paste articles/text1.txt | grep “Interesting String”";
        try {
            Command command = CommandBuilder.parseCommand(args, applicationRunner);
            assertEquals(command.getClass(), PipeCommand.class);
        } catch (ShellException ignored) {

        }
    }

    @Test
    void parseCommand_CallArgsWithSequenceOperator_ReturnsSequenceCommand() {
        String args = "cd articles; paste text1.txt";
        try {
            Command command = CommandBuilder.parseCommand(args, applicationRunner);
            assertEquals(command.getClass(), SequenceCommand.class);
        } catch (ShellException ignored) {

        }
    }

    @Test
    void parseCommand_EmptyArgs_ThrowsShellException() {
        assertThrows(ShellException.class, () -> {
            CommandBuilder.parseCommand("", applicationRunner);
        });
    }
    @Test
    void parseCommand_BlankArgs_ThrowsShellException() {
        assertThrows(ShellException.class, () -> {
            CommandBuilder.parseCommand("                       ", applicationRunner);
        });
    }

    @Test
    void parseCommand_ArgsContainNewLine_ThrowsShellException() {
        assertThrows(ShellException.class, () -> {
            CommandBuilder.parseCommand("echo hello" + System.lineSeparator(), applicationRunner);
        });
    }

    @Test
    void parseCommand_EmptyPipeArgs_ThrowsShellException() {
        assertThrows(ShellException.class, () -> {
            CommandBuilder.parseCommand("   |grep", applicationRunner);
        });
    }

    @Test
    void parseCommand_EmptySequenceArgs_ThrowsShellException() {
        assertThrows(ShellException.class, () -> {
            CommandBuilder.parseCommand("   ;grep", applicationRunner);
        });
    }

    @Test
    void parseCommand_MismatchQuotesArgs_ThrowsShellException() {
        assertThrows(ShellException.class, () -> {
            CommandBuilder.parseCommand("echo \"hello word'", applicationRunner);
        });
    }

    @Test
    void parseCommand_CallArgsWithRedirections_ReturnsCallCommandWithCorrectArgTokens() {
        String args = "echo hi < text1.txt > result.txt";
        List<String> expectedTokens = Arrays.asList("echo", "hi", "<", "text1.txt", ">", "result.txt"); //NOPMD
        try {
            Command command = CommandBuilder.parseCommand(args, applicationRunner);
            List<String> argTokens = ((CallCommand) command).getArgsList();
            assertEquals(expectedTokens, argTokens);
        } catch (ShellException ignored) {

        }
    }

    @Test
    void parseCommand_CallArgsWithNoQuotes_ReturnsCallCommandWithCorrectArgTokens() {
        String args = "echo hello world";
        List<String> expectedTokens = Arrays.asList("echo", "hello", "world"); //NOPMD
        try {
            Command command = CommandBuilder.parseCommand(args, applicationRunner);
            List<String> argTokens = ((CallCommand) command).getArgsList();
            assertEquals(expectedTokens, argTokens);
        } catch (ShellException ignored) {

        }
    }
    @Test
    void parseCommand_CallArgsWithSimpleSingleQuotes_ReturnsCallCommandWithCorrectArgTokens() {
        String args = "echo 'hello world'";
        List<String> expectedTokens = Arrays.asList("echo", "'hello world'");
        try {
            Command command = CommandBuilder.parseCommand(args, applicationRunner);
            List<String> argTokens = ((CallCommand) command).getArgsList();
            assertEquals(expectedTokens, argTokens);
        } catch (ShellException ignored) {

        }
    }

    @Test
    void parseCommand_CallArgsWithComplexSingleQuotes_ReturnsCallCommandWithCorrectArgTokens() {
        String args = "echo 'Travel time Singapore -> Paris is 13h and 15`'";
        List<String> expectedTokens = Arrays.asList("echo", "'Travel time Singapore -> Paris is 13h and 15`'");
        try {
            Command command = CommandBuilder.parseCommand(args, applicationRunner);
            List<String> argTokens = ((CallCommand) command).getArgsList();
            assertEquals(expectedTokens, argTokens);
        } catch (ShellException ignored) {

        }
    }

    @Test
    void parseCommand_CallArgsWithSpecialCharsInSingleQuotes_ReturnsCallCommandWithCorrectArgTokens() {
        String args = "echo '-> ` | ;'";
        List<String> expectedTokens = Arrays.asList("echo", "'-> ` | ;'");
        try {
            Command command = CommandBuilder.parseCommand(args, applicationRunner);
            List<String> argTokens = ((CallCommand) command).getArgsList();
            assertEquals(expectedTokens, argTokens);
        } catch (ShellException ignored) {

        }
    }

    @Test
    void parseCommand_CallArgsWithSimpleDoubleQuotes_ReturnsCallCommandWithCorrectArgTokens() {
        String args = "echo \"hello world\"";
        List<String> expectedTokens = Arrays.asList("echo", "\"hello world\"");
        try {
            Command command = CommandBuilder.parseCommand(args, applicationRunner);
            List<String> argTokens = ((CallCommand) command).getArgsList();
            assertEquals(expectedTokens, argTokens);
        } catch (ShellException ignored) {

        }
    }

    @Test
    void parseCommand_CallArgsWithComplexDoubleQuotes_ReturnsCallCommandWithCorrectArgTokens() {
        String args = "echo \"'This is space `echo \" \"`'\"";
        List<String> expectedTokens = Arrays.asList("echo", "\"'This is space `echo \" \"`'\"");
        try {
            Command command = CommandBuilder.parseCommand(args, applicationRunner);
            List<String> argTokens = ((CallCommand) command).getArgsList();
            assertEquals(expectedTokens, argTokens);
        } catch (ShellException ignored) {

        }
    }

    @Test
    void parseCommand_CallArgsWithSpecialCharsInDoubleQuotes_ReturnsCallCommandWithCorrectArgTokens() {
        String args = "echo \"-> ` | ;\"";
        List<String> expectedTokens = Arrays.asList("echo", "\"-> ` | ;\"");
        try {
            Command command = CommandBuilder.parseCommand(args, applicationRunner);
            List<String> argTokens = ((CallCommand) command).getArgsList();
            assertEquals(expectedTokens, argTokens);
        } catch (ShellException ignored) {

        }
    }

    @Test
    void parseCommand_CallArgsWithSimpleBackQuotes_ReturnsCallCommandWithCorrectArgTokens() {
        String args = "echo `hello world`";
        List<String> expectedTokens = Arrays.asList("echo", "`hello world`");
        try {
            Command command = CommandBuilder.parseCommand(args, applicationRunner);
            List<String> argTokens = ((CallCommand) command).getArgsList();
            assertEquals(expectedTokens, argTokens);
        } catch (ShellException ignored) {

        }
    }

    @Test
    void parseCommand_CallArgsWithSubstitution_ReturnsCallCommandWithCorrectArgTokens() {
        String args = "echo `echo hello world`";
        List<String> expectedTokens = Arrays.asList("echo", "`echo hello world`");
        try {
            Command command = CommandBuilder.parseCommand(args, applicationRunner);
            List<String> argTokens = ((CallCommand) command).getArgsList();
            assertEquals(expectedTokens, argTokens);
        } catch (ShellException ignored) {

        }
    }

    @Test
    void parseCommand_CallArgsWithSubstitutionAndQuotes_ReturnsCallCommandWithCorrectArgTokens() {
        String args = "echo `echo \"‘quote is not interpreted as special character’\"`";
        List<String> expectedTokens = Arrays.asList("echo", "`echo \"‘quote is not interpreted as special character’\"`");
        try {
            Command command = CommandBuilder.parseCommand(args, applicationRunner);
            List<String> argTokens = ((CallCommand) command).getArgsList();
            assertEquals(expectedTokens, argTokens);
        } catch (ShellException ignored) {

        }
    }

    @Test
    void parseCommand_CallArgsWithSubstitutionAndRedirection_ReturnsCallCommandWithCorrectArgTokens() {
        String args = "paste `ls x*` > all.txt";
        List<String> expectedTokens = Arrays.asList("paste", "`ls x*`", ">", "all.txt");
        try {
            Command command = CommandBuilder.parseCommand(args, applicationRunner);
            List<String> argTokens = ((CallCommand) command).getArgsList();
            assertEquals(expectedTokens, argTokens);
        } catch (ShellException ignored) {

        }
    }
}
