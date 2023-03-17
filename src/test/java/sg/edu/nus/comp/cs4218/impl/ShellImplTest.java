package sg.edu.nus.comp.cs4218.impl;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sg.edu.nus.comp.cs4218.exception.ShellException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ShellImplTest {

    private final ShellImpl shell = new ShellImpl();
    private OutputStream outputStream;

    @BeforeEach
    void setup() {
        outputStream = new ByteArrayOutputStream();
    }

    @AfterEach
    void reset() throws IOException {
        outputStream.close();
    }

    @Test
    void parseAndEvaluate_SimpleValidCommand_DoesNotThrowException() {
        String command = "echo hello";
        assertDoesNotThrow(() -> {
            shell.parseAndEvaluate(command, outputStream);
        });
    }

    @Test
    void parseAndEvaluate_ComplexValidCommand_DoesNotThrowException() {
        String command = "echo hello | grep \"hello\"";
        assertDoesNotThrow(() -> {
            shell.parseAndEvaluate(command, outputStream);
        });
    }

    @Test
    void parseAndEvaluate_InvalidCommand_ThrowsShellExceptionWithCorrectMessage() {
        String command = "fakecmd hello";
        Throwable exception = assertThrows(ShellException.class, () -> shell.parseAndEvaluate(command, outputStream));
        assertEquals("shell: fakecmd: Invalid app", exception.getMessage());
    }
}
