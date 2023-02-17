package sg.edu.nus.comp.cs4218.impl.app;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sg.edu.nus.comp.cs4218.exception.EchoException;

import static org.junit.jupiter.api.Assertions.*;

public class EchoApplicationTest {
    EchoApplication echoApplication;

    @BeforeEach
    void setup() {
        echoApplication = new EchoApplication();
    }

    @Test
    void echo_WithSpaces_ShouldEchoCorrectly() throws EchoException {
        String[] array = {"A", "B", "C"};
        String result = echoApplication.constructResult(array);

        assertEquals("A B C", result);
    }
}
