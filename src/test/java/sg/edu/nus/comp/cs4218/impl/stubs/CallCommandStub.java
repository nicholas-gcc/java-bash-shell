package sg.edu.nus.comp.cs4218.impl.stubs;

import sg.edu.nus.comp.cs4218.exception.AbstractApplicationException;
import sg.edu.nus.comp.cs4218.exception.ShellException;
import sg.edu.nus.comp.cs4218.impl.cmd.CallCommand;
import sg.edu.nus.comp.cs4218.impl.util.ApplicationRunner;
import sg.edu.nus.comp.cs4218.impl.util.ArgumentResolver;
import sg.edu.nus.comp.cs4218.impl.util.IOUtils;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.stream.Collectors;

public class CallCommandStub extends CallCommand {

    public enum CommandType {
        LS_SUCCESS_STUB,
        GREP_SUCCESS_STUB,
        GREP_SUCCESS_SUBSEQUENT_STUB,
        LS_ERROR,
        GREP_ERROR
    }

    private CommandType commandType;

    public CallCommandStub(List<String> argsList, ApplicationRunner applicationRunner, ArgumentResolver argumentResolver, CommandType commandType) {
        super(argsList, applicationRunner, argumentResolver);
        this.commandType = commandType;
    }

    @Override
    public void evaluate(InputStream stdin, OutputStream stdout) throws AbstractApplicationException, ShellException {
        // stub output for ls command
        String lsOutput = "src" + System.lineSeparator() + "assets" + System.lineSeparator() + "target";

        // stub output for grep "src"
        String grepOutput = "src";

        switch(this.commandType) {
            case LS_SUCCESS_STUB:
                try {
                    stdout.write(lsOutput.getBytes());
                } catch (Exception ignored) {

            }
                break;
            case GREP_SUCCESS_STUB:
                try {
                    // to stub the "left side" of the pipe, aka incoming data
                    String dataSource = IOUtils.getLinesFromInputStream(stdin)
                            .stream()
                            .collect(Collectors.joining(System.lineSeparator()));

                    // check incoming data (from GREP stdin) is as expected from LS_SUCCESS_STUB (stdout from LS).
                    // If so, write the expected grep output
                    if (dataSource.equals(lsOutput)) {
                        stdout.write(grepOutput.getBytes());
                    }
                    else throw new ShellException("Data not piped correctly");
                } catch (ShellException e) {
                    throw e;
                } catch (Exception otherErrorsIgnored) {

                }
                break;
            case LS_ERROR:
                throw new ShellException("Something went wrong in ls");
            case GREP_ERROR:
                throw new ShellException("Something went wrong in grep");
            default:
                throw new ShellException("Piping failure");
        }
    }
}
