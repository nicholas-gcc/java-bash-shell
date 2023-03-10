package sg.edu.nus.comp.cs4218.testutils;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TestEnvironmentUtil {

    private static final String CURRENT_DIR_FIELD = "currentDirectory";
    private static Class<?> environmentClass;

    private static String retrievePackageNameForClassName(String className) {
        try (Stream<Path> filesWalk = Files.walk(Paths.get("src"))) {

            List<String> result = filesWalk.map(Path::toString)
                    .filter(s -> s.contains(className))
                    .collect(Collectors.toList());

            Path path = Paths.get(result.get(0));
            Optional<String> packageDeclarationLine = Files.lines(path).findFirst();

            if (packageDeclarationLine.isPresent()) {
                return packageDeclarationLine.get().replaceAll("package |;", "");
            }

            System.err.println("Package declaration not present in " + className);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "";
    }

    // Adapted from https://stackoverflow.com/questions/28678026/how-can-i-get-all-class-files-in-a-specific-package-in-java
    private static List<Class<?>> getClassesInPackage(String packageName) {
        String path = packageName.replaceAll("\\.", File.separator);
        List<Class<?>> classes = new ArrayList<>();
        String[] classPathEntries = System.getProperty("java.class.path").split(
                System.getProperty("path.separator")
                                                                               );
        String name;
        for (String classpathEntry : classPathEntries) {
            if (classpathEntry.endsWith(".jar")) {
                continue;
            }
            try {
                File base = new File(classpathEntry + File.separatorChar + path);
                for (File file : base.listFiles()) {
                    name = file.getName();
                    if (name.endsWith(".class")) {
                        name = name.substring(0, name.length() - 6);
                        classes.add(Class.forName(packageName + "." + name));
                    }
                }
            } catch (Exception ex) {
                System.err.println("An error occured while obtaining classes: ");
                System.err.println(ex.getMessage());
                ex.printStackTrace();
            }
        }

        return classes;
    }

    private static void getEnvironmentClass() {
        String packageName = retrievePackageNameForClassName("Environment");
        List<Class<?>> classes = getClassesInPackage(packageName);

        for (Class<?> packageClass : classes) {
            if (packageClass.getName().contains("Environment")) {
                environmentClass = packageClass;
                break;
            }
        }
    }

    public static void setCurrentDirectory(String directory) throws NoSuchFieldException, IllegalAccessException {
        if (environmentClass == null) {
            getEnvironmentClass();
        }

        Field currentDir = environmentClass.getField(CURRENT_DIR_FIELD);
        currentDir.set(null, directory);
    }

    public static String getCurrentDirectory() throws NoSuchFieldException, IllegalAccessException {
        if (environmentClass == null) {
            getEnvironmentClass();
        }

        Field currentDir = environmentClass.getField(CURRENT_DIR_FIELD);
        return currentDir.get(null).toString();
    }
}
