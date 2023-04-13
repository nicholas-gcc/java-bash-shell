package sg.edu.nus.comp.cs4218.impl.app.args;

import sg.edu.nus.comp.cs4218.exception.AbstractApplicationException;
import sg.edu.nus.comp.cs4218.exception.CpException;

import java.io.File;
import java.util.ArrayList;

import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.ERR_IS_DIR;
import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.ERR_INVALID_FLAG;
import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.ERR_IS_NOT_DIR;
import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.ERR_MISSING_ARG;
import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.ERR_TOO_MANY_ARGS;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.CHAR_FLAG_PREFIX;

public class CpArguments {

    public boolean isRecursive;
    public static final char CHAR_RECURSIVE = 'r';
    private static final String NOT_COPIED = "(not copied)";

    public CpArguments() {
        this.isRecursive = false;
    }

    /**
     * retrieve file arguments (source and destination file)
     * @param args Arguments from stdin
     * @param files Empty arraylist, in which file arguments will be stored
     * @return boolean isRecursive
     * @throws AbstractApplicationException
     */
    public boolean getArguments(String[] args, ArrayList<String> files) throws AbstractApplicationException {
        boolean isFirstArg = true;

        for (String arg: args) {
            if (!isFirstArg) {
                files.add(arg);
                continue;
            }
            if (arg.charAt(0) == CHAR_FLAG_PREFIX) {
                if (arg.length() == 2 && Character.toLowerCase(arg.charAt(1)) == CHAR_RECURSIVE) {
                    isRecursive = true;
                    isFirstArg = false;
                } else {
                    throw new CpException(ERR_INVALID_FLAG);
                }
            } else {
                files.add(arg);
            }
        }

        if (files.size() < 2) {
            throw new CpException(ERR_MISSING_ARG);
        } else if (files.size() > 2) {
            throw new CpException(ERR_TOO_MANY_ARGS);
        }

        return isRecursive;
    }

    /**
     * check if the input source file, flag and output file are valid
     * throws error if:
     * - input contains multiple files or is a directory but output is file
     * - input is a folder but there is no -r/-R flag
     * @param src
     * @param dest
     * @param isRecursive
     * @throws CpException
     */
    public void checkFilesValidity(File src, File dest, boolean isRecursive) throws CpException {
        if (src.isDirectory() && !isRecursive) {
            throw new CpException(ERR_IS_DIR + NOT_COPIED);
        }
        if (src.isDirectory() && dest.isFile()) {
            throw new CpException(dest.getPath() + ": " + ERR_IS_NOT_DIR);
        }
    }

}
