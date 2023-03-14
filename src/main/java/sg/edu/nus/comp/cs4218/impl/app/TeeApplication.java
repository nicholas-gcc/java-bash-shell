package sg.edu.nus.comp.cs4218.impl.app;

import sg.edu.nus.comp.cs4218.app.TeeInterface;
import sg.edu.nus.comp.cs4218.exception.AbstractApplicationException;
import sg.edu.nus.comp.cs4218.exception.InvalidArgsException;
import sg.edu.nus.comp.cs4218.exception.TeeException;
import sg.edu.nus.comp.cs4218.impl.parser.TeeArgsParser;
import sg.edu.nus.comp.cs4218.impl.util.FileSystemUtils;
import sg.edu.nus.comp.cs4218.impl.util.IOUtils;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.ERR_NO_ISTREAM;
import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.ERR_NO_OSTREAM;
import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.ERR_NULL_ARGS;
import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.ERR_WRITE_STREAM;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.STRING_NEWLINE;

public class TeeApplication implements TeeInterface {
    public String teeFromStdin(Boolean isAppend, InputStream stdin, String... fileName) throws TeeException {
        // TODO: Implement method
        if (stdin == null) {
            throw new TeeException(ERR_NO_ISTREAM);
        }

        if (fileName == null) {
            throw new TeeException(ERR_NULL_ARGS);
        }

        List<String> teeContent = getTeeLinesFromInputStream(stdin);

        String teeContentString = String.join(STRING_NEWLINE ,teeContent) + STRING_NEWLINE;

        for (String file : fileName) {
            teeToFile(isAppend, teeContentString, file);
        }

        return teeContentString;


    }

    public void run(String[] args, InputStream stdin, OutputStream stdout)
            throws AbstractApplicationException {
        if (args == null) {
            throw new TeeException(ERR_NULL_ARGS);
        }

        if (stdout == null) {
            throw new TeeException(ERR_NO_OSTREAM);
        }

        TeeArgsParser parser = new TeeArgsParser();
        try {
            parser.parse(args);
        } catch (InvalidArgsException e) {
            throw new TeeException(e.getMessage());//NOPMD
        }

        boolean isAppend = parser.isAppend();
        String[] filenames = parser.getFilesNames().toArray(new String[0]);

        String output = teeFromStdin(isAppend, stdin, filenames);

        try {
            stdout.write(output.getBytes());
        } catch (Exception e) {
            throw new TeeException(ERR_WRITE_STREAM);//NOPMD
        }
    }

    private List<String> getTeeLinesFromInputStream(InputStream stdin) throws TeeException {
        try {
            List<String> results = IOUtils.getLinesFromInputStream(stdin);
            return results;
        } catch (Exception e) {
            throw new TeeException(e.getMessage());
        }
    }

    private void teeToFile(boolean isAppend, String content, String filename) throws TeeException {
        try {
            FileSystemUtils.writeStrToFile(isAppend, content, filename);
        } catch (Exception e) {
            throw new TeeException(e.getMessage());
        }
    }



}
