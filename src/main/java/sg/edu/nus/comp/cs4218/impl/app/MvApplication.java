package sg.edu.nus.comp.cs4218.impl.app;

import sg.edu.nus.comp.cs4218.app.MvInterface;
import sg.edu.nus.comp.cs4218.exception.MvException;
import sg.edu.nus.comp.cs4218.exception.InvalidArgsException;
import sg.edu.nus.comp.cs4218.impl.parser.MvArgsParser;
import sg.edu.nus.comp.cs4218.impl.util.FileSystemUtils;

import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.ERR_FILE_NOT_FOUND;
import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.ERR_MISSING_ARG;
import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.ERR_NO_PERM;
import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.ERR_NULL_ARGS;
import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.ERR_NO_OSTREAM;



import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.AccessDeniedException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;


public class MvApplication implements MvInterface {

    @Override
    public String mvSrcFileToDestFile(Boolean isOverwrite, String srcFile, String destFile) throws MvException {
        String destFilePath = FileSystemUtils.getAbsolutePathName(destFile);

        // Check if the file tree permits the move
        if (FileSystemUtils.isSubDir(srcFile, destFile)) {
            throw new MvException(srcFile + " is the sub dir of " + destFile + " or they are the same file.");
        }
        if (FileSystemUtils.isFileInFolder(srcFile, destFile)) {
            throw new MvException("Cannot move file, as the source file is the parent of the destination file.");
        }

        try {
            // Delete the destination file if it already exists and overwrite is enabled
            if (isOverwrite && new File(destFilePath).exists()) {
                new File(destFile).delete();
            }

            // Move the file to the destination path
            Files.move(Paths.get(FileSystemUtils.getAbsolutePathName(srcFile)), Paths.get(destFilePath));

        } catch (NoSuchFileException e) {
            // If the source file does not exist
            throw new MvException(ERR_FILE_NOT_FOUND + ":" + e.getMessage(), e);

        } catch (FileAlreadyExistsException e) {
            // If the destination file already exists
            throw new MvException("Target file already exists: " + e.getMessage(), e);

        } catch (IOException e) {
            // If there is any other exception while moving the file
            throw new MvException(e.getMessage(), e);
        }

        return destFilePath;

    }

    @Override
    public String mvFilesToFolder(Boolean isOverwrite, String destFolder, String... fileNames) throws MvException {
        String destFolderPath = FileSystemUtils.getAbsolutePathName(destFolder);
        String destFilePath = null;
        try {
            for (String fileName : fileNames) {
                String srcFilePath = FileSystemUtils.getAbsolutePathName(fileName);
                String srcFileName = new File(srcFilePath).getName();

                // check if file can be moved to destination folder
                if (FileSystemUtils.isFileInFolder(srcFilePath, destFolderPath)) {
                    throw new MvException("Cannot move file: source file is at the same level or inside destination folder");
                }
                if (FileSystemUtils.isSubDir(srcFilePath, destFolderPath)) {
                    throw new MvException("Cannot move file: destination folder is nested in source file dir");
                }

                // prepare destination file path
                destFilePath = FileSystemUtils.joinPath(destFolderPath, srcFileName);
                File destFile = new File(destFilePath);

                // delete existing file if override is enabled
                if (isOverwrite && destFile.exists()) {
                    destFile.delete();
                }

                // move file to destination folder
                Files.move(Paths.get(srcFilePath), Paths.get(destFilePath));

            }
        } catch (FileAlreadyExistsException e) {
            throw new MvException("Cannot move file: a file with the same name already exists in destination folder", e);
        } catch (AccessDeniedException e) {
            throw new MvException(ERR_NO_PERM + ":" + e.getFile(), e);
        } catch (NoSuchFileException e) {
            throw new MvException(ERR_FILE_NOT_FOUND + ": " + e.getMessage(), e);
        } catch (IOException e) {
            throw new MvException(e.getMessage(), e);
        }
        return destFilePath;
    }

    @Override
    public void run(String[] args, InputStream stdin, OutputStream stdout) throws MvException {
        if (args == null) {
            throw new MvException(ERR_NULL_ARGS);
        }
        if (stdout == null) {
            throw new MvException(ERR_NO_OSTREAM);
        }

        // try parsing
        MvArgsParser mvArgsParser = new MvArgsParser();
        try {
            mvArgsParser.parse(args);
        } catch (InvalidArgsException e) {
            throw new MvException(e.getMessage(), e);
        }

        String[] toMoveFiles = mvArgsParser.getSourceFiles();
        if (toMoveFiles.length == 0) {
            throw new MvException(ERR_MISSING_ARG);
        }
        String destPath = mvArgsParser.getDestFile();
        boolean isOverwrite = mvArgsParser.shouldOverwrite();
        if (new File(FileSystemUtils.getAbsolutePathName(destPath)).isDirectory()) {
            mvFilesToFolder(isOverwrite, destPath, toMoveFiles);
        } else {
            if (toMoveFiles.length != 1) {
                throw new MvException(ERR_MISSING_ARG);
            }
            if (!isOverwrite && new File(destPath).exists()) {
                throw new MvException("Destination file '" + destPath + "' already exists and cannot be replaced.");
            }
            mvSrcFileToDestFile(isOverwrite, toMoveFiles[0], destPath);
        }

    }

}
