package sg.edu.nus.comp.cs4218.impl.util;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import sg.edu.nus.comp.cs4218.exception.ShellException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class IORedirectionHandlerTest {
    private static String filename = "file.txt";
    private static List<String> argsList = Arrays.asList("paste", "<", filename);
    private static ArgumentResolver argumentResolver;
    private static InputStream origInputStream;
    private static OutputStream origOutputStream;
    static IORedirectionHandler handler;

    @BeforeAll
    static void setup() throws IOException {
        File file = new File(filename);
        if (!file.exists()) {
            file.createNewFile();
        }
        argumentResolver = new ArgumentResolver();
        origInputStream = new ByteArrayInputStream(new byte[0]);
        origOutputStream = new ByteArrayOutputStream();
        handler = new IORedirectionHandler( argsList, origInputStream,
                origOutputStream, argumentResolver);
    }

    @AfterAll
    static void cleanup() {
        File file = new File(filename);
        if (file.exists()) {
            file.delete();
        }
    }

    @Test
    void extract_Arguments_ShouldNotThrowException() {
        assertDoesNotThrow(() -> {
            handler.extractRedirOptions();
            assertEquals(Arrays.asList("paste"), handler.getNoRedirArgsList());
            assertEquals(Files.readString(Path.of(filename)),
                    new String(handler.getInputStream().readAllBytes()));
        });
    }

    @Disabled
    @Test
    void extract_EmptyArguments_ShouldThrowShellException() {
        IORedirectionHandler handler1 = new IORedirectionHandler(new ArrayList(), origInputStream,
                origOutputStream, argumentResolver);
        assertThrows(ShellException.class, () -> {
            handler1.extractRedirOptions();
        });
    }
}
