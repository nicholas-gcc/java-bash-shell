package sg.edu.nus.comp.cs4218.impl.util;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import sg.edu.nus.comp.cs4218.exception.ShellException;

import java.io.*;
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
    static IORedirectionHandler ioRedirectionHandler;

    @BeforeAll
    static void setup() throws IOException {
        File file = new File(filename);
        if (!file.exists()) {
            file.createNewFile();
        }
        argumentResolver = new ArgumentResolver();
        origInputStream = new ByteArrayInputStream(new byte[0]);
        origOutputStream = new ByteArrayOutputStream();
        ioRedirectionHandler = new IORedirectionHandler( argsList, origInputStream,
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
            ioRedirectionHandler.extractRedirOptions();
            assertEquals(Arrays.asList("paste"), ioRedirectionHandler.getNoRedirArgsList());
            assertEquals(Files.readString(Path.of(filename)),
                    new String(ioRedirectionHandler.getInputStream().readAllBytes()));
        });
    }

    @Test
    void extract_EmptyArguments_ShouldThrowShellException() {
        IORedirectionHandler ioRedirectionHandler2 = new IORedirectionHandler(new ArrayList(), origInputStream,
                origOutputStream, argumentResolver);
        assertThrows(ShellException.class, () -> {
            ioRedirectionHandler2.extractRedirOptions();
        });
    }
}
