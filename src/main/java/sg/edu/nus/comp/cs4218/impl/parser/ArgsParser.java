package sg.edu.nus.comp.cs4218.impl.parser;

import sg.edu.nus.comp.cs4218.exception.InvalidArgsException;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.CHAR_FLAG_PREFIX;

/**
 * Every application's parser should extend this class to encapsulate their own parsing details and
 * information.
 */
public class ArgsParser {
    public static final String ILLEGAL_FLAG_MSG = "illegal option -- ";

    protected Set<Character> flags;
    protected Set<Character> legalFlags;
    protected List<String> nonFlagArgs;

    protected ArgsParser() {
        flags = new HashSet<>();
        legalFlags = new HashSet<>();
        nonFlagArgs = new ArrayList<>();
    }

    /**
     * Separates command flags from non-flag arguments given a tokenized command.
     *
     * @param args
     */
    public void parse(String... args) throws InvalidArgsException {
        for (String arg : args) {
            if (arg.length() > 1 && arg.charAt(0) == CHAR_FLAG_PREFIX) {
                // Treat the characters (excluding CHAR_FLAG_PREFIX) as individual flags.
                for (int i = 1; i < arg.length(); i++) {
                    flags.add(arg.charAt(i));
                }
            } else {
                nonFlagArgs.add(arg);
            }
        }

        validateArgs();
    }

    /**
     * Checks for the existence of illegal flags. Presence of any illegal flags would result in a
     * non-empty set after subtracting the set of legal flags from the set of parsed flags.
     * <p>
     * Note on usage: Do not call this method directly in any application.
     *
     * @throws InvalidArgsException
     */
    protected void validateArgs() throws InvalidArgsException {
        Set<Character> illegalFlags = new HashSet<>(flags);
        illegalFlags.removeAll(legalFlags);

        // construct exception message with the first illegal flag encountered
        for (Character flag : illegalFlags) {
            String exceptionMessage = ILLEGAL_FLAG_MSG + flag;
            throw new InvalidArgsException(exceptionMessage);
        }
    }
}
