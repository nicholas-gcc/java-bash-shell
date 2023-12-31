package sg.edu.nus.comp.cs4218.impl.app;

import sg.edu.nus.comp.cs4218.Environment;
import sg.edu.nus.comp.cs4218.app.UniqInterface;
import sg.edu.nus.comp.cs4218.exception.AbstractApplicationException;
import sg.edu.nus.comp.cs4218.exception.UniqException;
import sg.edu.nus.comp.cs4218.impl.app.args.UniqArguments;
import sg.edu.nus.comp.cs4218.impl.util.FileSystemUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.ERR_FILE_DIR_NOT_FOUND;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.CHAR_FILE_SEP;

public class UniqApplication implements UniqInterface {
    @Override
    public void run(String[] args, InputStream stdin, OutputStream stdout) throws AbstractApplicationException {
        UniqArguments uniqArgs = new UniqArguments();
        String[] fileNames = uniqArgs.getFiles(args);
        String result;
        try{
            if(fileNames[0] == null) { //uniq from stdin
                result = uniqFromStdin(uniqArgs.isCount(), uniqArgs.isRepeated(), uniqArgs.isAllRepeated(),
                        stdin, fileNames[1]);
            } else {
                result = uniqFromFile(uniqArgs.isCount(), uniqArgs.isRepeated(), uniqArgs.isAllRepeated(),
                        fileNames[0], fileNames[1]);
            }
            if (fileNames[1] == null) {//write to stdout
                stdout.write(result.getBytes());
            } else {
                if (!FileSystemUtils.fileOrDirExist(fileNames[1])) {
                    FileSystemUtils.createEmptyFile(fileNames[1]);
                }
                FileSystemUtils.writeStrToFile(false, result, fileNames[1]);
            }
        } catch (Exception exception) {
            throw new UniqException(exception.getMessage(), exception);
        }

    }

    @Override
    public String uniqFromFile(Boolean isCount, Boolean isRepeated, Boolean isAllRepeated, String inputFileName,
                               String outputFileName) throws Exception {
        File inputFile = new File(convertToAbsolutePath(inputFileName));
        if (!inputFile.isFile()) {
            throw new UniqException(ERR_FILE_DIR_NOT_FOUND);
        }
        BufferedReader reader = new BufferedReader(new FileReader(inputFile));
        String result;
        try {
            result = getResultFromReader(isCount, isRepeated, isAllRepeated, reader);
        } finally {
            reader.close();
        }
        return result;
    }

    @Override
    public String uniqFromStdin(Boolean isCount, Boolean isRepeated, Boolean isAllRepeated, InputStream stdin, String outputFileName) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(stdin));
        return getResultFromReader(isCount, isRepeated, isAllRepeated, reader);
    }

    /**
     * take in the options and a reader, and return the result that should be printed to stdout or file
     * @param isCount Whether option count is provided.
     * @param isRepeated Whether option repeat is provided.
     * @param isAllRepeated Whether option all repeat is provided.
     * @param reader BufferedReader, can be for either stdin or file reader
     * @return String result.
     * @throws IOException Thows IOExeption.
     */
    String getResultFromReader(Boolean isCount, Boolean isRepeated, Boolean isAllRepeated, BufferedReader reader) throws IOException {
        String result = "";
        int count = 1;

        String line;
        while ((line = reader.readLine()) != null) {
            String memorisedLine = "";
            while (line != null) {
                if (line.equals(memorisedLine)) { //line matches previous line
                    if(isAllRepeated) {
                        result = isCount ? result + count + " " + memorisedLine + System.lineSeparator()
                                : result + memorisedLine + System.lineSeparator();
                    }
                    count++;
                } else if (count > 1) {//line does not match previous line and previous line was repeated
                    result = isCount ? result + count + " " + memorisedLine + System.lineSeparator()
                            : result + memorisedLine + System.lineSeparator();
                    count = 1;
                } else {//line does not match previous line and previous line was NOT repeated
                    if (!isAllRepeated && !isRepeated && !memorisedLine.isEmpty()) {
                        result = isCount ? result + count + " " + memorisedLine + System.lineSeparator()
                                : result + memorisedLine + System.lineSeparator();
                    }
                    count = 1;
                }
                memorisedLine = line;
                line = reader.readLine();
            }
            if (!isRepeated && !isAllRepeated || count > 1) {
                result = isCount ? result + count + " " + memorisedLine + System.lineSeparator()
                        : result + memorisedLine + System.lineSeparator();

            }
        }
        reader.close();
        return result;
    }

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
        String pathIdentifier = "\\" + CHAR_FILE_SEP;
        convertedPath = convertedPath.replaceAll("(\\\\)+", pathIdentifier);
        convertedPath = convertedPath.replaceAll("/+", pathIdentifier);

        if (convertedPath.length() != 0 && convertedPath.charAt(convertedPath.length() - 1) == CHAR_FILE_SEP) {
            convertedPath = convertedPath.substring(0, convertedPath.length() - 1);
        }

        return convertedPath;
    }
}
