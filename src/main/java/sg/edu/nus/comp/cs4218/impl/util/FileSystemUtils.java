package sg.edu.nus.comp.cs4218.impl.util;

import sg.edu.nus.comp.cs4218.Environment;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class FileSystemUtils {

    private FileSystemUtils() {}

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
     * Checks if file or directory exists in the current working directory.
     *
     * @param name  Name of file or directory
     * @return true if file or directory exist, else false
     */
    public static boolean fileOrDirExist(String name) {
        return new File(getAbsolutePathName(name)).exists();
    }

    /**
     * Creates a empty file in the current working directory
     *
     * @param filename  Name of file
     */
    public static void createEmptyFile(String filename) throws IOException, FileOrDirExistException, FileOrDirCreationException {
        String absolutePath = getAbsolutePathName(filename);
        if (fileOrDirExist(absolutePath)) {
            throw new FileOrDirExistException(filename);
        }

        File file = new File(absolutePath);

        if (!file.createNewFile()) {
            throw new FileOrDirCreationException(filename);
        }
    }

    /**
     * Deletes a file or empty directory in the current working directory
     *
     * @param name  Name of file or directory
     */
    public static void deleteFileOrDir(String name) throws FileOrDirDoesNotExistException, FileOrDirDeletionException {
        String absolutePath = getAbsolutePathName(name);
        File file = new File(absolutePath);
        if (!file.exists()) {
            throw new FileOrDirDoesNotExistException(name);
        }

        if (!file.delete()) {
            // Throws error if file is not deleted
            throw new FileOrDirDeletionException(name);
        }
    }

    /**
     * Creates a new directory in the current working directory
     *
     * @param dirname  Name of directory
     */
    public static void createEmptyDir(String dirname) throws FileOrDirExistException, FileOrDirCreationException, IOException {
        String absolutePath = getAbsolutePathName(dirname);
        if (fileOrDirExist(absolutePath)) {
            throw new FileOrDirExistException(dirname);
        }

        File file = new File(absolutePath);

        if (!file.mkdir()) {
            throw new FileOrDirCreationException(dirname);
        }
    }

    /**
     * Reads content of the file and return the content as a string
     *
     * @param filename  Name of file
     * @return content of the file
     */
    public static String readFileContent(String filename) throws FileOrDirExistException, IOException {
        String absolutePath = getAbsolutePathName(filename);
        if (fileOrDirExist(absolutePath)) {
            throw new FileOrDirExistException(filename);
        }
        return Files.readString(Paths.get(absolutePath));
    }

    /**
     * Appends String to an existing file
     *
     * @param filename  Name of file
     * @param str  String to be appended to content of file
     */
    public static void appendStrToFile(String filename, String str) throws FileOrDirExistException, IOException {
        String absolutePath = getAbsolutePathName(filename);
        if (fileOrDirExist(absolutePath)) {
            throw new FileOrDirExistException(filename);
        }
        Files.write(Paths.get(absolutePath), str.getBytes(), StandardOpenOption.APPEND);
    }

    /**
     * Checks if file is a directory.
     *
     * @param dirname Directory name.
     * @return true if file is a directory.
     */
    public static boolean isDir(String dirname) throws FileOrDirDoesNotExistException {
        String absolutePath = getAbsolutePathName(dirname);
        if (!fileOrDirExist(absolutePath)) {
            throw new FileOrDirDoesNotExistException(dirname);
        }
        return new File(absolutePath).isDirectory();
    }

    /**
     * Checks if file is an empty directory.
     *
     * @param dirname Directory name.
     * @return true if file is an empty directory.
     */
    public static boolean isEmptyDir(String dirname) throws FileOrDirDoesNotExistException {
        if (!isDir(dirname)) {
            return false;
        }
        String absolutePath = getAbsolutePathName(dirname);
        return Objects.requireNonNull(new File(absolutePath).listFiles()).length == 0;
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
     * Return list of filenames in directory.
     *
     * @param dirname Name of directory.
     * @return list of filenames in directory
     */
    public static String[] getFilesInFolder(String dirname) throws FileOrDirDoesNotExistException {
        if (!isDir(dirname)) {
            throw new FileOrDirDoesNotExistException(dirname);
        }
        String absolutePath = getAbsolutePathName(dirname);

        List<String> filenames = new ArrayList<>();

        if (isEmptyDir(dirname)) {
            return filenames.toArray(new String[0]);
        }

        for (File file : Objects.requireNonNull(new File(absolutePath).listFiles())) {
            filenames.add(file.getName());
        }
        return filenames.toArray(new String[0]);
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
            super(String.format("File or directory %s already exist", name));
        }
    }

    private static class FileOrDirDoesNotExistException extends Exception {
        public FileOrDirDoesNotExistException(String name) {
            super(String.format("File or directory %s does not exist", name));
        }
    }

    private static class FileOrDirCreationException extends Exception {
        public FileOrDirCreationException(String name) {
            super(String.format("Failed to create file or directory %s", name));
        }
    }

    private static class FileOrDirDeletionException extends Exception {
        public FileOrDirDeletionException(String name) {
            super(String.format("Failed to delete file or directory %s", name));
        }
    }
}


