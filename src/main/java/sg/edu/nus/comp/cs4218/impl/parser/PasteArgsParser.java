package sg.edu.nus.comp.cs4218.impl.parser;

import sg.edu.nus.comp.cs4218.exception.InvalidArgsException;

import java.util.List;

public class PasteArgsParser extends ArgsParser {
    private static final char FLAG_IS_SERIAL = 's';
    public PasteArgsParser() {
        super();
        legalFlags.add(FLAG_IS_SERIAL);
    }

    @Override
    public void parse(String... args) throws InvalidArgsException {
        if (args == null) {
            super.parse();
        } else {
            super.parse(args);
        }
    }

    public boolean isSerial() {
        return flags.contains(FLAG_IS_SERIAL);
    }

    public List<String> getFileNames() {
        return List.copyOf(nonFlagArgs);
    }
}
