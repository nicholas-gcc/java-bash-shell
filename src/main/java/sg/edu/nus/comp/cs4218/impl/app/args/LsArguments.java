package sg.edu.nus.comp.cs4218.impl.app.args;

import sg.edu.nus.comp.cs4218.exception.LsException;

import java.util.ArrayList;
import java.util.List;

import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.CHAR_FLAG_PREFIX;

public class LsArguments {
    private final List<String> folders;
    private boolean recursive;

    public LsArguments() {
        this.recursive = false;
        this.folders = new ArrayList<>();
    }

    /**
     * Handles argument list parsing for the `ls` application.
     *
     * @param args Array of arguments to parse
     * @throws LsException
     */
    public void parse(String... args) throws LsException {
        if (args != null && args.length > 0) {
            for (int index = 0; index < args.length; ++index) {
                String arg = args[index];
                if (arg.length() == 0) {
                    continue;
                }
                if (arg.charAt(0) == CHAR_FLAG_PREFIX) {
                    if (arg.equals(CHAR_FLAG_PREFIX + "R")) {
                        this.recursive = true;
                    } else {
                        this.folders.add(arg.trim());
                    }
                } else { // Otherwise, treat as regular folder
                    this.folders.add(arg.trim());
                }
            }
        }
    }

    public boolean isRecursive() {
        return recursive;
    }

    public List<String> getFolders() {
        return folders;
    }
}
