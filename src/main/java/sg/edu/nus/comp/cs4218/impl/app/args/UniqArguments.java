package sg.edu.nus.comp.cs4218.impl.app.args;

import sg.edu.nus.comp.cs4218.exception.UniqException;

import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.*;

public class UniqArguments {
    private static final char CHAR_FLAG = '-';
    private static final char CHAR_COUNT = 'c';
    private static final char CHAR_REPEATED = 'd';
    private static final char CHAR_ALL_REPEATED = 'D';
    private boolean count;
    private boolean repeated;
    private boolean allRepeated;

    private String inputFile, outputFile;

    public UniqArguments() {
        this.count = false;
        this.repeated = false;
        this.allRepeated = false;
        this.inputFile = null;
        this.outputFile = null;
    }

    public boolean isCount() {
        return count;
    }

    public boolean isRepeated() {
        return repeated;
    }

    public boolean isAllRepeated() {
        return allRepeated;
    }

    public void parse(String... args) throws Exception {

    }

    public String[] getArguments(String[] args) throws UniqException {
        boolean isOption = true;
        boolean isInput = false;
        boolean isOutput = false;
        String[] result = {null, null};
        if(args.length == 0) {
            return new String[] {};
        }

        for (String arg: args) {
            if (arg.charAt(0) == CHAR_FLAG && arg.length() == 2){//option flag
                switch (arg.charAt(1)) {
                    case CHAR_COUNT:
                        this.count = true;
                        break;
                    case CHAR_REPEATED:
                        this.repeated = true;
                        break;
                    case CHAR_ALL_REPEATED:
                        this.allRepeated = true;
                        break;
                    default:
                        throw new UniqException(ERR_INVALID_FLAG);
                }
            } else {
                isOption = false;
            }
        }
        if(args.length == 1) {

        }
    }
}
