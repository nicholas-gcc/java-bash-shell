package sg.edu.nus.comp.cs4218.impl.cmd;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sg.edu.nus.comp.cs4218.exception.AbstractApplicationException;
import sg.edu.nus.comp.cs4218.exception.ShellException;
import sg.edu.nus.comp.cs4218.impl.util.ApplicationRunner;
import sg.edu.nus.comp.cs4218.impl.util.ArgumentResolver;
import sg.edu.nus.comp.cs4218.impl.stubs.CallCommandStub;

import java.io.*;

import java.util.LinkedList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class PipeCommandTest {
    private InputStream stdin;
    private OutputStream stdout;
    private ApplicationRunner applicationRunner;
    private ArgumentResolver argumentResolver;
    private LinkedList<CallCommand> callCommands;
    private LinkedList<String> args;

    @BeforeEach
    void setUp() {
        stdin = new ByteArrayInputStream(new byte[0]);
        stdout = new ByteArrayOutputStream();
        applicationRunner = new ApplicationRunner();
        argumentResolver = new ArgumentResolver();
        callCommands = new LinkedList<>();
        args = new LinkedList<>();
    }

    @Test
    public void evaluate_PipeBetweenCommandsSuccess_NoException() throws AbstractApplicationException, ShellException, FileNotFoundException {
        // simulate successful ls | grep "src"
        String expected = "src";

        // set up stub commands
        CallCommandStub lsCommand = new CallCommandStub(args, applicationRunner, argumentResolver, CallCommandStub.CommandType.LS_SUCCESS_STUB);
        CallCommandStub grepCommand = new CallCommandStub(args, applicationRunner, argumentResolver, CallCommandStub.CommandType.GREP_SUCCESS_STUB);
        callCommands.add(lsCommand);
        callCommands.add(grepCommand);

        // set up piped commands
        PipeCommand pipe = new PipeCommand(callCommands);
        pipe.evaluate(stdin, stdout);

        String actual = stdout.toString();
        assertEquals(expected, actual);

    }

    @Test
    public void evaluate_PipeFirstCommandFailed_ThrowShellException() throws AbstractApplicationException, ShellException, FileNotFoundException {
        // simulate failure in ls | grep "..." when ls fails
        String expected = "shell: Something went wrong in ls";

        // set up stub commands
        CallCommandStub lsCommand = new CallCommandStub(args, applicationRunner, argumentResolver, CallCommandStub.CommandType.LS_ERROR);
        CallCommandStub grepCommand = new CallCommandStub(args, applicationRunner, argumentResolver, CallCommandStub.CommandType.GREP_SUCCESS_STUB);
        callCommands.add(lsCommand);
        callCommands.add(grepCommand);

        // set up piped commands
        PipeCommand pipe = new PipeCommand(callCommands);

        Throwable err = assertThrows(ShellException.class, () -> pipe.evaluate(stdin, stdout));
        assertEquals(expected, err.getMessage());

    }

    @Test
    public void evaluate_PipeSecondCommandFailed_ThrowShellException() throws AbstractApplicationException, ShellException, FileNotFoundException {
        // simulate failure in ls | grep "..." when ls fails
        String expected = "shell: Something went wrong in grep";

        // set up stub commands
        CallCommandStub lsCommand = new CallCommandStub(args, applicationRunner, argumentResolver, CallCommandStub.CommandType.LS_SUCCESS_STUB);
        CallCommandStub grepCommand = new CallCommandStub(args, applicationRunner, argumentResolver, CallCommandStub.CommandType.GREP_ERROR);
        callCommands.add(lsCommand);
        callCommands.add(grepCommand);

        // set up piped commands
        PipeCommand pipe = new PipeCommand(callCommands);

        Throwable err = assertThrows(ShellException.class, () -> pipe.evaluate(stdin, stdout));
        assertEquals(expected, err.getMessage());

    }
    @Test
    public void evaluate_PipeBothCommandsFailed_ThrowShellException() throws AbstractApplicationException, ShellException, FileNotFoundException {
        // simulate failure in ls | grep "..." when ls fails
        String expected = "shell: Something went wrong in ls";

        // set up stub commands
        CallCommandStub lsCommand = new CallCommandStub(args, applicationRunner, argumentResolver, CallCommandStub.CommandType.LS_ERROR);
        CallCommandStub grepCommand = new CallCommandStub(args, applicationRunner, argumentResolver, CallCommandStub.CommandType.GREP_ERROR);
        callCommands.add(lsCommand);
        callCommands.add(grepCommand);

        // set up piped commands
        PipeCommand pipe = new PipeCommand(callCommands);

        Throwable err = assertThrows(ShellException.class, () -> pipe.evaluate(stdin, stdout));
        assertEquals(expected, err.getMessage());

    }


}
