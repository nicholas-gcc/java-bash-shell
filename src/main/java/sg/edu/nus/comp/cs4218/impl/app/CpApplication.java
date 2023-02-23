package sg.edu.nus.comp.cs4218.impl.app;

import sg.edu.nus.comp.cs4218.app.CpInterface;
import sg.edu.nus.comp.cs4218.exception.AbstractApplicationException;
import sg.edu.nus.comp.cs4218.impl.exception.CpException;

import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.*;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.CHAR_FLAG_PREFIX;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;


public class CpApplication implements CpInterface {
    private static final char RECURSIVE = 'r';

    private boolean getArguments(String[] args, ArrayList<String> files) throws AbstractApplicationException {
        boolean isFirstArg = true;
        boolean isRecursive = false;

        for (String arg: args) {
            if (!isFirstArg) {
                files.add(arg);
                continue;
            }
            if (arg.charAt(0) == CHAR_FLAG_PREFIX) {
                if (arg.length() == 2 && Character.toLowerCase(arg.charAt(1)) == RECURSIVE) {
                    isRecursive = true;
                    isFirstArg = false;
                } else {
                    throw new CpException(ERR_SYNTAX);
                }
            } else {
                files.add(arg);
            }
        }

        return isRecursive;
    }

    @Override
    public void run(String[] args, InputStream stdin, OutputStream stdout) throws AbstractApplicationException {
        try {
            ArrayList<String> files = new ArrayList<>();
            boolean isRecursive = getArguments(args, files);

            for (String file: files) {
                BufferedReader reader = null;

            }
        } catch (CpException cpException) {
            throw cpException;
        }

    }

    @Override
    public String cpSrcFileToDestFile(Boolean isRecursive, String srcFile, String destFile) throws Exception {
        return null;
    }

    @Override
    public String cpFilesToFolder(Boolean isRecursive, String destFolder, String... fileName) throws Exception {
        return null;
    }
}
