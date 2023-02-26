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

            }
        } catch (Exception exception) {
            throw new UniqException(exception.getMessage());
        }

    }

    @Override
    public String uniqFromFile(Boolean isCount, Boolean isRepeated, Boolean isAllRepeated, String inputFileName, String outputFileName) throws Exception {
        File inputFile = new File(inputFileName);
        String result = "";
        int count = 1;
        if (!inputFile.isFile()) {
            throw new UniqException(ERR_FILE_NOT_FOUND);
        }
        BufferedReader reader = new BufferedReader(new FileReader(inputFile));
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
        if ((!isRepeated && !isAllRepeated && count == 1) || ((isAllRepeated || isRepeated) && count > 1)) {
            result = isCount ? result + count + " " + memorisedLine + System.lineSeparator()
                    : result + memorisedLine + System.lineSeparator();

        }
        return result;
    }

    @Override
    public String uniqFromStdin(Boolean isCount, Boolean isRepeated, Boolean isAllRepeated, InputStream stdin, String outputFileName) throws Exception {
        return null;
    }
}
