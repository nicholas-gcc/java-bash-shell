package sg.edu.nus.comp.cs4218.impl.app;

import sg.edu.nus.comp.cs4218.app.CatInterface;
import sg.edu.nus.comp.cs4218.exception.CatException;
import sg.edu.nus.comp.cs4218.exception.InvalidArgsException;
import sg.edu.nus.comp.cs4218.impl.app.args.CatArguments;
import sg.edu.nus.comp.cs4218.impl.util.IOUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Arrays;

import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.ERR_FILE_DIR_NOT_FOUND;
import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.ERR_FILE_NOT_FOUND;
import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.ERR_NO_FILE_ARGS;
import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.ERR_NO_INPUT;
import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.ERR_NO_OSTREAM;
import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.ERR_NULL_ARGS;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.STRING_NEWLINE;

public class CatApplication implements CatInterface {
    public static final String ERR_IS_DIR = "This is a directory";
    public static final String ERR_READING_FILE = "Could not read file";
    public static final String ERR_WRITE_STREAM = "Could not write to output stream";
    public static final String ERR_NULL_STREAMS = "Null Pointer Exception";
    public static final String ERR_GENERAL = "Exception Caught";
    public static final String ERR_INVALID_FILE = "Invalid file";

    /**
     * Runs the cat application with the specified arguments.
     *
     * @param args   Array of arguments for the application. Each array element is the path to a
     *               file. If no files are specified stdin is used.
     * @param stdin  An InputStream. The input for the command is read from this InputStream if no
     *               files are specified.
     * @param stdout An OutputStream. The output of the command is written to this OutputStream.
     * @throws CatException If the file(s) specified do not exist or are unreadable.
     */
    @Override
    public void run(String[] args, InputStream stdin, OutputStream stdout) throws CatException {
        if (stdout == null) {
            throw new CatException(ERR_NO_OSTREAM);
        }
        if (args == null) {
            throw new CatException(ERR_NULL_ARGS);
        }
        if (Arrays.asList(args).contains(null)) {
            throw new CatException(ERR_NULL_ARGS);
        }
        CatArguments catArgsParser = new CatArguments();
        try {
            catArgsParser.parse(args);
        } catch (InvalidArgsException e) {
            throw new CatException(e.getMessage(), e);
        }

        boolean isLineNo = catArgsParser.hasLineNumbers();
        String[] fileNames = catArgsParser.getFiles();

        if (stdin == null && (fileNames == null || fileNames.length == 0)) {
            throw new CatException(ERR_NO_INPUT);
        }

        String output;
        if (fileNames == null || fileNames.length == 0) {
            output = catStdin(isLineNo, stdin);
        } else if (Arrays.asList(fileNames).contains("-")) {
            output = catFileAndStdin(isLineNo, stdin, fileNames);
        } else {
            output = catFiles(isLineNo, fileNames);
        }

        try {
            stdout.write(output.getBytes());
        } catch (Exception e) {
            throw new CatException(ERR_WRITE_STREAM, e);
        }

    }

    @Override
    public String catFiles(Boolean isLineNumber, String... fileName) throws CatException {
        if (fileName == null || fileName.length == 0) {
            throw new CatException(ERR_NO_FILE_ARGS);
        }
        if (isLineNumber == null) {
            throw new CatException(ERR_NULL_ARGS);
        }
        StringBuilder sb = new StringBuilder();//NOPMD
        for (String name : fileName) {
            if (name == null) {
                throw new CatException(ERR_NULL_ARGS);
            }
            try {
                File file = IOUtils.resolveFilePath(name).toFile();
                if (!file.exists()) {
                    throw new CatException(name + ": " + ERR_FILE_NOT_FOUND);
                }

                if (file.isDirectory()) {
                    throw new CatException(ERR_IS_DIR + ": " + name);
                }

                InputStream inputStream = new FileInputStream(file);
                buildSB(inputStream, sb, isLineNumber);
                inputStream.close();
            } catch (CatException e) {
                throw e;
            }
            catch (Exception e) {
                throw new CatException(e.getMessage(), e);
            }
        }

        return sb.toString();
    }

    @Override
    public String catStdin(Boolean isLineNumber, InputStream stdin) throws CatException {
        if (stdin == null) {
            throw new CatException(ERR_NULL_STREAMS);
        }
        if (isLineNumber == null) {
            throw new CatException(ERR_NULL_ARGS);
        }

        StringBuilder sb = new StringBuilder();//NOPMD
        // handle stdin
        buildSB(stdin, sb, isLineNumber);
        return sb.toString();
    }

    /**
     * Performs cat on both files and stdin (both cannot be null)
     * Files will always be cat first followed by stdin.
     *
     * @param isLineNumber Prefix lines with their corresponding line number starting from 1
     * @param stdin        InputStream containing arguments from Stdin
     * @param fileName     Array of String of file names (including "-" for reading from stdin)
     * @return a string
     */
    @Override
    public String catFileAndStdin(Boolean isLineNumber, InputStream stdin, String... fileName) throws CatException {
        if (fileName == null || fileName.length == 0) {
            throw new CatException(ERR_NO_FILE_ARGS);
        }
        if (isLineNumber == null) {
            throw new CatException(ERR_NULL_ARGS);
        }
        if (stdin == null) {
            throw new CatException(ERR_NULL_STREAMS);
        }

        // Handle files
        StringBuilder sb = new StringBuilder();//NOPMD
        for (String name : fileName) {
            if (name == null) {
                throw new CatException(ERR_NULL_ARGS);
            }
            File file = IOUtils.resolveFilePath(name).toFile();
            if (!file.exists()) {
                throw new CatException(ERR_FILE_DIR_NOT_FOUND + ": " + name + " does not exist.");
            }
            if (file.isDirectory()) {
                throw new CatException(ERR_IS_DIR + ": " + name);
            }
            try (InputStream inputStream = new FileInputStream(file)){
                buildSB(inputStream, sb, isLineNumber);
            } catch (Exception e) {
                throw new CatException(e.getMessage(), e);
            }

        }

        // handle stdin
        buildSB(stdin, sb, isLineNumber);

        return sb.toString();
    }

    private void buildSB(InputStream inputStream, StringBuilder sb, boolean isLineNumber) throws CatException {//NOPMD
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            int lineNo = 1;
            String line;
            while ((line = reader.readLine()) != null) {
                if (isLineNumber) {
                    sb.append(String.format("%d %s", lineNo++, line)).append(STRING_NEWLINE);
                } else {
                    sb.append(line).append(STRING_NEWLINE);
                }
            }
        } catch (Exception e) {
            throw new CatException(e.getMessage(), e);
        }
    }
}
