package sg.edu.nus.comp.cs4218.impl.app.args;

import sg.edu.nus.comp.cs4218.exception.WcException;

import java.util.ArrayList;
import java.util.List;

import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.ERR_INVALID_FLAG;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.CHAR_FLAG_PREFIX;


public class WcArguments {

    public static final char CHAR_BYTES_OPTION = 'c';
    public static final char CHAR_LINES_OPTION = 'l';
    public static final char CHAR_WORDS_OPTION = 'w';
    private final List<String> files;
    private boolean lines, words, bytes;

    public WcArguments() {
        this.lines = true;
        this.words = true;
        this.bytes = true;
        this.files = new ArrayList<>();
    }

    /**
     * Handles argument list parsing for the `wc` application.
     *
     * @param args Array of arguments to parse
     */
    public void parse(String... args) throws WcException {
        boolean parsingFlag = true, isLines = false, isWords = false, isBytes = false;
        // Parse arguments
        if (args != null && args.length > 0) {
            for (String arg : args) {
                if (arg.isEmpty()) {
                    continue;
                }
                // `parsingFlag` is to ensure all flags come first, followed by files.
                if (parsingFlag && arg.charAt(0) == CHAR_FLAG_PREFIX) {
                    for (char c : arg.toCharArray()) {
                        if (c == CHAR_FLAG_PREFIX) {
                            continue;
                        }
                        if (c == CHAR_BYTES_OPTION) {
                            isBytes = true;
                            continue;
                        }
                        if (c == CHAR_LINES_OPTION) {
                            isLines = true;
                            continue;
                        }
                        if (c == CHAR_WORDS_OPTION) {
                            isWords = true;
                            continue;
                        }
                        throw new WcException(ERR_INVALID_FLAG);
                    }
                } else {
                    parsingFlag = false;
                    this.files.add(arg.trim());
                }
            }
        }
        if (isLines || isWords || isBytes) {
            this.lines = isLines;
            this.words = isWords;
            this.bytes = isBytes;
        }
    }

    public boolean isLines() {
        return lines;
    }

    public boolean isWords() {
        return words;
    }

    public boolean isBytes() {
        return bytes;
    }

    public List<String> getFiles() {
        return files;
    }
}
