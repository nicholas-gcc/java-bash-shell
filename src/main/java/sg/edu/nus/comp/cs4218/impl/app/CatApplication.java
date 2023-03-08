package sg.edu.nus.comp.cs4218.impl.app;

import sg.edu.nus.comp.cs4218.app.CatInterface;
import sg.edu.nus.comp.cs4218.exception.CatException;
import sg.edu.nus.comp.cs4218.impl.util.IOUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.ERR_FILE_NOT_FOUND;
import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.ERR_NO_FILE_ARGS;
import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.ERR_NULL_ARGS;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.STRING_NEWLINE;

public class CatApplication implements CatInterface {
    public static final String ERR_IS_DIR = "This is a directory";
    public static final String ERR_READING_FILE = "Could not read file";
    public static final String ERR_WRITE_STREAM = "Could not write to output stream";
    public static final String ERR_NULL_STREAMS = "Null Pointer Exception";
    public static final String ERR_GENERAL = "Exception Caught";
    public static final String ERR_NO_FILE = "No file specified";

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
        // TODO: To implement
    }

    @Override
    public String catFiles(Boolean isLineNumber, String... fileName) throws Exception {
        if (fileName == null || fileName.length == 0) {
            throw new CatException(ERR_NO_FILE_ARGS);
        }
        if (isLineNumber == null) {
            throw new CatException(ERR_NULL_ARGS);
        }
        StringBuilder sb = new StringBuilder();
        for (String name : fileName) {
            if (name == null) {
                throw new CatException(ERR_NULL_ARGS);
            }
            File file = IOUtils.resolveFilePath(name).toFile();
            if (!file.exists()) {
                throw new CatException(ERR_FILE_NOT_FOUND + ": " + name + " does not exist.");
            }

            if (file.isDirectory()) {
                throw new CatException(ERR_IS_DIR + ": " + name);
            }


            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(new FileInputStream(file)))) {
                // TODO: refactor possible DRY violation
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
                throw new CatException(e.getMessage());
            }
        }

        return sb.toString();
    }

    @Override
    public String catStdin(Boolean isLineNumber, InputStream stdin) throws Exception {
        if (stdin == null) {
            throw new CatException(ERR_NULL_STREAMS);
        }
        if (isLineNumber == null) {
            throw new CatException(ERR_NULL_ARGS);
        }

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(stdin))) {
            StringBuilder sb = new StringBuilder();
            int lineNo = 1;
            String line;
            while ((line = reader.readLine()) != null) {
                if (isLineNumber) {
                    sb.append(String.format("%d %s", lineNo++, line)).append(STRING_NEWLINE);
                } else {
                    sb.append(line).append(STRING_NEWLINE);
                }
            }
            return sb.toString();
        } catch (Exception e) {
            throw new CatException(e.getMessage());
        }
    }

    @Override
    public String catFileAndStdin(Boolean isLineNumber, InputStream stdin, String... fileName) throws Exception {
        return null;
    }
}
