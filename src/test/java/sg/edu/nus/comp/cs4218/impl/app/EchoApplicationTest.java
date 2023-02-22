package sg.edu.nus.comp.cs4218.impl.app;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sg.edu.nus.comp.cs4218.exception.EchoException;

import static org.junit.jupiter.api.Assertions.*;

import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.STRING_NEWLINE;

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

    @Test
    void echo_WithQuotes_ShouldEchoCorrectly() throws EchoException {
        String[] array = {"A*B*C"};
        String result = echoApplication.constructResult(array);
        assertEquals("A*B*C", result);
    }

    @Test
    void echo_NoArgs_ShouldEchoCorrectly() throws EchoException {
        String[] array = {};
        String result = echoApplication.constructResult(array);
        assertEquals(STRING_NEWLINE, result);
    }

    @Test
    void echo_EmptyString_ShouldEchoCorrectly() throws EchoException {
        String [] array = {""};
        String result = echoApplication.constructResult(array);
        assertEquals("", result);
    }

    @Test
    void echo_SpecialCharacters_ShouldEchoCorrectly() throws EchoException {
        String [] array = {"!@#$%^&*()"};
        String result = echoApplication.constructResult(array);
        assertEquals("!@#$%^&*()", result);
    }
}
