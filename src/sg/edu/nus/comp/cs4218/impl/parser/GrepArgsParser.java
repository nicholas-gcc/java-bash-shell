package sg.edu.nus.comp.cs4218.impl.parser;

public class GrepArgsParser extends ArgsParser {
    private final static char FLAG_IS_INVERT = 'v';
    private final static int INDEX_PATTERN = 0;
    private final static int INDEX_FILES = 1;

    public GrepArgsParser() {
        super();
        legalFlags.add(FLAG_IS_INVERT);
    }

    public Boolean isInvert() {
        return flags.contains(FLAG_IS_INVERT);
    }

    public String getPattern() {
        return nonFlagArgs.isEmpty() ? nonFlagArgs.get(INDEX_PATTERN) : null;
    }

    public String[] getFileNames() {
        return nonFlagArgs.size() <= 1 ? null : nonFlagArgs.subList(INDEX_FILES, nonFlagArgs.size())
                .toArray(new String[0]);
    }
}
