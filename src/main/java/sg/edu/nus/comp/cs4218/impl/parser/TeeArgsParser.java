package sg.edu.nus.comp.cs4218.impl.parser;

import java.util.List;

public class TeeArgsParser extends ArgsParser {
    private final static char FLAG_IS_APPEND = 'a';

    public TeeArgsParser() {
        super();
        legalFlags.add(FLAG_IS_APPEND);
    }

    public Boolean isAppend() {
        return flags.contains(FLAG_IS_APPEND);
    }

    public List<String> getFilesNames() {
        return nonFlagArgs;
    }
}
