package sg.edu.nus.comp.cs4218.impl.app;

import sg.edu.nus.comp.cs4218.app.PasteInterface;
import sg.edu.nus.comp.cs4218.exception.AbstractApplicationException;
import sg.edu.nus.comp.cs4218.exception.InvalidArgsException;
import sg.edu.nus.comp.cs4218.exception.PasteException;
import sg.edu.nus.comp.cs4218.impl.parser.PasteArgsParser;
import sg.edu.nus.comp.cs4218.exception.PasteException;
import sg.edu.nus.comp.cs4218.impl.util.IOUtils;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.ERR_FILE_NOT_FOUND;
import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.ERR_IS_DIR;
import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.ERR_NO_INPUT;
import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.ERR_NO_ISTREAM;
import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.ERR_NO_OSTREAM;
import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.ERR_NULL_ARGS;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.CHAR_TAB;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.STRING_NEWLINE;

public class PasteApplication implements PasteInterface {

    /**
     * Runs application with specified input data and specified output stream.
     *
     * @param args [Option] [FILES]...
     *             Option: -s, paste one file at a time instead of in parallel
     *             FILES: File names to be read and merged (including "-" for reading from stdin)
     * @param stdin InputStream containing arguments from Stdin
     * @param stdout OutputStream to write the result to
     */
    @Override
    public void run(String[] args, InputStream stdin, OutputStream stdout) throws AbstractApplicationException {
        PasteArgsParser pasteArgsParser = new PasteArgsParser();

        try {
            pasteArgsParser.parse(args);
        } catch (InvalidArgsException e) {
            throw new PasteException(e.getMessage());
        }

        boolean isSerial = pasteArgsParser.isSerial();
        String[] fileNames = pasteArgsParser.getFileNames().toArray(String[]::new);
        if (args == null) {
            throw new PasteException(ERR_NULL_ARGS);
        }
        if (stdout == null) {
            throw new PasteException(ERR_NO_OSTREAM);
        }
        if (stdin == null && (fileNames == null || fileNames.length == 0)) {
            throw new PasteException(ERR_NO_INPUT);
        }

        try {
            List<InputStream> streams = getInputStreamsFromFiles(stdin, fileNames);
            String result = mergeInputStreams(isSerial, streams.toArray(InputStream[]::new));
            stdout.write(result.getBytes());
        } catch (Exception e) {
            throw new PasteException(e.getMessage());
        }
    }

    /**
     * Returns string of line-wise concatenated (tab-separated) Stdin arguments. If only one Stdin
     * arg is specified, echo back the Stdin.
     *
     * @param isSerial Paste one file at a time instead of in parallel
     * @param stdin    InputStream containing arguments from Stdin
     * @throws Exception
     */
    @Override
    public String mergeStdin(Boolean isSerial, InputStream stdin) throws Exception {
        try {
            if (isSerial == null) {
                throw new PasteException(ERR_NULL_ARGS);
            }
            if (stdin == null) {
                throw new PasteException(ERR_NO_ISTREAM);
            }
            InputStream[] streams = {stdin};
            return mergeInputStreams(isSerial, streams);
        } catch (Exception e) {
            throw new PasteException(e.getMessage());
        }
    }

    /**
     * Returns string of line-wise concatenated (tab-separated) files. If only one file is
     * specified, echo back the file content.
     *
     * @param isSerial Paste one file at a time instead of in parallel
     * @param fileNames Array of file names to be read and merged (not including "-" for reading from stdin)
     * @throws Exception if an error occurs while reading from the input streams
     */
    @Override
    @SuppressWarnings("PMD.CloseResource")
    public String mergeFile(Boolean isSerial, String... fileNames) throws Exception {
        try {
            if (fileNames == null || fileNames.length == 0 || isSerial == null) {
                throw new PasteException(ERR_NULL_ARGS);
            }
            List<InputStream> inputStreams = getInputStreamsFromFiles(null, fileNames);
            return mergeInputStreams(isSerial, inputStreams.toArray(InputStream[]::new));
        } catch (Exception e) {
            throw new PasteException(e.getMessage());
        }
    }

    /**
     * Returns string of line-wise concatenated (tab-separated) files and Stdin arguments.
     *
     * @param isSerial Paste one file at a time instead of in parallel
     * @param stdin    InputStream containing arguments from Stdin
     * @param fileName Array of file names to be read and merged (including "-" for reading from stdin)
     * @throws Exception if an error occurs while reading from the input streams
     */
    @Override
    public String mergeFileAndStdin(Boolean isSerial, InputStream stdin, String... fileName) throws Exception {
        try {
            if (fileName == null || fileName.length == 0 || isSerial == null) {
                throw new PasteException(ERR_NULL_ARGS);
            }
            if (stdin == null) {
                throw new PasteException(ERR_NO_ISTREAM);
            }
            List<InputStream> inputStreams = getInputStreamsFromFiles(stdin, fileName);
            inputStreams.add(0, stdin);
            return mergeInputStreams(isSerial, inputStreams.toArray(InputStream[]::new));
        } catch (Exception e) {
            throw new PasteException(e.getMessage());
        }
    }

    /**
     * read data from multiple input streams and concatenate it into a single String,
     * either by reading each stream in sequence or by reading them in parallel and
     * merging the lines together
     *
     * @param isSerial     whether to read each stream in sequence or in parallel
     * @param inputStreams the input streams to read from
     * @return the concatenated String
     * @throws Exception if an error occurs while reading from the input streams
     */
    @SuppressWarnings("PMD.CloseResource")
    private String mergeInputStreams(Boolean isSerial, InputStream... inputStreams) throws Exception { //NOPMD
        if (inputStreams == null || inputStreams.length == 0) {
            throw new PasteException(ERR_NULL_ARGS);
        }
        StringBuilder output = new StringBuilder();
        if (isSerial) {
            for (InputStream inputStream : inputStreams) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                // reads all the lines from the stream and concatenates them separated by tabs
                String line = reader.lines().collect(Collectors.joining(String.valueOf(CHAR_TAB)));
                output.append(line.trim()).append(STRING_NEWLINE);
                reader.close();
                inputStream.close();
            }
        } else {
            boolean hasData = true;
            Map<InputStream, BufferedReader> readerMap = new HashMap<>();
            for (InputStream inputStream : inputStreams) {
                if (!readerMap.containsKey(inputStream)) {
                    readerMap.put(inputStream, new BufferedReader(new InputStreamReader(inputStream)));
                }
            }
            while (hasData) {
                StringBuilder lineSb = new StringBuilder();
                hasData = false;
                // iterates over each input stream and reads a line of text
                // from its corresponding BufferedReader object
                for (InputStream inputStream : inputStreams) {
                    BufferedReader reader = readerMap.get(inputStream);
                    String line = reader.readLine();
                    if (line != null) {
                        lineSb.append(line.trim());
                        hasData = true;
                    }
                    lineSb.append(CHAR_TAB);
                }
                if (hasData) {
                    // instead of trimming lineSb when appending to output,
                    // we trim the last tab character here for cases where
                    // non empty file is merged with empty file
                    // (i.e. the line could be "A\t\t") and we want to remove the ONE extra tab ONLY
                    if (lineSb.toString().endsWith(String.valueOf(CHAR_TAB))) {
                        lineSb.deleteCharAt(lineSb.length() - 1);
                    }
                    output.append(lineSb).append(STRING_NEWLINE);
                }
            }
            // close inputStreams and their corresponding BufferedReader objects
            for (InputStream inputStream : inputStreams) {
                readerMap.get(inputStream).close();
                inputStream.close();
            }
        }
        return output.toString();
    }

    /**
     * Convert a mix of String file names and an InputStream object (which may represent stdin)
     * into a List of InputStream objects that can be processed together
     *
     * @param stdin     the InputStream object representing stdin
     * @param fileNames the String file names
     * @return a List of InputStream objects
     * @throws Exception if an error occurs while reading from the input streams
     */
    public List<InputStream> getInputStreamsFromFiles(InputStream stdin, String... fileNames) throws Exception {
        List<InputStream> inputStreams = new ArrayList<>();
        if (fileNames == null || fileNames.length == 0) {
            inputStreams.add(stdin);
            return inputStreams;
        }
        for (String fileName : fileNames) {
            // From project document: when FILES is -, read standard input.
            if (stdin != null && fileName.equals("-")) {
                inputStreams.add(stdin);
                continue;
            }

            Path filePath = IOUtils.resolveFilePath(fileName);
            if (Files.notExists(filePath)) {
                throw new FileNotFoundException(fileName + ": " + ERR_FILE_NOT_FOUND);
            }
            if (Files.isDirectory(filePath)) {
                throw new FileNotFoundException(fileName + ": " + ERR_IS_DIR);
            }
            inputStreams.add(Files.newInputStream(filePath));
        }
        return inputStreams;
    }

}
