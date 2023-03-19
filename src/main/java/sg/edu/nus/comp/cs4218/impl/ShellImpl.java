package sg.edu.nus.comp.cs4218.impl;

import sg.edu.nus.comp.cs4218.Command;
import sg.edu.nus.comp.cs4218.Environment;
import sg.edu.nus.comp.cs4218.Shell;
import sg.edu.nus.comp.cs4218.exception.AbstractApplicationException;
import sg.edu.nus.comp.cs4218.exception.ShellException;
import sg.edu.nus.comp.cs4218.impl.util.ApplicationRunner;
import sg.edu.nus.comp.cs4218.impl.util.CommandBuilder;
import sg.edu.nus.comp.cs4218.impl.util.StringUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
public class ShellImpl implements Shell {

    /**
     * Main method for the Shell Interpreter program.
     *
     * @param args List of strings arguments, unused.
     */
    public static void main(String... args) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        Shell shell = new ShellImpl();

        try {
            String commandString;
            while (true) {
                // try to read and set up shell
                try {
                    String currentDirectory = Environment.currentDirectory;
                    System.out.print(new File(currentDirectory).getAbsolutePath() + "> ");
                    commandString = reader.readLine();
                    if (commandString == null) {
                        // EOF or Ctrl + D detected, continue process, note this will cause infinite loop
                        // if running in intellij terminal, but works fine using jar
                        continue;
                    }
                } catch (IOException e) {
                    break; // Streams are closed, terminate process
                }

                // try to parse commands
                try {
                    if (!StringUtils.isBlank(commandString)) {
                        shell.parseAndEvaluate(commandString, System.out);
                        System.out.println();
                    }
                } catch (ShellException | AbstractApplicationException e) {
                    System.out.println(e.getMessage());
                }
            }

        } catch (Exception e) {
            System.out.println(e.getMessage());
        } finally {
            try {
                reader.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void parseAndEvaluate(String commandString, OutputStream stdout)
            throws AbstractApplicationException, ShellException, FileNotFoundException {
        Command command = CommandBuilder.parseCommand(commandString, new ApplicationRunner());
        command.evaluate(System.in, stdout);
    }
}
