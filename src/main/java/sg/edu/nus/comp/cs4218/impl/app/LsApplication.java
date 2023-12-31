package sg.edu.nus.comp.cs4218.impl.app;

import sg.edu.nus.comp.cs4218.Environment;
import sg.edu.nus.comp.cs4218.app.LsInterface;
import sg.edu.nus.comp.cs4218.exception.InvalidArgsException;
import sg.edu.nus.comp.cs4218.exception.LsException;
import sg.edu.nus.comp.cs4218.impl.parser.LsArgsParser;
import sg.edu.nus.comp.cs4218.impl.util.FileSystemUtils;
import sg.edu.nus.comp.cs4218.impl.util.StringUtils;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.ERR_NO_OSTREAM;
import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.ERR_NULL_ARGS;
import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.ERR_WRITE_STREAM;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.CHAR_FILE_SEP;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.STRING_CURR_DIR;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.STRING_NEWLINE;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.sortFilenamesByExt;
import static sg.edu.nus.comp.cs4218.impl.util.FileSystemUtils.resolvePath;
import static sg.edu.nus.comp.cs4218.impl.util.FileSystemUtils.getRelativeToCwd;

@SuppressWarnings({"PMD.GodClass", "PMD.LongVariable"})
public class LsApplication implements LsInterface {

    private final static String PATH_CURR_DIR = STRING_CURR_DIR + CHAR_FILE_SEP;
    private final static String EMPTY_FILE_STRING = "";

    @Override
    public void run(String[] args, InputStream stdin, OutputStream stdout)
            throws LsException {
        if (args == null) {
            throw new LsException(ERR_NULL_ARGS);
        }

        if (stdout == null) {
            throw new LsException(ERR_NO_OSTREAM);
        }

        LsArgsParser parser = new LsArgsParser();
        try {
            parser.parse(args);
        } catch (InvalidArgsException e) {
            throw new LsException(e.getMessage(), e);
        }

        Boolean recursive = parser.isRecursive();
        Boolean sortByExt = parser.isSortByExt();
        String[] filesOrDirs = parser.getFilesOrDirNames()
                .toArray(new String[parser.getFilesOrDirNames().size()]);
        String result = listFolderContent(recursive, sortByExt, filesOrDirs);

        try {
            stdout.write(result.getBytes());
            stdout.write(STRING_NEWLINE.getBytes());
        } catch (Exception e) {
            throw new LsException(ERR_WRITE_STREAM, e);
        }
    }

    @Override
    public String listFolderContent(Boolean isRecursive, Boolean isSortByExt,
                                    String... filesAndDirsNames) throws LsException {
        if (filesAndDirsNames.length == 0 && !isRecursive) {
            return listCwdContent(isSortByExt);
        }


        List<Path> paths;
        if (filesAndDirsNames.length == 0) {
            String[] directories = new String[1];
            directories[0] = Environment.currentDirectory;
            paths = resolvePaths(directories);
            return buildResultBasedOnDirPaths(paths, true, isSortByExt).trim();
        } else {
            // Extracts invalid files
            List<String> invalidFileMessages = extractInvalidFiles(filesAndDirsNames);

            // there are files/dir names in args
            paths = resolvePaths(filesAndDirsNames);

            // List empty string path as a file
            List<Path> filePaths = paths.stream().filter(path -> isEmptyPath(path) || !Files.isDirectory(path)).collect(Collectors.toList());
            // Does not add empty path as a directory as empty path is recognized as CWD
            List<Path> dirPaths = paths.stream().filter(path -> !isEmptyPath(path) && Files.isDirectory(path)).collect(Collectors.toList());
            String result = String.join(STRING_NEWLINE, invalidFileMessages) + STRING_NEWLINE
                    + buildResultBasedOnFilePaths(filePaths, isSortByExt) + buildResultBasedOnDirPaths(dirPaths, isRecursive, isSortByExt);
            return result.trim();
        }

    }

    /**
     * Checks each file for its validity as a file and extract the invalid files into error messages.
     *
     * @param filesAndDirsNames The names for file and directories
     * @return List of error messages for invalid files or directories.
     */
    private List<String> extractInvalidFiles(String... filesAndDirsNames) throws LsException {
        List<String> invalidFilesMessages = new ArrayList<>();
        for (int i = 0; i < filesAndDirsNames.length; i++) {
            // Standardizes file separators for Windows
            String file = filesAndDirsNames[i];
            String modifiedFilename = file.replaceAll("/", "\\" + File.separator);
            if (isEmptyFilename(modifiedFilename) || !FileSystemUtils.isExistingFilesInPath(modifiedFilename)) {
                invalidFilesMessages.add(String.format("ls: cannot access '%s': No such file or directory", file));
                // Replace invalid file in array with null
                filesAndDirsNames[i] = null;
                continue;
            }
            try {
                if (!FileSystemUtils.isValidDirsInPath(modifiedFilename)) {
                    invalidFilesMessages.add(String.format("ls: cannot access '%s': Not a directory", file));
                    filesAndDirsNames[i] = null;
                }
            } catch (Exception e) {
                throw new LsException(e.getMessage(), e);
            }

        }
        return invalidFilesMessages;
    }

    private boolean isEmptyFilename(String filename) {
        return filename.isBlank();
    }

    /**
     * Lists only the current directory's content and RETURNS. This does not account for recursive
     * mode in cwd.
     *
     * @param isSortByExt
     * @return
     */
    private String listCwdContent(Boolean isSortByExt) throws LsException {
        String cwd = Environment.currentDirectory;
        try {
            return formatContents(getContents(Paths.get(cwd)), isSortByExt);
        } catch (InvalidFileOrDirectoryException e) {
            throw new LsException("Unexpected error occurred!", e);
        }
    }

    /**
     * Builds the resulting string on file paths to be written into the output stream based.
     * <p>
     * NOTE: Does not need recursive boolean since these are not directory paths
     *
     * @param paths         - list of java.nio.Path objects that points to files to list
     * @param isSortByExt   - sorts files alphabetically by extension.
     * @return String to be written to output stream.
     */
    private String buildResultBasedOnFilePaths(List<Path> paths, Boolean isSortByExt) {
        if (paths.isEmpty()) {
            return EMPTY_FILE_STRING;
        }
        StringBuilder validResult = new StringBuilder();
        List<String> fileNames = new ArrayList<>();

        for (Path path : paths) {
            String relativeFilename = getRelativeToCwd(path).toString();
            // Standardizes file separators for Windows
            fileNames.add(relativeFilename);
        }
        if (isSortByExt) {
            sortFilenamesByExt(fileNames);
        }

        for (String fileName: fileNames) {
            validResult.append(fileName);
            validResult.append(STRING_NEWLINE);
        }
        // To prevent extra new line when there is no valid file names
        return fileNames.isEmpty() ? EMPTY_FILE_STRING : validResult + STRING_NEWLINE;
    }

        /**
         * Builds the resulting string based on directory paths to be written into the output stream.
         * <p>
         * NOTE: This is recursively called if user wants recursive mode.
         *
         * @param dirPaths      - list of java.nio.Path objects that points to directories to list
         * @param isRecursive   - recursive mode, repeatedly ls the child directories
         * @param isSortByExt - sorts folder contents alphabetically by file extension (characters after the last ‘.’ (without quotes)). Files with no extension are sorted first.
         * @return String to be written to output stream.
         */
    private String buildResultBasedOnDirPaths(List<Path> dirPaths, Boolean isRecursive, Boolean isSortByExt) {
        StringBuilder result = new StringBuilder();
        for (Path path : dirPaths) {
            try {
                List<Path> contents = getContents(path);
                String formatted = formatContents(contents, isSortByExt);
                String relativePath = getRelativeToCwd(path).toString();
                result.append(StringUtils.isBlank(relativePath) ? PATH_CURR_DIR : relativePath);
                result.append(':');
                result.append(STRING_NEWLINE);
                result.append(formatted);

                if (!formatted.isEmpty()) {
                    // Empty directories should not have an additional new line
                    result.append(STRING_NEWLINE);
                }
                result.append(STRING_NEWLINE);

                // RECURSE!
                if (isRecursive) {
                    result.append(buildResultBasedOnDirPaths(contents, isRecursive, isSortByExt));
                }
            } catch (InvalidFileOrDirectoryException e) {
                // NOTE: This is pretty hackish IMO - we should find a way to change this
                // If the user is in recursive mode, and if we resolve a file that isn't a directory
                // we should not spew the error message.
                //
                // However the user might have written a command like `ls invalid1 valid1 -R`, what
                // do we do then?
                if (!isRecursive) {
                    result.append(e.getMessage());
                    result.append(STRING_NEWLINE);
                }
            }
        }

        return result.toString();
    }


    /**
     * Formats the contents of a directory into a single string.
     *
     * @param contents    - list of items in a directory
     * @param isSortByExt - sorts folder contents alphabetically by file extension (characters after the last ‘.’ (without quotes)). Files with no extension are sorted first.
     * @return
     */
    private String formatContents(List<Path> contents, Boolean isSortByExt) {
        List<String> fileNames = new ArrayList<>();
        for (Path path : contents) {
            fileNames.add(path.getFileName().toString());
        }

        if (isSortByExt) {
            sortFilenamesByExt(fileNames);
        }

        StringBuilder result = new StringBuilder();
        for (String fileName : fileNames) {
            result.append(fileName);
            result.append(STRING_NEWLINE);
        }

        return result.toString().trim();
    }

    /**
     * Gets the contents in a single specified directory.
     *
     * @param directory
     * @return List of files + directories in the passed directory.
     */
    private List<Path> getContents(Path directory)
            throws InvalidFileOrDirectoryException {
        if (directory.toString().equals("")) {
            throw new InvalidFileOrDirectoryException(directory.toString());
        }

        if (!Files.exists(directory)) {
            throw new InvalidFileOrDirectoryException(getRelativeToCwd(directory).toString());
        }

        if (!Files.isDirectory(directory)) {
            throw new InvalidFileOrDirectoryException(getRelativeToCwd(directory).toString());
        }

        List<Path> result = new ArrayList<>();
        File pwd = directory.toFile();
        for (File f : pwd.listFiles()) {
            if (!f.isHidden()) {
                result.add(f.toPath());
            }
        }

        Collections.sort(result);

        return result;
    }

    private boolean isEmptyPath(Path path) {
        return path.toString().strip().equals("");
    }

    /**
     * Resolve all paths given as arguments into a list of Path objects for easy path management.
     * This method assumes that all the fileOrDirPaths provided are valid.
     *
     * @param fileOrDirPaths
     * @return List of java.nio.Path objects
     */
    private List<Path> resolvePaths(String... fileOrDirPaths) {
        List<Path> paths = new ArrayList<>();
        for (String fileOrDirPath : fileOrDirPaths) {
            if (fileOrDirPath == null) {
                continue;
            }
            paths.add(resolvePath(fileOrDirPath));
        }

        return paths;
    }


    private class InvalidFileOrDirectoryException extends Exception {
        InvalidFileOrDirectoryException(String fileOrDir) {
            super(String.format("ls: cannot access '%s': No such file or directory", fileOrDir));
        }
    }
}
