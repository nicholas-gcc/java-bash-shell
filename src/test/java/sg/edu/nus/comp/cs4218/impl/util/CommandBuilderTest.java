package sg.edu.nus.comp.cs4218.impl.util;

import org.junit.jupiter.api.BeforeEach;
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
    static final String ECHO_CMD = "echo";
    @BeforeEach
    void setUp() {
        this.applicationRunner = new ApplicationRunner();
    }

    @Test
    void parseCommand_SimpleCallArgs_ReturnsCallCommand() throws ShellException {
        String args = ECHO_CMD + " hello";
        Command command = CommandBuilder.parseCommand(args, applicationRunner);
        assertEquals(command.getClass(), CallCommand.class);
    }

    @Test
    void parseCommand_ComplexCallArgs_ReturnsCallCommand() throws ShellException {
        String args = "grep “Interesting String” < text1.txt > result.txt";
        Command command = CommandBuilder.parseCommand(args, applicationRunner);
        assertEquals(command.getClass(), CallCommand.class);
    }

    @Test
    void parseCommand_ComplexSymbolArgs_ReturnsCallCommand() throws ShellException {
        String args = ECHO_CMD + " ‡…™©~Œ{}[]\\^_:,+*&%$#=";
        Command command = CommandBuilder.parseCommand(args, applicationRunner);
        assertEquals(command.getClass(), CallCommand.class);
    }

    @Test
    void parseCommand_CallArgsWithPipeOperator_ReturnsPipeCommand() throws ShellException {
        String args = "paste articles/text1.txt | grep “Interesting String”";
        Command command = CommandBuilder.parseCommand(args, applicationRunner);
        assertEquals(command.getClass(), PipeCommand.class);
    }

    @Test
    void parseCommand_CallArgsWithSequenceOperator_ReturnsSequenceCommand() throws ShellException {
        String args = "cd articles; paste text1.txt";
        Command command = CommandBuilder.parseCommand(args, applicationRunner);
        assertEquals(command.getClass(), SequenceCommand.class);
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
            CommandBuilder.parseCommand(ECHO_CMD + " hello" + System.lineSeparator(), applicationRunner);
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
            CommandBuilder.parseCommand(ECHO_CMD + " \"hello word'", applicationRunner);
        });
    }

    @Test
    void parseCommand_CallArgsWithRedirections_ReturnsCallCommandWithCorrectArgTokens() throws ShellException {
        String args = ECHO_CMD + " hi < text1.txt > result.txt";
        List<String> expectedTokens = Arrays.asList(ECHO_CMD, "hi", "<", "text1.txt", ">", "result.txt");
        Command command = CommandBuilder.parseCommand(args, applicationRunner);
        List<String> argTokens = ((CallCommand) command).getArgsList();
        assertEquals(expectedTokens, argTokens);
    }

    @Test
    void parseCommand_CallArgsWithNoQuotes_ReturnsCallCommandWithCorrectArgTokens() throws ShellException {
        String args = ECHO_CMD + " hello world";
        List<String> expectedTokens = Arrays.asList(ECHO_CMD, "hello", "world");
        Command command = CommandBuilder.parseCommand(args, applicationRunner);
        List<String> argTokens = ((CallCommand) command).getArgsList();
        assertEquals(expectedTokens, argTokens);
    }
    @Test
    void parseCommand_CallArgsWithSimpleSingleQuotes_ReturnsCallCommandWithCorrectArgTokens() throws ShellException {
        String args = ECHO_CMD + " 'hello world'";
        List<String> expectedTokens = Arrays.asList(ECHO_CMD, "'hello world'");
        Command command = CommandBuilder.parseCommand(args, applicationRunner);
        List<String> argTokens = ((CallCommand) command).getArgsList();
        assertEquals(expectedTokens, argTokens);
    }

    @Test
    void parseCommand_CallArgsWithComplexSingleQuotes_ReturnsCallCommandWithCorrectArgTokens() throws ShellException {
        String args = ECHO_CMD + " 'Travel time Singapore -> Paris is 13h and 15`'";
        List<String> expectedTokens = Arrays.asList(ECHO_CMD, "'Travel time Singapore -> Paris is 13h and 15`'");
        Command command = CommandBuilder.parseCommand(args, applicationRunner);
        List<String> argTokens = ((CallCommand) command).getArgsList();
        assertEquals(expectedTokens, argTokens);
    }

    @Test
    void parseCommand_CallArgsWithSpecialCharsInSingleQuotes_ReturnsCallCommandWithCorrectArgTokens() throws ShellException {
        String args = ECHO_CMD + " '-> ` | ;'";
        List<String> expectedTokens = Arrays.asList(ECHO_CMD, "'-> ` | ;'");
        Command command = CommandBuilder.parseCommand(args, applicationRunner);
        List<String> argTokens = ((CallCommand) command).getArgsList();
        assertEquals(expectedTokens, argTokens);
    }

    @Test
    void parseCommand_CallArgsWithSimpleDoubleQuotes_ReturnsCallCommandWithCorrectArgTokens() throws ShellException {
        String args = ECHO_CMD + " \"hello world\"";
        List<String> expectedTokens = Arrays.asList(ECHO_CMD, "\"hello world\"");
        Command command = CommandBuilder.parseCommand(args, applicationRunner);
        List<String> argTokens = ((CallCommand) command).getArgsList();
        assertEquals(expectedTokens, argTokens);
    }

    @Test
    void parseCommand_CallArgsWithComplexDoubleQuotes_ReturnsCallCommandWithCorrectArgTokens() throws ShellException {
        String args = ECHO_CMD + " \"'This is space `echo \" \"`'\"";
        List<String> expectedTokens = Arrays.asList(ECHO_CMD, "\"'This is space `echo \" \"`'\"");
        Command command = CommandBuilder.parseCommand(args, applicationRunner);
        List<String> argTokens = ((CallCommand) command).getArgsList();
        assertEquals(expectedTokens, argTokens);
    }

    @Test
    void parseCommand_CallArgsWithSpecialCharsInDoubleQuotes_ReturnsCallCommandWithCorrectArgTokens() throws ShellException {
        String args = ECHO_CMD + " \"-> ` | ;\"";
        List<String> expectedTokens = Arrays.asList(ECHO_CMD, "\"-> ` | ;\"");
        Command command = CommandBuilder.parseCommand(args, applicationRunner);
        List<String> argTokens = ((CallCommand) command).getArgsList();
        assertEquals(expectedTokens, argTokens);
    }

    @Test
    void parseCommand_CallArgsWithSimpleBackQuotes_ReturnsCallCommandWithCorrectArgTokens() throws ShellException {
        String args = ECHO_CMD + " `hello world`";
        List<String> expectedTokens = Arrays.asList(ECHO_CMD, "`hello world`");
        Command command = CommandBuilder.parseCommand(args, applicationRunner);
        List<String> argTokens = ((CallCommand) command).getArgsList();
        assertEquals(expectedTokens, argTokens);
    }

    @Test
    void parseCommand_CallArgsWithSubstitution_ReturnsCallCommandWithCorrectArgTokens() throws ShellException {
        String args = ECHO_CMD + " `echo hello world`";
        List<String> expectedTokens = Arrays.asList(ECHO_CMD, "`echo hello world`");
        Command command = CommandBuilder.parseCommand(args, applicationRunner);
        List<String> argTokens = ((CallCommand) command).getArgsList();
        assertEquals(expectedTokens, argTokens);
    }

    @Test
    void parseCommand_CallArgsWithSubstitutionAndQuotes_ReturnsCallCommandWithCorrectArgTokens() throws ShellException {
        String args = ECHO_CMD + " `echo \"‘quote is not interpreted as special character’\"`";
        List<String> expectedTokens = Arrays.asList(ECHO_CMD, "`echo \"‘quote is not interpreted as special character’\"`");
            Command command = CommandBuilder.parseCommand(args, applicationRunner);
            List<String> argTokens = ((CallCommand) command).getArgsList();
            assertEquals(expectedTokens, argTokens);
    }

    @Test
    void parseCommand_CallArgsWithSubstitutionAndRedirection_ReturnsCallCommandWithCorrectArgTokens() throws ShellException {
        String args = "paste `ls x*` > all.txt";
        List<String> expectedTokens = Arrays.asList("paste", "`ls x*`", ">", "all.txt");
        Command command = CommandBuilder.parseCommand(args, applicationRunner);
        List<String> argTokens = ((CallCommand) command).getArgsList();
        assertEquals(expectedTokens, argTokens);
    }

}
