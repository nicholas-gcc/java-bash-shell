package sg.edu.nus.comp.cs4218.impl.app;

import sg.edu.nus.comp.cs4218.app.GrepInterface;
import sg.edu.nus.comp.cs4218.exception.AbstractApplicationException;
import sg.edu.nus.comp.cs4218.exception.GrepException;
import sg.edu.nus.comp.cs4218.impl.app.args.GrepArguments;

import java.io.*;
import java.util.ArrayList;
import java.util.StringJoiner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.ERR_FILE_DIR_NOT_FOUND;
import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.ERR_FILE_NOT_FOUND;
import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.ERR_INVALID_REGEX;
import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.ERR_NO_INPUT;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.CHAR_FLAG_PREFIX;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.STRING_NEWLINE;

@SuppressWarnings({"PMD.GodClass"})
public class GrepApplication implements GrepInterface {
    public static final String IS_DIRECTORY = "Is a directory";
    public static final String NULL_POINTER = "Null Pointer Exception";

    private final GrepArguments grepArgs;
    public GrepApplication() {
        grepArgs = new GrepArguments();
    }

    @Override
    public String grepFromFiles(String pattern, Boolean isCaseInsensitive, Boolean isCountLines, Boolean isPrefixFileName, String... fileNames) throws Exception {
        if (fileNames == null || pattern == null) {
            throw new GrepException(NULL_POINTER);
        }

        StringJoiner lineResults = new StringJoiner(STRING_NEWLINE);
        StringJoiner countResults = new StringJoiner(STRING_NEWLINE);

        grepResultsFromFiles(pattern, isCaseInsensitive, isPrefixFileName, lineResults, countResults, fileNames);

        String results = "";
        if (isCountLines) {
            results = countResults + STRING_NEWLINE;
        } else {
            if (!lineResults.toString().isEmpty()) {

                results = lineResults + STRING_NEWLINE;
            }
        }
        return results;
    }

    /**
     * Extract the lines and count number of lines for grep from files and insert them into
     * lineResults and countResults respectively.
     *
     * @param pattern           supplied by user
     * @param isCaseInsensitive supplied by user
     * @param lineResults       a StringJoiner of the grep line results
     * @param countResults      a StringJoiner of the grep line count results
     * @param fileNames         a String Array of file names supplied by user
     */
    private void grepResultsFromFiles(String pattern, Boolean isCaseInsensitive, Boolean hasPrefix, StringJoiner lineResults, StringJoiner countResults, String... fileNames) throws Exception {
        int count;
        boolean isSingleFile = (fileNames.length == 1);
        for (String f : fileNames) {
            BufferedReader reader = null;
            try {
                String path = grepArgs.convertToAbsolutePath(f);
                File file = new File(path);
                if (!file.exists()) {
                    lineResults.add(f + ": " + ERR_FILE_DIR_NOT_FOUND);
                    countResults.add(f + ": " + ERR_FILE_DIR_NOT_FOUND);
                    continue;
                }
                if (file.isDirectory()) { // ignore if it's a directory
                    lineResults.add(f + ": " + IS_DIRECTORY);
                    countResults.add(f + ": " + IS_DIRECTORY);
                    continue;
                }
                reader = new BufferedReader(new FileReader(path));
                String line;
                Pattern compiledPattern;
                compiledPattern = isCaseInsensitive ? Pattern.compile(pattern, Pattern.CASE_INSENSITIVE) : Pattern.compile(pattern);

                count = 0;
                while ((line = reader.readLine()) != null) {
                    Matcher matcher = compiledPattern.matcher(line);
                    if (matcher.find()) { // match
                        if (isSingleFile) {
                            lineResults.add(hasPrefix ? f + ": " + line : line);
                        } else {
                            lineResults.add(f + ": " + line);
                        }
                        count++;
                    }
                }
                if (isSingleFile && !hasPrefix) {
                    countResults.add("" + count);
                } else {
                    countResults.add(f + ": " + count);
                }
                reader.close();
            } catch (PatternSyntaxException pse) {
                throw new GrepException(ERR_INVALID_REGEX, pse);
            } finally {
                if (reader != null) {
                    reader.close();
                }
            }
        }
    }

    @Override
    public String grepFromStdin(String pattern, Boolean isCaseInsensitive, Boolean isCountLines, Boolean isPrefixFileName,
                                InputStream stdin) throws Exception {
        int count = 0;
        StringJoiner stringJoiner = new StringJoiner(STRING_NEWLINE);

        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(stdin));
            String line;
            Pattern compiledPattern;
            if (isCaseInsensitive) {
                compiledPattern = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE);
            } else {
                compiledPattern = Pattern.compile(pattern);
            }
            while ((line = reader.readLine()) != null) {
                Matcher matcher = compiledPattern.matcher(line);
                if (matcher.find()) { // match
                    line = isPrefixFileName ? "standard input: " + line: line;
                    stringJoiner.add(line);
                    count++;
                }
            }
            reader.close();
        } catch (PatternSyntaxException pse) {
            throw new GrepException(ERR_INVALID_REGEX, pse);
        } catch (NullPointerException npe) {
            throw new GrepException(ERR_FILE_NOT_FOUND, npe);
        }

        String results = "";
        if (isCountLines) {
            if (isPrefixFileName) {
                results = "standard input: " + count;
            } else {
                results = count + STRING_NEWLINE;
            }
        } else {
            if (!stringJoiner.toString().isEmpty()) {
                results = stringJoiner.toString() + STRING_NEWLINE;
            }
        }
        return results;
    }

    @Override
    public void run(String[] args, InputStream stdin, OutputStream stdout) throws AbstractApplicationException {
        try {
            ArrayList<String> inputFiles = new ArrayList<>();
            String pattern = grepArgs.getGrepArguments(args, inputFiles);
            GrepArguments.validate(pattern);

            String result;

            if (stdin == null && inputFiles.isEmpty()) {
                throw new Exception(ERR_NO_INPUT);
            }

            if (inputFiles.isEmpty()) {
                result = grepFromStdin(pattern, grepArgs.isCaseInsensitive(), grepArgs.isCountOfLinesOnly(), grepArgs.isPrefixFile(), stdin);
            } else {
                String[] inputFilesArray = new String[inputFiles.size()];
                inputFilesArray = inputFiles.toArray(inputFilesArray);
                if (inputFilesArray.length < 2) {
                    if (inputFilesArray[0].equals("-")) {
                        result = grepFromStdin(pattern, grepArgs.isCaseInsensitive(), grepArgs.isCountOfLinesOnly(), grepArgs.isPrefixFile(), stdin );
                    } else {
                        result = grepFromFiles(pattern, grepArgs.isCaseInsensitive(), grepArgs.isCountOfLinesOnly(), grepArgs.isPrefixFile(), inputFilesArray);
                    }
                } else {
                    result = grepFromFileAndStdin(pattern, grepArgs.isCaseInsensitive(), grepArgs.isCountOfLinesOnly(), grepArgs.isPrefixFile(), stdin, inputFilesArray);
                }

            }
            stdout.write(result.getBytes());
        } catch (GrepException grepException) {
            throw grepException;
        } catch (Exception e) {
            throw new GrepException(e.getMessage(), e);
        }
    }

    @Override
    public String grepFromFileAndStdin(String pattern, Boolean isCaseInsensitive, Boolean isCountLines,
                                       Boolean isPrefixFileName, InputStream stdin, String... fileNames) throws Exception {
        String result = "";
        for (String input: fileNames) {
            if (String.valueOf(CHAR_FLAG_PREFIX).equals(input)) {
                result = result + grepFromStdin(pattern, isCaseInsensitive, isCountLines, true, stdin);
            } else {
                String[] file = new String[] {input};
                result = result + grepFromFiles(pattern, isCaseInsensitive, isCountLines, true, file);
            }
        }
        return result;
    }
}
