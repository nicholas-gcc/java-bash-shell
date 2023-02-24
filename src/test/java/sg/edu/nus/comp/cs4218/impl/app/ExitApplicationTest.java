package sg.edu.nus.comp.cs4218.impl.app;

import mockit.Mock;
import mockit.MockUp;
import org.junit.jupiter.api.Test;
import sg.edu.nus.comp.cs4218.impl.util.ApplicationRunner;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;


public class ExitApplicationTest {
    private final ExitApplication exitApplication = new ExitApplication();
    private final String[] args = {};
    @Test
    void testExit_ExitAppRun_ReturnsCorrectStatusCode() {
        new MockUp<System>() {
            @Mock
            public void exit(int value) {
                throw new RuntimeException(String.valueOf(value));
            }
        };

        Throwable thrown = assertThrows(RuntimeException.class, () -> exitApplication.run(args, System.in, System.out));
        assertEquals("0", thrown.getMessage());
    }

    @Test
    void testExit_AppRunnerRunApp_ReturnsCorrectStatusCode() {
        new MockUp<System>() {
            @Mock
            public void exit(int value) {
                throw new RuntimeException(String.valueOf(value));
            }
        };
        ApplicationRunner applicationRunner = new ApplicationRunner();
        Throwable thrown = assertThrows(RuntimeException.class, () -> {
            applicationRunner.runApp("exit", args, System.in, System.out);
        });
        assertEquals("0", thrown.getMessage());
    }
}
