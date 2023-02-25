package sg.edu.nus.comp.cs4218.impl.app;

import sg.edu.nus.comp.cs4218.Environment;
import sg.edu.nus.comp.cs4218.app.GrepInterface;
import sg.edu.nus.comp.cs4218.exception.AbstractApplicationException;
import sg.edu.nus.comp.cs4218.exception.GrepException;
import sg.edu.nus.comp.cs4218.exception.InvalidArgsException;
import sg.edu.nus.comp.cs4218.impl.app.args.GrepArguments;
import sg.edu.nus.comp.cs4218.impl.parser.GrepArgsParser;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.StringJoiner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.*;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.CHAR_FILE_SEP;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.CHAR_FLAG_PREFIX;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.STRING_NEWLINE;

public class GrepApplication implements GrepInterface {
    public static final String INVALID_PATTERN = "Invalid pattern syntax";
    public static final String EMPTY_PATTERN = "Pattern should not be empty.";
    public static final String IS_DIRECTORY = "Is a directory";
    public static final String NULL_POINTER = "Null Pointer Exception";
    private static final int NUM_ARGUMENTS = 3;



    private static GrepArguments grepArgs = new GrepArguments();

    @Override
    public String grepFromFiles(String pattern, Boolean isCaseInsensitive, Boolean isCountLines, Boolean isPrefixFileName, String... fileNames) throws Exception {
        // TODO: To implement -H flag print file name with output lines
        if (fileNames == null || pattern == null) {
            throw new GrepException(NULL_POINTER);
        }

        StringJoiner lineResults = new StringJoiner(STRING_NEWLINE);
        StringJoiner countResults = new StringJoiner(STRING_NEWLINE);

        grepResultsFromFiles(pattern, isCaseInsensitive, isPrefixFileName, lineResults, countResults, fileNames);

        String results = "";
        if (isCountLines) {
            results = countResults.toString() + STRING_NEWLINE;
        } else {
            if (!lineResults.toString().isEmpty()) {

                results = lineResults.toString() + STRING_NEWLINE;
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
                String path = convertToAbsolutePath(f);
                File file = new File(path);
                if (!file.exists()) {
                    lineResults.add(f + ": " + ERR_FILE_NOT_FOUND);
                    countResults.add(f + ": " + ERR_FILE_NOT_FOUND);
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
                compiledPattern = isCaseInsensitive? Pattern.compile(pattern, Pattern.CASE_INSENSITIVE) : Pattern.compile(pattern);

                count = 0;
                while ((line = reader.readLine()) != null) {
                    Matcher matcher = compiledPattern.matcher(line);
                    if (matcher.find()) { // match
                        if (isSingleFile) {
                            lineResults.add(hasPrefix? f + ": " + line : line);
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
                throw new GrepException(ERR_INVALID_REGEX);
            } finally {
                if (reader != null) {
                    reader.close();
                }
            }
        }
    }

    /**
     * Converts filename to absolute path, if initially was relative path
     *
     * @param fileName supplied by user
     * @return a String of the absolute path of the filename
     */
    private String convertToAbsolutePath(String fileName) {
        String home = System.getProperty("user.home").trim();
        String currentDir = Environment.currentDirectory.trim();
        String convertedPath = convertPathToSystemPath(fileName);

        String newPath;
        if (convertedPath.length() >= home.length() && convertedPath.substring(0, home.length()).trim().equals(home)) {
            newPath = convertedPath;
        } else {
            newPath = currentDir + CHAR_FILE_SEP + convertedPath;
        }
        return newPath;
    }

    /**
     * Converts path provided by user into path recognised by the system
     *
     * @param path supplied by user
     * @return a String of the converted path
     */
    private String convertPathToSystemPath(String path) {
        String convertedPath = path;
        String pathIdentifier = "\\" + Character.toString(CHAR_FILE_SEP);
        convertedPath = convertedPath.replaceAll("(\\\\)+", pathIdentifier);
        convertedPath = convertedPath.replaceAll("/+", pathIdentifier);

        if (convertedPath.length() != 0 && convertedPath.charAt(convertedPath.length() - 1) == CHAR_FILE_SEP) {
            convertedPath = convertedPath.substring(0, convertedPath.length() - 1);
        }

        return convertedPath;
    }

    @Override
    public String grepFromStdin(String pattern, Boolean isCaseInsensitive, Boolean isCountLines, Boolean isPrefixFileName, InputStream stdin) throws Exception {
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
                    line = isPrefixFileName? "standard input: " + line: line;
                    stringJoiner.add(line);
                    count++;
                }
            }
            reader.close();
        } catch (PatternSyntaxException pse) {
            throw new GrepException(ERR_INVALID_REGEX);
        } catch (NullPointerException npe) {
            throw new GrepException(ERR_FILE_NOT_FOUND);
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

            String result = "";

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
            throw new GrepException(e.getMessage());
        }
    }

    @Override
    public String grepFromFileAndStdin(String pattern, Boolean isCaseInsensitive, Boolean isCountLines, Boolean isPrefixFileName, InputStream stdin, String... fileNames) throws Exception {
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
