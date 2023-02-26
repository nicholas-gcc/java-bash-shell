package sg.edu.nus.comp.cs4218.impl.app;

import sg.edu.nus.comp.cs4218.app.MvInterface;
import sg.edu.nus.comp.cs4218.exception.MvException;
import sg.edu.nus.comp.cs4218.exception.InvalidArgsException;
import sg.edu.nus.comp.cs4218.impl.parser.MvArgsParser;
import sg.edu.nus.comp.cs4218.impl.util.ErrorConstants;
import sg.edu.nus.comp.cs4218.impl.util.FileSystemUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.*;

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
            throw new MvException(ErrorConstants.ERR_FILE_NOT_FOUND + ":" + e.getMessage()); //NOPMD

        } catch (FileAlreadyExistsException e) {
            // If the destination file already exists
            throw new MvException("Target file already exists: " + e.getMessage()); //NOPMD

        } catch (IOException e) {
            // If there is any other exception while moving the file
            throw new MvException(e.getMessage()); //NOPMD
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
            throw (MvException) new MvException("Cannot move file: a file with the same name already exists in destination folder")
                    .initCause(e);
        } catch (AccessDeniedException e) {
            throw (MvException) new MvException(ErrorConstants.ERR_NO_PERM + ":" + e.getFile()).initCause(e);
        } catch (NoSuchFileException e) {
            throw (MvException) new MvException(ErrorConstants.ERR_FILE_NOT_FOUND + ":" + e.getMessage()).initCause(e);
        } catch (IOException e) {
            throw (MvException) new MvException(e.getMessage()).initCause(e);
        }
        return destFilePath;
    }

    @Override
    public void run(String[] args, InputStream stdin, OutputStream stdout) throws MvException {
        if (args == null) {
            throw new MvException(ErrorConstants.ERR_NULL_ARGS);
        }
        MvArgsParser mvArgsParser = new MvArgsParser();
        try {
            mvArgsParser.parse(args);
            String[] toMoveFiles = mvArgsParser.getSourceFiles();
            if (toMoveFiles.length == 0) {
                throw new InvalidArgsException(ErrorConstants.ERR_MISSING_ARG);
            }
            String destPath = mvArgsParser.getDestFile();
            boolean isOverwrite = mvArgsParser.shouldOverwrite();
            if (new File(FileSystemUtils.getAbsolutePathName(destPath)).isDirectory()) {
                mvFilesToFolder(isOverwrite, destPath, toMoveFiles);
            } else {
                if (toMoveFiles.length != 1) {
                    throw new InvalidArgsException(ErrorConstants.ERR_MISSING_ARG);
                }
                if (!isOverwrite && new File(destPath).exists()) {
                    throw new MvException("Destination file '" + destPath + "' already exists and cannot be replaced.");
                }
                mvSrcFileToDestFile(isOverwrite, toMoveFiles[0], destPath);
            }
        } catch (Exception e) {
            try {
                if (stdout == null) {
                    throw (MvException) new MvException("OutputStream cannot be null").initCause(e);
                }
                else {
                    stdout.write(e.getMessage().getBytes());
                    throw new MvException(e.getMessage()); //NOPMD
                }
            } catch (IOException ex) {
                throw (MvException) new MvException("Could not write to output stream").initCause(ex);
            }
        }
    }

}
