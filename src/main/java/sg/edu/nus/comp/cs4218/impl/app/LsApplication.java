package sg.edu.nus.comp.cs4218.impl.app;

import sg.edu.nus.comp.cs4218.Environment;
import sg.edu.nus.comp.cs4218.app.LsInterface;
import sg.edu.nus.comp.cs4218.exception.InvalidArgsException;
import sg.edu.nus.comp.cs4218.exception.LsException;
import sg.edu.nus.comp.cs4218.impl.parser.LsArgsParser;
import sg.edu.nus.comp.cs4218.impl.util.StringUtils;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.*;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.*;

public class LsApplication implements LsInterface {

    private final static String PATH_CURR_DIR = STRING_CURR_DIR + CHAR_FILE_SEP;
    private final static String EMPTY_FILE_PATHS_STRING = "";
    @Override
    public String listFolderContent(Boolean isRecursive, Boolean isSortByExt,
                                    String... filesAndDirsNames) throws LsException {
        if (filesAndDirsNames.length == 0 && !isRecursive) {
            return listCwdContent(isSortByExt);
        }
        List<Path> paths;
        if (filesAndDirsNames.length == 0 && isRecursive) {
            String[] directories = new String[1];
            directories[0] = Environment.currentDirectory;
            paths = resolvePaths(directories);
            return buildResultBasedOnDirPaths(paths, isRecursive, isSortByExt).trim();
        } else {
            // there are files/dir names in args
            paths = resolvePaths(filesAndDirsNames);

            List<Path> filePaths = paths.stream().filter(path -> !Files.isDirectory(path)).collect(Collectors.toList());
            List<Path> dirPaths = paths.stream().filter(Files::isDirectory).collect(Collectors.toList());
            String result = buildResultBasedOnFilePaths(filePaths, isSortByExt) + buildResultBasedOnDirPaths(dirPaths, isRecursive, isSortByExt);
            return result.trim();
        }

    }

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
            throw new LsException(e.getMessage());//NOPMD
        }

        Boolean recursive = parser.isRecursive();
        Boolean sortByExt = parser.isSortByExt();
        String[] filesOrDirs = parser.getDirectories()
                .toArray(new String[parser.getDirectories().size()]);
        String result = listFolderContent(recursive, sortByExt, filesOrDirs);

        try {
            stdout.write(result.getBytes());
            stdout.write(StringUtils.STRING_NEWLINE.getBytes());
        } catch (Exception e) {
            throw new LsException(ERR_WRITE_STREAM);//NOPMD
        }
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
            throw new LsException("Unexpected error occurred!");//NOPMD
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
        if (paths.size() == 0) {
            return EMPTY_FILE_PATHS_STRING;
        }
        StringBuilder invalidResult = new StringBuilder();
        StringBuilder validResult = new StringBuilder();
        List<String> fileNames = new ArrayList<>();
        List<String> filesThatDoesNotExist = new ArrayList<>();

        for (Path path : paths) {
            if (Files.exists(path)) {
                fileNames.add(path.getFileName().toString());
            } else {
                filesThatDoesNotExist.add(path.getFileName().toString());
            }
        }
        if (isSortByExt) {
            sortFilenamesByExt(fileNames);
        }

        for (String nonexistentFile: filesThatDoesNotExist) {
            invalidResult.append(new InvalidFileOrDirectoryException(nonexistentFile).getMessage());
            invalidResult.append(STRING_NEWLINE);
        }

        // To prevent extra new line when there is no valid file names
        if (fileNames.size() == 0) {
            return invalidResult.toString();
        }

        for (String fileName: fileNames) {
            validResult.append(fileName);
            validResult.append(CHAR_SPACE);
        }

        return invalidResult + validResult.toString().trim() + STRING_NEWLINE;
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
                    result.append(StringUtils.STRING_NEWLINE);
                }
                result.append(StringUtils.STRING_NEWLINE);

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
     * Sorts the given fileNames by extension. If the filename has no extension, sort it first.
     *
     * @param fileNames    - list of filenames
     * @return
     */
    private void sortFilenamesByExt(List<String> fileNames) {
        Collections.sort(fileNames, new Comparator<String>() {
            @Override
            public int compare(String file1, String file2) {
                final int f1Dot = file1.lastIndexOf('.');
                final int f2Dot = file2.lastIndexOf('.');
                if ((f1Dot == -1) == (f2Dot == -1)) {
                    String formattedF1 = file1.substring(f1Dot + 1);
                    String formattedF2 = file2.substring(f2Dot + 1);
                    return formattedF1.compareTo(formattedF2);
                } else if (f1Dot == -1) {
                    return -1;
                } else {
                    return 1;
                }
            }
        });
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

    /**
     * Resolve all paths given as arguments into a list of Path objects for easy path management.
     *
     * @param fileOrDirectoriesPaths
     * @return List of java.nio.Path objects
     */
    private List<Path> resolvePaths(String... fileOrDirectoriesPaths) {
        List<Path> paths = new ArrayList<>();
        for (int i = 0; i < fileOrDirectoriesPaths.length; i++) {
            paths.add(resolvePath(fileOrDirectoriesPaths[i]));
        }

        return paths;
    }

    /**
     * Converts a String into a java.nio.Path objects. Also resolves if the current path provided
     * is an absolute path.
     *
     * @param directory
     * @return
     */
    private Path resolvePath(String directory) {
        // To account for absolute paths for Mac/Linux systems
        if (directory.charAt(0) == CHAR_FILE_SEP ||
                // To account for absolute paths for Windows systems
                (directory.length() > 1 && directory.charAt(1) == ':')) {
            // This is an absolute path
            return Paths.get(directory).normalize();
        }

        return Paths.get(Environment.currentDirectory, directory).normalize();
    }

    /**
     * Converts a path to a relative path to the current directory.
     *
     * @param path
     * @return
     */
    private Path getRelativeToCwd(Path path) {
        return Paths.get(Environment.currentDirectory).relativize(path);
    }

    public class InvalidFileOrDirectoryException extends Exception {
        InvalidFileOrDirectoryException(String fileOrDir) {
            super(String.format("ls: cannot access '%s': No such file or directory", fileOrDir));
        }

        InvalidFileOrDirectoryException(String directory, Throwable cause) {
            super(String.format("ls: cannot access '%s': No such file or directory", directory),
                    cause);
        }
    }
}
