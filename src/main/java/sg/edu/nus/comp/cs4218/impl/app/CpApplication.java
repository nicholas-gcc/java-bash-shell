package sg.edu.nus.comp.cs4218.impl.app;

import sg.edu.nus.comp.cs4218.app.CpInterface;
import sg.edu.nus.comp.cs4218.exception.AbstractApplicationException;
import sg.edu.nus.comp.cs4218.impl.app.args.CpArguments;
import sg.edu.nus.comp.cs4218.impl.exception.CpException;

import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.*;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.CHAR_FLAG_PREFIX;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;


public class CpApplication implements CpInterface {
    private static final char RECURSIVE = 'r';

    /**
     * @param srcFile String of source file
     * @return array of Files that fulfills pattern
     */
    private File[] getFilenamesWithPattern(String srcFile){
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

            String srcFileName = files.get(0);
            String destFileName = files.get(1);
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
    public String cpSrcFileToDestFile(Boolean isRecursive, String srcFile, String destFile) throws CpException, IOException {
        File src = new File(srcFile);
        File dest = new File(destFile);
        if(srcFile.contains("*.")) {
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

        return null;
    }

    @Override
    public String cpFilesToFolder(Boolean isRecursive, String destFolder, String... fileName) throws Exception {
        String srcName = fileName[0];
        File dest = new File(destFolder);
        File src = new File(srcName);

        CpArguments cpArgs = new CpArguments();
        cpArgs.checkFilesValidity(src, dest, isRecursive);

        if(srcName.contains("*.")) {
            File[] filenames = getFilenamesWithPattern(srcName);
            for (File f: filenames) {
                cpFilesToFolder(isRecursive,destFolder , f.getPath());
            }
            return null;
        }

        if (!src.exists()) {
            throw new CpException(srcName + ": " + ERR_FILE_NOT_FOUND);
        }

        if (!dest.exists()) {
            dest.mkdir();
        }
        if (dest.isFile()){
             throw new CpException(destFolder + ": " + ERR_IS_NOT_DIR);
        }
        dest = new File(destFolder + "/" + src.getName());

        if (src.isFile()) {
            cpSrcFileToDestFile(isRecursive,srcName,dest.getPath());
            return null;
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
        return null;
    }

}
