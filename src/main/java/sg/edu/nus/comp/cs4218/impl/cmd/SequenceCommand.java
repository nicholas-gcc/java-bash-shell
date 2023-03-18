package sg.edu.nus.comp.cs4218.impl.cmd;

import sg.edu.nus.comp.cs4218.Command;
import sg.edu.nus.comp.cs4218.exception.AbstractApplicationException;
import sg.edu.nus.comp.cs4218.exception.ExitException;
import sg.edu.nus.comp.cs4218.exception.ShellException;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.LinkedList;
import java.util.List;

import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.STRING_NEWLINE;

/**
 * A Sequence Command is a sub-command consisting of two Commands separated with a semicolon.
 * <p>
 * Command format: <Command> ; <Command>
 */
public class SequenceCommand implements Command {
    private final List<Command> commands;

    public SequenceCommand(List<Command> commands) {
        this.commands = commands;
    }

    @Override
    public void evaluate(InputStream stdin, OutputStream stdout)
            throws AbstractApplicationException, ShellException, FileNotFoundException {
        ExitException exitException = null;
        List<String> outputLines = new LinkedList<>();

        for (Command command : commands) {
            try {
                OutputStream outputStream = new ByteArrayOutputStream();
                command.evaluate(stdin, outputStream);

                String outputLine = outputStream.toString();
                if (!outputLine.isEmpty()) {
                    outputLines.add(outputLine);
                }
            } catch (ExitException e) {
                exitException = e;

            } catch (AbstractApplicationException | ShellException e) {
                outputLines.add(e.getMessage() + STRING_NEWLINE);
            }
        }

        for (String outputLine : outputLines) {
            try {
                stdout.write(outputLine.getBytes());
            } catch (IOException e) {
                throw new ShellException(e.getMessage(), e);
            }
        }

        if (exitException != null) {
            throw exitException;
        }
    }

    @Override
    public void terminate() {
        // Unused for now
    }

    public List<Command> getCommands() {
        return commands;
    }
}
