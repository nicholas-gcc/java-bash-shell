package sg.edu.nus.comp.cs4218.impl.app;

import sg.edu.nus.comp.cs4218.app.SortInterface;
import sg.edu.nus.comp.cs4218.exception.SortException;
import sg.edu.nus.comp.cs4218.impl.app.args.SortArguments;
import sg.edu.nus.comp.cs4218.impl.util.IOUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.OutputStream;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.ERR_FILE_DIR_NOT_FOUND;
import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.ERR_IS_DIR;
import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.ERR_NO_PERM;
import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.ERR_NULL_ARGS;
import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.ERR_NULL_STREAMS;
import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.ERR_WRITE_STREAM;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.STRING_DASH;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.STRING_NEWLINE;

@SuppressWarnings({"PMD.CloseResource", "PMD.GodClass"})
public class SortApplication implements SortInterface {

    /**
     * Runs the sort application with the specified arguments.
     *
     * @param args   Array of arguments for the application. Each array element is the path to a
     *               file. If no files are specified stdin is used.
     * @param stdin  An InputStream. The input for the command is read from this InputStream if no
     *               files are specified.
     * @param stdout An OutputStream. The output of the command is written to this OutputStream.
     * @throws SortException
     */
    @Override
    public void run(String[] args, InputStream stdin, OutputStream stdout) throws SortException {
        // Format: sort [-nrf] [FILES]
        if (args == null) {
            throw new SortException(ERR_NULL_ARGS);
        }
        if (stdout == null) {
            throw new SortException(ERR_NULL_STREAMS);
        }
        SortArguments sortArgs = new SortArguments();
        sortArgs.parse(args);
        StringBuilder output = new StringBuilder();
        if (sortArgs.getFiles().isEmpty()) {
            output.append(sortFromStdin(sortArgs.isFirstWordNumber(), sortArgs.isReverseOrder(), sortArgs.isCaseIndependent(), stdin));
        } else if (sortArgs.getFiles().contains(STRING_DASH)) {
            output.append(sortFromStdinAndFiles(sortArgs.isFirstWordNumber(), sortArgs.isReverseOrder(), sortArgs.isCaseIndependent(), sortArgs.getFiles().toArray(new String[0]), stdin));
        } else {
            output.append(sortFromFiles(sortArgs.isFirstWordNumber(), sortArgs.isReverseOrder(), sortArgs.isCaseIndependent(), sortArgs.getFiles().toArray(new String[0])));
        }

        try {
            if (!output.toString().isEmpty()) {
                stdout.write(output.toString().getBytes());
                stdout.write(STRING_NEWLINE.getBytes());
            }
        } catch (IOException e) {
            throw new SortException(ERR_WRITE_STREAM, e);
        }
    }

    /**
     * Returns string containing the orders of the lines of the specified file
     *
     * @param isFirstWordNumber Boolean option to treat the first word of a line as a number
     * @param isReverseOrder    Boolean option to sort in reverse order
     * @param isCaseIndependent Boolean option to perform case-independent sorting
     * @param fileNames         Array of String of file names
     * @throws Exception
     */
    @Override
    public String sortFromFiles(Boolean isFirstWordNumber, Boolean isReverseOrder, Boolean isCaseIndependent,
                                String... fileNames) throws SortException {
        if (fileNames == null) {
            throw new SortException(ERR_NULL_ARGS);
        }
        List<String> lines = new ArrayList<>();
        for (String file : fileNames) {
            File node = IOUtils.resolveFilePath(file).toFile();
            if (!node.exists()) {
                throw new SortException(ERR_FILE_DIR_NOT_FOUND);
            }
            if (node.isDirectory()) {
                throw new SortException(ERR_IS_DIR);
            }
            if (!node.canRead()) {
                throw new SortException(ERR_NO_PERM);
            }
            try {
                InputStream input = IOUtils.openInputStream(file);
                lines.addAll(IOUtils.getLinesFromInputStream(input));
            } catch (Exception e) {
                throw new SortException(e.getMessage(), e);
            }
        }
        sortInputString(isFirstWordNumber, isReverseOrder, isCaseIndependent, lines);
        return String.join(STRING_NEWLINE, lines);
    }

    /**
     * Returns string containing the orders of the lines from the standard input
     *
     * @param isFirstWordNumber Boolean option to treat the first word of a line as a number
     * @param isReverseOrder    Boolean option to sort in reverse order
     * @param isCaseIndependent Boolean option to perform case-independent sorting
     * @param stdin             InputStream containing arguments from Stdin
     * @throws Exception
     */
    @Override
    public String sortFromStdin(Boolean isFirstWordNumber, Boolean isReverseOrder, Boolean isCaseIndependent,
                                InputStream stdin) throws SortException {
        if (stdin == null) {
            throw new SortException(ERR_NULL_STREAMS);
        }

        try {
            List<String> lines = IOUtils.getLinesFromInputStream(stdin);
            sortInputString(isFirstWordNumber, isReverseOrder, isCaseIndependent, lines);
            return String.join(STRING_NEWLINE, lines);
        } catch (Exception e) {
            throw new SortException(e.getMessage(), e);
        }

    }

    /**
     * Returns string containing the orders of the lines from the standard input and files.
     *
     * @param isFirstWordNumber Boolean option to treat the first word of a line as a number
     * @param isReverseOrder    Boolean option to sort in reverse order
     * @param isCaseIndependent Boolean option to perform case-independent sorting
     * @param fileNames         Array of String of file names
     * @param stdin             InputStream containing arguments from Stdin
     * @throws Exception
     */
    public String sortFromStdinAndFiles(Boolean isFirstWordNumber, Boolean isReverseOrder, Boolean isCaseIndependent,
                                        String[] fileNames, InputStream stdin) throws SortException {
        if (stdin == null) {
            throw new SortException(ERR_NULL_STREAMS);
        }

        List<String> lines = new ArrayList<>();
        for (String file : fileNames) {
            if (!file.equals(STRING_DASH)) {
                File node = IOUtils.resolveFilePath(file).toFile();
                if (!node.exists()) {
                    throw new SortException(ERR_FILE_DIR_NOT_FOUND);
                }
                if (node.isDirectory()) {
                    throw new SortException(ERR_IS_DIR);
                }
                if (!node.canRead()) {
                    throw new SortException(ERR_NO_PERM);
                }
                try {
                    InputStream input = IOUtils.openInputStream(file);
                    lines.addAll(IOUtils.getLinesFromInputStream(input));
                } catch (Exception e) {
                    throw new SortException(e.getMessage(), e);
                }
            }
        }

        try {
            // add all elements taken in from stdin
            lines.addAll(IOUtils.getLinesFromInputStream(stdin));

            sortInputString(isFirstWordNumber, isReverseOrder, isCaseIndependent, lines);
            return String.join(STRING_NEWLINE, lines);
        } catch (Exception e) {
            throw new SortException(e.getMessage(), e);
        }
    }


    /**
     * Sorts the input ArrayList based on the given conditions. Invoking this function will mutate the ArrayList.
     *
     * @param isFirstWordNumber Boolean option to treat the first word of a line as a number
     * @param isReverseOrder    Boolean option to sort in reverse order
     * @param isCaseIndependent Boolean option to perform case-independent sorting
     * @param input             ArrayList of Strings of lines
     */
    private void sortInputString(Boolean isFirstWordNumber, Boolean isReverseOrder, Boolean isCaseIndependent,
                                 List<String> input) {
        Collections.sort(input, new Comparator<String>() {
            @Override
            public int compare(String str1, String str2) {
                String temp1 = isCaseIndependent ? str1.toLowerCase(Locale.US) : str1;
                String temp2 = isCaseIndependent ? str2.toLowerCase(Locale.US) : str2;

                // Extract the first group of numbers if possible.
                if (isFirstWordNumber && !temp1.isEmpty() && !temp2.isEmpty()) {
                    String chunk1 = getChunk(temp1);//NOPMD
                    String chunk2 = getChunk(temp2);//NOPMD

                    // If both chunks can be represented as numbers, sort them numerically.
                    int result = 0;
                    if (Character.isDigit(chunk1.charAt(0)) && Character.isDigit(chunk2.charAt(0))) {
                        result = new BigInteger(chunk1).compareTo(new BigInteger(chunk2));
                    } else {
                        result = chunk1.compareTo(chunk2);
                    }
                    if (result != 0) {
                        return result;
                    }
                    return temp1.substring(chunk1.length()).compareTo(temp2.substring(chunk2.length()));
                }

                return temp1.compareTo(temp2);
            }
        });
        if (isReverseOrder) {
            Collections.reverse(input);
        }
    }

    /**
     * Extracts a chunk of numbers or non-numbers from str starting from index 0.
     *
     * @param str Input string to read from
     */
    private String getChunk(String str) {
        int startIndexLocal = 0;
        StringBuilder chunk = new StringBuilder();
        final int strLen = str.length();
        char chr = str.charAt(startIndexLocal++);
        chunk.append(chr);
        final boolean extractDigit = Character.isDigit(chr);
        while (startIndexLocal < strLen) {
            chr = str.charAt(startIndexLocal++);
            if ((extractDigit && !Character.isDigit(chr)) || (!extractDigit && Character.isDigit(chr))) {
                break;
            }
            chunk.append(chr);
        }
        return chunk.toString();
    }

}
