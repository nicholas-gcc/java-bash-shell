package sg.edu.nus.comp.cs4218.impl.app;

import sg.edu.nus.comp.cs4218.app.CpInterface;
import sg.edu.nus.comp.cs4218.exception.AbstractApplicationException;
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
    private static final String NOT_COPIED = "(not copied)";

    private boolean getArguments(String[] args, ArrayList<String> files) throws AbstractApplicationException {
        boolean isFirstArg = true;
        boolean isRecursive = false;

        for (String arg: args) {
            if (!isFirstArg) {
                files.add(arg);
                continue;
            }
            if (arg.charAt(0) == CHAR_FLAG_PREFIX) {
                if (arg.length() == 2 && Character.toLowerCase(arg.charAt(1)) == RECURSIVE) {
                    isRecursive = true;
                    isFirstArg = false;
                } else {
                    throw new CpException(ERR_INVALID_FLAG);
                }
            } else {
                files.add(arg);
            }
        }

        return isRecursive;
    }

    private File[] getFilenamesWithPattern(String srcFile){
        String[] arr = srcFile.split("\\*");
        String pattern = arr[1];
        srcFile = arr[0];
        File src = new File(srcFile);

        File[] filenames = src.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith(pattern);
            }
        });
        return filenames;
    }

    @Override
    public void run(String[] args, InputStream stdin, OutputStream stdout) throws AbstractApplicationException {
        try {
            ArrayList<String> files = new ArrayList<>();
            boolean isRecursive = getArguments(args, files);

            if (files.size() < 2) {
                CpException e = new CpException(ERR_MISSING_ARG);
                throw e;
            } else if (files.size() > 2) {
                throw new CpException(ERR_TOO_MANY_ARGS);
            }

            String srcFileName = files.get(0);
            String destFileName = files.get(1);
            File srcFile = new File(srcFileName);
            File destFile = new File(destFileName);

            if (srcFile.isDirectory() && !isRecursive) {
                throw new CpException(ERR_IS_DIR + NOT_COPIED);
            }

            if (srcFile.isDirectory() && destFile.isFile()) {
                throw new CpException(destFileName + ": " + ERR_IS_NOT_DIR);
            }

            if (destFile.isDirectory()) {
                if (srcFile.isDirectory()) {
                    File[] subfiles = srcFile.listFiles();
                    ArrayList<String> filenames = new ArrayList<String>();
                    for (File file: subfiles) {
                        filenames.add(file.getName());
                    }
                    String[] filename = new String[filenames.size()];
                    filename = filenames.toArray(filename);
                    cpFilesToFolder(isRecursive, destFileName, srcFileName);
                } else {
                    cpFilesToFolder(isRecursive, destFileName, srcFileName);
                }
            } else {
                    cpSrcFileToDestFile(isRecursive, srcFileName, destFileName);
            }
        } catch (CpException cpException) {
            throw cpException;
        } catch (Exception e) {
            //TODO: check excpetion type
            throw new RuntimeException(e);
        }

    }

    @Override
    public String cpSrcFileToDestFile(Boolean isRecursive, String srcFile, String destFile) throws Exception {
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

        FileInputStream in = new FileInputStream(src);
        FileOutputStream out = new FileOutputStream(dest);

        int n;

        while ((n = in.read()) != -1) {
            out.write(n);
        }

        return null;
    }

    @Override
    public String cpFilesToFolder(Boolean isRecursive, String destFolder, String... fileName) throws Exception {
        String srcName = fileName[0];
        File dest = new File(destFolder);
        File src = new File(srcName);

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
