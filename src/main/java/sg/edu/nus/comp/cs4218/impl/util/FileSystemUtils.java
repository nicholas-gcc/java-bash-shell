package sg.edu.nus.comp.cs4218.impl.util;

import java.io.File;
import java.io.IOException;

public final class FileSystemUtils {

    private FileSystemUtils() {}
    public static void createFile(String filename) throws FileExistException, FileCreationException, IOException {
        File file = new File(filename);
        if (file.exists()) {
            throw new FileExistException(filename);
        }
        if (!file.createNewFile()) {
            throw new FileCreationException(filename);
        }
    }

    public static void deleteFile(String filename) throws FileDoesNotExistException, FileDeletionException {
        File file = new File(filename);
        if (!file.exists()) {
            throw new FileDoesNotExistException(filename);
        }

        if (!file.delete()) {
            throw new FileDeletionException(filename);
        }
    }

    private static class FileExistException extends Exception {
        public FileExistException(String filename) {
            super("File already exist: " + filename);
        }
    }

    private static class FileDoesNotExistException extends Exception {
        public FileDoesNotExistException(String filename) {
            super("File does not exist: " + filename);
        }
    }

    private static class FileCreationException extends Exception {
        public FileCreationException(String filename) {
            super("File already exist: " + filename);
        }
    }

    private static class FileDeletionException extends Exception {
        public FileDeletionException(String filename) {
            super("Failed to delete file: " + filename);
        }
    }
}


