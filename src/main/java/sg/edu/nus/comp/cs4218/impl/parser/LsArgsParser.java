package sg.edu.nus.comp.cs4218.impl.parser;

import java.util.List;

public class LsArgsParser extends ArgsParser {
    private final static char FLAG_IS_RECURSIVE = 'R';
    private final static char FLAG_SORT_BY_EXT = 'X';

    public LsArgsParser() {
        super();
        legalFlags.add(FLAG_IS_RECURSIVE);
        legalFlags.add(FLAG_SORT_BY_EXT);
    }

    public Boolean isRecursive() {
        return flags.contains(FLAG_IS_RECURSIVE);
    }

    public Boolean isSortByExt() {
        return flags.contains(FLAG_SORT_BY_EXT);
    }

    public List<String> getDirectories() {
        return nonFlagArgs;
    }
}
