package sg.edu.nus.comp.cs4218.impl.util;

import sg.edu.nus.comp.cs4218.Environment;
import sg.edu.nus.comp.cs4218.exception.ShellException;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.ERR_CLOSING_STREAMS;
import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.ERR_FILE_NOT_FOUND;

public final class IOUtils {
    private IOUtils() {
    }

    /**
     * Open an inputStream based on the file name.
     *
     * @param fileName String containing file name.
     * @return InputStream of file opened.
     * @throws ShellException If file destination is inaccessible.
     */
    public static InputStream openInputStream(String fileName) throws ShellException {
        String resolvedFileName = resolveFilePath(fileName).toString();

        FileInputStream fileInputStream;
        try {
            fileInputStream = new FileInputStream(new File(resolvedFileName));
        } catch (FileNotFoundException e) {
            throw new ShellException(ERR_FILE_NOT_FOUND, e);
        }

        return fileInputStream;
    }

    /**
     * Open an outputStream based on the file name.
     *
     * @param fileName String containing file name.
     * @return OutputStream of file opened.
     * @throws ShellException If file destination is inaccessible.
     */
    public static OutputStream openOutputStream(String fileName) throws ShellException, FileNotFoundException {
        String resolvedFileName = resolveFilePath(fileName).toString();

        FileOutputStream fileOutputStream;

        fileOutputStream = new FileOutputStream(new File(resolvedFileName));

        return fileOutputStream;
    }

    /**
     * Close an inputStream. If inputStream provided is System.in or null, it will be ignored.
     *
     * @param inputStream InputStream to be closed.
     * @throws ShellException If inputStream cannot be closed successfully.
     */
    public static void closeInputStream(InputStream inputStream) throws ShellException {
        if (inputStream == null || inputStream.equals(System.in)) {
            return;
        }

        try {
            inputStream.close();
        } catch (IOException e) {
            throw new ShellException(ERR_CLOSING_STREAMS, e);
        }
    }

    /**
     * Close an outputStream. If outputStream provided is System.out or null, it will be ignored.
     *
     * @param outputStream OutputStream to be closed.
     * @throws ShellException If outputStream cannot be closed successfully.
     */
    public static void closeOutputStream(OutputStream outputStream) throws ShellException {
        if (outputStream == null || outputStream.equals(System.out)) {
            return;
        }

        try {
            outputStream.close();
        } catch (IOException e) {
            throw new ShellException(ERR_CLOSING_STREAMS, e);
        }
    }

    public static Path resolveFilePath(String fileName) {
        Path currentDirectory = Paths.get(Environment.currentDirectory);
        return currentDirectory.resolve(fileName);
    }

    /**
     * Returns a list of lines based on the given InputStream.
     *
     * @param input InputStream containing arguments from System.in or FileInputStream
     * @throws Exception
     */
    public static List<String> getLinesFromInputStream(InputStream input) throws Exception {
        List<String> output = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new InputStreamReader(input));
        String line;
        while ((line = reader.readLine()) != null) {
            output.add(line);
        }
        reader.close();
        return output;
    }

    public static String convertStreamToString(InputStream inputStream) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            stringBuilder.append(line).append(System.lineSeparator());
        }
        return stringBuilder.toString();
    }
}
