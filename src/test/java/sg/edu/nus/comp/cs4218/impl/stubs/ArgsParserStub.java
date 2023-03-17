package sg.edu.nus.comp.cs4218.impl.stubs;

import sg.edu.nus.comp.cs4218.impl.parser.ArgsParser;

import java.util.List;

public class ArgsParserStub extends ArgsParser {
    private final static char TEST_FLAG_1 = 'A';
    private final static char TEST_FLAG_2 = 'B';
    public ArgsParserStub() {
        super();
        legalFlags.add(TEST_FLAG_1);
        legalFlags.add(TEST_FLAG_2);
    }

    public Boolean isFlag1() {
        return flags.contains(TEST_FLAG_1);
    }

    public Boolean isFlag2() {
        return flags.contains(TEST_FLAG_2);
    }

    public List<String> getNonFlagArgs() {
        return nonFlagArgs;
    }
}
