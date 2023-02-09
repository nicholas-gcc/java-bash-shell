package sg.edu.nus.comp.cs4218.app;

import sg.edu.nus.comp.cs4218.Application;

public interface CdInterface extends Application {
    /**
     * Change the environment context to a different directory.
     *
     * @param path String of the path to a directory
     * @throws Exception
     */
    void changeToDirectory(String path) throws Exception;
}