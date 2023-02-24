package sg.edu.nus.comp.cs4218.impl.util;

import java.io.File;
import java.io.IOException;

public final class FileSystemUtils {

    private FileSystemUtils() {}

    /**
     * Creates a file in the current working directory
     *
     * @param filename  Name of file
     */

    public static boolean fileOrDirExist(String filename) {
        return new File(filename).exists();
    }

    public static void createFile(String filename) throws IOException, FileOrDirExistException, FileOrDirCreationException {
        if (fileOrDirExist(filename)) {
            throw new FileOrDirExistException(filename);
        }

        File file = new File(filename);

        if (!file.createNewFile()) {
            throw new FileOrDirCreationException(filename);
        }
    }

    public static void deleteFile(String filename) throws FileOrDirDoesNotExistException, FileOrDirDeletionException {
        if (!fileOrDirExist(filename)) {
            throw new FileOrDirDoesNotExistException(filename);
        }

    /**
     * Deletes a file in the current working directory
     *
     * @param filename  Name of file
     */
    public static void deleteFile(String filename) throws FileDoesNotExistException, FileDeletionException {
        File file = new File(filename);
        if (!file.exists()) {
            throw new FileDoesNotExistException(filename);
        }

        if (!file.delete()) {
            throw new FileOrDirDeletionException(filename);
        }
    }

    public static void createDir(String dirname) throws FileOrDirExistException, FileOrDirCreationException, IOException {
        if (fileOrDirExist(dirname)) {
            throw new FileOrDirExistException(dirname);
        }

        File file = new File(dirname);

        if (!file.mkdir()) {
            throw new FileOrDirCreationException(dirname);
        }
    }

    // TODO: Create a util method to recursively delete directories

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


