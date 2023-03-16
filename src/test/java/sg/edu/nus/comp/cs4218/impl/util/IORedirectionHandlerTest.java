package sg.edu.nus.comp.cs4218.impl.util;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import sg.edu.nus.comp.cs4218.exception.AbstractApplicationException;
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

    private static String filename2 = "file2.txt";
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
        FileWriter myWriter = new FileWriter(filename);
        try {
            myWriter.write("Test Message");
            myWriter.close();
        } catch (IOException e) {
            throw e;
        } finally {
            myWriter.close();
        }

        argumentResolver = new ArgumentResolver();
        origInputStream = new ByteArrayInputStream(new byte[0]);
        origOutputStream = new ByteArrayOutputStream();
    }

    @AfterAll
    static void cleanup() {
        File file = new File(filename);
        if (file.exists()) {
            file.delete();
        }
        File file2 = new File(filename2);
        if (file2.exists()) {
            file2.delete();
        }
    }

    @Test
    void extract_Arguments_ShouldNotThrowException() throws IOException, AbstractApplicationException, ShellException {
        IORedirectionHandler handler = new IORedirectionHandler( argsList, origInputStream,
                origOutputStream, argumentResolver);
        assertDoesNotThrow(() -> handler.extractRedirOptions());
    }

    @Test
    void extract_Arguments_NoRedirArgShouldBePopulatedCorrectly() throws FileNotFoundException, AbstractApplicationException, ShellException {
        IORedirectionHandler handler = new IORedirectionHandler( argsList, origInputStream,
                origOutputStream, argumentResolver);
        handler.extractRedirOptions();
        assertEquals(Arrays.asList("paste"), handler.getNoRedirArgsList());
    }

    @Test
    void extract_Arguments_InputStreamShouldBeAlteredCorrectly() throws IOException, AbstractApplicationException, ShellException {
        IORedirectionHandler handler = new IORedirectionHandler( argsList, origInputStream,
                origOutputStream, argumentResolver);
        handler.extractRedirOptions();
        assertEquals(Files.readString(Path.of(filename)),
                new String(handler.getInputStream().readAllBytes()));
    }

    @Test
    void extract_Arguments_OutputStreamShouldBeAlteredCorrectly() throws IOException, AbstractApplicationException, ShellException {
        List<String> argList1 = Arrays.asList("ls", ">", filename);
        IORedirectionHandler handler1 = new IORedirectionHandler(argList1, origInputStream,
                origOutputStream, argumentResolver);
        handler1.extractRedirOptions();
        assertEquals(filename,
                handler1.getOutputFilePath());
    }

    @Test
    void extract_ArgumentsAppend_OutputStreamShouldBeAlteredCorrectly() throws IOException, AbstractApplicationException, ShellException {
        List<String> argList1 = Arrays.asList("ls", ">", ">", filename);
        IORedirectionHandler handler1 = new IORedirectionHandler(argList1, origInputStream,
                origOutputStream, argumentResolver);
        handler1.extractRedirOptions();
        assertEquals(filename,
                handler1.getOutputFilePath());
    }

    @Test
    void extract_ArgumentsNoAppend_IsAppendIsFalse() throws FileNotFoundException, AbstractApplicationException, ShellException {
        IORedirectionHandler handler = new IORedirectionHandler( argsList, origInputStream,
                origOutputStream, argumentResolver);
        handler.extractRedirOptions();
        assertEquals(false, handler.isAppend());
    }

    @Test
    void extract_ArgumentsAppend_IsAppendIsTrue() throws FileNotFoundException, AbstractApplicationException, ShellException {
        List<String> argList1 = Arrays.asList("ls", ">", ">", filename);
        IORedirectionHandler handler1 = new IORedirectionHandler(argList1, origInputStream,
                origOutputStream, argumentResolver);
        handler1.extractRedirOptions();
        assertEquals(true, handler1.isAppend());
    }
    @Test
    void extract_EmptyArguments_ShouldThrowShellException() {
        IORedirectionHandler handler1 = new IORedirectionHandler(new ArrayList(), origInputStream,
                origOutputStream, argumentResolver);
        assertThrows(ShellException.class, () -> {
            handler1.extractRedirOptions();
        });
    }

    @Test
    void extract_WrongRedirArg_ShouldThrowShellException() {
        List<String> argList1 = Arrays.asList("ls", "<", ">", filename);
        IORedirectionHandler handler1 = new IORedirectionHandler(argList1, origInputStream,
                origOutputStream, argumentResolver);
        assertThrows(ShellException.class, () -> {
            handler1.extractRedirOptions();
        });

        List<String> argList2 = Arrays.asList("ls", "<", "<", filename);
        IORedirectionHandler handler2 = new IORedirectionHandler(argList2, origInputStream,
                origOutputStream, argumentResolver);
        assertThrows(ShellException.class, () -> {
            handler2.extractRedirOptions();
        });

        List<String> argList3 = Arrays.asList("ls", ">", "<", filename);
        IORedirectionHandler handler3 = new IORedirectionHandler(argList3, origInputStream,
                origOutputStream, argumentResolver);
        assertThrows(ShellException.class, () -> {
            handler3.extractRedirOptions();
        });
    }

    @Test
    void extract_TooManyFiles_ShouldThrowShellException() {
        List<String> argList1 = Arrays.asList("ls", ">", filename, filename);
        IORedirectionHandler handler1 = new IORedirectionHandler(argList1, origInputStream,
                origOutputStream, argumentResolver);
        assertThrows(ShellException.class, () -> {
            handler1.extractRedirOptions();
        });
    }

    @Test
    void extract_TooFewFiles_ShouldThrowShellException() {
        List<String> argList1 = Arrays.asList("ls", ">");
        IORedirectionHandler handler1 = new IORedirectionHandler(argList1, origInputStream,
                origOutputStream, argumentResolver);
        assertThrows(ShellException.class, () -> {
            handler1.extractRedirOptions();
        });

        List<String> argList2 = Arrays.asList("ls", ">", ">");
        IORedirectionHandler handler2 = new IORedirectionHandler(argList2, origInputStream,
                origOutputStream, argumentResolver);
        assertThrows(ShellException.class, () -> {
            handler2.extractRedirOptions();
        });
    }

    @Test
    void extract_BothRedirChar_BothInputOutputStreamShouldBeAlteredCorrectly() throws IOException, AbstractApplicationException, ShellException {
        List<String> argList1 = Arrays.asList("ls", "<", filename, ">", filename2);
        IORedirectionHandler handler1 = new IORedirectionHandler(argList1, origInputStream,
                origOutputStream, argumentResolver);
        handler1.extractRedirOptions();
        assertEquals(Files.readString(Path.of(filename)),
                new String(handler1.getInputStream().readAllBytes()));
        assertEquals(filename2,
                handler1.getOutputFilePath());

        List<String> argList2 = Arrays.asList("ls", ">", filename2, "<", filename);
        IORedirectionHandler handler2 = new IORedirectionHandler(argList2, origInputStream,
                origOutputStream, argumentResolver);
        handler2.extractRedirOptions();
        assertEquals(Files.readString(Path.of(filename)),
                new String(handler2.getInputStream().readAllBytes()));
        assertEquals(filename2,
                handler2.getOutputFilePath());
    }
}
