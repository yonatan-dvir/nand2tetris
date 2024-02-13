import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class JackAnalyzer {

    public static void main(String[] args) throws IOException {

        if (args.length != 1) {
            System.out.println("Usage: java JackAnalyzer <file or directory>");
            return;
        }

        File inputFile = new File(args[0]);

        if (inputFile.isFile() && args[0].endsWith(".jack")) {
            // Single .jack file provided
            processFile(inputFile);
        } else if (inputFile.isDirectory()) {
            // Directory provided, translate all .jack files within the folder
            File[] jackFiles = inputFile.listFiles((dir, name) -> name.endsWith(".jack"));
            if (jackFiles != null) {
                for (File jackFile : jackFiles) {
                    processFile(jackFile);
                }
            }
        } else {
            System.out.println("Invalid input. Please provide a valid .jack file or directory.");
        }
    }

    private static void processFile(File inputFile) throws IOException {
        // Create the output file with the same name as the prepared file but with .xml extension
        String outputFileName = inputFile.getAbsolutePath().replace(".jack", ".xml");
        File outputFile = new File(outputFileName);

        // Creates a CompilationEngine and calls the compileClass method
        CompilationEngine compilationEngine = new CompilationEngine(inputFile, outputFile);
        compilationEngine.compileClass();


        // Close the buffer writer.
        //compilationEngine.bufferedWriter.close();
    }
}

