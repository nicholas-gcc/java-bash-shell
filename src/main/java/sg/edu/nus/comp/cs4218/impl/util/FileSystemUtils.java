package sg.edu.nus.comp.cs4218.impl.util;

import sg.edu.nus.comp.cs4218.Environment;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

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
     * Resolves absolute path of a file given the name
     *
     * @param name of file
     * @return String representing absolute path to file
     */
    public static String getAbsolutePathName(String name) {
        Path path = Paths.get(name).normalize();
        if (!path.isAbsolute()) {
            path = Paths.get(Environment.currentDirectory).resolve(path);
        }
        return path.toString();
    }

    /**
     * Creates a file in the current working directory
     *
     * @param filename  Name of file
     */
    public static void createFile(String filename) throws IOException, FileOrDirExistException, FileOrDirCreationException {
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
     * Creates a new directory in the current working directory
     *
     * @param dirname  Name of directory
     */
    public static void createDir(String dirname) throws FileOrDirExistException, FileOrDirCreationException, IOException {
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
     * Checks if a folder is in the other's subdirectory
     *
     * @param parentFolder
     * @param childFolder
     * @return true if childFolder is nested in parentFolder
     */
    public static boolean isSubDir(String parentFolder, String childFolder) {
        Path parentPath = Paths.get(FileSystemUtils.getAbsolutePathName(parentFolder)).normalize();
        Path childPath = Paths.get(FileSystemUtils.getAbsolutePathName(childFolder)).normalize();
        return childPath.startsWith(parentPath) && !childPath.equals(parentPath);
    }

    /**
     * Determines whether a file is located directly inside a folder.
     *
     * @param filePath The path to the file.
     * @param folderPath The path to the folder.
     * @return {@code true} if the file is directly inside the folder, {@code false} otherwise.
     */
    public static boolean isFileInFolder(String filePath, String folderPath) {
        Path file = Paths.get(getAbsolutePathName(filePath));
        Path folder = Paths.get(getAbsolutePathName(folderPath));
        Path parent = file.getParent();
        return parent != null && parent.equals(folder);
    }

    public static String joinPath(String... fileFolderName) {
        String separator = File.separator;
        String joinedPath = String.join(separator, fileFolderName);
        if (!joinedPath.endsWith(separator)) {
            joinedPath += separator;
        }
        return joinedPath;
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


