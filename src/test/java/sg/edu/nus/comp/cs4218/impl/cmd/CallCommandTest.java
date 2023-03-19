package sg.edu.nus.comp.cs4218.impl.cmd;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sg.edu.nus.comp.cs4218.exception.AbstractApplicationException;
import sg.edu.nus.comp.cs4218.exception.ShellException;
import sg.edu.nus.comp.cs4218.impl.util.ApplicationRunner;
import sg.edu.nus.comp.cs4218.impl.util.ArgumentResolver;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.STRING_NEWLINE;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CallCommandTest {
    ApplicationRunner applicationRunner;
    ArgumentResolver argumentResolver;
    CallCommand callCommand;
    private InputStream stdin;
    private OutputStream stdout;

    private static final List<String> TEST_ARG_LIST = Arrays.asList("echo", "hello", "world");

    @BeforeEach
    void setup() {
        applicationRunner = new ApplicationRunner();
        argumentResolver = new ArgumentResolver();
        stdin = new ByteArrayInputStream(new byte[0]);
        stdout = new ByteArrayOutputStream();
    }

    @AfterEach
    void reset() throws IOException {
        stdin.close();
        stdout.close();
    }

    @Test
    void evaluate_emptyArgList_ThrowShellException() {
        callCommand = new CallCommand(new ArrayList<>(), applicationRunner, argumentResolver);
        assertThrows(ShellException.class, () -> {
            callCommand.evaluate(stdin, stdout);
        });
    }

    @Test
    void evaluate_nullArgList_ThrowShellException() {
        callCommand = new CallCommand(null, applicationRunner, argumentResolver);
        assertThrows(ShellException.class, () -> {
            callCommand.evaluate(stdin, stdout);
        });
    }

    @Test
    void evaluate_simpleCommandArgList_OutputsCorrectly() throws FileNotFoundException, AbstractApplicationException, ShellException {
        List<String> args = Arrays.asList("echo", "hello world");
        callCommand = new CallCommand(args, applicationRunner, argumentResolver);
        String expectedOutput = "hello world" + STRING_NEWLINE;
        callCommand.evaluate(stdin, stdout);
        assertEquals(expectedOutput, stdout.toString());
    }

    @Test
    void getArgList_emptyArgListCallCommand_ReturnsEmptyArgList() {
        callCommand = new CallCommand(new ArrayList<>(), applicationRunner, argumentResolver);
        List<String> expectedList = new ArrayList<>();
        assertEquals(expectedList, callCommand.getArgsList());
    }

    @Test
    void getArgList_nonEmptyArgListCallCommand_ReturnsCorrectArgList() {
        callCommand = new CallCommand(TEST_ARG_LIST, applicationRunner, argumentResolver);
        // Creating a new List object
        List<String> expectedList = Arrays.asList("echo", "hello", "world");
        assertEquals(expectedList, callCommand.getArgsList());
    }

}
