package sg.edu.nus.comp.cs4218.impl.app;

import sg.edu.nus.comp.cs4218.app.UniqInterface;
import sg.edu.nus.comp.cs4218.exception.AbstractApplicationException;
import sg.edu.nus.comp.cs4218.exception.UniqException;
import sg.edu.nus.comp.cs4218.impl.app.args.UniqArguments;

import java.io.*;

import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.*;

public class UniqApplication implements UniqInterface {
    static final UniqArguments uniqArgs = new UniqArguments();
    @Override
    public void run(String[] args, InputStream stdin, OutputStream stdout) throws AbstractApplicationException {
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
                File outputFile = new File(fileNames[1]);
                if (!outputFile.exists()) {
                    outputFile.createNewFile();
                }
                FileWriter inputWriter = null;
                try {
                    inputWriter = new FileWriter(fileNames[1]);
                    inputWriter.write(result);
                } catch (IOException ioException) {
                    throw ioException;
                } finally {
                    inputWriter.close();
                }
            }
        } catch (Exception exception) {
            throw new UniqException(exception.getMessage());
        }

    }

    @Override
    public String uniqFromFile(Boolean isCount, Boolean isRepeated, Boolean isAllRepeated, String inputFileName,
                               String outputFileName) throws Exception {
        File inputFile = new File(inputFileName);
        if (!inputFile.isFile()) {
            throw new UniqException(ERR_FILE_NOT_FOUND);
        }
        BufferedReader reader = new BufferedReader(new FileReader(inputFile));
        return getResultFromReader(isCount, isRepeated, isAllRepeated, reader);
    }

    @Override
    public String uniqFromStdin(Boolean isCount, Boolean isRepeated, Boolean isAllRepeated, InputStream stdin, String outputFileName) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(stdin));
        return getResultFromReader(isCount, isRepeated, isAllRepeated, reader);
    }

    String getResultFromReader(Boolean isCount, Boolean isRepeated, Boolean isAllRepeated, BufferedReader reader) throws IOException {
        String result = "";
        int count = 1;
        String line = reader.readLine();
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
        if ((!isRepeated && !isAllRepeated) || ((isAllRepeated || isRepeated) && count > 1)) {
            result = isCount ? result + count + " " + memorisedLine + System.lineSeparator()
                    : result + memorisedLine + System.lineSeparator();

        }
        return result;
    }
}
