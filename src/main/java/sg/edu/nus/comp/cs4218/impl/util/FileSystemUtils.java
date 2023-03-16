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

import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.CHAR_FILE_SEP;

@SuppressWarnings("PMD.GodClass")
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
     * Creates a new directory in the current working directory
     *
     * @param dirname  Name of directory
     */
    public static void createEmptyDir(String dirname) throws FileOrDirExistException, FileOrDirCreationException {
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
            throw new FileOrDirDeletionException(name);
        }
    }

    /**
     * Reads content of the file and return the content as a string
     *
     * @param filename  Name of file
     * @return content of the file
     */
    public static String readFileContent(String filename) throws FileOrDirDoesNotExistException, ReadFileException  {
        String absolutePath = getAbsolutePathName(filename);
        if (!fileOrDirExist(absolutePath)) {
            throw new FileOrDirDoesNotExistException(filename);
        }
        try {
            return Files.readString(Paths.get(absolutePath));
        } catch (IOException e) {
            throw new ReadFileException(filename, e);
        }
    }

    /**
     * Appends String to an existing file.
     *
     * @param filename  Name of file
     * @param str  String to be appended to content of file
     */
    public static void writeStrToFile(boolean isAppend, String str, String filename) throws FileOrDirDoesNotExistException, WriteToFileException {
        String absolutePath = getAbsolutePathName(filename);
        if (!fileOrDirExist(absolutePath)) {
            throw new FileOrDirDoesNotExistException(filename);
        }
        try {
            Path path = Paths.get(absolutePath);
            if (isAppend) {
                Files.write(path, str.getBytes(), StandardOpenOption.APPEND);
            } else {
                Files.write(path, str.getBytes());
            }
        } catch (IOException e) {
            throw new WriteToFileException(filename, e);
        }
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

    /**
     * Return list of filenames in directory.
     *
     * @param dirname Name of directory.
     * @return list of filenames in directory
     */
    public static String[] getFilesInFolder(String dirname) throws FileOrDirDoesNotExistException, IsNotDirectoryException {
        String absolutePath = getAbsolutePathName(dirname);

        if (!fileOrDirExist(dirname)) {
            throw new FileOrDirDoesNotExistException(dirname);
        }
        if (!isDir(dirname)) {
            throw new IsNotDirectoryException(dirname);
        }

        List<String> filenames = new ArrayList<>();

        if (isEmptyDir(dirname)) {
            return filenames.toArray(new String[0]);
        }

        for (File file : Objects.requireNonNull(new File(absolutePath).listFiles())) {
            filenames.add(file.getName());
        }
        return filenames.toArray(new String[0]);
    }

    public static String joinPath(String... fileFolderName) {
        String separator = File.separator;
        String joinedPath = String.join(separator, fileFolderName);
        if (!joinedPath.endsWith(separator)) {
            joinedPath += separator;
        }
        return joinedPath;
    }

    /**
     * Converts a String into a java.nio.Path objects. Also resolves if the current path provided
     * is an absolute path.
     *
     * @param directory
     * @return
     */
    public static Path resolvePath(String directory) {
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
    public static Path getRelativeToCwd(Path path) {
        return Paths.get(Environment.currentDirectory).relativize(path);
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

    private static class WriteToFileException extends Exception {
        public WriteToFileException(String name) {
            super(String.format("Failed to write to file %s", name));
        }

        public WriteToFileException(String name, Throwable cause) {
            super(String.format("Failed to write to file %s", name));
        }
    }

    private static class ReadFileException extends Exception {
        public ReadFileException(String name) {
            super(String.format("Failed to read file %s", name));
        }

        public ReadFileException(String name, Throwable cause) {
            super(String.format("Failed to read file %s", name), cause);
        }
    }

    private static class IsNotDirectoryException extends Exception {
        public IsNotDirectoryException(String name) {
            super(String.format("%s is not a directory", name));
        }
    }
}


