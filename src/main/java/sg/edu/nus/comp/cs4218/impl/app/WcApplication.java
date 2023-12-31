package sg.edu.nus.comp.cs4218.impl.app;

import sg.edu.nus.comp.cs4218.app.WcInterface;
import sg.edu.nus.comp.cs4218.exception.WcException;
import sg.edu.nus.comp.cs4218.impl.app.args.WcArguments;
import sg.edu.nus.comp.cs4218.impl.util.IOUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import static sg.edu.nus.comp.cs4218.impl.app.CatApplication.ERR_GENERAL;
import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.ERR_IS_DIR;
import static sg.edu.nus.comp.cs4218.impl.app.CatApplication.ERR_NULL_STREAMS;
import static sg.edu.nus.comp.cs4218.impl.app.CatApplication.ERR_WRITE_STREAM;
import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.ERR_FILE_DIR_NOT_FOUND;
import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.ERR_NO_PERM;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.STRING_NEWLINE;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.STRING_DASH;

@SuppressWarnings({"PMD.CloseResource", "PMD.GodClass", "PMD.ExcessiveMethodLength"})
public class WcApplication implements WcInterface {

    private static final String NUMBER_FORMAT = " %7d";
    private static final int LINES_INDEX = 0;
    private static final int WORDS_INDEX = 1;
    private static final int BYTES_INDEX = 2;
    private static final String WC_EX_PREFIX = "wc: ";

    /**
     * Runs the wc application with the specified arguments.
     *
     * @param args   Array of arguments for the application. Each array element is the path to a
     *               file. If no files are specified stdin is used.
     * @param stdin  An InputStream. The input for the command is read from this InputStream if no
     *               files are specified.
     * @param stdout An OutputStream. The output of the command is written to this OutputStream.
     * @throws WcException Throws WcException
     */
    @Override
    public void run(String[] args, InputStream stdin, OutputStream stdout)
            throws WcException {
        // Format: wc [-clw] [FILES]
        if (stdout == null) {
            throw new WcException(ERR_NULL_STREAMS);
        }
        WcArguments wcArgs = new WcArguments();
        wcArgs.parse(args);
        String result;
        try {
            if (wcArgs.getFiles().isEmpty()) {
                result = countFromStdin(wcArgs.isBytes(), wcArgs.isLines(), wcArgs.isWords(), stdin);
            } else if (wcArgs.getFiles().contains(STRING_DASH)) {
                result = countFromFileAndStdin(wcArgs.isBytes(), wcArgs.isLines(), wcArgs.isWords(), stdin, wcArgs.getFiles().toArray(new String[0]));
            }
            else {
                result = countFromFiles(wcArgs.isBytes(), wcArgs.isLines(), wcArgs.isWords(), wcArgs.getFiles().toArray(new String[0]));
            }
        } catch (Exception e) {
            // Will never happen, actually happens though
            throw new WcException(e.getMessage(), e);
        }
        try {
            stdout.write(result.getBytes());
            stdout.write(STRING_NEWLINE.getBytes());
        } catch (IOException e) {
            throw new WcException(ERR_WRITE_STREAM, e);
        }
    }

    /**
     * Returns string containing the number of lines, words, and bytes in input files
     *
     * @param isBytes  Boolean option to count the number of Bytes
     * @param isLines  Boolean option to count the number of lines
     * @param isWords  Boolean option to count the number of words
     * @param fileName Array of String of file names
     * @throws Exception Throws Exception
     */
    @Override
    public String countFromFiles(Boolean isBytes, Boolean isLines, Boolean isWords,
                                 String... fileName) throws Exception {
        if (fileName == null) {
            throw new Exception(ERR_GENERAL);
        }
        List<String> result = new ArrayList<>();
        long totalBytes = 0, totalLines = 0, totalWords = 0;
        for (String file : fileName) {
            File node = IOUtils.resolveFilePath(file).toFile();
            if (!node.exists()) {
                result.add(WC_EX_PREFIX + ERR_FILE_DIR_NOT_FOUND);
                continue;
            }
            if (node.isDirectory()) {
                result.add(WC_EX_PREFIX + ERR_IS_DIR);
                continue;
            }
            if (!node.canRead()) {
                result.add(WC_EX_PREFIX + ERR_NO_PERM);
                continue;
            }

            InputStream input = IOUtils.openInputStream(file);
            long[] count = getCountReport(input); // lines words bytes
            IOUtils.closeInputStream(input);

            // Update total count
            totalLines += count[0];
            totalWords += count[1];
            totalBytes += count[2];

            // Format all output: " %7d %7d %7d %s"
            // Output in the following order: lines words bytes filename
            StringBuilder stringBuilder = new StringBuilder();
            if (isLines) {
                stringBuilder.append(String.format(NUMBER_FORMAT, count[0]));
            }
            if (isWords) {
                stringBuilder.append(String.format(NUMBER_FORMAT, count[1]));
            }
            if (isBytes) {
                stringBuilder.append(String.format(NUMBER_FORMAT, count[2]));
            }
            stringBuilder.append(String.format(" %s", file));
            result.add(stringBuilder.toString());
        }

        // Print cumulative counts for all the files
        if (fileName.length > 1) {
            StringBuilder stringBuilder = new StringBuilder();
            if (isLines) {
                stringBuilder.append(String.format(NUMBER_FORMAT, totalLines));
            }
            if (isWords) {
                stringBuilder.append(String.format(NUMBER_FORMAT, totalWords));
            }
            if (isBytes) {
                stringBuilder.append(String.format(NUMBER_FORMAT, totalBytes));
            }
            stringBuilder.append(" total");
            result.add(stringBuilder.toString());
        }
        return String.join(STRING_NEWLINE, result);
    }

    /**
     * Returns string containing the number of lines, words, and bytes in standard input
     *
     * @param isBytes Boolean option to count the number of Bytes
     * @param isLines Boolean option to count the number of lines
     * @param isWords Boolean option to count the number of words
     * @param stdin   InputStream containing arguments from Stdin
     * @throws Exception Throws Exception
     */
    @Override
    public String countFromStdin(Boolean isBytes, Boolean isLines, Boolean isWords,
                                 InputStream stdin) throws Exception {
        if (stdin == null) {
            throw new Exception(ERR_NULL_STREAMS);
        }
        long[] count = getCountReport(stdin); // lines words bytes;

        StringBuilder stringBuilder = new StringBuilder();
        if (isLines) {
            stringBuilder.append(String.format(NUMBER_FORMAT, count[0]));
        }
        if (isWords) {
            stringBuilder.append(String.format(NUMBER_FORMAT, count[1]));
        }
        if (isBytes) {
            stringBuilder.append(String.format(NUMBER_FORMAT, count[2]));
        }

        return stringBuilder.toString();
    }

    @Override
    public String countFromFileAndStdin(Boolean isBytes, Boolean isLines, Boolean isWords,
                                        InputStream stdin, String... fileName) throws Exception {
        if (fileName == null) {
            throw new Exception(ERR_GENERAL);
        }
        if (stdin == null) {
            throw new WcException(ERR_NULL_STREAMS);
        }
        List<String> result = new ArrayList<>();
        long totalBytes = 0, totalLines = 0, totalWords = 0;
        for (String file : fileName) {
            if (file.equals(STRING_DASH)) {
                long[] count = getCountReport(stdin); // lines words bytes;

                // Update total count
                totalLines += count[0];
                totalWords += count[1];
                totalBytes += count[2];

                StringBuilder stringBuilder = new StringBuilder();
                if (isLines) {
                    stringBuilder.append(String.format(NUMBER_FORMAT, count[0]));
                }
                if (isWords) {
                    stringBuilder.append(String.format(NUMBER_FORMAT, count[1]));
                }
                if (isBytes) {
                    stringBuilder.append(String.format(NUMBER_FORMAT, count[2]));
                }
                stringBuilder.append(String.format(" %s", file));
                result.add(stringBuilder.toString());
            } else {
                File node = IOUtils.resolveFilePath(file).toFile();
                if (!node.exists()) {
                    result.add(WC_EX_PREFIX + ERR_FILE_DIR_NOT_FOUND);
                    continue;
                }
                if (node.isDirectory()) {
                    result.add(WC_EX_PREFIX + ERR_IS_DIR);
                    continue;
                }
                if (!node.canRead()) {
                    result.add(WC_EX_PREFIX + ERR_NO_PERM);
                    continue;
                }

                InputStream input = IOUtils.openInputStream(file);
                long[] count = getCountReport(input); // lines words bytes
                IOUtils.closeInputStream(input);

                // Update total count
                totalLines += count[0];
                totalWords += count[1];
                totalBytes += count[2];

                // Format all output: " %7d %7d %7d %s"
                // Output in the following order: lines words bytes filename
                StringBuilder stringBuilder = new StringBuilder();
                if (isLines) {
                    stringBuilder.append(String.format(NUMBER_FORMAT, count[0]));
                }
                if (isWords) {
                    stringBuilder.append(String.format(NUMBER_FORMAT, count[1]));
                }
                if (isBytes) {
                    stringBuilder.append(String.format(NUMBER_FORMAT, count[2]));
                }
                stringBuilder.append(String.format(" %s", file));
                result.add(stringBuilder.toString());
            }
        }

        // Print cumulative counts for all the files
        if (fileName.length > 1) {
            StringBuilder stringBuilder = new StringBuilder();
            if (isLines) {
                stringBuilder.append(String.format(NUMBER_FORMAT, totalLines));
            }
            if (isWords) {
                stringBuilder.append(String.format(NUMBER_FORMAT, totalWords));
            }
            if (isBytes) {
                stringBuilder.append(String.format(NUMBER_FORMAT, totalBytes));
            }
            stringBuilder.append(" total");
            result.add(stringBuilder.toString());
        }
        return String.join(STRING_NEWLINE, result);
    }

    /**
     * Returns array containing the number of lines, words, and bytes based on data in InputStream.
     *
     * @param input An InputStream
     * @throws IOException Throws IOException
     */
    public long[] getCountReport(InputStream input) throws Exception {
        if (input == null) {
            throw new Exception(ERR_NULL_STREAMS);
        }
        long[] result = new long[3]; // lines, words, bytes

        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        byte[] data = new byte[1024];
        int inRead;
        boolean inWord = false;
        while ((inRead = input.read(data, 0, data.length)) != -1) {
            for (int i = 0; i < inRead; ++i) {
                if (Character.isWhitespace(data[i])) {
                    // Use <newline> character here. (Ref: UNIX)
                    if (data[i] == '\n') {
                        ++result[LINES_INDEX];
                    }
                    if (inWord) {
                        ++result[WORDS_INDEX];
                    }

                    inWord = false;
                } else {
                    inWord = true;
                }
            }
            result[BYTES_INDEX] += inRead;
            buffer.write(data, 0, inRead);
        }
        buffer.flush();
        if (inWord) {
            ++result[WORDS_INDEX]; // To handle last word
        }

        return result;
    }
}
