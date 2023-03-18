package sg.edu.nus.comp.cs4218.impl.app;

import sg.edu.nus.comp.cs4218.app.CpInterface;
import sg.edu.nus.comp.cs4218.exception.AbstractApplicationException;
import sg.edu.nus.comp.cs4218.exception.CpException;
import sg.edu.nus.comp.cs4218.exception.InvalidArgsException;
import sg.edu.nus.comp.cs4218.impl.parser.CpArgsParser;
import sg.edu.nus.comp.cs4218.impl.util.FileSystemUtils;

import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.ERR_FILE_NOT_FOUND;
import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.ERR_INVALID_ARG;
import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.ERR_IS_DIR;
import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.ERR_MISSING_ARG;
import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.ERR_NULL_ARGS;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;

import java.nio.file.FileAlreadyExistsException;
import java.nio.file.FileVisitOption;
import java.nio.file.FileVisitResult;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.EnumSet;

public class CpApplication implements CpInterface {
    @Override
    public void run(String[] args, InputStream stdin, OutputStream stdout) throws AbstractApplicationException {
        if (args == null) {
            throw new CpException(ERR_NULL_ARGS);
        }

        CpArgsParser parser = new CpArgsParser();

        try {
            parser.parse(args);
        } catch (InvalidArgsException e) {
            throw new CpException(e.getMessage(), e);
        }

        boolean isRecursive = parser.isRecursive();
        String[] filesToCopy = parser.getSourceFiles();
        String dest = parser.getDestFileOrFolder();

        try {
            // no source file identified
            if (filesToCopy.length < 1) {
                throw new CpException(ERR_MISSING_ARG);
            } else if (filesToCopy.length == 1) {
                // case: only one source and one destination
                String sourceFileName = filesToCopy[0];

                Path sourcePath = FileSystemUtils.resolvePath(sourceFileName);
                Path destinationPath = FileSystemUtils.resolvePath(dest);

                // check if copying file to folder
                if (Files.isDirectory(sourcePath) || Files.isDirectory(destinationPath)) {
                    cpFilesToFolder(isRecursive, dest, sourceFileName);
                } else { // check if copying file contents to another file
                    cpSrcFileToDestFile(isRecursive, sourceFileName, dest);
                }
            } else {
                // case: copy contents of multiple files to a folder
                cpFilesToFolder(isRecursive, dest, filesToCopy);
            }
        } catch (CpException e) {
            throw e;
        } catch (Exception e) {
            throw new CpException(e.getMessage(), e);
        }

    }

    @Override
    public String cpSrcFileToDestFile(Boolean isRecursive, String srcFile, String destFile)
            throws CpException, IOException {
        if (srcFile == null || srcFile.isEmpty()) {
            throw new CpException(ERR_NULL_ARGS);
        }
        if (srcFile.equals(destFile)) {
            throw new CpException(ERR_INVALID_ARG + ": Cannot copy file to itself");
        }

        Path srcFilePath = FileSystemUtils.resolvePath(srcFile);
        Path destFilePath = FileSystemUtils.resolvePath(destFile);

        if (Files.notExists(srcFilePath)) {
            throw new CpException(ERR_FILE_NOT_FOUND);
        }
        if (!Files.isRegularFile(srcFilePath)) {
            throw new CpException(ERR_INVALID_ARG + ": source file is a directory");
        }

        Files.copy(srcFilePath, destFilePath, StandardCopyOption.REPLACE_EXISTING);

        return null;
    }

    @Override
    public String cpFilesToFolder(Boolean isRecursive, String destFolder, String... fileNames) throws Exception {
        try {
            Path destFolderPath = FileSystemUtils.resolvePath(destFolder);

            // Check if the destination path exists
            if (Files.exists(destFolderPath)) {
                // Check if the destination path is a directory
                if (!Files.isDirectory(destFolderPath)) {
                    throw new CpException(ERR_INVALID_ARG + ": destination file should be a directory");
                }
            } else {
                // Create the destination directory if it doesn't exist
                Files.createDirectories(destFolderPath);
            }

            // Copy each file to the destination folder
            for (String fileName : fileNames) {
                if (fileName == null || fileName.isEmpty()) {
                    throw new CpException(ERR_NULL_ARGS);
                }

                Path srcFilePath = FileSystemUtils.resolvePath(fileName);
                if (Files.notExists(srcFilePath)) {
                    throw new CpException(ERR_FILE_NOT_FOUND + ": " + fileName);
                }

                // Do not copy recursively to subdirectories if -r flag is absent and file encountered is directory
                if (Files.isDirectory(srcFilePath) && !isRecursive) {
                    throw new CpException(ERR_IS_DIR + ": " + fileName);
                }

                Path destFilePath = destFolderPath.resolve(srcFilePath.getFileName());
                if (Files.isRegularFile(srcFilePath) || isRecursive) {
                    Files.copy(srcFilePath, destFilePath, StandardCopyOption.REPLACE_EXISTING);
                }

                // If recursive copy is enabled, copy any subdirectories and files within the source directory
                if (isRecursive) {
                    Files.walkFileTree(srcFilePath, EnumSet.of(FileVisitOption.FOLLOW_LINKS), Integer.MAX_VALUE,
                            new CopyFileVisitor(srcFilePath, destFilePath));
                }
            }
            return null;
        } catch (IOException e) {
            throw new CpException(e.getMessage(), e);
        }
    }

    private static class CopyFileVisitor extends SimpleFileVisitor<Path> {
        private final Path srcPath;
        private final Path destPath;

        public CopyFileVisitor(Path srcPath, Path destPath) {
            this.srcPath = srcPath;
            this.destPath = destPath;
        }

        /**
         * Invoked for a directory before entries in the directory are visited.
         * This method copies the directory to the destination path and creates any missing directories along the way.
         *
         * @param dir  The directory being visited
         * @param attrs The basic attributes of the directory
         * @return FileVisitResult.CONTINUE to continue visiting the directory
         * @throws IOException If there was an error copying the directory or creating missing directories
         */
        @Override
        public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs)
                throws IOException {
            Path targetDir = destPath.resolve(srcPath.relativize(dir));
            try {
                // Copy the current directory to the destination
                Files.copy(dir, targetDir, StandardCopyOption.REPLACE_EXISTING);
            } catch (FileAlreadyExistsException e) { // If the copied file already exists, check if it's a directory and continue
                if (!Files.isDirectory(targetDir)) {
                    throw e;
                }
            }
            return FileVisitResult.CONTINUE;
        }

        /**
         * Invoked for a file in a directory.
         * This method copies the file to the destination directory.
         *
         * @param file The file being visited
         * @param attrs The basic attributes of the file
         * @return FileVisitResult.CONTINUE to continue visiting the file
         * @throws IOException If there was an error copying the file
         */
        @Override
        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs)
                throws IOException {
            Path targetFile = destPath.resolve(srcPath.relativize(file));
            Files.copy(file, targetFile, StandardCopyOption.REPLACE_EXISTING);
            return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult visitFileFailed(Path file, IOException exc)
                throws IOException {
            // log or throw an exception, depending on the requirements
            return FileVisitResult.CONTINUE;
        }
    }

}