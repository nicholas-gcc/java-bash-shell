package sg.edu.nus.comp.cs4218.app;

import sg.edu.nus.comp.cs4218.Application;

public interface ExitInterface extends Application {
    /**
     * Terminate shell.
     *
     * @throws Exception
     */
    void terminateExecution() throws Exception;
}
