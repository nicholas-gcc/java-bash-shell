package sg.edu.nus.comp.cs4218.impl.app;

import sg.edu.nus.comp.cs4218.app.EchoInterface;
import sg.edu.nus.comp.cs4218.exception.EchoException;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.*;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.STRING_NEWLINE;

/**
 * The echo command writes its arguments separated by spaces and terminates by a newline on the
 * standard output.
 *
 * <p>
 * <b>Command format:</b> <code>echo [ARG]...</code>
 * </p>
 */
public class EchoApplication implements EchoInterface {

    @Override
    public String constructResult(String[] args) throws EchoException {
        if (args == null) {
            throw new EchoException(ERR_NULL_ARGS);
        }

        String result;
        if (args.length == 0) {
            result = STRING_NEWLINE;
        } else {
            result = String.join(" ", args);
        }

        return result;
    }

    /**
     * Runs the echo application with the specified arguments.
     *
     * @param args   Array of arguments for the application.
     * @param stdin  An InputStream, not used.
     * @param stdout An OutputStream. Elements of args will be output to stdout, separated by a
     *               space character.
     * @throws EchoException If an I/O exception occurs.
     */
    public void run(String[] args, InputStream stdin, OutputStream stdout) throws EchoException {
        if (stdout == null) {
            throw new EchoException(ERR_NO_OSTREAM);
        }

        String result = constructResult(args);
        try {
            stdout.write(result.getBytes());
        } catch (IOException e) {
            throw new EchoException(ERR_IO_EXCEPTION);
        }
    }
}
