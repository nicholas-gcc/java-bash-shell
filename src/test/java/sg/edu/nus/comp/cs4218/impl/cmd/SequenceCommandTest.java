package sg.edu.nus.comp.cs4218.impl.cmd;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sg.edu.nus.comp.cs4218.Command;
import sg.edu.nus.comp.cs4218.Environment;
import sg.edu.nus.comp.cs4218.exception.AbstractApplicationException;
import sg.edu.nus.comp.cs4218.exception.ShellException;
import sg.edu.nus.comp.cs4218.impl.stubs.CallCommandStub;
import sg.edu.nus.comp.cs4218.impl.util.ApplicationRunner;
import sg.edu.nus.comp.cs4218.impl.util.ArgumentResolver;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SequenceCommandTest {
    private InputStream stdin;
    private OutputStream stdout;
    private ApplicationRunner applicationRunner;
    private ArgumentResolver argumentResolver;
    private List<Command> callCommands;
    private LinkedList<String> args;

    @BeforeEach
    void setUp() {
        stdin = new ByteArrayInputStream(new byte[0]);
        stdout = new ByteArrayOutputStream();
        applicationRunner = new ApplicationRunner();
        argumentResolver = new ArgumentResolver();
        callCommands = new LinkedList<Command>();
        args = new LinkedList<>();
    }

    @AfterEach
    void resetDir() {
        Environment.currentDirectory = System.getProperty("user.dir");
    }

    @Test
    public void evaluate_CdAndEchoCommandSequence_NoException() throws FileNotFoundException, AbstractApplicationException, ShellException {
        // set up stub commands

        // cdCommand stub will change directory to src
        CallCommandStub cdCommand = new CallCommandStub(args, applicationRunner, argumentResolver, CallCommandStub.CommandType.CD_SUCCESS_STUB);
        // echoCommand stub will return string "test"
        CallCommandStub echoCommand = new CallCommandStub(args, applicationRunner, argumentResolver, CallCommandStub.CommandType.ECHO_SUCCESS_STUB);
        callCommands.add(cdCommand);
        callCommands.add(echoCommand);

        // set up sequence command
        SequenceCommand sequence = new SequenceCommand(callCommands);
        sequence.evaluate(stdin, stdout);

        assertEquals(System.getProperty("user.dir") + File.separator + "src", Environment.currentDirectory);

        String echoActual = stdout.toString();
        assertEquals("test", echoActual);
    }

    @Test
    public void evaluate_SequenceFirstCommandFailed_ThrowsException() throws FileNotFoundException, AbstractApplicationException, ShellException {
        // 2nd command should still evaluate
        // set up stub commands
        CallCommandStub cdCommand = new CallCommandStub(args, applicationRunner, argumentResolver, CallCommandStub.CommandType.CD_ERROR);
        CallCommandStub echoCommand = new CallCommandStub(args, applicationRunner, argumentResolver, CallCommandStub.CommandType.ECHO_SUCCESS_STUB);
        callCommands.add(cdCommand);
        callCommands.add(echoCommand);

        // set up sequence command
        SequenceCommand sequence = new SequenceCommand(callCommands);
        sequence.evaluate(stdin, stdout);
        String expected = "shell: Something went wrong in cd" + System.lineSeparator() + "test";
        assertEquals(expected, stdout.toString());
    }
}