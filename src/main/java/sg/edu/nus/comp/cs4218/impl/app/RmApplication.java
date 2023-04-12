package sg.edu.nus.comp.cs4218.impl.app;

import sg.edu.nus.comp.cs4218.app.RmInterface;
import sg.edu.nus.comp.cs4218.exception.InvalidArgsException;
import sg.edu.nus.comp.cs4218.exception.RmException;
import sg.edu.nus.comp.cs4218.impl.parser.RmArgsParser;
import sg.edu.nus.comp.cs4218.impl.util.FileSystemUtils;

import java.io.InputStream;
import java.io.OutputStream;

import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.ERR_NO_FILE_ARGS;
import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.ERR_NO_OSTREAM;
import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.ERR_NULL_ARGS;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.CHAR_FILE_SEP;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.STRING_CURR_DIR;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.STRING_PARENT_DIR;

public class RmApplication implements RmInterface {

    @Override
    public void remove(Boolean isEmptyFolder, Boolean isRecursive, String... fileNames) throws RmException {

        if (fileNames == null || fileNames.length == 0 ) {
            throw new RmException(ERR_NO_FILE_ARGS);
        }

        if (isRecursive) {
            try {
                removeRecursive("", fileNames);
                return;
            } catch (Exception e) {
                throw new RmException(e.getMessage(), e);
            }
        }


        for (String name : fileNames) {
            // Check if file is a directory when no IS_EMPTY_FOLDER arg is provided
            if (!isEmptyFolder) {
                try {
                    if (FileSystemUtils.isDir(name)) {
                      throw new Exception(String.format("Cannot remove %s: Is a directory", name));
                    }
                } catch (Exception e) {
                    throw new RmException(e.getMessage(), e);
                }
            }

            // Check if file is an empty directory when  IS_EMPTY_FOLDER arg is provided
            try {
                if (FileSystemUtils.isDir(name) && !FileSystemUtils.isEmptyDir(name)) {
                    throw new Exception(String.format("Cannot remove %s: directory is not empty", name));
                }

                // Check if file name is . or ..
                if (name.equals(STRING_CURR_DIR) || name.equals(STRING_PARENT_DIR)) {
                    throw new Exception(String.format("refusing to remove '.' or '..' directory: skipping '%s'", name));
                }
                FileSystemUtils.deleteFileOrDir(name);
            } catch (Exception e) {
                throw new RmException(e.getMessage(), e);
            }
        }
    }

    @Override
    public void run(String[] args, InputStream stdin, OutputStream stdout)
            throws RmException {
        if (args == null) {
            throw new RmException(ERR_NULL_ARGS);
        }

        if (stdout == null) {
            throw new RmException(ERR_NO_OSTREAM);
        }

        RmArgsParser parser = new RmArgsParser();
        try {
            parser.parse(args);
        } catch (InvalidArgsException e) {
            throw new RmException(e.getMessage(), e);
        }
        boolean isEmptyFolder = parser.isRmEmptyDir();
        boolean isRecursive = parser.isRecursive();
        String[] filesOrDirs = parser.getFilesOrDirNames()
                .toArray(new String[0]);

        remove(isEmptyFolder, isRecursive, filesOrDirs);
    }

    /**
     * Deletes the provided files in relative current working directory. If file is a directory, the method recursively
     * traverse into the directory to delete its contents before deleting the directory.
     *
     * @param relativeCwd  The current working directory relative to where the method is first called
     * @param filenames  List of file names.
     */
    private void removeRecursive( String relativeCwd, String... filenames) throws Exception {
        for (String name : filenames) {
            String relativePath = relativeCwd + name;
            // Check if relative path is . or ..
            if (relativePath.equals(STRING_CURR_DIR) || relativePath.equals(STRING_PARENT_DIR)) {
                throw new Exception(String.format("refusing to remove '.' or '..' directory: skipping '%s'", relativePath));
            }
            // If directory, recurse to remove content within directory first
            if (FileSystemUtils.isDir(relativePath)) {
                String[] filenamesInDir = FileSystemUtils.getFilesInFolder(relativePath);
                removeRecursive(relativePath + CHAR_FILE_SEP,  filenamesInDir);
            }

            FileSystemUtils.deleteFileOrDir(relativePath);
        }
    }

}
