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

    @Test
    void echo_LongString_ShouldEchoCorrectly() throws EchoException {
        String [] array = {"PqZu6co8rw7SWvSqreE56i09RKf2LYfrGhsGPqbOPuqdrgZ0adHF28LWi2RjIpJtCtc6z9HEYLMVezXMzdUy1r" +
                "UVT3V3NZVpYVoigZP6yIp4wSqZ3M9D8Ail8NLeSLGUNyEL0ZwKA3agPity5jk3K3eeikT0YnN3y5a0IR9U858LOANl0hL5gew" +
                "hgOA225JqW3nuy69U2Xfh6DXA8AY4qSn5zMbCK1X7VAF2qiFBt1kCDhpuhwJCa7yB147GAhTFg0SN3dGNWEeOQVPjo8T03Fih" +
                "QfrCfkZUNpllTU4DpBdlizRxCNVpRjh2UxpZo2AD6aXkjKKKJw4sRgMTAizXxq489837f2bkylQBX3hCx2YppTiw5rZXbkKu7" +
                "H4FwJAC85J6vvwFsMlUfxPoustRtGVukFtIIRKnL7omUOAxaSH515m043UT2ckSKSGbzcSc0bUy1V6X28rI6ZnnINV8J6uZ3X" +
                "pc5DisJSEfm4WadhZC7fqPkPVj"};
        String result = echoApplication.constructResult(array);
        assertEquals(array[0], result);
    }
}
