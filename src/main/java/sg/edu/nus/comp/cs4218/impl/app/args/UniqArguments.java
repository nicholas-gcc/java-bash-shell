package sg.edu.nus.comp.cs4218.impl.app.args;

import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.*;

public class UniqArguments {
    private static final char CHAR_FLAG = '-';
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

    public String[] getArguments(String[] args) {
        if(args.length == 0) {
            return new String[] {};
        }
        if(args.length == 1) {

        }
    }
}
