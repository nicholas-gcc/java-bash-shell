package sg.edu.nus.comp.cs4218.impl.app;

import sg.edu.nus.comp.cs4218.app.CutInterface;
import sg.edu.nus.comp.cs4218.exception.CutException;
import sg.edu.nus.comp.cs4218.impl.app.args.CutArguments;
import sg.edu.nus.comp.cs4218.impl.util.IOUtils;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
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
            throw new CutException(e.getMessage(), e);
        }

        StringBuilder result = new StringBuilder();
        if (cutArgs.getFiles().isEmpty()) {
            if (stdin == null) {
                throw new CutException(ERR_NULL_STREAMS);
            }
            result.append(cutFromStdin(cutArgs.isCharPo(), cutArgs.isBytePo(), cutArgs.getRanges(), stdin));
        } else {
            for (String fileName : cutArgs.getFiles()) {
                //  If a FILE is ‘-’, read standard input instead of file
                if ("-".equals(fileName)) {
                    result.append(cutFromStdin(cutArgs.isCharPo(), cutArgs.isBytePo(), cutArgs.getRanges(), stdin));
                } else {
                    result.append(cutFromFiles(cutArgs.isCharPo(), cutArgs.isBytePo(),
                            cutArgs.getRanges(), fileName));
                }
            }
        }

        try {
            stdout.write(result.toString().getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            throw new CutException(ERR_WRITE_STREAM, e);
        }
    }

    @Override
    public String cutFromFiles(Boolean isCharPo, Boolean isBytePo, List<int[]> ranges, String... fileName)
            throws CutException {
        if (fileName == null) {
            throw new CutException(ERR_NULL_ARGS);
        }
        List<String> lines = new ArrayList<>();
        for (String fileString : fileName) {
            File file = IOUtils.resolveFilePath(fileString).toFile();
            if (!file.exists()) {
                throw new CutException(ERR_FILE_DIR_NOT_FOUND);
            }
            if (file.isDirectory()) {
                throw new CutException(ERR_IS_DIR);
            }
            if (!file.canRead()) {
                throw new CutException(ERR_NO_PERM);
            }
            try (InputStream fileStream = IOUtils.openInputStream(fileString)) {
                lines.addAll(IOUtils.getLinesFromInputStream(fileStream));
                IOUtils.closeInputStream(fileStream);
            } catch (Exception e) {
                throw new CutException(ERR_IO_EXCEPTION, e);
            }
        }
        return cutString(isCharPo, isBytePo, ranges, lines);
    }

    @Override
    public String cutFromStdin(Boolean isCharPo, Boolean isBytePo, List<int[]> ranges, InputStream stdin)
            throws CutException {
        if (stdin == null) {
            throw new CutException(ERR_NULL_STREAMS);
        }
        try {
            List<String> lines = IOUtils.getLinesFromInputStream(stdin);
            return cutString(isCharPo, isBytePo, ranges, lines);
        } catch (Exception e) {
            throw new CutException(e.getMessage(), e);
        }
    }

    /**
     * Cuts out selected portions of each line
     * @param isCharPo Boolean option to cut by character position
     * @param isBytePo Boolean option to cut by byte position
     * @param ranges List of 2-element arrays containing the start and end indices for cut.
     * @param lines List of lines to be cut
     */
    private String cutString(Boolean isCharPo, Boolean isBytePo, List<int[]> ranges, List<String> lines) throws CutException {
        StringBuilder result = new StringBuilder();
        for (String line : lines) {
            for (int[] range : ranges) {
                int start = range[0];
                int end = range[1];

                // https://askubuntu.com/questions/1102240/what-is-the-difference-between-cut-s-b-and-c-option
                if (isCharPo) {
                    // If range is invalid based on string length
                    if (end > line.length()) {
                        throw new CutException(ERR_OUT_OF_BOUNDS);
                    }
                    result.append(line, start - 1, end);
                } else if (isBytePo) {
                    byte[] bytes = line.getBytes(StandardCharsets.UTF_8);
                    // If range is invalid based on byte length
                    if (end > bytes.length) {
                        throw new CutException(ERR_OUT_OF_BOUNDS);
                    }
                    byte[] slicedBytes = Arrays.copyOfRange(bytes, start - 1, end);
                    String slicedStr = new String(slicedBytes);
                    result.append(slicedStr);
                }
            }
            result.append(STRING_NEWLINE);
        }
        return result.toString();
    }
}
