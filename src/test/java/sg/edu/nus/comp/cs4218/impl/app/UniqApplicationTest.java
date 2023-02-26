package sg.edu.nus.comp.cs4218.impl.app;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;

public class UniqApplicationTest {
    UniqApplication uniqApplication = new UniqApplication();

    String fileContent = "Hello World" + System.lineSeparator() +
            "Hello World" + System.lineSeparator() +
            "Hello World" + System.lineSeparator() +
            "Hello World" + System.lineSeparator() +
            "Hello World" + System.lineSeparator() +
            "Alice" + System.lineSeparator() +
            "Alice" + System.lineSeparator() +
            "Bob" + System.lineSeparator() +
            "Alice" + System.lineSeparator() +
            "Bob" + System.lineSeparator() +
            "Bob";
    String inputFileName = "test.txt";
    @BeforeAll
    void setUpFile() throws IOException {
        File inputFile = new File(inputFileName);
        if(!inputFile.exists()) {
            inputFile.createNewFile();
            FileWriter inputWriter = null;
            try {
                inputWriter = new FileWriter(inputFile);
                inputWriter.write(fileContent);
            } catch (IOException ioException) {
                throw ioException;
            } finally {
                inputWriter.close();
            }
        }
    }

    @Test
    void uniq_FileToFileNoTag_ShouldCompleteProperly(){
        uniqApplication.run(inputFileName, );
    }
}
