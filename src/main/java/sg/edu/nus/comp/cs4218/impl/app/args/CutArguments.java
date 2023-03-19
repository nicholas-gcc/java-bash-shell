package sg.edu.nus.comp.cs4218.impl.app.args;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.ERR_INVALID_ARG;
import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.ERR_INVALID_FLAG;
import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.ERR_NO_ARGS;
import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.ERR_NULL_ARGS;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.CHAR_FLAG_PREFIX;

public class CutArguments {
    public static final char CHAR_CUT_BY_BYTE = 'b';
    public static final char CHAR_CUT_BY_CHAR = 'c';
    private final List<String> files = new ArrayList<>();
    private boolean charPo, bytePo;
    private final List<int[]> ranges = new ArrayList<>();

    public CutArguments() {
        this.charPo = false;
        this.bytePo = false;
    }

    public List<String> getFiles() {
        return files;
    }

    public boolean isCharPo() {
        return charPo;
    }

    public boolean isBytePo() {
        return bytePo;
    }

    public List<int[]> getRanges() {
        return ranges;
    }

    /**
     * Parse 2 or 3 arguments for the cut command
     * options: -c, -b
     * ranges
     * files (optional)
     * @param args arguments to parse
     */
    public void parse(String... args) throws Exception {
        if (args == null) {
            throw new NullPointerException(ERR_NULL_ARGS);
        }
        if (args.length < 2) {
            throw new IllegalArgumentException(ERR_NO_ARGS);
        }

        // check and set cut by byte or character option
        if (args[0].charAt(0) == CHAR_FLAG_PREFIX && args[0].charAt(1) == CHAR_CUT_BY_CHAR) {
            charPo = true;
        } else if (args[0].charAt(0) == CHAR_FLAG_PREFIX && args[0].charAt(1) == CHAR_CUT_BY_BYTE) {
            bytePo = true;
        } else {
            throw new IllegalArgumentException(ERR_INVALID_FLAG);
        }

        // Parse indexes / list
        // For example, 1,2,3-5,6-8,9 should be parsed into [1,1], [2,2], [3,5], [6,8], [9,9]
        String[] rangeStr = args[1].split(",");
        for (String s : rangeStr) {
            String[] range = s.split("-");
             if (range.length == 1) {
                int[] rangeEntry = new int[2];
                try {
                    rangeEntry[0] = Integer.parseInt(range[0]);
                    rangeEntry[1] = Integer.parseInt(range[0]);
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException("Invalid indexes provided", e);
                }
                this.ranges.add(rangeEntry);
            } else if (range.length == 2) {
                int[] rangeEntry = new int[2];
                 rangeEntry[0] = Integer.parseInt(range[0]);
                 rangeEntry[1] = Integer.parseInt(range[1]);
                if (rangeEntry[0] > rangeEntry[1]) {
                    throw new IllegalArgumentException(ERR_INVALID_ARG);
                }
                this.ranges.add(rangeEntry);
            } else {
                throw new IllegalArgumentException(ERR_INVALID_ARG);
            }
        }

        files.addAll(Arrays.asList(args).subList(2, args.length));
    }
}
