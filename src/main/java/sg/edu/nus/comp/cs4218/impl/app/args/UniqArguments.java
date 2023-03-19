package sg.edu.nus.comp.cs4218.impl.app.args;

import sg.edu.nus.comp.cs4218.exception.UniqException;

import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.ERR_INVALID_FLAG;

public class UniqArguments {
    private static final char CHAR_FLAG = '-';
    private static final char CHAR_COUNT = 'c';
    private static final char CHAR_REPEATED = 'd';
    private static final char CHAR_ALL_REPEATED = 'D';
    private boolean count;
    private boolean repeated;
    private boolean allRepeated;

    public UniqArguments() {
        this.count = false;
        this.repeated = false;
        this.allRepeated = false;
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

    /**
     * Given an array of arguments, extract flags (options) and return input and output filename
     * if input/output file is not specified, null is contained in the array
     * @param args Arguments for the command
     * @return [inputFileName, outputFileName]
     * @throws UniqException Throws UniqException if invalid flag is met
     */
    public String[] getFiles(String... args) throws UniqException {
        boolean isOption = true;
        String[] result = {null, null};

        for (String arg: args) {
            if (isOption && !arg.isEmpty() && arg.charAt(0) == CHAR_FLAG){//option flag
                if (arg.length() > 2) {
                    throw new UniqException(ERR_INVALID_FLAG);
                }
                if (arg.length() == 1) {//- is for stdin, end of options
                    isOption = false;
                    continue;
                }
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
            } else if (isOption){//this is input file
                isOption = false;
                result[0] = arg;
            } else {//this is output file
                result[1] = arg;
            }
        }
        return result;
    }
}
