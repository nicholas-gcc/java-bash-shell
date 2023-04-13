package sg.edu.nus.comp.cs4218.impl.app.args;

import sg.edu.nus.comp.cs4218.exception.InvalidArgsException;

import java.util.Arrays;
import java.util.List;

import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.ERR_NO_ARGS;
import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.ERR_NULL_ARGS;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.CHAR_FLAG_PREFIX;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.STRING_DASH;

public class CatArguments {
    String[] files;

    boolean isLineNumbers;

    private static final String FLAG_NUM_STRING = "-n";

    /**
     * Parse 1 or 2 arguments for cat command
     * option (optional): -n
     * FILES: the name of the file or files. With no FILES, or when FILES is -, read standard input
     *
     *  @param args arguments to parse
     * @throws InvalidArgsException when arguments are invalid
     */
    public void parse(String... args) throws InvalidArgsException {
        if (args == null) {
            throw new NullPointerException(ERR_NULL_ARGS);
        }

        // check if arg is only - or empty (open stdin)
        if ((args.length == 1 && args[0].equals(STRING_DASH)) || args.length == 0) {
            this.files = new String[]{"-"};
            return;
        }
        // check if option flag -n exists, set boolean accordingly
        isLineNumbers = args[0].equals(FLAG_NUM_STRING);

        List<String> filesArr;
        if (isLineNumbers) {
            filesArr = Arrays.asList(args).subList(1, args.length);
        } else {
            filesArr = Arrays.asList(args).subList(0, args.length);
        }

        // some logic to set the files correctly
        this.files = new String[filesArr.size()];
        this.files = filesArr.toArray(this.files);
    }

    public boolean hasLineNumbers() {
        return isLineNumbers;
    }

    public String[] getFiles() {
        return files;
    }
}
