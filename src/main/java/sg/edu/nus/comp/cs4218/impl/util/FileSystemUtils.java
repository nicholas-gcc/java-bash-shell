package sg.edu.nus.comp.cs4218.impl.util;

import sg.edu.nus.comp.cs4218.Environment;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.CHAR_FILE_SEP;

public final class FileSystemUtils {

    private FileSystemUtils() {}

    /**
     * Checks if file or directory exists in the current working directory
     *
     * @param name  Name of file or directory
     * @return true if file or directory exist, else false
     */
    public static boolean fileOrDirExist(String name) {
        return new File(name).exists();
    }

    /**
     * Creates an emptyfile in the current working directory
     *
     * @param filename  Name of file
     */
    public static void createEmptyFile(String filename) throws IOException, FileOrDirExistException, FileOrDirCreationException {
        if (fileOrDirExist(filename)) {
            throw new FileOrDirExistException(filename);
        }

        File file = new File(filename);

        if (!file.createNewFile()) {
            throw new FileOrDirCreationException(filename);
        }
    }

    /**
     * Deletes a file in the current working directory
     *
     * @param filename  Name of file
     */
    public static void deleteFile(String filename) throws FileOrDirDoesNotExistException, FileOrDirDeletionException {
        File file = new File(filename);
        if (!file.exists()) {
            throw new FileOrDirDoesNotExistException(filename);
        }

        if (!file.delete()) {
            throw new FileOrDirDeletionException(filename);
        }
    }

    /**
     * Creates a new empty directory in the current working directory
     *
     * @param dirname  Name of directory
     */
    public static void createEmptyDir(String dirname) throws FileOrDirExistException, FileOrDirCreationException, IOException {
        if (fileOrDirExist(dirname)) {
            throw new FileOrDirExistException(dirname);
        }

        File file = new File(dirname);

        if (!file.mkdir()) {
            throw new FileOrDirCreationException(dirname);
        }
    }

    /**
     * Deletes an empty directory in the current working directory.
     *
     * @param dirname  Name of directory
     */
    public static void deleteEmptyDir(String dirname) {
        // TODO: Implement method
    }

    /**
     * Recursively deletes a directory's content and the directory itself in the current working directory.
     *
     * @param dirname  Name of directory
     */
    public static void deleteDirRecursively(String dirname) {
        // TODO: Implement method
    }

    /**
     * Reads content of the file and return the content as a string
     *
     * @param filename  Name of file
     * @return content of the file
     */
    public static String readFileContent(String filename) throws FileOrDirExistException, IOException {
        if (fileOrDirExist(filename)) {
            throw new FileOrDirExistException(filename);
        }
        return Files.readString(Paths.get(Environment.currentDirectory + CHAR_FILE_SEP + filename));
    }

    /**
     * Appends String to an existing file
     *
     * @param filename  Name of file
     * @param str  String to be appended to content of file
     */
    public static void appendStrToFile(String filename, String str) throws FileOrDirExistException, IOException {
        if (fileOrDirExist(filename)) {
            throw new FileOrDirExistException(filename);
        }
        Files.write(Paths.get(Environment.currentDirectory + CHAR_FILE_SEP + filename), str.getBytes(), StandardOpenOption.APPEND);
    }

    private static class FileOrDirExistException extends Exception {
        public FileOrDirExistException(String name) {
            super("File or directory already exist: " + name);
        }
    }

    private static class FileOrDirDoesNotExistException extends Exception {
        public FileOrDirDoesNotExistException(String name) {
            super("File or directory does not exist: " + name);
        }
    }

    private static class FileOrDirCreationException extends Exception {
        public FileOrDirCreationException(String name) {
            super("Failed to create file or directory: " + name);
        }
    }

    private static class FileOrDirDeletionException extends Exception {
        public FileOrDirDeletionException(String name) {
            super("Failed to delete file or directory: " + name);
        }
    }
}


