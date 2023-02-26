package sg.edu.nus.comp.cs4218.impl.parser;

public class MvArgsParser extends ArgsParser {
    private static final char FLAG_NO_OVERWRITE = 'n';
    private String[] sourceFiles;
    private String destFile;

    public MvArgsParser() {
        super();
        legalFlags.add(FLAG_NO_OVERWRITE);
    }

    public String[] getSourceFiles() {
        // if source files not initialised, get them from nonFlagArgs
        if (sourceFiles == null) {
            splitArgs();
        }
        return sourceFiles;
    }

    public String getDestFile() {
        // if dest files not initialised, get them from nonFlagArgs
        if (destFile == null) {
            splitArgs();
        }
        return destFile;
    }


    public boolean shouldOverwrite() {
        return !flags.contains(FLAG_NO_OVERWRITE);
    }

    private void splitArgs() {
        int argsSize = nonFlagArgs.size();
        if (argsSize < 2) {
            sourceFiles = new String[0];
            return;
        }

        sourceFiles = nonFlagArgs.subList(0, argsSize - 1).toArray(new String[0]);
        destFile = nonFlagArgs.get(argsSize - 1);
    }


}
