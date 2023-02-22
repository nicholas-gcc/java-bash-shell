package sg.edu.nus.comp.cs4218.impl.app;

import sg.edu.nus.comp.cs4218.app.CutInterface;
import sg.edu.nus.comp.cs4218.exception.CutException;
import sg.edu.nus.comp.cs4218.impl.app.args.CutArguments;
import sg.edu.nus.comp.cs4218.impl.util.IOUtils;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.*;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.STRING_NEWLINE;

public class CutApplication implements CutInterface {
    @Override
    public void run(String[] args, InputStream stdin, OutputStream stdout)
            throws CutException {
        if (args == null) {
            throw new CutException(ERR_NULL_ARGS);
        }
        if (stdout == null) {
            throw new CutException(ERR_NULL_STREAMS);
        }
        CutArguments cutArgs = new CutArguments();
        try {
            cutArgs.parse(args);
        } catch (Exception e) {
            throw new CutException(e.getMessage());
        }

        StringBuilder result = new StringBuilder();
        try {
            if (cutArgs.getFiles().isEmpty()) {
                result.append(cutFromStdin(cutArgs.isCharPo(), cutArgs.isBytePo(), cutArgs.getRanges(), stdin));
                result.append(STRING_NEWLINE);
            } else {
                for (String fileName : cutArgs.getFiles()) {
                    //  If a FILE is ‘-’, read standard input instead of file
                    if (fileName.equals("-")) {
                        result.append(cutFromStdin(cutArgs.isCharPo(), cutArgs.isBytePo(), cutArgs.getRanges(), stdin));
                        result.append(STRING_NEWLINE);
                    } else {
                        result.append(cutFromFiles(cutArgs.isCharPo(), cutArgs.isBytePo(),
                                cutArgs.getRanges(), fileName));
                        result.append(STRING_NEWLINE);
                    }
                }
            }
        } catch (Exception e) {
            throw new CutException(e.getMessage());
        }

        try {
            stdout.write(result.toString().getBytes());
        } catch (Exception e) {
            throw new CutException(ERR_WRITE_STREAM);
        }
    }

    @Override
    public String cutFromFiles(Boolean isCharPo, Boolean isBytePo, List<int[]> ranges, String... fileName)
            throws Exception {
        // TODO: Complete implementation
        return null;
    }

    @Override
    public String cutFromStdin(Boolean isCharPo, Boolean isBytePo, List<int[]> ranges, InputStream stdin)
            throws Exception {
        if (stdin == null) {
            throw new CutException(ERR_NULL_STREAMS);
        }
        List<String> lines = IOUtils.getLinesFromInputStream(stdin);
        return cutString(isCharPo, isBytePo, ranges, lines);
    }

    /**
     * Cuts out selected portions of each line
     * @param isCharPo Boolean option to cut by character position
     * @param isBytePo Boolean option to cut by byte position
     * @param ranges List of 2-element arrays containing the start and end indices for cut.
     * @param lines List of lines to be cut
     */
    private String cutString(Boolean isCharPo, Boolean isBytePo, List<int[]> ranges, List<String> lines) {
        StringBuilder result = new StringBuilder();
        for (String line : lines) {
            for (int[] range : ranges) {
                int start = range[0];
                int end = range[1];
                // TODO: Implement char vs byte cutting (might be useful to test against in hackathon)
                // https://askubuntu.com/questions/1102240/what-is-the-difference-between-cut-s-b-and-c-option
                if (isCharPo) {
                    result.append(line, start - 1, end);
                } else if (isBytePo) {
                    result.append(line, start - 1, end);
                }
            }
        }
        return result.toString();
    }
}
