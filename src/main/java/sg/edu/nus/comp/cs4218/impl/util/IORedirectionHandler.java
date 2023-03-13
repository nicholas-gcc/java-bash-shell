package sg.edu.nus.comp.cs4218.impl.util;

import sg.edu.nus.comp.cs4218.exception.AbstractApplicationException;
import sg.edu.nus.comp.cs4218.exception.ShellException;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.ERR_MULTIPLE_STREAMS;
import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.ERR_SYNTAX;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.CHAR_REDIR_INPUT;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.CHAR_REDIR_OUTPUT;

public class IORedirectionHandler {
    private final List<String> argsList;
    private final ArgumentResolver argumentResolver;
    private final InputStream origInputStream;
    private final OutputStream origOutputStream;
    private List<String> noRedirArgsList;
    private InputStream inputStream;
    private OutputStream outputStream;

    private boolean isAppend = false;

    public IORedirectionHandler(List<String> argsList, InputStream origInputStream,
                                OutputStream origOutputStream, ArgumentResolver argumentResolver) {
        this.argsList = argsList;
        this.inputStream = origInputStream;
        this.origInputStream = origInputStream;
        this.outputStream = origOutputStream;
        this.origOutputStream = origOutputStream;
        this.argumentResolver = argumentResolver;
    }

    public void extractRedirOptions() throws AbstractApplicationException, ShellException, FileNotFoundException {
        // TODO: Fix this?
        if (argsList == null || argsList.isEmpty()) {
            throw new ShellException(ERR_SYNTAX);
        }

        noRedirArgsList = new LinkedList<>();

        // extract redirection operators (with their corresponding files) from argsList
        ListIterator<String> argsIterator = argsList.listIterator();
        while (argsIterator.hasNext()) {
            String arg = argsIterator.next();

            // leave the other args untouched
            if (!isRedirOperator(arg)) {
                noRedirArgsList.add(arg);
                continue;
            }

            String file = "";
            // if current arg is < or >, fast-forward to the next arg to extract the specified file
            String nextArg = argsIterator.next();
            if (isOutputRedirOperator(nextArg)) {
                if (!isOutputRedirOperator(arg)) { // if "<>" is supplied
                    throw new ShellException(ERR_SYNTAX);
                }
                //if >> is supplied
                this.isAppend = true;
                file = argsIterator.next();
            } else {
                file = nextArg;
            }

            // handle quoting + globing + command substitution in file arg
            List<String> fileSegment = argumentResolver.resolveOneArgument(file);
            if (fileSegment.size() > 1) {
                // ambiguous redirect if file resolves to more than one parsed arg
                throw new ShellException(ERR_SYNTAX);
            }
            file = fileSegment.get(0);

            // replace existing inputStream / outputStream
            if (arg.equals(String.valueOf(CHAR_REDIR_INPUT))) {
                IOUtils.closeInputStream(inputStream);
                if (!inputStream.equals(origInputStream)) { // Already have a stream
                    throw new ShellException(ERR_MULTIPLE_STREAMS);
                }
                inputStream = IOUtils.openInputStream(file);
            } else if (arg.equals(String.valueOf(CHAR_REDIR_OUTPUT))) {
                IOUtils.closeOutputStream(outputStream);
                if (!outputStream.equals(origOutputStream)) { // Already have a stream
                    throw new ShellException(ERR_MULTIPLE_STREAMS);
                }
                outputStream = IOUtils.openOutputStream(file, isAppend);
            }
        }
    }

    public List<String> getNoRedirArgsList() {
        return noRedirArgsList;
    }

    public InputStream getInputStream() {
        return inputStream;
    }

    public OutputStream getOutputStream() {
        return outputStream;
    }

    private boolean isRedirOperator(String str) {
        return str.equals(String.valueOf(CHAR_REDIR_INPUT)) || str.equals(String.valueOf(CHAR_REDIR_OUTPUT));
    }

    private boolean isOutputRedirOperator(String str) {
        return str.equals(String.valueOf(CHAR_REDIR_OUTPUT));
    }
}
