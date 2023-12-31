package sg.edu.nus.comp.cs4218.impl.app.args;

import sg.edu.nus.comp.cs4218.Environment;
import sg.edu.nus.comp.cs4218.exception.GrepException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.*;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.CHAR_FILE_SEP;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.CHAR_FLAG_PREFIX;

public class GrepArguments {

    public static final char CHAR_CASE_IGNORE = 'i';
    public static final char CHAR_COUNT_LINES = 'c';
    private static final int NUM_ARGUMENTS = 3;
    private static final char CASE_INSEN_IDENT = 'i';
    private static final char COUNT_IDENT = 'c';
    private static final char PREFIX_FN = 'H';

    public static final String INVALID_PATTERN = "Invalid pattern syntax";
    public static final String EMPTY_PATTERN = "Pattern should not be empty.";

    private final List<String> files;
    private String pattern;
    private boolean caseInsensitive, countOfLinesOnly, prefixFile;

    boolean[] grepFlags = new boolean[NUM_ARGUMENTS];

    public GrepArguments() {
        this.pattern = null;
        this.caseInsensitive = false;
        this.countOfLinesOnly = false;
        this.prefixFile = false;
        this.files = new ArrayList<>();
    }

    public static void validate(String pattern) throws Exception {
        if (pattern == null) {
            throw new Exception(ERR_NULL_ARGS);
        }
        if (pattern.isEmpty()) {
            throw new Exception(ERR_EMPTY_REGEX);
        }
        try {
            Pattern.compile(pattern); // Test if valid regex
        } catch (PatternSyntaxException e) {
            throw new Exception(ERR_INVALID_REGEX, e);
        }
    }

    /**
     * Handles argument list parsing for the `grep` application.
     *
     * @param args Array of arguments to parse
     * @throws Exception
     */
    public void parse(String... args) throws Exception {
        if (args == null) {
            throw new Exception(ERR_NULL_ARGS);
        }
        if (args.length < 1) {
            throw new Exception(ERR_NO_REGEX);
        }

        boolean parsingFlag = true;
        for (String arg : args) {
            if (arg.isEmpty()) {
                continue;
            }
            // `parsingFlag` is to ensure all flags come first, followed by files.
            if (parsingFlag && arg.charAt(0) == CHAR_FLAG_PREFIX) {
                if (arg.equals(CHAR_FLAG_PREFIX + "" + CHAR_CASE_IGNORE)) {
                    this.caseInsensitive = true;
                } else if (arg.equals(CHAR_FLAG_PREFIX + "" + CHAR_COUNT_LINES)) {
                    this.countOfLinesOnly = true;
                } else {
                    // If we are in here, it must be that pattern is null.
                    parsingFlag = false;
                    this.pattern = arg.trim();
                }
            } else {
                parsingFlag = false;
                if (this.pattern == null) {
                    this.pattern = arg.trim();
                } else {
                    this.files.add(arg.trim());
                }
            }
        }
        validate(this.pattern);
    }

    public List<String> getFiles() {
        return files;
    }

    public String getPattern() {
        return pattern;
    }

    public boolean isCaseInsensitive() {
        return caseInsensitive;
    }

    public boolean isCountOfLinesOnly() {
        return countOfLinesOnly;
    }

    public boolean isPrefixFile() { return prefixFile;}

    /**
     * Separates the arguments provided by user into the flags, pattern and input files.
     *
     * @param args       supplied by user
     * @param inputFiles a ArrayList<String> of file names supplied by user
     * @return regex pattern supplied by user. An empty String if not supplied.
     */
    public String getGrepArguments(String[] args, ArrayList<String> inputFiles) throws Exception {
        boolean isFile = false; // files can only appear after pattern

        for (String s : args) {
            char[] arg = s.toCharArray();
            if (isFile) {
                inputFiles.add(s);
                files.add(s);
            } else {
                if (!s.isEmpty() && arg[0] == CHAR_FLAG_PREFIX) {
                    arg = Arrays.copyOfRange(arg, 1, arg.length);
                    for (char c : arg) {
                        switch (c) {
                            case CASE_INSEN_IDENT:
                                this.caseInsensitive = true;
                                break;
                            case COUNT_IDENT:
                                this.countOfLinesOnly = true;
                                break;
                            case PREFIX_FN:
                                this.prefixFile = true;
                                break;
                            default:
                                throw new GrepException(ERR_SYNTAX);
                        }
                    }
                } else { // pattern must come before file names
                    pattern = s;
                    isFile = true; // next arg onwards will be file
                }
            }
        }
        return pattern;
    }

    /**
     * Converts filename to absolute path, if initially was relative path
     *
     * @param fileName supplied by user
     * @return a String of the absolute path of the filename
     */
    public String convertToAbsolutePath(String fileName) {
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
    public String convertPathToSystemPath(String path) {
        String convertedPath = path;
        String pathIdentifier = "\\" + Character.toString(CHAR_FILE_SEP);
        convertedPath = convertedPath.replaceAll("(\\\\)+", pathIdentifier);
        convertedPath = convertedPath.replaceAll("/+", pathIdentifier);

        if (convertedPath.length() != 0 && convertedPath.charAt(convertedPath.length() - 1) == CHAR_FILE_SEP) {
            convertedPath = convertedPath.substring(0, convertedPath.length() - 1);
        }

        return convertedPath;
    }
}
