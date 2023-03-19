package sg.edu.nus.comp.cs4218.impl.app;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sg.edu.nus.comp.cs4218.exception.EchoException;
import sg.edu.nus.comp.cs4218.exception.ShellException;
import sg.edu.nus.comp.cs4218.impl.util.IOUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.ERR_IO_EXCEPTION;
import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.ERR_NO_OSTREAM;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.STRING_NEWLINE;

public class EchoApplicationTest {

    EchoApplication echoApplication;
    InputStream stdin;
    OutputStream stdout;
    private static final String ABC_TEST = "A*B*C";

    @BeforeEach
    void setup() {
        echoApplication = new EchoApplication();
        stdin = System.in;
        stdout = new ByteArrayOutputStream();
    }

    @Test
    void echo_WithSpaces_ShouldEchoCorrectly() throws EchoException {
        String[] array = {"A", "B", "C"};
        String result = echoApplication.constructResult(array);
        assertEquals("A B C" + STRING_NEWLINE, result);
    }

    @Test
    void echo_WithQuotes_ShouldEchoCorrectly() throws EchoException {
        String[] array = {ABC_TEST};
        String result = echoApplication.constructResult(array);
        assertEquals(ABC_TEST + STRING_NEWLINE, result);
    }

    @Test
    void echo_NoArgs_ShouldEchoCorrectly() throws EchoException {
        String[] array = {};
        String result = echoApplication.constructResult(array);
        assertEquals("", result);
    }

    @Test
    void echo_EmptyString_ShouldEchoCorrectly() throws EchoException {
        String [] array = {""};
        String result = echoApplication.constructResult(array);
        assertEquals("" + STRING_NEWLINE, result);
    }

    @Test
    void echo_SpecialCharacters_ShouldEchoCorrectly() throws EchoException {
        String [] array = {"!@#$%^&*()"};
        String result = echoApplication.constructResult(array);
        assertEquals("!@#$%^&*()" + STRING_NEWLINE, result);
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
        assertEquals(array[0] + STRING_NEWLINE, result);
    }

    @Test
    void echo_Run_ShouldEchoCorrectly() throws EchoException {
        String[] array = {ABC_TEST};
        echoApplication.run(array, stdin, stdout);
        assertEquals(array[0] + STRING_NEWLINE, stdout.toString());
    }

    @Test
    void echo_RunNullStdout_ThrowsException() {
        String[] array = {ABC_TEST};
        EchoException echoException = assertThrows(EchoException.class, () -> echoApplication.run(array, stdin, null));
        assertEquals("echo: " + ERR_NO_OSTREAM, echoException.getMessage());
    }

    @Test
    void echo_RunClosedStdout_ThrowsException() throws ShellException, IOException {
        String[] array = {ABC_TEST};
        stdout = new FileOutputStream("output.txt");
        IOUtils.closeOutputStream(stdout);
        EchoException echoException = assertThrows(EchoException.class, () -> echoApplication.run(array, stdin, stdout));
        assertEquals("echo: " + ERR_IO_EXCEPTION, echoException.getMessage());
        File file = new File("output.txt");
        file.delete();
    }
}
