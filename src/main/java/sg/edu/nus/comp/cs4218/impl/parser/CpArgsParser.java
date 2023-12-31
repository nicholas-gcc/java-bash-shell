package sg.edu.nus.comp.cs4218.impl.parser;

public class CpArgsParser extends ArgsParser {
    private final static char RECURSIVE_FLAG_L = 'r';
    private final static char RECURSIVE_FLAG_U = 'R';
    private String[] sourceFiles;
    private String destFile;

    public CpArgsParser() {
        super();
        legalFlags.add(RECURSIVE_FLAG_L);
        legalFlags.add(RECURSIVE_FLAG_U);
    }

    public boolean isRecursive() {
        return flags.contains(RECURSIVE_FLAG_U) || flags.contains(RECURSIVE_FLAG_L);
    }

    public String[] getSourceFiles() {
        // if source files not initialised, get them from nonFlagArgs
        if (sourceFiles == null) {
            splitArgs();
        }
        return sourceFiles;
    }

    public String getDestFileOrFolder() {
        // if dest files not initialised, get them from nonFlagArgs
        if (destFile == null) {
            splitArgs();
        }
        return destFile;
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
