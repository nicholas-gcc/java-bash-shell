package sg.edu.nus.comp.cs4218.impl.app;

import sg.edu.nus.comp.cs4218.Environment;
import sg.edu.nus.comp.cs4218.app.CpInterface;
import sg.edu.nus.comp.cs4218.exception.AbstractApplicationException;
import sg.edu.nus.comp.cs4218.impl.app.args.CpArguments;
import sg.edu.nus.comp.cs4218.exception.CpException;

import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.*;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.CHAR_FILE_SEP;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.CHAR_FLAG_PREFIX;

import java.io.*;
import java.util.ArrayList;

public class CpApplication implements CpInterface {
    private static final char RECURSIVE = 'r';

    /**
     * Converts filename to absolute path, if initially was relative path
     *
     * @param fileName supplied by user
     * @return a String of the absolute path of the filename
     */
    private String convertToAbsolutePath(String fileName) {
        String home = System.getProperty("user.home").trim();
        String currentDir = Environment.currentDirectory.trim();
        String convertedPath = convertPathToSystemPath(fileName);

        String newPath;
        if (convertedPath.length() >= home.length() && convertedPath.substring(0, home.length()).trim().equals(home)) {
            newPath = convertedPath;
        } else {
            newPath = currentDir + CHAR_FILE_SEP + convertedPath;
        }
        return newPath;
    }

    /**
     * Converts path provided by user into path recognised by the system
     *
     * @param path supplied by user
     * @return a String of the converted path
     */
    private String convertPathToSystemPath(String path) {
        String convertedPath = path;
        String pathIdentifier = "\\" + Character.toString(CHAR_FILE_SEP);
        convertedPath = convertedPath.replaceAll("(\\\\)+", pathIdentifier);
        convertedPath = convertedPath.replaceAll("/+", pathIdentifier);

        if (convertedPath.length() != 0 && convertedPath.charAt(convertedPath.length() - 1) == CHAR_FILE_SEP) {
            convertedPath = convertedPath.substring(0, convertedPath.length() - 1);
        }

        return convertedPath;
    }

    /**
     * for file path/name containing "*" wildcard
     * retrieve filenames that qualify for the wildcard
     * 
     * @param srcFile String of source file
     * @return array of Files that fulfills pattern
     */
    private File[] getFilenamesWithPattern(String srcFile) {
        String[] arr = srcFile.split("\\*");
        String pattern = arr[1];
        String filename = arr[0];
        File src = new File(filename);

        return src.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith(pattern);
            }
        });
    }

    @Override
    public void run(String[] args, InputStream stdin, OutputStream stdout) throws AbstractApplicationException {
        try {
            CpArguments cpArgs = new CpArguments();
            ArrayList<String> files = new ArrayList<>();
            boolean isRecursive = cpArgs.getArguments(args, files);

            String srcFileName = convertToAbsolutePath(files.get(0));
            String destFileName = convertToAbsolutePath(files.get(1));
            File srcFile = new File(srcFileName);
            File destFile = new File(destFileName);

            cpArgs.checkFilesValidity(srcFile, destFile, isRecursive);

            if (destFile.isDirectory()) {
                cpFilesToFolder(isRecursive, destFileName, srcFileName);
            } else {
                cpSrcFileToDestFile(isRecursive, srcFileName, destFileName);
            }
        } catch (CpException cpException) {
            throw cpException;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public void cpSrcFileToDestFile(Boolean isRecursive, String srcFile, String destFile)
            throws CpException, IOException {
        File src = new File(srcFile);
        File dest = new File(destFile);
        if (srcFile.contains("*.")) {
            throw new CpException(destFile + " is " + ERR_IS_NOT_DIR);
        }

        if (!dest.exists()) {
            dest.createNewFile();
        }

        if (!src.exists()) {

            throw new CpException(srcFile + ": " + ERR_FILE_NOT_FOUND);
        }

        FileInputStream inputStream = new FileInputStream(src);
        FileOutputStream outputStream = new FileOutputStream(dest);

        int line;

        try {
            while ((line = inputStream.read()) != -1) {
                outputStream.write(line);
            }
        } catch (IOException ioexception) {
            throw ioexception;
        } finally {
            inputStream.close();
            outputStream.close();
        }
        return;
    }

    @Override
    public void cpFilesToFolder(Boolean isRecursive, String destFolder, String... fileName) throws Exception {
        String srcName = fileName[0];
        File dest = new File(destFolder);
        File src = new File(srcName);

        CpArguments cpArgs = new CpArguments();
        cpArgs.checkFilesValidity(src, dest, isRecursive);

        if (srcName.contains("*.")) {
            File[] filenames = getFilenamesWithPattern(srcName);
            for (File f : filenames) {
                cpFilesToFolder(isRecursive, destFolder, f.getPath());
            }
            return;
        }

        if (!src.exists()) {
            throw new CpException(srcName + ": " + ERR_FILE_NOT_FOUND);
        }

        if (!dest.exists()) {
            dest.mkdir();
        }
        if (dest.isFile()) {
            throw new CpException(destFolder + ": " + ERR_IS_NOT_DIR);
        }
        dest = new File(destFolder + "/" + src.getName());

        if (src.isFile()) {
            cpSrcFileToDestFile(isRecursive, srcName, dest.getPath());
            return;
        }
        if (!dest.exists()) {
            dest.mkdir();
        }
        for (String f : src.list()) {
            File source = new File(src, f);
            File destination = new File(dest, f);

            if (source.isDirectory()) {
                cpFilesToFolder(true, destination.getPath(), source.getPath());
            } else {
                cpSrcFileToDestFile(true, source.getPath(), destination.getPath());
            }
        }
        return;
    }

}
