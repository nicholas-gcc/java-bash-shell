package sg.edu.nus.comp.cs4218.impl.app.args;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.*;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.CHAR_FLAG_PREFIX;

public class GrepArguments {

    public static final char CHAR_CASE_IGNORE = 'i';
    public static final char CHAR_COUNT_LINES = 'c';
    private final List<String> files;
    private String pattern;
    private boolean caseInsensitive, countOfLinesOnly;

    public GrepArguments() {
        this.pattern = null;
        this.caseInsensitive = false;
        this.countOfLinesOnly = false;
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
            throw new Exception(ERR_INVALID_REGEX);//NOPMD
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
}
