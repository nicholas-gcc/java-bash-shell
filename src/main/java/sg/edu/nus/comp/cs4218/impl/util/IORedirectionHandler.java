package sg.edu.nus.comp.cs4218.impl.util;

import sg.edu.nus.comp.cs4218.exception.AbstractApplicationException;
import sg.edu.nus.comp.cs4218.exception.ShellException;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.*;
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

    private String outputFilePath;

    private boolean append = false;

    public IORedirectionHandler(List<String> argsList, InputStream origInputStream,
                                OutputStream origOutputStream, ArgumentResolver argumentResolver) {
        this.argsList = argsList;
        this.inputStream = origInputStream;
        this.origInputStream = origInputStream;
        this.outputStream = origOutputStream;
        this.origOutputStream = origOutputStream;
        this.argumentResolver = argumentResolver;
    }

    /**
     * @param argsIterator from extractRedirOptions, iterator of remaining args
     * @param arg current arg, from extractRedirOption
     * @return filename of the file
     * @throws ShellException
     */
    private String getFileName(ListIterator<String> argsIterator, String arg) throws ShellException {
        if (!argsIterator.hasNext()) {
            throw new ShellException(ERR_MISSING_ARG);//no file is supplied
        }
        String nextArg = argsIterator.next();
        if (isRedirOperator(nextArg)) { // to account for >>
            if (!isOutputRedirOperator(nextArg) || !isOutputRedirOperator(arg)) {
                throw new ShellException(ERR_SYNTAX);
            }
            this.append = true;
            if (!argsIterator.hasNext()) { //no file is supplied
                throw new ShellException(ERR_MISSING_ARG);
            }
            nextArg = argsIterator.next();
        }
        String file = nextArg;
        return file;
    }

    /**
     * @throws AbstractApplicationException
     * @throws ShellException
     * @throws FileNotFoundException
     * extract redirction options and substitute input or output stream accordingly
     */
    public void extractRedirOptions() throws AbstractApplicationException, ShellException, FileNotFoundException {
        if (argsList == null || argsList.isEmpty()) {
            throw new ShellException(ERR_SYNTAX);
        }
        noRedirArgsList = new LinkedList<>();
        ListIterator<String> argsIterator = argsList.listIterator();
        boolean redirExpected = false;
        while (argsIterator.hasNext()) {
            String arg = argsIterator.next();
            if (redirExpected && !isRedirOperator(arg)) {
                throw new ShellException((ERR_TOO_MANY_ARGS)); //more than 1 file is supplied after redir char
            }
            if (!isRedirOperator(arg)) {// leave the other args untouched
                noRedirArgsList.add(arg);
                continue;
            }
            // if current arg is < or >
            String file = "";
            try {
                file = getFileName(argsIterator, arg);
                redirExpected = true; //next arg must be > or <
            } catch (ShellException e) {
                throw e;
            }
            List<String> fileSegment = argumentResolver.resolveOneArgument(file);// handle quoting + globing + command substitution in file arg
            if (fileSegment.size() > 1) {// ambiguous redirect if file resolves to more than one parsed arg
                throw new ShellException(ERR_SYNTAX);
            }
            file = fileSegment.get(0);
            if (arg.equals(String.valueOf(CHAR_REDIR_INPUT))) {// replace existing inputStream / outputStream
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
                this.outputFilePath = file;
                outputStream = IOUtils.openOutputStream(file, append);
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

    public boolean isAppend() { return append; }

    private boolean isRedirOperator(String str) {
        return str.equals(String.valueOf(CHAR_REDIR_INPUT)) || str.equals(String.valueOf(CHAR_REDIR_OUTPUT));
    }

    public String getOutputFilePath() {
        return outputFilePath;
    }

    private boolean isOutputRedirOperator(String str) {
        return str.equals(String.valueOf(CHAR_REDIR_OUTPUT));
    }

    private boolean isInputRedirOperator(String str) {
        return str.equals(String.valueOf(CHAR_REDIR_INPUT));
    }
}
