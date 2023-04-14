package sg.edu.nus.comp.cs4218.impl.util;

import sg.edu.nus.comp.cs4218.Environment;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.CHAR_ASTERISK;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.CHAR_FILE_SEP;


@SuppressWarnings("PMD.AvoidStringBufferField")
public final class RegexArgument {
    private final StringBuilder plaintext;
    private final StringBuilder regex;
    private boolean isRegex;

    public RegexArgument() {
        this.plaintext = new StringBuilder();
        this.regex = new StringBuilder();
        this.isRegex = false;
    }

    public RegexArgument(String str) {
        this();
        merge(str);
    }

    // Used for `find` command.
    // `text` here corresponds to the folder that we want to look in.
    public RegexArgument(String str, String text, boolean isRegex) {
        this();
        this.plaintext.append(text);
        this.isRegex = isRegex;
        this.regex.append(".*"); // We want to match filenames
        for (char c : str.toCharArray()) {
            if (c == CHAR_ASTERISK) {
                this.regex.append("[^" + StringUtils.fileSeparator() + "]*");
            } else if (c == '/') {
                // Handles inconsistency in Windows
                regex.append(Pattern.quote(StringUtils.fileSeparator()));
            } else {
                this.regex.append(Pattern.quote(String.valueOf(c)));
            }
        }
    }

    public void append(char chr) {
        plaintext.append(chr);
        if (chr == '/') {
            // Handles inconsistency in Windows
            regex.append(Pattern.quote(String.valueOf('\\')));
        } else {
            regex.append(Pattern.quote(String.valueOf(chr)));
        }
    }

    public void appendAsterisk() {
        plaintext.append(CHAR_ASTERISK);
        regex.append("[^");
        regex.append(StringUtils.fileSeparator());
        regex.append("]*");
        isRegex = true;
    }

    public void merge(RegexArgument other) {
        plaintext.append(other.plaintext);
        regex.append(other.regex);
        isRegex = isRegex || other.isRegex;
    }

    public void merge(String str) {
        plaintext.append(str);
        regex.append(Pattern.quote(str));
    }

    public List<String> globFiles() {
        if (!isRegex) {
            return List.of(plaintext.toString());
        }

        String modifiedPlaintext = plaintext.toString().replaceAll(Pattern.quote(File.separator), "/");
        String[] tokens = modifiedPlaintext.split("/");

        List<String> globbedFiles = new ArrayList<>();
        globFilesRecursive(tokens, 0, Environment.currentDirectory, globbedFiles);

        Collections.sort(globbedFiles);

        if (globbedFiles.isEmpty()) {
            globbedFiles.add(plaintext.toString());
        }

        return globbedFiles;
    }

    private void globFilesRecursive(String[] tokens, int tokenIndex, String currentPath, List<String> globbedFiles) {
        if (tokenIndex == tokens.length) {
            return;
        }

        String token = tokens[tokenIndex];
        Pattern regexPattern = Pattern.compile(token.replaceAll("\\*", ".*"));
        File currentDir = new File(currentPath);

        if (currentDir.exists()) {
            for (File file : currentDir.listFiles()) {
                String filePathName = file.getName();

                if (!regexPattern.matcher(filePathName).matches()) {
                    continue;
                }

                String fullPath = currentPath + File.separator + filePathName;

                if (file.isDirectory()) {
                    fullPath += File.separator;
                    if (tokenIndex < tokens.length - 1) {
                        globFilesRecursive(tokens, tokenIndex + 1, fullPath, globbedFiles);
                    }
                }

                if (tokenIndex == tokens.length - 1) {
                    globbedFiles.add(fullPath);
                }
            }
        }
    }



    /**
     * Traverses a given File node and returns a list of absolute path that match the given regexPattern.
     * <p>
     * Assumptions:
     * - ignores files and folders that we do not have access to (insufficient read permissions)
     * - regexPattern should not be null
     *
     * @param regexPattern    Pattern object
     * @param node            File object
     * @param isAbsolute      Boolean option to indicate that the regexPattern refers to an absolute path
     * @param onlyDirectories Boolean option to list only the directories
     */
    private List<String> traverseAndFilter(Pattern regexPattern, File node, boolean isAbsolute, boolean onlyDirectories) {
        List<String> matches = new ArrayList<>();
        if (regexPattern == null || !node.canRead() || !node.isDirectory()) {
            return matches;
        }
        for (String current : node.list()) {
            File nextNode = new File(node, current);
            String match = isAbsolute
                    ? nextNode.getPath()
                    : nextNode.getPath().substring(Environment.currentDirectory.length() + 1);
            if (onlyDirectories && nextNode.isDirectory()) {
                match += File.separator;
            }
            if (!nextNode.isHidden() && regexPattern.matcher(match).matches()) {
                matches.add(nextNode.getAbsolutePath());
            }
            matches.addAll(traverseAndFilter(regexPattern, nextNode, isAbsolute, onlyDirectories));
        }
        return matches;
    }

    public boolean isEmpty() {
        return plaintext.length() == 0;
    }

    public String toString() {
        return plaintext.toString();
    }
}
