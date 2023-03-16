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



import java.io.*;
import java.nio.file.FileVisitOption;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
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
            throw new CpException(e.getMessage());
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
            throw new CpException(e.getMessage());
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
            if (!Files.isDirectory(destFolderPath)) {
                throw new CpException(ERR_INVALID_ARG + ": destination file should be a directory");
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
                            new FileVisitor<Path>() {
                                // Create the target directory in the destination folder
                                @Override
                                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                                    Path targetDir = destFilePath.resolve(srcFilePath.relativize(dir));
                                    Files.createDirectories(targetDir);
                                    return FileVisitResult.CONTINUE;
                                }

                                // Copy each file to the destination folder. Called for each file in the file tree
                                @Override
                                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                                    Files.copy(file, destFilePath.resolve(srcFilePath.relativize(file)), StandardCopyOption.REPLACE_EXISTING);
                                    return FileVisitResult.CONTINUE;
                                }

                                @Override
                                public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
                                    throw exc;
                                }

                                @Override
                                public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                                    if (exc != null) {
                                        throw exc;
                                    }
                                    return FileVisitResult.CONTINUE;
                                }
                            });
                }
            }
            return null;
        } catch (Exception e) {
            throw new CpException(e.getMessage());
        }
    }
}
