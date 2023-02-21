package sg.edu.nus.comp.cs4218.impl.app;

import sg.edu.nus.comp.cs4218.app.CutInterface;
import sg.edu.nus.comp.cs4218.exception.AbstractApplicationException;
import sg.edu.nus.comp.cs4218.exception.CutException;
import sg.edu.nus.comp.cs4218.impl.util.IOUtils;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.ERR_NULL_ARGS;
import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.ERR_NULL_STREAMS;

public class CutApplication implements CutInterface {
    @Override
    public void run(String[] args, InputStream stdin, OutputStream stdout)
            throws AbstractApplicationException {
        if (args == null || stdout == null) {
            throw new CutException(ERR_NULL_ARGS);
        }
        // TODO: Complete CutArgument class and the rest of this function
//        CutArguments cutArgs = new CutArguments(args);
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
