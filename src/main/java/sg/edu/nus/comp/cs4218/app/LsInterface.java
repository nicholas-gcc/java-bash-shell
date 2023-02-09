package sg.edu.nus.comp.cs4218.app;

import sg.edu.nus.comp.cs4218.Application;

public interface LsInterface extends Application {
    /**
     * Return the string listing the folder content of the specified folders. If no folder names are
     * specified, list the content of the current folder.
     *
     * @param isRecursive   Boolean option to recursively list the folder contents (traversing
     *                      through all folders inside the specified folder)
     * @param isSortByExt   Boolean option to sort folder contents alphabetically by file extension (characters after the last '.' (without quotes)) Files with no extension are sorted first.
     * @param folderName    Array of String of folder names
     * @throws Exception
     */
    String listFolderContent(Boolean isRecursive, Boolean isSortByExt,
                             String... folderName) throws Exception;
}
